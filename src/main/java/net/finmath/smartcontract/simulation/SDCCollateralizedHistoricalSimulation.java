package net.finmath.smartcontract.simulation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.smartcontract.model.ExceptionId;
import net.finmath.smartcontract.model.SDCException;
import net.finmath.smartcontract.product.IRSwapGenerator;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationContextImpl;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationParserDataItems;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationResult;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationSpecProvider;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.Calibrator;
import net.finmath.time.FloatingpointDate;


public class SDCCollateralizedHistoricalSimulation {
	
	private static final String FIXING = "Fixing";
	private static final String DEPOSIT = "Deposit";
	private static final String FORWARD_EUR_6M = "forward-EUR-6M";
	private static final String DISCOUNT_EUR_OIS = "discount-EUR-OIS";
	
	// TODO initialize LocalDate keys with marketData dates
	private static TreeMap<LocalDateTime, Double> marketValueMap = new TreeMap<>(); // V_i
	private static TreeMap<LocalDateTime, Double> marketValueChangeMap = new TreeMap<>(); // Y_i
	private static TreeMap<LocalDateTime, Double> cappedMarketValueChangeMap  = new TreeMap<>(); // X_i
	private static TreeMap<LocalDateTime, Double> collateralAccountMap = new TreeMap<>(); // C_i
	private static TreeMap<LocalDateTime, Double> gapAmountMap = new TreeMap<>(); // Z_i
	private static TreeMap<LocalDateTime, Double> gapAccountMap = new TreeMap<>(); //D_i
	
	private static double marginLimitLower = 0.0;
	private static double marginLimitUpper = 0.0;
	
	
	public static void main(String args[]) throws Exception {

		final LocalDate startDate = LocalDate.of(2007, 1, 1);
		final LocalDate maturity = LocalDate.of(2012, 1, 3);
		final String fileName = "timeseriesdatamap.json";
		final List<CalibrationDataset> scenarioListRaw = CalibrationParserDataItems.getScenariosFromJsonFile(fileName).stream().filter(S -> S.getDate().toLocalDate().isAfter(startDate)).filter(S -> S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());
		final List<CalibrationDataset> scenarioList = scenarioListRaw.stream().map(scenario -> scenario.getScaled(100)).collect(Collectors.toList());
		// TODO add ESTR/EONIA? fixings to the calibration data elements

		/*Generate Sample Product */
		final double notional = 1.0E7;
		final String MaturityKey = "5Y";
		final String forwardCurveKey = "forward-EUR-6M";
		final String discountCurveKey = "discount-EUR-OIS";
		final LocalDate productStartDate = scenarioList.get(0).getDate().toLocalDate();
		/* Product starts at Par */
		final double fixRate = scenarioList.get(0).getDataPoints().stream()
				.filter(datapoint -> datapoint.getSpec().getCurveName().equals("Euribor6M") &&
						datapoint.getSpec().getProductName().equals("Swap-Rate") &&
						datapoint.getSpec().getMaturity().equals("5Y")).mapToDouble(e -> e.getQuote()).findAny().getAsDouble();

		final Swap swap = IRSwapGenerator.generateAnalyticSwapObject(productStartDate, MaturityKey, notional, fixRate, true, forwardCurveKey, discountCurveKey);
		// Assumption float and fix leg have same schedule
		List<LocalDate> paymentDates = ((SwapLeg)swap.getLegPayer()).getSchedule().getPeriods().stream().map(period -> period.getPayment()).collect(Collectors.toList());

		// TODO just print results instead of storing them in maps?
		// TODO check alignment of times, i.e. calibration time e.g. 17:00 or atStartOfDay() and LocalDate of swap schedule
		AnalyticModel calibratedModelPrevious = getCalibratedModel(scenarioList.get(0), scenarioList.get(0).getDate());
		for (int i = 1; i < scenarioList.size(); i++) {
			LocalDateTime scenarioDatePrevious = scenarioList.get(i-1).getDate();
			LocalDateTime scenarioDateCurrent = scenarioList.get(i).getDate();
			AnalyticModel calibratedModelCurrent = getCalibratedModel(scenarioList.get(i), scenarioDateCurrent);

			double valuePrevious = swap.getValue(0.0, calibratedModelPrevious);
			double valueCurrent = swap.getValue(0.0, calibratedModelCurrent);
			// Y-i = V(t_i) - V(t_{i-1})*(1+r_{i-1}*(t_i-t_{i-1}))
			double marketValueChange = valueCurrent - valuePrevious;
			// X_i
			double cappedMarketValueChange = Math.min(Math.max(marketValueChange, -marginLimitLower), marginLimitUpper);

			double dt = FloatingpointDate.getFloatingPointDateFromDate(scenarioDatePrevious,  scenarioDateCurrent);
			double accrualRate = 1 / calibratedModelPrevious.getDiscountCurve(DISCOUNT_EUR_OIS).getDiscountFactor(dt);
			// C_i = C_{i-1}*(1+r_{i-1}*(t_i-t_{i-1})) + X_i
			double collateralAccount = collateralAccountMap.get(scenarioDatePrevious) * accrualRate + cappedMarketValueChange;
			// Z_i = X_i - Y_i
			double gapAmount = cappedMarketValueChange - marketValueChange;
			// D_i = D_{i-1}*(1+r_{i-1}*(t_i-t_{i-1})) + Z_i
			double gapAccount = gapAccountMap.get(scenarioDatePrevious) * accrualRate + gapAmount;
			// on paymentDate reduce collateralAmount by A_k - D_k
			if (scenarioDateCurrent.toLocalDate().equals(paymentDates.get(0))) { // TODO find current paymentDate period
				// TODO determine cash flow value at paymentDate
				double forward = calibratedModelCurrent.getForwardCurve(FORWARD_EUR_6M).getForward(calibratedModelCurrent, 0.0);

				// set gapAccount to zero
			}

			// compare value of plain swap to SDC+C

			calibratedModelPrevious = calibratedModelCurrent;
		}
		
	}

	
	private static AnalyticModel getCalibratedModel(CalibrationDataset calibrationDataset, LocalDateTime marketDataTime) {
		final CalibrationParserDataItems parser = new CalibrationParserDataItems();
		try {
			final Stream<CalibrationSpecProvider> calibrationItems = calibrationDataset.getDataAsCalibrationDataPointStream(parser);
			List<CalibrationDataItem> fixings = calibrationDataset.getDataPoints().stream().filter(
					cdi -> cdi.getSpec().getProductName().equals(FIXING) || cdi.getSpec().getProductName().equals(DEPOSIT)).toList();

			Calibrator calibrator = new Calibrator(fixings, new CalibrationContextImpl(marketDataTime, 1E-9));
			final Optional<CalibrationResult> optionalCalibrationResult = calibrator.calibrateModel(calibrationItems, new CalibrationContextImpl(marketDataTime, 1E-9));

			AnalyticModel calibratedModel = optionalCalibrationResult.orElseThrow().getCalibratedModel();
			return calibratedModel;
		} catch (final Exception e) {
			throw new SDCException(ExceptionId.SDC_CALIBRATION_ERROR, e.getMessage());
		}
	}

	// TODO check if necessary?
	// Search for the nearest ESTR fixing and add it to the calibration items as 1-day discount rate proxy
	private void addOvernightDepositRate(CalibrationDataset calibrationDataset) {
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
				CalibrationDataItem.Spec calibrationItemSpecON = new CalibrationDataItem.Spec("EUREST1D", "ESTR","Overnight-Rate","1D");
				CalibrationDataItem calibrationItemON = new CalibrationDataItem(calibrationItemSpecON, nearestFixing.getQuote(), nearestFixing.getDateTime());
				calibrationDataset.getCalibrationDataItems().add(calibrationItemON);
			}
		}
	}
	

}
