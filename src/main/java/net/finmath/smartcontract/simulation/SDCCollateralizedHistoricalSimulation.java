package net.finmath.smartcontract.simulation;

import java.nio.file.Path;
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
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;


public class SDCCollateralizedHistoricalSimulation {
	
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

		/*Load historical market rates for curve bootstrapping*/
		MarketDataLoader discountCurveLoader = new MarketDataLoader(Path.of("D:/Papers/MarketData/20251203-20150101_ESTR.csv"));
		List<MarketDataSnapshot> discountCurveSnapshots = discountCurveLoader.load();
		MarketDataLoader forwardCurveLoader = new MarketDataLoader(Path.of("D:/Papers/MarketData/20251203-20150101_6M.csv"));
		List<MarketDataSnapshot> forwardCurveSnapshots = forwardCurveLoader.load();
		
		MarketDataSnapshot initialForwardRates = forwardCurveSnapshots.get(forwardCurveSnapshots.size() - 1);
		final LocalDate startDate = initialForwardRates.getValuationDate();
		
		/*Generate 6M Payer Swap */
		final String maturityKey = "5Y";
		final Schedule scheduleRec = ScheduleGenerator.createScheduleFromConventions(startDate, 2, "0D", maturityKey, "semiannual", "ACT/360", "first", "modfollow", new BusinessdayCalendarExcludingTARGETHolidays(), -2, 0);
		final Schedule schedulePay = ScheduleGenerator.createScheduleFromConventions(startDate, 2, "0D", maturityKey, "annual", "E30/360", "first", "modfollow", new BusinessdayCalendarExcludingTARGETHolidays(), -2, 0);
		/* Product starts at Par */
		final double fixRate = initialForwardRates.getQuotes()[11]; // TBSEUREUR03MEUR06M05Y
		final Swap swap = new Swap(scheduleRec, InterestRateAnalyticCalibration.FORWARD_EUR_6M, 0.0, InterestRateAnalyticCalibration.DISCOUNT_EUR_OIS, // receiver leg
									schedulePay, "", fixRate, InterestRateAnalyticCalibration.DISCOUNT_EUR_OIS, false); // payer leg
		
		/* Get combined payment dates, as legs can have different payment dates, e.g. fix leg pays only annually while float leg pays semi-annually */
		TreeSet<LocalDate> paymentDates = getPaymentDates(scheduleRec, schedulePay);

		
		System.out.printf(headerFormat, "Valuation Date", "Value V(t)", "Value Change Y_i", "Capped Change X_i", "Gap Amount Z_i", "Gap Account D_i", "Coll Account C_i", "Net Cashflow A_k");
		
		/* Initialize calibrator*/
		InterestRateAnalyticCalibration calibrator = new InterestRateAnalyticCalibration();

		
		LocalDate valuationDatePrevious = startDate;
		AnalyticModel calibratedModelPrevious = calibrator.getCalibratedModel(valuationDatePrevious, initialForww, null);
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

	private CalibrationDataItem createFixingItemEur6M(LocalDate fixingDate, Double fixing) {
		CalibrationDataItem.Spec spec = new CalibrationDataItem.Spec("EURIBOR06M", "Euribor6M", "FIXING", "6M");
		CalibrationDataItem item = new CalibrationDataItem(spec, fixing, fixingDate.atStartOfDay());
		return item;
	}
	
	private CalibrationDataItem createFixingItemEurOIS(LocalDate fixingDate, Double fixing) {
		CalibrationDataItem.Spec spec = new CalibrationDataItem.Spec("IREURDRFO_N", "ESTR", "FIXING", "1D");
		CalibrationDataItem item = new CalibrationDataItem(spec, fixing, fixingDate.atStartOfDay());
		return item;
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
	
	
	private static TreeSet<LocalDate> getPaymentDates(Schedule... schedules) {
	    return Arrays.stream(schedules)
	            .flatMap(schedule -> schedule.getPeriods().stream())
	            .map(Period::getPayment)
	            .collect(Collectors.toCollection(TreeSet::new));
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


}
