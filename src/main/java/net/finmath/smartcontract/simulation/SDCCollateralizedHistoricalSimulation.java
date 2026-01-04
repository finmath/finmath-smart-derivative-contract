package net.finmath.smartcontract.simulation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.products.Swap;
import net.finmath.smartcontract.simulation.InterestRateAnalyticCalibrator.CURVE_NAME;
import net.finmath.time.FloatingpointDate;
import net.finmath.time.Period;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendar;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;


public class SDCCollateralizedHistoricalSimulation {
	
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static String headerFormat = "%-22s %20s %20s %20s %20s %20s %20s %20s%n";
	private static String dataRowFormat = "%-22s %20.6f %20.6f %20.6f %20.6f %20.6f %20.6f %20.6f%n";
	
	public static void main(String args[]) throws Exception {

		/*Load historical market rates for curve bootstrapping*/
		BusinessdayCalendar businessdayCalendar = new BusinessdayCalendarExcludingTARGETHolidays();
		MarketDataLoader discountCurveLoader = new MarketDataLoader(Path.of("D:\\Papers\\MarketData\\20251203-20150101_ESTR.csv"));
		List<MarketDataSnapshot> discountCurveSnapshots = discountCurveLoader.load(businessdayCalendar);
		MarketDataLoader forwardCurveLoader = new MarketDataLoader(Path.of("D:\\Papers\\MarketData\\20251203-20150101_6M.csv"));
		List<MarketDataSnapshot> forwardCurveSnapshots = forwardCurveLoader.load(businessdayCalendar);
		
		int valuationStartIndex = 1100;
		MarketDataSnapshot initialForwardRates = forwardCurveSnapshots.get(valuationStartIndex);
		final LocalDate startDate = initialForwardRates.getValuationDate();
		
		/*Generate 6M Payer Swap with notional 1 */
		final String maturityKey = "5Y";
		final Schedule scheduleRec = ScheduleGenerator.createScheduleFromConventions(startDate, 2, "0D", maturityKey, "semiannual", "ACT/360", "first", "modfollow", businessdayCalendar, -2, 0);
		final Schedule schedulePay = ScheduleGenerator.createScheduleFromConventions(startDate, 2, "0D", maturityKey, "annual", "E30/360", "first", "modfollow", businessdayCalendar, -2, 0);
		/* Product starts at Par */
		final double fixRate = initialForwardRates.getQuotes()[11]; // TBSEUREUR03MEUR06M05Y
		final Swap swap = new Swap(scheduleRec, InterestRateAnalyticCalibrator.FORWARD_EUR_6M, 0.0, InterestRateAnalyticCalibrator.DISCOUNT_EUR_OIS, // receiver leg
									schedulePay, "", fixRate, InterestRateAnalyticCalibrator.DISCOUNT_EUR_OIS, false); // payer leg

		// Get combined payment dates, as legs can have different payment dates, e.g. fix leg pays only annually while float leg pays semi-annually
		TreeSet<LocalDate> paymentDates = getPaymentDates(scheduleRec, schedulePay);
		// Get fixing dates of float leg to minimize data storage and transformation within calibrator 
		TreeSet<LocalDate> fixingDates = getFixingDates(scheduleRec);
		
		// Constant funding spread 50bp 
		double fundingSpread = 0.005;

		// Get last scenario index based on maturity
		int valuationEndIndex = getLastValuationIndex(forwardCurveSnapshots, paymentDates.last());
		
		int numberOfMarginIncrements = 20;
		List<Double> margin = new ArrayList<>(numberOfMarginIncrements);
		List<Double> cashFlow = new ArrayList<>(numberOfMarginIncrements);
		List<Double> funding = new ArrayList<>(numberOfMarginIncrements);
		// Loop over increasing margin limits 
		for (int j = 0; j <= numberOfMarginIncrements; j++) {
			// Margin limits
			double marginFloor	= j * 0.0005;
			double marginCap	= j * 0.0005;
			
			/* Initialize calibrator*/
			InterestRateAnalyticCalibrator calibrator = new InterestRateAnalyticCalibrator();
			calibrator.addFixingItem(CURVE_NAME.EURIBOR06M, startDate, initialForwardRates.getFixing());
			
			/* Initialize valuation loop and set up collateral account*/
			LocalDate valuationDatePrevious = startDate;
			AnalyticModel calibratedModelPrevious = calibrator.getCalibratedModel(valuationDatePrevious, discountCurveSnapshots.get(0).getQuotes(), initialForwardRates.getQuotesWithoutFixing());
			double valuePrevious = swap.getValue(0.0, calibratedModelPrevious);
			double accrualFactor, valueCurrent, valueChange, cappedValueChange, gapAmount;
			double gapAccount = 0.0;
			double realizedCashFlows = 0.0;
			// If trade has initially a non-zero value -> collateral needs to be set up by an up-front payment
			double collateralAccount = valuePrevious;		
			
			//System.out.printf(headerFormat, "Valuation Date", "Value V(t)", "Value Change Y_i", "Capped Change X_i", "Gap Amount Z_i", "Gap Account D_i", "Coll Account C_i", "Net Cashflow A_k");
			for (int i = valuationStartIndex + 1; i < valuationEndIndex; i++) {
				LocalDate valuationDateCurrent = forwardCurveSnapshots.get(i).getValuationDate();
				// We need the 6m Fixings for the fixing dates of the swap
				if (fixingDates.contains(valuationDateCurrent)) {
					calibrator.addFixingItem(CURVE_NAME.EURIBOR06M, valuationDateCurrent, forwardCurveSnapshots.get(i).getFixing());
				}

				AnalyticModel calibratedModelCurrent = calibrator.getCalibratedModel(valuationDateCurrent, discountCurveSnapshots.get(i).getQuotes(), forwardCurveSnapshots.get(i).getQuotesWithoutFixing());
				// 1+r(t_{i-1})*(t_i-t_{i-1})
				accrualFactor = 1.0 / calibratedModelPrevious.getDiscountCurve(InterestRateAnalyticCalibrator.DISCOUNT_EUR_OIS).getDiscountFactor(FloatingpointDate.getFloatingPointDateFromDate(valuationDatePrevious, valuationDateCurrent)); 
				 // V(t_i)
				valueCurrent = swap.getValue(0.0, calibratedModelCurrent);
				// If the evaluation date is a payment date t_k of the swap, the returned value V(t_k) is already reduced by the cash-flow A_k of that day, i.e. it is V(t_k+)
				// Y_i = V(t_i) - V(t_{i-1}) * (1+r_{i-1}*(t_i-t_{i-1})
				valueChange = valueCurrent - valuePrevious * accrualFactor;
				// X_i = min(max(Y_i, -M), M)
				cappedValueChange = Math.min(Math.max(valueChange, -marginFloor), marginCap);			
				// Z_i = Y_i - X_i
				gapAmount = valueChange - cappedValueChange;
				if (paymentDates.contains(valuationDateCurrent)) {
					// C_k+ = C_k - (A_k - D_k) = C_{i-1}*(1+rt) + D_{i-1}(1+rt) + V(t_k+) - V(i-1)*(1+rt) = V(t_k+) + (1+rt) * (C_{i-1} + D_{i-1} - V_{i-1})
					collateralAccount = valueCurrent + accrualFactor * (collateralAccount + gapAccount - valuePrevious);
					// A_k - D_k = V(t_k) - V(t_{i-1}) * (1+rt) - D_k = Y_i - D_k
					realizedCashFlows = realizedCashFlows + valueChange - gapAccount;
					// D_k^+= 0
					gapAccount = 0.0;
				} else {
					// C_i = C_{i-1}*(1+rt) + X_i
					collateralAccount = collateralAccount + accrualFactor * cappedValueChange;
					// D_i = D_{i-1}*(1+rt) + Z_i
					gapAccount = gapAccount * accrualFactor + gapAmount;
				}		
				// Reuse current values as previous ones for next iteration
				valuationDatePrevious = valuationDateCurrent;
				valuePrevious = valueCurrent;
				calibratedModelPrevious = calibratedModelCurrent;			
				
				//System.out.printf(dataRowFormat, formatter.format(valuationDateCurrent), valueCurrent, valueChange, cappedValueChange, gapAmount, gapAccount, collateralAccount, realizedCashFlows);
			}
			
			// Add increment results to result array
			margin.add(marginFloor);
			cashFlow.add(realizedCashFlows);
			// Constant Margin buffer and funding spread, but can be time dependent
			double fundingCosts = marginFloor * (1.0 - Math.exp(-fundingSpread * FloatingpointDate.getFloatingPointDateFromDate(startDate, forwardCurveSnapshots.get(valuationEndIndex - 1).getValuationDate())));			
			funding.add(fundingCosts);
		}
		
		// Write result arrays to Excel
		writeToExcel(margin, cashFlow, funding);
	}
	
	
	private static TreeSet<LocalDate> getPaymentDates(Schedule... schedules) {
	    return Arrays.stream(schedules)
	            .flatMap(schedule -> schedule.getPeriods().stream())
	            .map(Period::getPayment)
	            .collect(Collectors.toCollection(TreeSet::new));
	}
	
	private static TreeSet<LocalDate> getFixingDates(Schedule... schedules) {
	    return Arrays.stream(schedules)
	            .flatMap(schedule -> schedule.getPeriods().stream())
	            .map(Period::getFixing)
	            .collect(Collectors.toCollection(TreeSet::new));
	}

	private static int getLastValuationIndex(List<MarketDataSnapshot> list, LocalDate cutoffDate) {
	    int lo = 0, hi = list.size();
	    while (lo < hi) {
	        int mid = (lo + hi) >>> 1;
	        if (list.get(mid).getValuationDate().isAfter(cutoffDate)) hi = mid;
	        else lo = mid + 1;
	    }
	    return lo; 
	}
	
	private static void writeToExcel(List<Double> margin, List<Double> cashFlow, List<Double> funding) throws IOException {
		Path file = Paths.get("D:\\Papers\\SDC Collateralized\\Margin Analysis.xlsx");
		LinkedHashMap<String, List<?>> cols = new LinkedHashMap<>();
		cols.put("margin", margin);
		cols.put("cashFlow", cashFlow);
		cols.put("funding", funding);
		
		try (ExcelTableWriter writer = new ExcelTableWriter(file)) {
		    writer.writeTable("Data", cols, true, false); // replace sheet, autosize
		    writer.save();
		}
	}
	
}
