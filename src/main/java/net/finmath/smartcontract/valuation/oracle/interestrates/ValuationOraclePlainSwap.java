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
			// TODO this is only a hotfix, the ESTR OIS MUST be included in the calibration items!!
			addMissingOverNightRate(scenario);

			final CalibrationParserDataItems parser = new CalibrationParserDataItems();

			try {

				final Stream<CalibrationSpecProvider> allCalibrationItems =
						scenario.getDataAsCalibrationDataPointStream(parser);
				List<CalibrationDataItem> fixings = scenario.getDataPoints().stream().filter(
						cdi -> cdi.getSpec().getProductName().equals("Fixing") || cdi.getSpec().getProductName().equals(
								"Deposit")).toList();
				Calibrator calibrator = new Calibrator(fixings, new CalibrationContextImpl(marketDataTime, 1E-9));
				// TODO check conventions for 1-day OIS -> uses CalibrationSpecProviderOis
				final Optional<CalibrationResult> optionalCalibrationResult =
						calibrator.calibrateModel(allCalibrationItems, new CalibrationContextImpl(marketDataTime, 1E-9));
				AnalyticModel calibratedModel = optionalCalibrationResult.orElseThrow().getCalibratedModel();

				final double evaluationTime = FloatingpointDate.getFloatingPointDateFromDate(marketDataTime,evaluationDate);
						//referenceDate.atStartOfDay(),
						//marketDataTime);

				Map<String,BigDecimal> values = (
						products.entrySet().stream().collect(Collectors.toMap(
								e -> e.getKey(),
								e -> BigDecimal.valueOf(e.getValue().getValue(evaluationTime, calibratedModel)).setScale(scale,RoundingMode.HALF_UP))));

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

	private void addMissingOverNightRate(CalibrationDataset calibrationDataset) {
		// If overnight rate is present in calibration data items, do nothing
		Optional<CalibrationDataItem> oisRateOptional = calibrationDataset.getDataPoints().stream().filter(
				cdi -> cdi.getSpec().getKey().equals("EURESTSD") || cdi.getSpec().getKey().equals("EUREST1D")).findAny(); // Correct key name for OIS instrument?
		if (oisRateOptional.isPresent()) {
			return;
		}
		// Search for nearest ESTR fixing
		List<CalibrationDataItem> estrFixings = calibrationDataset.getFixingDataItems().stream().filter(fixingItem ->
				fixingItem.getSpec().getCurveName().equals("ESTR") &&
						fixingItem.getSpec().getMaturity().equals("1D")).toList();
		if (!estrFixings.isEmpty()) {
			CalibrationDataItem nearestFixing = estrFixings.stream()
					.min((item1, item2) -> {
						double diff1 = FloatingpointDate.getFloatingPointDateFromDate(calibrationDataset.getDate(), item1.getDateTime());
						double diff2 = FloatingpointDate.getFloatingPointDateFromDate(calibrationDataset.getDate(), item2.getDateTime());
						return Double.compare(Math.abs(diff1), Math.abs(diff2));
					})
					.orElse(null);
			if (nearestFixing != null) {
				CalibrationDataItem.Spec oisCalibrationItemSpec = new CalibrationDataItem.Spec("EUREST1D", "ESTR","Swap-Rate","1D");
				CalibrationDataItem oisCalibrationItem = new CalibrationDataItem(oisCalibrationItemSpec, nearestFixing.getQuote(), nearestFixing.getDateTime());
				calibrationDataset.getCalibrationDataItems().add(oisCalibrationItem);
			}
		}
	}
}