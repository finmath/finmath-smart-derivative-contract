package net.finmath.smartcontract.simulation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.AnalyticModelFromCurvesAndVols;
import net.finmath.marketdata.model.curves.Curve;
import net.finmath.marketdata.model.curves.CurveInterpolation;
import net.finmath.marketdata.model.curves.DiscountCurveInterpolation;
import net.finmath.marketdata.model.curves.ForwardCurve;
import net.finmath.marketdata.model.curves.ForwardCurveFromDiscountCurve;
import net.finmath.marketdata.model.curves.ForwardCurveInterpolation;
import net.finmath.marketdata.model.curves.ForwardCurveWithFixings;
import net.finmath.optimizer.SolverException;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.time.FloatingpointDate;
import net.finmath.time.Period;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleFromPeriods;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendar;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;
import net.finmath.time.daycount.DayCountConvention_ACT_360;


public class InterestRateAnalyticCalibrator {

	public static String DISCOUNT_EUR_OIS = "discount-EUR-OIS";
	public static String FORWARD_EUR_6M = "forward-EUR-6M";
	private static String FORWARD_EUR_OIS = "forward-EUR-OIS";
	private static String FIXED_EUR_6M = "fixed-EUR-6M";
	
	
	public enum CURVE_NAME {
		ESTR,
		EURIBOR06M
	}

	private HashMap<CURVE_NAME, List<CalibrationDataItem>> fixings = new HashMap<>();
	
	public InterestRateAnalyticCalibrator() {
	}

	public AnalyticModel getCalibratedModel(LocalDate referenceDate, double[] discountCurveQuotes, double[] forwardCurveQuotes) throws CloneNotSupportedException, SolverException {
		
		final AnalyticModelFromCurvesAndVols model = new AnalyticModelFromCurvesAndVols(new Curve[] { getDiscountCurveEurOIS(referenceDate), getForwardCurveEurOIS(referenceDate), getForwardCurveEur6M(referenceDate)});
		CalibratedCurves.CalibrationSpec[] specs =
			    Stream.of(getCalibrationSpecsEurOIS(referenceDate, discountCurveQuotes), getCalibrationSpecsEur6M(referenceDate, forwardCurveQuotes))
			          .flatMap(Arrays::stream)
			          .toArray(CalibratedCurves.CalibrationSpec[]::new);
		
		CalibratedCurves calibratedCurves = new CalibratedCurves(specs, model, 1E-9);
		return calibratedCurves.getModel();
	}
	

	public void addFixingItem(CURVE_NAME curveName, LocalDate fixingDate, Double fixing) {
		switch(curveName) {
		case ESTR:
			this.fixings.computeIfAbsent(curveName, k -> new ArrayList<>()).add(createFixingItemEurOIS(fixingDate, fixing));
			break;
		case EURIBOR06M:
			this.fixings.computeIfAbsent(curveName, k -> new ArrayList<>()).add(createFixingItemEur6M(fixingDate, fixing));
			break;
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
	
	private DiscountCurveInterpolation getDiscountCurveEurOIS(LocalDate referenceDate) {
		ArrayList<Double> fixingValuesList = new ArrayList<>();
		ArrayList<Double> fixingTimesList = new ArrayList<>();
		ArrayList<Double> dfList = new ArrayList<>();
		ArrayList<Double> dfTimesList = new ArrayList<>();
		this.fixings.getOrDefault(CURVE_NAME.ESTR, new ArrayList<>()).stream().sorted(Comparator.comparing(CalibrationDataItem::getDate).reversed())
				.forEach(x -> {
					double time = FloatingpointDate.getFloatingPointDateFromDate(
							referenceDate,
							x.getDate());
					if (time < 0) {
						fixingTimesList.add(time);
						fixingValuesList.add(365.0 * Math.log(1 + x.getQuote() / 360.0));
						//conversion from 1-day ESTR (ACT/360) to zero-rate (ACT/ACT)
						//see https://quant.stackexchange.com/questions/73522/how-does-bloomberg-calculate-the-discount-rate-from-eur-estr-curve
					}
				});
		// Add initial zero entries for calculations
		fixingTimesList.add(0, 0.0);
		fixingValuesList.add(0, 0.0);
		// Add time zero discount factor
		dfTimesList.add(0, 0.0);
		dfList.add(0, 1.0);
		IntStream.range(1, fixingTimesList.size()).forEach(i -> {
			double df = dfList.get(i - 1) * Math.exp(-fixingValuesList.get(i) * (fixingTimesList.get(i) - fixingTimesList.get(i - 1)));
			dfList.add(df);
			dfTimesList.add(fixingTimesList.get(i));
		});
		boolean[] isParameters = ArrayUtils.toPrimitive(
				IntStream.range(0, dfTimesList.size()).boxed().map(x -> false).toList().toArray(Boolean[]::new));
		double[] dfTimes = dfTimesList.stream().mapToDouble(Double::doubleValue).toArray();
		double[] dfValues = dfList.stream().mapToDouble(Double::doubleValue).toArray();
		return DiscountCurveInterpolation.createDiscountCurveFromDiscountFactors(DISCOUNT_EUR_OIS,
				referenceDate, dfTimes, dfValues, isParameters, CurveInterpolation.InterpolationMethod.LINEAR,
				CurveInterpolation.ExtrapolationMethod.CONSTANT, CurveInterpolation.InterpolationEntity.LOG_OF_VALUE);

	}
	
	private ForwardCurve getForwardCurveEurOIS(LocalDate referenceDate) {
		final ForwardCurve forwardCurve	= new ForwardCurveFromDiscountCurve(FORWARD_EUR_OIS, 
				DISCOUNT_EUR_OIS, 
				DISCOUNT_EUR_OIS, 
				referenceDate, 
				"1D",
				new BusinessdayCalendarExcludingTARGETHolidays(),
				BusinessdayCalendar.DateRollConvention.FOLLOWING,
				365.0 / 360.0, 0.0);
		return forwardCurve;
	}
		
	private ForwardCurve getForwardCurveEur6M(LocalDate referenceDate) {
		double[] fixingTimes = this.fixings.getOrDefault(CURVE_NAME.EURIBOR06M, new ArrayList<>()).stream().map(x -> x.getDate())
				.map(x -> FloatingpointDate.getFloatingPointDateFromDate(referenceDate, x))
				.mapToDouble(Double::doubleValue).sorted().toArray();
		if (fixingTimes.length == 0) { //if there are no fixings return empty curve
			return new ForwardCurveInterpolation(FORWARD_EUR_6M,
					referenceDate,
					"6M",
					new BusinessdayCalendarExcludingTARGETHolidays(),
					BusinessdayCalendar.DateRollConvention.FOLLOWING,
					CurveInterpolation.InterpolationMethod.LINEAR,
					CurveInterpolation.ExtrapolationMethod.CONSTANT,
					CurveInterpolation.InterpolationEntity.VALUE,
					ForwardCurveInterpolation.InterpolationEntityForward.FORWARD,
					DISCOUNT_EUR_OIS);
		}
		double[] fixingValues = this.fixings.getOrDefault(CURVE_NAME.EURIBOR06M, new ArrayList<>()).stream()
				.sorted(Comparator.comparing(CalibrationDataItem::getDate)).map(CalibrationDataItem::getQuote)
				.mapToDouble(Double::doubleValue).toArray();
		ForwardCurve fixedPart = ForwardCurveInterpolation.createForwardCurveFromForwards(FIXED_EUR_6M,
				referenceDate,
				"6M",
				new BusinessdayCalendarExcludingTARGETHolidays(),
				BusinessdayCalendar.DateRollConvention.FOLLOWING,
				CurveInterpolation.InterpolationMethod.LINEAR,
				CurveInterpolation.ExtrapolationMethod.CONSTANT,
				CurveInterpolation.InterpolationEntity.VALUE,
				ForwardCurveInterpolation.InterpolationEntityForward.FORWARD,
				DISCOUNT_EUR_OIS, null, fixingTimes, fixingValues);
		ForwardCurve forwardPart = new ForwardCurveInterpolation(FORWARD_EUR_6M,
				referenceDate,
				"6M",
				new BusinessdayCalendarExcludingTARGETHolidays(),
				BusinessdayCalendar.DateRollConvention.FOLLOWING,
				CurveInterpolation.InterpolationMethod.LINEAR,
				CurveInterpolation.ExtrapolationMethod.CONSTANT,
				CurveInterpolation.InterpolationEntity.VALUE,
				ForwardCurveInterpolation.InterpolationEntityForward.FORWARD,
				DISCOUNT_EUR_OIS);
		// this is a dirty-ish fix: if the extrema of the fixed part lay exactly on the time specified by the data point,
		// some weird jumpiness occurs... TODO: mayb there's a smarter solution
		return new ForwardCurveWithFixings(forwardPart, fixedPart,
				Arrays.stream(fixingTimes).min().orElseThrow() - 1.0 / 365.0,
				Arrays.stream(fixingTimes).max().orElseThrow() + 1.0 / 365.0);
	}
	
	private static CalibratedCurves.CalibrationSpec[] getCalibrationSpecsEurOIS(LocalDate referenceDate, double[] quotes) {
		final String[] maturities		= { "1D", "7D", "14D", "21D", "1M", "2M", "3M", "4M", "5M", "6M", "7M", "8M", "9M", "10M", "11M", "1Y", "15M", "18M", "21M", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "12Y", "15Y", "20Y", "25Y", "30Y"};
		final String[] frequency		= { "tenor", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual"};
		
		if (quotes.length != maturities.length) {
			throw new IllegalArgumentException("Size of provided quotes does not match the number of EUR-OIS bootstrapp instruments");
		}
		
		CalibratedCurves.CalibrationSpec[] specs = new CalibratedCurves.CalibrationSpec[maturities.length];
		BusinessdayCalendar calendar = new BusinessdayCalendarExcludingTARGETHolidays();
		// The first product is an overnight cash deposit, followed by 32 swaps
		Schedule scheduleInterfaceRec = ScheduleGenerator.createScheduleFromConventions(referenceDate, 0, "0D", maturities[0], frequency[0], "ACT/360", "first", "follow", calendar, 0, 0);
		double calibrationTime = scheduleInterfaceRec.getPayment(scheduleInterfaceRec.getNumberOfPeriods() - 1);
		specs[0] = new CalibratedCurves.CalibrationSpec(String.format("EUR-OIS-%1$s", maturities[0]), "Deposit", scheduleInterfaceRec, FORWARD_EUR_OIS, quotes[0], DISCOUNT_EUR_OIS, null, "", 0.0, DISCOUNT_EUR_OIS, DISCOUNT_EUR_OIS, calibrationTime);

		// OIS ESTR Swaps
		for (int i=1; i < quotes.length; i++) {
			Schedule scheduleRec = ScheduleGenerator.createScheduleFromConventions(referenceDate, 2, "0D", maturities[i], frequency[i], "ACT/360", "first", "modified_following", calendar, 0, 1);
			Schedule schedulePay = ScheduleGenerator.createScheduleFromConventions(referenceDate, 2, "0D", maturities[i], frequency[i], "ACT/360", "first", "modified_following", calendar, 0, 1);
			calibrationTime = scheduleRec.getPayment(scheduleRec.getNumberOfPeriods() - 1);
			specs[i] = new CalibratedCurves.CalibrationSpec(String.format("EUR-OIS-%1$s", maturities[i]), "Swap", scheduleRec, FORWARD_EUR_OIS, 0.0, DISCOUNT_EUR_OIS, schedulePay, "", quotes[i], DISCOUNT_EUR_OIS, DISCOUNT_EUR_OIS, calibrationTime);
		}
		return specs;
	}
	
	private static CalibratedCurves.CalibrationSpec[] getCalibrationSpecsEur6M(LocalDate referenceDate, double[] quotes) {
		final String tenorLabel = "6M";
		final String[] maturities				= { "7M", "8M", "9M", "10M", "12M", "15M", "18M", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "12Y", "15Y", "20Y", "25Y", "30Y"};
		final String[] frequencyFloat			= { "", "", "", "", "", "", "", "semiannual", "semiannual", "semiannual", "semiannual", "semiannual", "semiannual", "semiannual", "semiannual", "semiannual", "semiannual", "semiannual", "semiannual", "semiannual", "semiannual"};
		final String[] frequency				= { "tenor", "tenor", "tenor", "tenor", "tenor", "tenor", "tenor", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual", "annual"};
		final String[] daycountConventionsFloat	= { "", "", "", "", "", "", "", "ACT/360", "ACT/360", "ACT/360", "ACT/360", "ACT/360", "ACT/360", "ACT/360", "ACT/360", "ACT/360", "ACT/360", "ACT/360", "ACT/360", "ACT/360", "ACT/360"};
		final String[] daycountConventions		= { "ACT/360", "ACT/360", "ACT/360", "ACT/360", "ACT/360", "ACT/360", "ACT/360", "E30/360", "E30/360", "E30/360", "E30/360", "E30/360", "E30/360", "E30/360", "E30/360", "E30/360", "E30/360", "E30/360", "E30/360", "E30/360", "E30/360"};
		
		if (quotes.length != maturities.length) {
			throw new IllegalArgumentException("Size of provided quotes does not match the number of EUR-6M bootstrapp instruments");
		}
		
		CalibratedCurves.CalibrationSpec[] specs = new CalibratedCurves.CalibrationSpec[maturities.length];
		BusinessdayCalendar calendar = new BusinessdayCalendarExcludingTARGETHolidays();
		// The first 7 product are FRAs, followed by 14 swaps
		for (int i=0; i < 7; i++) {
			int nMonthMaturity = Integer.parseInt(maturities[i].replace("M", ""));
			int nMonthOffset = nMonthMaturity - Integer.parseInt(tenorLabel.replace("M", ""));
			String startOffsetLabel = nMonthOffset + "M";
			
			Schedule scheduleInterfacePrelim = ScheduleGenerator.createScheduleFromConventions(referenceDate, 2, startOffsetLabel, tenorLabel, frequency[i], daycountConventions[i], "first", "follow", calendar, -2, 0);
			LocalDate periodStartDate = scheduleInterfacePrelim.getPeriod(0).getPeriodStart();
			LocalDate paymentDate = scheduleInterfacePrelim.getPeriod(0).getPayment();
			LocalDate fixingDate = scheduleInterfacePrelim.getPeriod(0).getFixing();
			LocalDate periodEndDate = calendar.getAdjustedDate(periodStartDate, tenorLabel, BusinessdayCalendar.DateRollConvention.FOLLOWING);
			
			Period period = new Period(fixingDate, paymentDate, periodStartDate, periodEndDate);
			Schedule scheduleInterfaceFinal = new ScheduleFromPeriods(referenceDate, new DayCountConvention_ACT_360(), period);
			double calibrationTime = scheduleInterfaceFinal.getFixing(scheduleInterfaceFinal.getNumberOfPeriods() - 1);
			// Check correctness of discountCurvePayerName = null
			specs[i] = new CalibratedCurves.CalibrationSpec("EUR-" + tenorLabel + maturities[i], "FRA", scheduleInterfaceFinal, FORWARD_EUR_6M, quotes[i], DISCOUNT_EUR_OIS, null, "", 0.0, null, FORWARD_EUR_6M, calibrationTime);
		}
		// 6M Swaps
		for (int i=7; i < quotes.length; i++) {
			Schedule scheduleInterfaceRec = ScheduleGenerator.createScheduleFromConventions(referenceDate, 2, "0D", maturities[i], frequencyFloat[i], daycountConventionsFloat[i], "first", "modfollow", new BusinessdayCalendarExcludingTARGETHolidays(), -2, 0);
			Schedule scheduleInterfacePay = ScheduleGenerator.createScheduleFromConventions(referenceDate, 2, "0D", maturities[i], frequency[i], daycountConventions[i], "first", "modfollow", new BusinessdayCalendarExcludingTARGETHolidays(), -2, 0);
			double calibrationTime = scheduleInterfaceRec.getFixing(scheduleInterfaceRec.getNumberOfPeriods() - 1);
			specs[i] = new CalibratedCurves.CalibrationSpec("EUR-" + tenorLabel + maturities[i], "Swap", scheduleInterfaceRec, FORWARD_EUR_6M, 0.0, DISCOUNT_EUR_OIS, scheduleInterfacePay, "", quotes[i], DISCOUNT_EUR_OIS, FORWARD_EUR_6M, calibrationTime);	
		}
		return specs;
	}
			

}
