package net.finmath.smartcontract.simulation;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;


import net.finmath.exception.CalculationException;
import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.curves.DiscountCurve;
import net.finmath.marketdata.model.curves.ForwardCurve;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.RandomVariableFromArrayFactory;
import net.finmath.montecarlo.RandomVariableFromDoubleArray;
import net.finmath.montecarlo.interestrate.CalibrationProduct;
import net.finmath.montecarlo.interestrate.LIBORMarketModel;
import net.finmath.montecarlo.interestrate.LIBORModelMonteCarloSimulationModel;
import net.finmath.montecarlo.interestrate.LIBORMonteCarloSimulationFromLIBORModel;
import net.finmath.montecarlo.interestrate.models.LIBORMarketModelFromCovarianceModel;
import net.finmath.montecarlo.interestrate.models.covariance.AbstractLIBORCovarianceModelParametric;
import net.finmath.montecarlo.interestrate.models.covariance.DisplacedLocalVolatilityModel;
import net.finmath.montecarlo.interestrate.models.covariance.LIBORCorrelationModel;
import net.finmath.montecarlo.interestrate.models.covariance.LIBORCorrelationModelExponentialDecay;
import net.finmath.montecarlo.interestrate.models.covariance.LIBORCovarianceModelFromVolatilityAndCorrelation;
import net.finmath.montecarlo.interestrate.models.covariance.LIBORVolatilityModel;
import net.finmath.montecarlo.interestrate.models.covariance.LIBORVolatilityModelPiecewiseConstant;
import net.finmath.montecarlo.interestrate.products.Swap;
import net.finmath.montecarlo.interestrate.products.components.Notional;
import net.finmath.montecarlo.interestrate.products.components.NotionalFromConstant;
import net.finmath.montecarlo.interestrate.products.indices.AbstractIndex;
import net.finmath.montecarlo.interestrate.products.indices.LIBORIndex;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.optimizer.SolverException;
import net.finmath.smartcontract.simulation.InterestRateAnalyticCalibrator.CURVE_NAME;
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

	
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static String headerFormat = "%-22s %20s %20s %20s %20s %20s %20s %n";
	private static String dataRowFormat = "%-22s %20.6f %20.6f %20.6f %20.6f %20.6f %20.6f %n";
	
	private static final LocalDate REFERENCE_DATE =  LiborMarketModelCalibrator.REFERENCE_DATE;
	
	public static void main(String args[]) throws Exception {

		/*Generate 6M Payer Swap*/
		final String maturityKey = "5Y";
		BusinessdayCalendar businessdayCalendar = new BusinessdayCalendarExcludingTARGETHolidays();
		final Schedule scheduleRec = ScheduleGenerator.createScheduleFromConventions(REFERENCE_DATE, 2, "0D", maturityKey, "semiannual", "ACT/360", "first", "modfollow", businessdayCalendar, -2, 0);
		final Schedule schedulePay = ScheduleGenerator.createScheduleFromConventions(REFERENCE_DATE, 2, "0D", maturityKey, "annual", "E30/360", "first", "modfollow", businessdayCalendar, -2, 0);
		final Notional notional = new NotionalFromConstant(1.0);
		final AbstractIndex index = new LIBORIndex(2.0/365.0, 0.5);
		final Swap swap = new Swap(notional, scheduleRec, index, 0.0, schedulePay, null, 0.02349);
		
		TreeSet<LocalDate> paymentDates = getPaymentDates(scheduleRec, schedulePay);
		LocalDate lastPaymentDate = paymentDates.last();

		// Model has been calibrated in LiborMarketModelCalibrator.java
		final LIBORModelMonteCarloSimulationModel model = getLIBORMarketModel();
		
		// Constant funding spread 50bp 
		double fundingSpread = 0.005;
		
		// Result lists for Excel export
		int numberOfMarginIncrements = 20;
		List<Double> margin 	= new ArrayList<>(numberOfMarginIncrements);
		List<Double> cashFlow 	= new ArrayList<>(numberOfMarginIncrements);
		List<Double> funding	= new ArrayList<>(numberOfMarginIncrements);
		
		for (int i = 0; i <= numberOfMarginIncrements; i++) {    
			// Margin limits
			RandomVariable marginFloor = new Scalar((-1.0) * i * 0.0005);
			RandomVariable marginCap = new Scalar(i * 0.0005);
			
			LocalDate datePrevious = REFERENCE_DATE;
			double timePrevious = 0.0;
			RandomVariable valuePrevious = swap.getValue(timePrevious, model);
			RandomVariable valueCurrent, valueChange, cappedValueChange;
			RandomVariable accrualFactor, gapAmount;
			RandomVariable gapAccount = new RandomVariableFromDoubleArray(0.0);
			// Trade has initially a non-zero value -> collateral needs to be set up by an up-front payment
			RandomVariable collateralAccount = valuePrevious; 
			
			// Sum over T_k of E[(A_k - D_k)/N_k]
			RandomVariable payments = new RandomVariableFromDoubleArray(0.0);
			RandomVariable mva = new RandomVariableFromDoubleArray(0.0);
			RandomVariable numerairePrevious = model.getNumeraire(timePrevious);
			RandomVariable numeraireCurrent, fundingNumeraire;
			
			System.out.printf(headerFormat, "Valuation Date", "Value V(t)", "Value Change Y_i", "Capped Change X_i", "Gap Amount Z_i", "Gap Account D_i", "Coll Account C_i");
			for (LocalDate dateCurrent = REFERENCE_DATE.plusDays(1); dateCurrent.isBefore(lastPaymentDate.plusDays(1)); dateCurrent = dateCurrent.plusDays(1)) {
				if (businessdayCalendar.isBusinessday(dateCurrent)) {
					double timeCurrent = FloatingpointDate.getFloatingPointDateFromDate(REFERENCE_DATE, dateCurrent);
					double dt = FloatingpointDate.getFloatingPointDateFromDate(datePrevious, dateCurrent);
					// V(t_i)
					valueCurrent = swap.getValue(timeCurrent, model);
					// N(t_i)
					numeraireCurrent = model.getNumeraire(timeCurrent);
					// (1 + r_{i-1}*(t_i - t_{i-1})) = N(t_i) / N(t_{i-1})
					accrualFactor = numeraireCurrent.div(numerairePrevious);
					// Y_i = V(t_i) - V(t_{i-1}) * (1+r_{i-1}*(t_i-t_{i-1})
					valueChange = valueCurrent.sub(valuePrevious.mult(accrualFactor)); 
					// X_i
					cappedValueChange = valueChange.floor(marginFloor).cap(marginCap);
					// Z_i = Y_i - X_i
					gapAmount = valueChange.sub(cappedValueChange);
					if (paymentDates.contains(dateCurrent)) {
						// C_k+ = C_k - (A_k - D_k) = C_{i-1}*(1+rt) + D_{i-1}(1+rt) + V(t_k+) - V(i-1)*(1+rt) = V(t_k+) + (1+rt) * (C_{i-1} + D_{i-1} - V_{i-1})
						collateralAccount = valueCurrent.add(accrualFactor.mult(collateralAccount.add(gapAccount).sub(valuePrevious)));
						// (A_k - D_k)/N_k = (V(t_k) - V(t_{i-1}) * (1+rt) - D_k)/N_k = (Y_i - D_k)/N_k
						payments = payments.add((valueChange.sub(gapAccount)).div(numeraireCurrent));
						gapAccount = new RandomVariableFromDoubleArray(0.0);
					} else {
						// C_i = C_{i-1}*(1+r_{i-1}*(t_i-t_{i-1})) + X_i
						collateralAccount = collateralAccount.mult(accrualFactor).add(cappedValueChange);
						// D_i = D_{i-1}*(1+r_{i-1}*(t_i-t_{i-1})) + Z_i
						gapAccount = gapAccount.mult(accrualFactor).add(gapAmount);
					}
					// d(1/N^fd(t)) = 1/N^fd(t_i+1) - 1/N^fd(t_i)
					fundingNumeraire = numeraireCurrent.mult(Math.exp((timeCurrent) * fundingSpread)).invert();
					fundingNumeraire = fundingNumeraire.sub(numerairePrevious.mult(Math.exp(timePrevious * fundingSpread)).invert());
					mva = mva.add(fundingNumeraire.mult(marginCap));

					// Simple control path
					System.out.printf(dataRowFormat, formatter.format(dateCurrent), valueCurrent.get(0), valueChange.get(0), cappedValueChange.get(0), gapAmount.get(0), gapAccount.get(0), collateralAccount.get(0));
					timePrevious  = timeCurrent;
					datePrevious = dateCurrent;
					valuePrevious = valueCurrent;
					numerairePrevious = numeraireCurrent;
				}
			}
			// Store margin increment results to result array
			margin.add(i * 0.0005);
			cashFlow.add(payments.getAverage());
			funding.add(-mva.getAverage());			
		}	
		// Write result arrays to Excel
		//writeToExcel(marginLimitUpperResult, marginLimitLowerResult, mvaUpperResult, mvaLowerResult, cashFlowPaymentsResult, "D:/Papers/SDC Collateralized/Margin Analysis.xlsx");

	}	
	
	private static TreeSet<LocalDate> getPaymentDates(Schedule... schedules) {
	    return Arrays.stream(schedules)
	            .flatMap(schedule -> schedule.getPeriods().stream())
	            .map(Period::getPayment)
	            .collect(Collectors.toCollection(TreeSet::new));
	}
	

	public static LIBORModelMonteCarloSimulationModel getLIBORMarketModel() throws CalculationException, CloneNotSupportedException, SolverException {
		
		int numberOfPaths = LiborMarketModelCalibrator.NUMBER_OF_PATHS;
		int numberOfFactors = LiborMarketModelCalibrator.NUMBER_OF_FACTORS;
		double liborRateTimeHorizon = LiborMarketModelCalibrator.LIBOR_TIME_HORIZON;
		double liborPeriodLength = LiborMarketModelCalibrator.LIBOR_PERIOD_LENGTH;
		double simulationTimeStep = LiborMarketModelCalibrator.SIMULATION_TIME_STEP;
		
		/* Initialize IRA calibrator*/
		InterestRateAnalyticCalibrator calibrator = new InterestRateAnalyticCalibrator();
		calibrator.addFixingItem(CURVE_NAME.EURIBOR06M, REFERENCE_DATE, 0.02127);
		// Create the forward and discount curve (initial value of the LIBOR market model)
		double[] forwardCurveQuotes	= new double[] {0.02113,0.02102,0.02087,0.02074,0.02054,0.02046,0.02068,0.02141,0.022105,0.022825,0.02349,0.02413,0.02474,0.02533,0.02588,0.026395,0.027295,0.02825,0.02884,0.02879,0.028615};
		double[] discountCurveQuotes = new double[] {0.01931,0.0192885,0.019292,0.0192995,0.0193025,0.019294,0.0192815,0.0192525,0.019192,0.019129,0.0190685,0.019008,0.018942,0.0188925,0.0188515,0.018821,0.0187145,0.018719,0.01881,0.018953,0.019661,0.020461,0.0212175,0.021926,0.0226005,0.023262,0.023891,0.0244875,0.025499,0.0266305,0.027421,0.027506,0.027422};
		AnalyticModel curveModel = calibrator.getCalibratedModel(REFERENCE_DATE, discountCurveQuotes, forwardCurveQuotes);
				
		final ForwardCurve forwardCurve = curveModel.getForwardCurve(InterestRateAnalyticCalibrator.FORWARD_EUR_6M);
		final DiscountCurve discountCurve = curveModel.getDiscountCurve(InterestRateAnalyticCalibrator.DISCOUNT_EUR_OIS);
	
		// Create a simulation time discretization
		// If simulation time is below libor time, exceptions will be hard to track.;
		TimeDiscretizationFromArray timeDiscretizationFromArray = new TimeDiscretizationFromArray(0.0, (int) (liborRateTimeHorizon / simulationTimeStep), simulationTimeStep);

		// Create the libor tenor structure and the initial values
		TimeDiscretizationFromArray liborPeriodDiscretization = new TimeDiscretizationFromArray(0.0, (int) (liborRateTimeHorizon / liborPeriodLength), liborPeriodLength);

		// Create Brownian motions
		final BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(timeDiscretizationFromArray, numberOfFactors, numberOfPaths, 31415 /* seed */);

		// Create a volatility model: Piecewise constant volatility
		double[] volatility = new double[] {9.503986828492626E-4, 0.0013854091244639847, 0.0015504532773712853, 6.79485126080056E-4, 0.0014231156592602748, 0.0010011866484630283, 0.0016485503338307087, 0.004999999999999998, 0.0016961125989920181, 0.0012383973801516235, 0.002003164809383976, 0.005, 0.005};
		//double[] volatility = new double[] {0.007917640152172153, 0.008444828514918408, 0.011903094386501008, 0.005000000000000346, 0.021676871474132164, 0.004841279913803049, 0.013640963631216477, -0.00863984710409209, 0.014914764783913457, 0.009332835587252468, 0.005000000000000001};	
		LIBORVolatilityModel volatilityModel = new LIBORVolatilityModelPiecewiseConstant(new RandomVariableFromArrayFactory(), timeDiscretizationFromArray, liborPeriodDiscretization, new TimeDiscretizationFromArray(0.00, 1.0, 2.0, 5.0, 10.0, 20.0, 30.0, 40.0), new TimeDiscretizationFromArray(0.00, 1.0, 2.0, 5.0, 10.0, 20.0, 30.0, 40.0), volatility, false);

		// Create a correlation model
		LIBORCorrelationModel correlationModel = new LIBORCorrelationModelExponentialDecay(timeDiscretizationFromArray, liborPeriodDiscretization, numberOfFactors, 0.05, false);

		// Create a covariance model
		AbstractLIBORCovarianceModelParametric covarianceModelParametric = new LIBORCovarianceModelFromVolatilityAndCorrelation(timeDiscretizationFromArray, liborPeriodDiscretization, volatilityModel, correlationModel);

		// Create blended local volatility model with fixed parameter (0=lognormal, > 1 = almost a normal model).
		final AbstractLIBORCovarianceModelParametric covarianceModelDisplaced = new DisplacedLocalVolatilityModel(covarianceModelParametric, 1.0/0.25, false /* isCalibrateable */);
		
		
		// Set model properties
		final Map<String, String> properties = new HashMap<>();

		// Choose the simulation measure
		properties.put("measure", LIBORMarketModelFromCovarianceModel.Measure.SPOT.name());

		// Choose log normal model
		properties.put("stateSpace", LIBORMarketModelFromCovarianceModel.StateSpace.NORMAL.name());

		// Empty array of calibration items - hence, model will use given covariance
		final CalibrationProduct[] calibrationItems = new CalibrationProduct[0];

		/*
		 * Create corresponding LIBOR Market Model
		 */		
		final LIBORMarketModel liborMarketModel = new LIBORMarketModelFromCovarianceModel(
				liborPeriodDiscretization, curveModel, forwardCurve, discountCurve, 
				new RandomVariableFromArrayFactory(), covarianceModelDisplaced, calibrationItems, properties);

		final EulerSchemeFromProcessModel process = new EulerSchemeFromProcessModel(liborMarketModel, brownianMotion);

		return new LIBORMonteCarloSimulationFromLIBORModel(process);
	}
}
