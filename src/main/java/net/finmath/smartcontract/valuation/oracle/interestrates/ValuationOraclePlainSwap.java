package net.finmath.smartcontract.valuation.oracle.interestrates;

import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.products.AnalyticProduct;
import net.finmath.marketdata.products.Swap;
import net.finmath.smartcontract.model.ExceptionId;
import net.finmath.smartcontract.model.SDCException;
import net.finmath.modelling.Product;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.*;
import net.finmath.smartcontract.valuation.oracle.ValuationOracle;
import net.finmath.smartcontract.valuation.oracle.ValuationType;
import net.finmath.time.FloatingpointDate;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.DoubleUnaryOperator;
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

	private final CurrencyUnit currency = Monetary.getCurrency("EUR");
	private final List<CalibrationDataset> scenarioList;
	private final Map<String, AnalyticProduct> products;
	private final double notionalAmount;
	private final DoubleUnaryOperator rounding;

	/**
	 * Oracle will be instantiated based on a Swap product an market data scenario list
	 *
	 * @param products        The underlying products.
	 * @param notionalAmount The notional of the product.
	 * @param scenarioList   The list of market data scenarios to be used for valuation.
	 * @param rounding       An operator implementing the rounding.
	 */
	public ValuationOraclePlainSwap(final Map<String, AnalyticProduct> products, final double notionalAmount, final List<CalibrationDataset> scenarioList, DoubleUnaryOperator rounding) {
		this.notionalAmount = notionalAmount;
		this.products = products;
		this.scenarioList = scenarioList;
		this.rounding = rounding;
	}

	/**
	 * Oracle will be instantiated based on a Swap product and market data scenario list
	 *
	 * @param products       A list of products to valuate.
	 * @param notionalAmount The notional of the product.
	 * @param scenarioList   The list of market data scenarios to be used for valuation.
	 */
	public ValuationOraclePlainSwap(final Map<String, AnalyticProduct> products, final double notionalAmount, final List<CalibrationDataset> scenarioList) {
		this(products, notionalAmount, scenarioList, x -> Math.round(x * 100) / 100.0);
	}

	@Override
	public MonetaryAmount getAmount(final LocalDateTime evaluationTime, final LocalDateTime marketDataTime) {
		return Money.of(getValue(evaluationTime, marketDataTime), currency);
	}

	@Override
	public Double getValue(final LocalDateTime evaluationDate, final LocalDateTime marketDataTime) {
		return getValues(evaluationDate, marketDataTime).get("value");
	}

	public Map<String, Double> getValues(final LocalDateTime evaluationDate, final LocalDateTime marketDataTime) {
		final Optional<CalibrationDataset> optionalScenario =
				scenarioList.stream().filter(scenario -> scenario.getDate().equals(marketDataTime)).findAny();
		if (optionalScenario.isPresent()) {
			final CalibrationDataset scenario = optionalScenario.get();
			final LocalDate referenceDate = marketDataTime.toLocalDate();

			final CalibrationParserDataItems parser = new CalibrationParserDataItems();

			try {

				final Stream<CalibrationSpecProvider> allCalibrationItems =
						scenario.getDataAsCalibrationDataPointStream(parser);
				List<CalibrationDataItem> fixings = scenario.getDataPoints().stream().filter(
						cdi -> cdi.getSpec().getProductName().equals("Fixing") || cdi.getSpec().getProductName().equals(
								"Deposit")).toList();
				Calibrator calibrator = new Calibrator(fixings, new CalibrationContextImpl(referenceDate, 1E-9));

				final Optional<CalibrationResult> optionalCalibrationResult =
						calibrator.calibrateModel(allCalibrationItems, new CalibrationContextImpl(referenceDate, 1E-9));
				AnalyticModel calibratedModel = optionalCalibrationResult.orElseThrow().getCalibratedModel();

				final double evaluationTime = FloatingpointDate.getFloatingPointDateFromDate(
						referenceDate.atStartOfDay(),
						marketDataTime);

				Map<String,Double> values = (products.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> (Double)rounding.applyAsDouble(e.getValue().getValue(evaluationTime, calibratedModel)))));

//				final double valueWithCurves = product.getValue(evaluationTime, calibratedModel) * notionalAmount;

				return values;
//				return rounding.applyAsDouble(valueWithCurves);
			} catch (final Exception e) {
				throw new SDCException(ExceptionId.SDC_CALIBRATION_ERROR, e.getMessage());
			}
		} else {
			return null;
		}
	}
}