package net.finmath.smartcontract.valuation.oracle.interestrates;

import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.products.AnalyticProduct;
import net.finmath.smartcontract.model.ExceptionId;
import net.finmath.smartcontract.model.SDCException;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.*;
import net.finmath.smartcontract.valuation.oracle.ValuationOracle;
import net.finmath.smartcontract.valuation.oracle.ValuationType;
import net.finmath.time.FloatingpointDate;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An oracle for swap valuation which generates values using externally provided historical market data scenarios.
 *
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */

public class ValuationOraclePlainSwap implements ValuationOracle {

	public enum ValuationTypeSwap implements ValuationType {
		VALUE,
		VALUE_RECEIVER_LEG,
		VALUE_PAYER_LEG
	}

	private static final String FIXING = "Fixing";

	private final CurrencyUnit currency = Monetary.getCurrency("EUR");
	private final List<CalibrationDataset> scenarioList;
	private final Map<String, AnalyticProduct> products;
	private final int scale;

	/**
	 * Oracle will be instantiated based on a Swap product an market data scenario list
	 *
	 * @param products        The underlying products.
	 * @param scenarioList   The list of market data scenarios to be used for valuation.
	 * @param scale     Specification of the rounding.
	 */
	public ValuationOraclePlainSwap(final Map<String, AnalyticProduct> products, final List<CalibrationDataset> scenarioList, int scale) {
		this.products = products;
		this.scenarioList = scenarioList;
		this.scale = scale;
	}

	/**
	 * Oracle will be instantiated based on a Swap product and market data scenario list
	 *
	 * @param products       A list of products to valuate.
	 * @param scenarioList   The list of market data scenarios to be used for valuation.
	 */
	public ValuationOraclePlainSwap(final Map<String, AnalyticProduct> products, final List<CalibrationDataset> scenarioList) {
		this(products, scenarioList,2);
	}

	@Override
	public MonetaryAmount getAmount(final LocalDateTime evaluationTime, final LocalDateTime marketDataTime) {
		return Money.of(getValue(evaluationTime, marketDataTime), currency);
	}

	@Override
	public BigDecimal getValue(final LocalDateTime evaluationDate, final LocalDateTime marketDataTime) {
		return getValues(evaluationDate, marketDataTime).get("value");
	}

	public Map<String, BigDecimal> getValues(final LocalDateTime evaluationDate, final LocalDateTime marketDataTime) {
		final Optional<CalibrationDataset> optionalScenario =
				scenarioList.stream().filter(scenario -> scenario.getDate().equals(marketDataTime)).findAny();
		if (optionalScenario.isPresent()) {
			final CalibrationDataset scenario = optionalScenario.get();
			AnalyticModel calibratedModel = getCalibratedModel(scenario, marketDataTime);
			final double evaluationTime = FloatingpointDate.getFloatingPointDateFromDate(marketDataTime, evaluationDate);

			Map<String,BigDecimal> values = (
					products.entrySet().stream().collect(Collectors.toMap(
							e -> e.getKey(),
							e -> BigDecimal.valueOf(e.getValue().getValue(evaluationTime, calibratedModel)).setScale(scale,RoundingMode.HALF_UP))));
			return values;
		} else {
			return null;
		}
	}


	private AnalyticModel getCalibratedModel(CalibrationDataset calibrationDataset, LocalDateTime marketDataTime) {
		final CalibrationParserDataItems parser = new CalibrationParserDataItems();
		try {
			final Stream<CalibrationSpecProvider> calibrationItems = calibrationDataset.getDataAsCalibrationDataPointStream(parser);
			List<CalibrationDataItem> fixings = calibrationDataset.getDataPoints().stream().filter(
					cdi -> cdi.getSpec().getProductName().equals(FIXING)).toList();

			Calibrator calibrator = new Calibrator(fixings, new CalibrationContextImpl(marketDataTime, 1E-9));
			final Optional<CalibrationResult> optionalCalibrationResult = calibrator.calibrateModel(calibrationItems, new CalibrationContextImpl(marketDataTime, 1E-9));

			AnalyticModel calibratedModel = optionalCalibrationResult.orElseThrow().getCalibratedModel();
			return calibratedModel;
		} catch (final Exception e) {
			throw new SDCException(ExceptionId.SDC_CALIBRATION_ERROR, e.getMessage());
		}
	}

}