package net.finmath.smartcontract.simulation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
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
import net.finmath.time.Schedule;
import org.antlr.v4.runtime.tree.Tree;


public class SDCCollateralizedHistoricalSimulation {
	
	private static final String FIXING = "Fixing";
	private static final String DEPOSIT = "Deposit";
	private static final String FORWARD_EUR_6M = "forward-EUR-6M";
	private static final String DISCOUNT_EUR_OIS = "discount-EUR-OIS";
	private static double notional = 1.0E7;
	
	// TODO initialize LocalDate keys with marketData dates
	private static TreeMap<LocalDateTime, Double> marketValueMap = new TreeMap<>(); // V_i
	private static TreeMap<LocalDateTime, Double> marketValueChangeMap = new TreeMap<>(); // Y_i
	private static TreeMap<LocalDateTime, Double> cappedMarketValueChangeMap  = new TreeMap<>(); // X_i
	private static TreeMap<LocalDateTime, Double> collateralAccountMap = new TreeMap<>(); // C_i
	private static TreeMap<LocalDateTime, Double> gapAmountMap = new TreeMap<>(); // Z_i
	private static TreeMap<LocalDateTime, Double> gapAccountMap = new TreeMap<>(); //D_i
	
	private static double marginLimitLower = 10000.0;
	private static double marginLimitUpper = 10000.0;

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static String dailyFormat = "%-22s %20s %20s %20s %20s %20s %20s%n";
	private static String paymentFormat = "%-22s %20s %20s %20s %20s%n";

	private static String dataRowDailyFormat = "%-22s %20.2f %20.2f %20.2f %20.2f %20.2f %20.2f%n";
	private static String dataRowPaymentFormat = "%-22s %20.2f %20.2f %20.2f %20.2f%n";

	public static void main(String args[]) throws Exception {

		final LocalDate startDate = LocalDate.of(2007, 1, 1);
		final LocalDate maturity = LocalDate.of(2012, 1, 3);
		final String fileName = "timeseriesdatamap.json";
		final List<CalibrationDataset> scenarioList = CalibrationParserDataItems.getScenariosFromJsonFile(fileName).stream().filter(S -> S.getDate().toLocalDate().isAfter(startDate)).filter(S -> S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());
		//final List<CalibrationDataset> scenarioList = scenarioListRaw.stream().map(scenario -> scenario.getScaled(100)).collect(Collectors.toList());

		/* Initialize result maps with first scenario date*/
		initializeValueMaps(scenarioList.get(0).getDate());

		/*Generate Sample Product */
		final String MaturityKey = "5Y";
		final String forwardCurveKey = "forward-EUR-6M";
		final String discountCurveKey = "discount-EUR-OIS";
		final LocalDate productStartDate = scenarioList.get(0).getDate().toLocalDate();
		/* Product starts at Par */
		/*final double fixRate = scenarioList.get(0).getDataPoints().stream()
				.filter(datapoint -> datapoint.getSpec().getCurveName().equals("Euribor6M") &&
						datapoint.getSpec().getProductName().equals("Swap-Rate") &&
						datapoint.getSpec().getMaturity().equals("5Y")).mapToDouble(e -> e.getQuote()).findAny().getAsDouble();*/
		final double fixRate = 0.04419229093193379; // calculated previously
		boolean isReceiveFix = true;
		final Swap swap = IRSwapGenerator.generateAnalyticSwapObject(productStartDate, MaturityKey, notional, fixRate, isReceiveFix, forwardCurveKey, discountCurveKey);
		//double swapRate = swap.getForwardSwapRate(((SwapLeg) swap.getLegReceiver()).getSchedule(), ((SwapLeg) swap.getLegPayer()).getSchedule(), calibratedModelPrevious.getForwardCurve(forwardCurveKey), calibratedModelPrevious);

		// TODO They DO NOT! Assumes float and fix leg have the same schedule, float semiannually receiver annually!
		List<LocalDate> paymentDates = ((SwapLeg)swap.getLegPayer()).getSchedule().getPeriods().stream().map(period -> period.getPayment()).collect(Collectors.toList());

		/*
		The swap schedule is based on LocalDate, i.e. if we value the swap on a paymentDate t_i
		the cash flow is already excluded in the valuation.
		If the SDC settles daily at 15:UTC with the current value V(t_i) we assume that the cash flow occurs after the collateralization:
			- V(t_{i-1}) and V(t_i) include the cash flow
			- The amount Y_i of the SDC is given by: (V(t_i) + A_k) - V(t_{i-1})*(1+r_{i-1}*(t_i-t_{i-1}))
			- After the settlement we pay (A_k - D_k) as cash
			- Reduce the collateral accordingly C_k^+ = C_k - (A_k - D_k)
			- Set D_K^+ to zero
		 */
		System.out.printf(dailyFormat, "Scenario Date", "Current Value V(t)", "Value Change Y", "Capped Change X", "Gap Amount Z", "Gap Account D", "Collateral Acct C");
		// TODO marketValueMap.lastEntry().getValue() == valuePrevious(0.0)
		AnalyticModel calibratedModelPrevious = getCalibratedModel(scenarioList.get(0), scenarioList.get(0).getDate());

		double valueWhy = swap.getValue(0.0, calibratedModelPrevious);

		int periodIndex = 0;
		double dt, accrualRate;
		double valueCurrent, valuePrevious;
		double marketValueChange, cappedMarketValueChange;
		double collateralAccount, gapAmount, gapAccount, netCashFlow;
		for (int i = 1; i < scenarioList.size(); i++) {
			LocalDateTime scenarioDatePrevious = scenarioList.get(i-1).getDate();
			LocalDateTime scenarioDateCurrent = scenarioList.get(i).getDate();
			AnalyticModel calibratedModelCurrent = getCalibratedModel(scenarioList.get(i), scenarioDateCurrent);

			dt = FloatingpointDate.getFloatingPointDateFromDate(scenarioDatePrevious, scenarioDateCurrent);
			accrualRate = 1 / calibratedModelPrevious.getDiscountCurve(DISCOUNT_EUR_OIS).getDiscountFactor(dt); // 1+r(t_{i-1})*(t_i-t_{i-1})

			double valueCheck = swap.getValue(0.0, calibratedModelPrevious);
			valueCheck *= accrualRate; // must equal valuePrevious
			valuePrevious = swap.getValue(dt, calibratedModelPrevious); // V(t-1)*(1+r(t_{i-1})*(t_i-t_{i-1}))
			valueCurrent = swap.getValue(0.0, calibratedModelCurrent);
			netCashFlow = 0.0;
			// If the evaluation date equals a payment date t_k of the swap, the returned value is already reduced by the cash-flow of that day
			if (scenarioDateCurrent.toLocalDate().equals(paymentDates.get(0))) {
				// Assumption that payment of cash flows occur after daily settlement
				netCashFlow = getNetCashFlow(swap, isReceiveFix, scenarioList, periodIndex);
			}
			valueCurrent = valueCurrent + netCashFlow;
			marketValueMap.put(scenarioDateCurrent, valueCurrent);
			// Y_i = V(t_i) - V(t_{i-1})*(1+r_{i-1}*(t_i-t_{i-1}))
			marketValueChange = valueCurrent - valuePrevious;
			marketValueChangeMap.put(scenarioDateCurrent, marketValueChange);
			// X_i
			cappedMarketValueChange = Math.min(Math.max(marketValueChange, -marginLimitLower), marginLimitUpper);
			cappedMarketValueChangeMap.put(scenarioDateCurrent, cappedMarketValueChange);
			// (1+r_{i-1}*(t_i-t_{i-1}))
			// C_i = C_{i-1}*(1+r_{i-1}*(t_i-t_{i-1})) + X_i == C_k on payment date
			collateralAccount = collateralAccountMap.lastEntry().getValue() * accrualRate + cappedMarketValueChange;
			collateralAccountMap.put(scenarioDateCurrent, collateralAccount);
			// Z_i = X_i - Y_i
			gapAmount = cappedMarketValueChange - marketValueChange;
			gapAmountMap.put(scenarioDateCurrent, gapAmount);
			// D_i = D_{i-1}*(1+r_{i-1}*(t_i-t_{i-1})) + Z_i
			gapAccount = gapAccountMap.lastEntry().getValue() * accrualRate + gapAmount;
			gapAccountMap.put(scenarioDateCurrent, gapAccount);

			System.out.printf(dataRowDailyFormat, formatter.format(scenarioDateCurrent), valueCurrent, marketValueChange, cappedMarketValueChange, gapAmount, gapAccount, collateralAccount);

			if (scenarioDateCurrent.toLocalDate().equals(paymentDates.get(0))) {
				paymentDates.remove(0);
				periodIndex++;
				// Synthetic time to track the values in the result map after the payment of the cash flow
				LocalDateTime scenarioDateCurrentAfterPayment = scenarioDateCurrent.plusMinutes(1);
				// V(t_k+) = V(t_k) - A_k 
				marketValueMap.put(scenarioDateCurrentAfterPayment, valueCurrent - netCashFlow);
				// Reduce the collateral account by C_k^+ = C_k - (A_k - D_k)
				gapAccount = gapAccountMap.lastEntry().getValue();
				collateralAccount = collateralAccount - (netCashFlow - gapAccount);
				collateralAccountMap.put(scenarioDateCurrentAfterPayment, collateralAccount);
				gapAccountMap.put(scenarioDateCurrentAfterPayment, 0.0);

				// Placeholder values, nothing happens here
				marketValueChangeMap.put(scenarioDateCurrentAfterPayment, marketValueChange);
				cappedMarketValueChangeMap.put(scenarioDateCurrentAfterPayment, cappedMarketValueChange);
				gapAmountMap.put(scenarioDateCurrentAfterPayment, gapAmount);
				System.out.println("Starting payment process");
				System.out.printf(paymentFormat, "Payment Time", "Cash flow A", "Current Value", "Gap Account D", "Collateral Acct C");

				System.out.printf(dataRowPaymentFormat, formatter.format(scenarioDateCurrentAfterPayment), netCashFlow, valueCurrent - netCashFlow, gapAccountMap.lastEntry(), collateralAccount);

			}
			calibratedModelPrevious = calibratedModelCurrent;
		}
		
	}

	// Returns the net cash-flow of the current period on the payment date
	// Assumes float and fix leg have the same schedule
	private static double getNetCashFlow(Swap swap, boolean isReceiveFix, List<CalibrationDataset> scenarioList, int periodIndex) {
		SwapLeg swapLeg;
		if (isReceiveFix)
			swapLeg = ((SwapLeg) swap.getLegReceiver());
		else
			swapLeg = ((SwapLeg)swap.getLegPayer());
		Schedule swapSchedule = ((SwapLeg) swap.getLegReceiver()).getSchedule();
		LocalDate fixingDate = swapSchedule.getPeriods().get(periodIndex).getFixing();
		double fixedRate =  swapLeg.getSpreads()[periodIndex];
		double periodLength = swapSchedule.getPeriodLength(periodIndex);
		double forwardRate = getFixedForwardRate(scenarioList, fixingDate);

		double netCashFlow;
		if (isReceiveFix)
			netCashFlow = fixedRate - forwardRate;
		else
			netCashFlow = forwardRate - fixedRate;

		return netCashFlow * periodLength * notional;
	}

	private static double getFixedForwardRate(List<CalibrationDataset> scenarioList, LocalDate fixingDate) {
		return scenarioList.stream()
				.filter(scenario -> scenario.getDate().toLocalDate().equals(fixingDate))
				.findFirst()
				.flatMap(scenario -> scenario.getDataPoints().stream()
						.filter(dataPoint -> "EUB6DEP6M".equals(dataPoint.getSpec().getKey()))
						.findFirst())
				.map(dataPoint -> dataPoint.getQuote())
				.orElseThrow(() -> new NoSuchElementException(
						"Could not find rate for key 'EUB6DEP6M' on date " + fixingDate
				));
	}

	private static void initializeValueMaps(LocalDateTime scenarioStartDate) {
		Stream.of(
				marketValueMap,
				marketValueChangeMap,
				cappedMarketValueChangeMap,
				collateralAccountMap,
				gapAmountMap,
				gapAccountMap
		).forEach(map -> map.put(scenarioStartDate, 0.0));
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

}
