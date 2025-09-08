package net.finmath.smartcontract.simulation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import net.finmath.time.Period;
import net.finmath.time.Schedule;


public class SDCCollateralizedHistoricalSimulation {
	
	private static final String FIXING = "Fixing";
	private static final String DEPOSIT = "Deposit";
	private static final String FORWARD_EUR_6M = "forward-EUR-6M";
	private static final String DISCOUNT_EUR_OIS = "discount-EUR-OIS";

	private static double notional = 1.0E7;
	private static double marginLimitLower = notional * 0.005;
	private static double marginLimitUpper = notional * 0.005;

	private static TreeMap<LocalDateTime, Double> valueMap = new TreeMap<>(); // V_i
	private static TreeMap<LocalDateTime, Double> valueChangeMap = new TreeMap<>(); // Y_i
	private static TreeMap<LocalDateTime, Double> cappedValueChangeMap  = new TreeMap<>(); // X_i
	private static TreeMap<LocalDateTime, Double> collateralAccountMap = new TreeMap<>(); // C_i
	private static TreeMap<LocalDateTime, Double> gapAmountMap = new TreeMap<>(); // Z_i
	private static TreeMap<LocalDateTime, Double> gapAccountMap = new TreeMap<>(); //D_i

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static String headerFormat = "%-22s %20s %20s %20s %20s %20s %20s %20s%n";
	private static String dataRowFormat = "%-22s %20.2f %20.2f %20.2f %20.2f %20.2f %20.2f %20.2f%n";

	public static void main(String args[]) throws Exception {

		final LocalDate startDate = LocalDate.of(2007, 1, 1);
		final LocalDate maturity = LocalDate.of(2012, 1, 3);
		final String fileName = "timeseriesdatamap.json";
		final List<CalibrationDataset> scenarioList = CalibrationParserDataItems.getScenariosFromJsonFile(fileName).stream().filter(S -> S.getDate().toLocalDate().isAfter(startDate)).filter(S -> S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());

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
		// dataset swap rate = 0.0442 -> we use the calibrated par swap rate
		final double fixRate = 0.04419229093193379;
		boolean isReceiveFix = true;
		final Swap swap = IRSwapGenerator.generateAnalyticSwapObject(productStartDate, MaturityKey, notional, fixRate, isReceiveFix, forwardCurveKey, discountCurveKey);
		//double swapRate = swap.getForwardSwapRate(((SwapLeg) swap.getLegReceiver()).getSchedule(), ((SwapLeg) swap.getLegPayer()).getSchedule(), calibratedModelPrevious.getForwardCurve(forwardCurveKey), calibratedModelPrevious);

		// Legs can have different payment dates, e.g. fix leg pays only annually while float leg pays semiannually
		SwapLeg receiverLeg = (SwapLeg) swap.getLegReceiver();
		SwapLeg payerLeg = (SwapLeg) swap.getLegPayer();
		List<LocalDate> paymentDatesReceiverLeg = getPaymentDates(receiverLeg);
		List<LocalDate> paymentDatesPayerLeg = getPaymentDates(payerLeg);

		// TODO check payment date logic
		// Add row with isPaymentDate == 1 or add net cash flow A_k to outputs
		/*
		The swap schedule is based on LocalDate, i.e. if we value the swap on a paymentDate t_i the cash flow is already excluded in the valuation.
		If the SDC settles daily at 15:UTC with the current value V(t_i) we assume that the cash flow occurs after the collateralization:
			- V(t_{i-1}) and V(t_i) include the cash flow
			- The amount Y_i of the SDC is given by: (V(t_i) + A_k) - V(t_{i-1})*(1+r_{i-1}*(t_i-t_{i-1}))
			- After the settlement we pay (A_k - D_k) as cash
			- Reduce the collateral accordingly C_k^+ = C_k - (A_k - D_k)
			- Set D_K^+ to zero
		 */
		System.out.printf(headerFormat, "Valuation Date", "Value V(t)", "Value Change Y_i", "Capped Change X_i", "Gap Amount Z_i", "Gap Account D_i", "Coll Account C_i", "Net Cashflow A_k");
		LocalDateTime scenarioDatePrevious = scenarioList.get(0).getDate();
		AnalyticModel calibratedModelPrevious = getCalibratedModel(scenarioList.get(0), scenarioDatePrevious);
		double valuePrevious = swap.getValue(0.0, calibratedModelPrevious); // V(t_{i-1})

		double dt, accrualRate, valueCurrent;
		double valueChange, cappedValueChange;
		double collateralAccount, gapAmount, gapAccount, netCashFlow;
		for (int i = 1; i < scenarioList.size(); i++) {
			LocalDateTime scenarioDateCurrent = scenarioList.get(i).getDate();
			AnalyticModel calibratedModelCurrent = getCalibratedModel(scenarioList.get(i), scenarioDateCurrent);

			dt = FloatingpointDate.getFloatingPointDateFromDate(scenarioDatePrevious, scenarioDateCurrent);
			accrualRate = 1 / calibratedModelPrevious.getDiscountCurve(DISCOUNT_EUR_OIS).getDiscountFactor(dt); // 1+r(t_{i-1})*(t_i-t_{i-1})

			valueCurrent = swap.getValue(0.0, calibratedModelCurrent); // V(t_i)
			netCashFlow = 0.0; // A_k
			// If the evaluation date equals a payment date t_k of the swap, the returned value is already reduced by the cash-flow of that day
			// Assumption that payment of cash flows occur after daily settlement
			if (paymentDatesReceiverLeg.contains(scenarioDateCurrent.toLocalDate())) {
				netCashFlow += getLegCashFlow(receiverLeg, scenarioDateCurrent.toLocalDate(), scenarioList);
			}
			if (paymentDatesPayerLeg.contains(scenarioDateCurrent.toLocalDate())) {
				netCashFlow -= getLegCashFlow(payerLeg, scenarioDateCurrent.toLocalDate(), scenarioList);
			}
			valueCurrent = valueCurrent + netCashFlow;
			valueMap.put(scenarioDateCurrent, valueCurrent);
			// Y_i = V(t_i) - V(t_{i-1})*(1+r_{i-1}*(t_i-t_{i-1}))
			valueChange = valueCurrent - valuePrevious * accrualRate;
			valueChangeMap.put(scenarioDateCurrent, valueChange);
			// X_i
			cappedValueChange = Math.min(Math.max(valueChange, -marginLimitLower), marginLimitUpper);
			cappedValueChangeMap.put(scenarioDateCurrent, cappedValueChange);
			// C_i = C_{i-1}*(1+r_{i-1}*(t_i-t_{i-1})) + X_i == C_k on payment date
			collateralAccount = collateralAccountMap.lastEntry().getValue() * accrualRate + cappedValueChange;
			collateralAccountMap.put(scenarioDateCurrent, collateralAccount);
			// Z_i = Y_i - X_i
			gapAmount = valueChange - cappedValueChange;
			gapAmountMap.put(scenarioDateCurrent, gapAmount);
			// D_i = D_{i-1}*(1+r_{i-1}*(t_i-t_{i-1})) + Z_i
			gapAccount = gapAccountMap.lastEntry().getValue() * accrualRate + gapAmount;
			gapAccountMap.put(scenarioDateCurrent, gapAccount);

			System.out.printf(dataRowFormat, formatter.format(scenarioDateCurrent), valueCurrent, valueChange, cappedValueChange, gapAmount, gapAccount, collateralAccount, 0.0); // cash-flow occurs after daily settlement

			if (paymentDatesReceiverLeg.contains(scenarioDateCurrent.toLocalDate()) || paymentDatesPayerLeg.contains(scenarioDateCurrent.toLocalDate())) {
				// Synthetic time to track the values in the result map after the payment of the cash flow
				LocalDateTime scenarioDateCurrentAfterPayment = scenarioDateCurrent.plusMinutes(1);
				// V(t_k+) = V(t_k) - A_k 
				valueCurrent = valueCurrent - netCashFlow;
				valueMap.put(scenarioDateCurrentAfterPayment, valueCurrent);
				// Reduce the collateral account by C_k^+ = C_k - (A_k - D_k)
				gapAccount = gapAccountMap.lastEntry().getValue();
				collateralAccount = collateralAccount - (netCashFlow - gapAccount);
				collateralAccountMap.put(scenarioDateCurrentAfterPayment, collateralAccount);
				gapAccountMap.put(scenarioDateCurrentAfterPayment, 0.0);
				// Placeholder values
				valueChangeMap.put(scenarioDateCurrentAfterPayment, 0.0);
				cappedValueChangeMap.put(scenarioDateCurrentAfterPayment, 0.0);
				gapAmountMap.put(scenarioDateCurrentAfterPayment, 0.0);

				System.out.printf(dataRowFormat, formatter.format(scenarioDateCurrentAfterPayment), valueCurrent, valueChangeMap.lastEntry().getValue(), cappedValueChangeMap.lastEntry().getValue(), 
						gapAmountMap.lastEntry().getValue(), gapAccountMap.lastEntry().getValue(), collateralAccount, netCashFlow);

			}
			scenarioDatePrevious = scenarioDateCurrent;
			valuePrevious = valueCurrent;
			calibratedModelPrevious = calibratedModelCurrent;
		}
		
	}


	// Returns the cash-flow for a swap leg on the payment date, i.e. P(T;t) = P(T;T) = 1
	private static double getLegCashFlow (SwapLeg swapLeg, LocalDate paymentDate, List<CalibrationDataset> marketData) {
		Schedule legSchedule = swapLeg.getSchedule();
		int periodIndex 	 = getCurrentPeriodIndex(swapLeg, paymentDate);
		double spread 		 = swapLeg.getSpreads()[periodIndex];
		double periodLength  = legSchedule.getPeriodLength(periodIndex);
		
		double forwardRate = 0.0;
		if(swapLeg.getForwardCurveName() != null) {
			LocalDate fixingDate = swapLeg.getSchedule().getPeriods().get(periodIndex).getFixing();
			forwardRate = getFixedForwardRate(marketData, fixingDate);
		}

		return notional * (spread + forwardRate) * periodLength;
	}

	private static List<LocalDate> getPaymentDates(SwapLeg swapLeg) {
		return swapLeg.getSchedule().getPeriods().stream()
				.map(Period::getPayment)
				.sorted().toList();
	}

	private static int getCurrentPeriodIndex(SwapLeg swapLeg, LocalDate paymentDate) {
	    List<Period> periods = swapLeg.getSchedule().getPeriods();
	    return IntStream.range(0, periods.size())
	            .filter(i -> periods.get(i).getPayment().equals(paymentDate))
	            .findFirst()
	            .orElseThrow(() -> new IllegalStateException(
	                "Assumption violated: Could not find a payment period for date: " + paymentDate
	            ));
	}

	private static double getFixedForwardRate(List<CalibrationDataset> marketData, LocalDate fixingDate) {
		return marketData.stream()
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
				valueMap,
				valueChangeMap,
				cappedValueChangeMap,
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
