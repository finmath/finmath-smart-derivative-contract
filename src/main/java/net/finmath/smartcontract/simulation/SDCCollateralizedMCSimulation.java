package net.finmath.smartcontract.simulation;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.finmath.exception.CalculationException;
import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.AnalyticModelFromCurvesAndVols;
import net.finmath.marketdata.model.curves.Curve;
import net.finmath.marketdata.model.curves.CurveInterpolation;
import net.finmath.marketdata.model.curves.DiscountCurve;
import net.finmath.marketdata.model.curves.DiscountCurveInterpolation;
import net.finmath.marketdata.model.curves.ForwardCurve;
import net.finmath.marketdata.model.curves.ForwardCurveInterpolation;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.RandomVariableFromArrayFactory;
import net.finmath.montecarlo.RandomVariableFromDoubleArray;
import net.finmath.montecarlo.interestrate.CalibrationProduct;
import net.finmath.montecarlo.interestrate.LIBORMarketModel;
import net.finmath.montecarlo.interestrate.LIBORModelMonteCarloSimulationModel;
import net.finmath.montecarlo.interestrate.LIBORMonteCarloSimulationFromLIBORModel;
import net.finmath.montecarlo.interestrate.models.LIBORMarketModelFromCovarianceModel;
import net.finmath.montecarlo.interestrate.models.covariance.LIBORCorrelationModelExponentialDecay;
import net.finmath.montecarlo.interestrate.models.covariance.LIBORCovarianceModelFromVolatilityAndCorrelation;
import net.finmath.montecarlo.interestrate.models.covariance.LIBORVolatilityModelFromGivenMatrix;
import net.finmath.montecarlo.interestrate.products.Swap;
import net.finmath.montecarlo.interestrate.products.SwapLeg;
import net.finmath.montecarlo.interestrate.products.components.Notional;
import net.finmath.montecarlo.interestrate.products.components.NotionalFromConstant;
import net.finmath.montecarlo.interestrate.products.indices.AbstractIndex;
import net.finmath.montecarlo.interestrate.products.indices.LIBORIndex;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.stochastic.RandomVariable;
import net.finmath.stochastic.Scalar;
import net.finmath.time.FloatingpointDate;
import net.finmath.time.Period;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.TimeDiscretizationFromArray;
import net.finmath.time.businessdaycalendar.BusinessdayCalendar;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;


public class SDCCollateralizedMCSimulation {

	
	private static double notionalDouble = 1E7;
	private static RandomVariable marginLimitLower = new Scalar((-1.0) * notionalDouble * 0.000001);
	private static RandomVariable marginLimitUpper = new Scalar(notionalDouble * 0.000001);
	
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static String headerFormat = "%-22s %20s %20s %20s %20s %20s %20s %n";
	private static String dataRowFormat = "%-22s %20.2f %20.2f %20.2f %20.2f %20.2f %20.2f %n";
	
	public static void main(String args[]) throws Exception {

		LocalDate referenceDate = LocalDate.of(2014,  Month.AUGUST,  12);
		/*
		 * Create Monte-Carlo model
		 */
		final int numberOfPaths = 1000;
		final int numberOfFactors = 5;
		final double correlationDecayParam = 0.2;
		final LIBORModelMonteCarloSimulationModel model = createMultiCurveLIBORMarketModel(referenceDate, numberOfPaths, numberOfFactors, correlationDecayParam);

		BusinessdayCalendar businessdayCalendar = new BusinessdayCalendarExcludingTARGETHolidays();
		Schedule schedule = createScheduleFromConventions(referenceDate, businessdayCalendar);
		List<LocalDate> paymentDates = getPaymentDates(schedule);
		LocalDate lastPaymentDate = paymentDates.get(paymentDates.size() - 1);
		
		SwapLeg fixLeg 	 = createFixLeg(schedule);
		SwapLeg floatLeg = createFloatLeg(schedule);	
		Swap swap = new Swap(fixLeg, floatLeg); //receiver swap

		double timePrevious = 0.0;
		LocalDate datePrevious = referenceDate;
		RandomVariable valuePrevious = swap.getValue(timePrevious, model);
		RandomVariable valueCurrent, valueChange, cappedValueChange;
		RandomVariable accrual, gapAmount;
		// Trade has initially a non-zero value -> collateral needs to be set up by an upfront payment
		RandomVariable collateralAccount = valuePrevious; 
		RandomVariable gapAccount = new RandomVariableFromDoubleArray(0.0);
		
		System.out.printf(headerFormat, "Valuation Date", "Value V(t)", "Value Change Y_i", "Capped Change X_i", "Gap Amount Z_i", "Gap Account D_i", "Coll Account C_i");

		for (LocalDate dateCurrent = referenceDate.plusDays(1); dateCurrent.isBefore(lastPaymentDate.plusDays(1)); dateCurrent = dateCurrent.plusDays(1)) {
			if (businessdayCalendar.isBusinessday(dateCurrent)) {
				double dt = FloatingpointDate.getFloatingPointDateFromDate(datePrevious, dateCurrent);
				double timeCurrent = FloatingpointDate.getFloatingPointDateFromDate(referenceDate, dateCurrent);
				// V(t_i)
				valueCurrent = swap.getValue(timeCurrent, model);
				// (1 + r_{i-1}*(t_i - t_{i-1}))
				accrual = model.getForwardRate(timePrevious, timePrevious, timeCurrent).mult(dt).add(1.0);		
				// Y_i = V(t_i) - V(t_{i-1}) * (1+r_{i-1}*(t_i-t_{i-1})
				valueChange = valueCurrent.sub(valuePrevious.mult(accrual)); 
				// X_i
				cappedValueChange = valueChange.floor(marginLimitLower).cap(marginLimitUpper);
				// Z_i = Y_i - X_i
				gapAmount = valueChange.sub(cappedValueChange);
				
				if (paymentDates.contains(dateCurrent)) {
					// C_k+ = C_k - (A_k - D_k) = C_{i-1}*(1+rt) + D_{i-1}(1+rt) + V(t_k+) - V(i-1)*(1+rt)
					collateralAccount = collateralAccount.mult(accrual).add(gapAccount.mult(accrual)).add(valueCurrent).sub(valuePrevious.mult(accrual));
					gapAccount = gapAccount.mult(0.0);
				} else {
					// C_i = C_{i-1}*(1+r_{i-1}*(t_i-t_{i-1})) + X_i
					collateralAccount = collateralAccount.mult(accrual).add(cappedValueChange);
					// D_i = D_{i-1}*(1+r_{i-1}*(t_i-t_{i-1})) + Z_i
					gapAccount = gapAccount.mult(accrual).add(gapAmount);
				}
				// Simple control path
				System.out.printf(dataRowFormat, formatter.format(dateCurrent), valueCurrent.get(0), valueChange.get(0), cappedValueChange.get(0), gapAmount.get(0), gapAccount.get(0), collateralAccount.get(0));
				timePrevious  = timeCurrent;
				datePrevious  = dateCurrent;
				valuePrevious = valueCurrent;
			}
		}
		
	}
	
	

	private static SwapLeg createFloatLeg(Schedule schedule) {
		/*
		 * Create Monte-Carlo leg
		 */
		final Notional notional = new NotionalFromConstant(notionalDouble);
		final AbstractIndex index = new LIBORIndex(0.0, 0.5);
		final double spread = 0.0;
		return new SwapLeg(schedule, notional, index, spread, false /* isNotionalExchanged */);
	}
	
	private static SwapLeg createFixLeg(Schedule schedule) {
		/*
		 * Create Monte-Carlo leg
		 */
		final Notional notional = new NotionalFromConstant(notionalDouble);
		final AbstractIndex index = null;
		final double spread = 0.05;
		return new SwapLeg(schedule, notional, index, spread, false /* isNotionalExchanged */);
	}
	
	
	private static List<LocalDate> getPaymentDates(Schedule schedule) {
		return schedule.getPeriods().stream()
				.map(Period::getPayment)
				.sorted().toList();
	}
	
	private static Schedule createScheduleFromConventions(LocalDate referenceDate, BusinessdayCalendar businessdayCalendar) {
		final int			spotOffsetDays = 2;
		final String		forwardStartPeriod = "0D";
		final String		maturity = "2Y";
		final String		frequency = "semiannual";
		final String		daycountConvention = "30/360";

		return ScheduleGenerator.createScheduleFromConventions(referenceDate, spotOffsetDays, forwardStartPeriod, maturity, frequency, daycountConvention, "first", "following", businessdayCalendar, -2, 0);	
	}
	
	
	public static LIBORModelMonteCarloSimulationModel createMultiCurveLIBORMarketModel(LocalDate referenceDate, final int numberOfPaths, final int numberOfFactors, final double correlationDecayParam) throws CalculationException {

		// Create the forward curve (initial value of the LIBOR market model)
		final ForwardCurveInterpolation forwardCurveInterpolation = ForwardCurveInterpolation.createForwardCurveFromForwards(
				"forwardCurve"								/* name of the curve */,
				referenceDate,
				"6M",
				new BusinessdayCalendarExcludingTARGETHolidays(),
				BusinessdayCalendar.DateRollConvention.FOLLOWING,
				CurveInterpolation.InterpolationMethod.LINEAR,
				CurveInterpolation.ExtrapolationMethod.CONSTANT,
				CurveInterpolation.InterpolationEntity.VALUE,
				ForwardCurveInterpolation.InterpolationEntityForward.FORWARD,
				null,
				null,
				new double[] {0.5 , 1.0 , 2.0 , 5.0 , 10.0}	/* fixings of the forward */,
				new double[] {0.05, 0.05, 0.05, 0.05, 0.05}	/* forwards */
				);

		// Create the discount curve
		final DiscountCurveInterpolation discountCurveInterpolation = DiscountCurveInterpolation.createDiscountCurveFromZeroRates(
				"discountCurve"								/* name of the curve */,
				new double[] {0.5 , 1.0 , 2.0 , 5.0 , 10.0}	/* maturities */,
				new double[] {0.04, 0.04, 0.04, 0.04, 0.05}	/* zero rates */
				);

		return createLIBORMarketModel(numberOfPaths, numberOfFactors, correlationDecayParam, discountCurveInterpolation, forwardCurveInterpolation);
	}

	public static LIBORModelMonteCarloSimulationModel createLIBORMarketModel(
			final int numberOfPaths, final int numberOfFactors, final double correlationDecayParam, final DiscountCurve discountCurve, final ForwardCurve forwardCurve) throws CalculationException {

		final AnalyticModel model = new AnalyticModelFromCurvesAndVols(new Curve[] { forwardCurve , discountCurve });

		/*
		 * Create the libor tenor structure and the initial values
		 */
		final double liborPeriodLength	= 0.5;
		final double liborRateTimeHorzion	= 10.0;
		final TimeDiscretizationFromArray liborPeriodDiscretization = new TimeDiscretizationFromArray(0.0, (int) (liborRateTimeHorzion / liborPeriodLength), liborPeriodLength);

		/*
		 * Create a simulation time discretization
		 */
		final double lastTime	= 10.0;
		final double dt		= 1.0/400.0;

		final TimeDiscretizationFromArray timeDiscretizationFromArray = new TimeDiscretizationFromArray(0.0, (int) (lastTime / dt), dt);

		/*
		 * Create a volatility structure v[i][j] = sigma_j(t_i)
		 */
		final double[][] volatility = new double[timeDiscretizationFromArray.getNumberOfTimeSteps()][liborPeriodDiscretization.getNumberOfTimeSteps()];
		for (int timeIndex = 0; timeIndex < volatility.length; timeIndex++) {
			for (int liborIndex = 0; liborIndex < volatility[timeIndex].length; liborIndex++) {
				// Create a very simple volatility model here
				final double time = timeDiscretizationFromArray.getTime(timeIndex);
				final double maturity = liborPeriodDiscretization.getTime(liborIndex);
				final double timeToMaturity = maturity - time;

				double instVolatility;
				if(timeToMaturity <= 0) {
					instVolatility = 0;				// This forward rate is already fixed, no volatility
				} else {
					instVolatility = 0.3 + 0.2 * Math.exp(-0.25 * timeToMaturity);
				}

				// Store
				volatility[timeIndex][liborIndex] = instVolatility;
			}
		}
		final LIBORVolatilityModelFromGivenMatrix volatilityModel = new LIBORVolatilityModelFromGivenMatrix(timeDiscretizationFromArray, liborPeriodDiscretization, volatility);

		/*
		 * Create a correlation model rho_{i,j} = exp(-a * abs(T_i-T_j))
		 */
		final LIBORCorrelationModelExponentialDecay correlationModel = new LIBORCorrelationModelExponentialDecay(
				timeDiscretizationFromArray, liborPeriodDiscretization, numberOfFactors,
				correlationDecayParam);


		/*
		 * Combine volatility model and correlation model to a covariance model
		 */
		final LIBORCovarianceModelFromVolatilityAndCorrelation covarianceModel =
				new LIBORCovarianceModelFromVolatilityAndCorrelation(timeDiscretizationFromArray,
						liborPeriodDiscretization, volatilityModel, correlationModel);

		// BlendedLocalVolatlityModel (future extension)
		//		AbstractLIBORCovarianceModel covarianceModel2 = new BlendedLocalVolatlityModel(covarianceModel, 0.00, false);

		// Set model properties
		final Map<String, String> properties = new HashMap<>();

		// Choose the simulation measure
		properties.put("measure", LIBORMarketModelFromCovarianceModel.Measure.SPOT.name());

		// Choose log normal model
		properties.put("stateSpace", LIBORMarketModelFromCovarianceModel.StateSpace.LOGNORMAL.name());

		// Empty array of calibration items - hence, model will use given covariance
		final CalibrationProduct[] calibrationItems = new CalibrationProduct[0];

		/*
		 * Create corresponding LIBOR Market Model
		 */
		final LIBORMarketModel liborMarketModel = new LIBORMarketModelFromCovarianceModel(
				liborPeriodDiscretization, model, forwardCurve, discountCurve, new RandomVariableFromArrayFactory(), covarianceModel, calibrationItems, properties);

		final EulerSchemeFromProcessModel process = new EulerSchemeFromProcessModel(
				liborMarketModel,
				new BrownianMotionFromMersenneRandomNumbers(timeDiscretizationFromArray,
						numberOfFactors, numberOfPaths, 3141 /* seed */, new RandomVariableFromArrayFactory()));
		//		process.setScheme(EulerSchemeFromProcessModel.Scheme.PREDICTOR_CORRECTOR);

		return new LIBORMonteCarloSimulationFromLIBORModel(process);
	}
}
