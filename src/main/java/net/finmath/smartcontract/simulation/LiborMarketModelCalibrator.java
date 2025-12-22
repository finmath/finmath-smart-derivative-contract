package net.finmath.smartcontract.simulation;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.finmath.exception.CalculationException;
import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.curves.DiscountCurve;
import net.finmath.marketdata.model.curves.ForwardCurve;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.RandomVariableFromArrayFactory;
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
import net.finmath.montecarlo.interestrate.products.AbstractTermStructureMonteCarloProduct;
import net.finmath.montecarlo.interestrate.products.SwaptionSimple;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.optimizer.OptimizerFactory;
import net.finmath.optimizer.OptimizerFactoryLevenbergMarquardt;
import net.finmath.optimizer.SolverException;
import net.finmath.optimizer.LevenbergMarquardt.RegularizationMethod;
import net.finmath.smartcontract.simulation.InterestRateAnalyticCalibrator.CURVE_NAME;
import net.finmath.time.TimeDiscretizationFromArray;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;
import net.finmath.time.daycount.DayCountConvention_ACT_365;

public class LiborMarketModelCalibrator {
	
	private static DecimalFormat formatterValue		= new DecimalFormat(" ##0.0000%;-##0.0000%", new DecimalFormatSymbols(Locale.ENGLISH));
	private static DecimalFormat formatterDeviation	= new DecimalFormat(" 0.00000E00;-0.00000E00", new DecimalFormatSymbols(Locale.ENGLISH));
	
	public static LocalDate REFERENCE_DATE = LocalDate.of(2025, Month.OCTOBER, 30);
	public static int NUMBER_OF_PATHS = 5000;
	public static int NUMBER_OF_FACTORS = 5;
	public static double LIBOR_TIME_HORIZON = 6.0;
	public static double LIBOR_PERIOD_LENGTH = 0.5;
	public static double SIMULATION_TIME_STEP= 0.0025;


	public static void main(String[] args) throws CloneNotSupportedException, CalculationException, SolverException {
		
		/* Initialize IRA calibrator*/
		InterestRateAnalyticCalibrator calibrator = new InterestRateAnalyticCalibrator();
		calibrator.addFixingItem(CURVE_NAME.EURIBOR06M, REFERENCE_DATE, 0.02127);
		// Create the forward and discount curve (initial value of the LIBOR market model)
		double[] forwardCurveQuotes	= new double[] {0.02113,0.02102,0.02087,0.02074,0.02054,0.02046,0.02068,0.02141,0.022105,0.022825,0.02349,0.02413,0.02474,0.02533,0.02588,0.026395,0.027295,0.02825,0.02884,0.02879,0.028615};
		double[] discountCurveQuotes = new double[] {0.01931,0.0192885,0.019292,0.0192995,0.0193025,0.019294,0.0192815,0.0192525,0.019192,0.019129,0.0190685,0.019008,0.018942,0.0188925,0.0188515,0.018821,0.0187145,0.018719,0.01881,0.018953,0.019661,0.020461,0.0212175,0.021926,0.0226005,0.023262,0.023891,0.0244875,0.025499,0.0266305,0.027421,0.027506,0.027422};
		AnalyticModel curveModel = calibrator.getCalibratedModel(REFERENCE_DATE, discountCurveQuotes, forwardCurveQuotes);
		
		// Create a set of calibration products.
		final ArrayList<String>				calibrationItemNames	= new ArrayList<>();
		final ArrayList<CalibrationProduct>	calibrationProducts		= new ArrayList<>();
		final double	swapPeriodLength	= 0.5;
		final String[] atmExpiries = {
				"1M", "1M", "1M", "1M", "1M", "1M", "1M", "1M", "1M", "1M", "1M", "1M", "1M", "1M",
				"2M", "2M", "2M","2M", "2M", "2M", "2M", "2M", "2M", "2M", "2M", "2M", "2M", "2M",
				"3M", "3M", "3M","3M", "3M", "3M", "3M", "3M", "3M", "3M", "3M", "3M", "3M", "3M",
				"6M", "6M", "6M", "6M", "6M", "6M", "6M", "6M", "6M", "6M", "6M", "6M", "6M", "6M",
				"9M", "9M", "9M", "9M", "9M", "9M", "9M", "9M", "9M", "9M", "9M", "9M", "9M", "9M", 
				"1Y", "1Y", "1Y", "1Y", "1Y", "1Y", "1Y", "1Y", "1Y", "1Y", "1Y", "1Y", "1Y", "1Y",
				"18M", "18M", "18M", "18M", "18M", "18M", "18M", "18M", "18M", "18M", "18M", "18M", "18M", "18M", 
				"2Y", "2Y", "2Y", "2Y", "2Y", "2Y", "2Y", "2Y", "2Y", "2Y", "2Y", "2Y", "2Y", "2Y", 
				"3Y", "3Y", "3Y", "3Y", "3Y", "3Y", "3Y", "3Y", "3Y", "3Y", "3Y", "3Y", "3Y", "3Y",
				"4Y", "4Y", "4Y", "4Y", "4Y", "4Y", "4Y", "4Y", "4Y", "4Y", "4Y", "4Y", "4Y", "4Y", 
				"5Y", "5Y", "5Y", "5Y", "5Y", "5Y", "5Y", "5Y", "5Y", "5Y", "5Y", "5Y", "5Y", "5Y",
				"7Y", "7Y", "7Y", "7Y", "7Y", "7Y", "7Y", "7Y", "7Y", "7Y", "7Y", "7Y", "7Y", "7Y", 
				"10Y", "10Y", "10Y", "10Y", "10Y", "10Y", "10Y", "10Y", "10Y", "10Y", "10Y", "10Y", "10Y", "10Y",
				"15Y", "15Y", "15Y", "15Y", "15Y", "15Y", "15Y", "15Y", "15Y", "15Y", "15Y", "15Y", "15Y", "15Y", 
				"20Y", "20Y", "20Y", "20Y", "20Y", "20Y", "20Y", "20Y", "20Y", "20Y", "20Y", "20Y", "20Y", "20Y",
				"25Y", "25Y", "25Y", "25Y", "25Y", "25Y", "25Y", "25Y", "25Y", "25Y", "25Y", "25Y", "25Y", "25Y",
				"30Y", "30Y", "30Y", "30Y", "30Y", "30Y", "30Y", "30Y", "30Y", "30Y", "30Y", "30Y", "30Y", "30Y"};
		final String[] atmTenors = {
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y",
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", 
				"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y"};
		final double[] atmNormalVolatilities = {
				0.002239, 0.003334, 0.004306, 0.004292, 0.004292, 0.004417, 0.004427, 0.004568, 0.004578, 0.004587, 0.004787, 0.004800, 0.005063, 0.005063,
				0.003026, 0.003882, 0.004574, 0.004582, 0.004593, 0.004784, 0.004795, 0.005033, 0.005044, 0.005055, 0.005513, 0.005527, 0.005884, 0.005883,
				0.003000, 0.003789, 0.004566, 0.004580, 0.004593, 0.004785, 0.004797, 0.005037, 0.005048, 0.005058, 0.005517, 0.005532, 0.005883, 0.005882,
				0.003473, 0.004274, 0.004892, 0.004906, 0.004919, 0.005115, 0.005127, 0.005343, 0.005354, 0.005365, 0.005705, 0.005719, 0.006132, 0.006131,
				0.004448, 0.004873, 0.005353, 0.005367, 0.005381, 0.005514, 0.005526, 0.005695, 0.005707, 0.005717, 0.006095, 0.006109, 0.006488, 0.006487,
				0.004286, 0.004810, 0.005315, 0.005331, 0.005344, 0.005476, 0.005489, 0.005655, 0.005667, 0.005677, 0.006008, 0.006019, 0.006386, 0.006384,
				0.005598, 0.005600, 0.005811, 0.005826, 0.005839, 0.005909, 0.005922, 0.006072, 0.006080, 0.006085, 0.006255, 0.006262, 0.006507, 0.006511,
				0.005435, 0.005590, 0.005825, 0.005839, 0.005852, 0.005915, 0.005927, 0.006064, 0.006069, 0.006073, 0.006234, 0.006238, 0.006487, 0.006491,
				0.005913, 0.006005, 0.006095, 0.006110, 0.006124, 0.006153, 0.006165, 0.006277, 0.006273, 0.006265, 0.006352, 0.006351, 0.006559, 0.006554,
				0.006241, 0.006295, 0.006274, 0.006290, 0.006303, 0.006318, 0.006320, 0.006411, 0.006405, 0.006397, 0.006406, 0.006402, 0.006591, 0.006585,
				0.006492, 0.006516, 0.006431, 0.006444, 0.006456, 0.006457, 0.006457, 0.006492, 0.006491, 0.006489, 0.006426, 0.006420, 0.006580, 0.006574,
				0.006703, 0.006734, 0.006621, 0.006624, 0.006628, 0.006568, 0.006578, 0.006586, 0.006584, 0.006577, 0.006413, 0.006403, 0.006502, 0.006495,
				0.006774, 0.006806, 0.006658, 0.006661, 0.006663, 0.006589, 0.006587, 0.006580, 0.006572, 0.006563, 0.006348, 0.006336, 0.006335, 0.006327,
				0.006724, 0.006744, 0.006554, 0.006553, 0.006549, 0.006430, 0.006423, 0.006360, 0.006361, 0.006363, 0.006063, 0.006053, 0.006005, 0.005997,
				0.006638, 0.006659, 0.006357, 0.006357, 0.006359, 0.006198, 0.006218, 0.006115, 0.006107, 0.006093, 0.005805, 0.005797, 0.005748, 0.005741,
				0.006579, 0.006560, 0.006196, 0.006198, 0.006199, 0.006022, 0.006019, 0.005847, 0.005857, 0.005872, 0.005612, 0.005605, 0.005540, 0.005531,
				0.006491, 0.006504, 0.006115, 0.006117, 0.006116, 0.005929, 0.005925, 0.005689, 0.005698, 0.005709, 0.005431, 0.005423, 0.005388, 0.005382};

		final BusinessdayCalendarExcludingTARGETHolidays cal = new BusinessdayCalendarExcludingTARGETHolidays();
		final DayCountConvention_ACT_365 modelDC = new DayCountConvention_ACT_365();
		for(int i=0; i < atmNormalVolatilities.length; i++ ) {

			final LocalDate exerciseDate = cal.getDateFromDateAndOffsetCode(REFERENCE_DATE, atmExpiries[i]);
			final LocalDate tenorEndDate = cal.getDateFromDateAndOffsetCode(exerciseDate, atmTenors[i]);
			double	exercise		= modelDC.getDaycountFraction(REFERENCE_DATE, exerciseDate);
			double	tenor			= modelDC.getDaycountFraction(exerciseDate, tenorEndDate);

			exercise = Math.round(exercise/0.25)*0.25;
			tenor = Math.round(tenor/0.25)*0.25;
			if(exercise < 1.0) {
				continue;
			}
			final int numberOfPeriods = (int)Math.round(tenor / swapPeriodLength);
			final double	moneyness			= 0.0;
			final double	targetVolatility	= atmNormalVolatilities[i];
			final String	targetVolatilityType = "VOLATILITYNORMAL";

			final double	weight = 1.0;


			calibrationProducts.add(createCalibrationItem(weight, exercise, swapPeriodLength, numberOfPeriods, moneyness, targetVolatility, targetVolatilityType, curveModel.getForwardCurve(InterestRateAnalyticCalibrator.FORWARD_EUR_6M), curveModel.getDiscountCurve(InterestRateAnalyticCalibrator.DISCOUNT_EUR_OIS)));
			calibrationItemNames.add(atmExpiries[i]+"\t"+atmTenors[i]);
		}

		// If simulation time is below libor time, exceptions will be hard to track.
		TimeDiscretizationFromArray timeDiscretizationFromArray = new TimeDiscretizationFromArray(0.0, (int) (LIBOR_TIME_HORIZON / SIMULATION_TIME_STEP), SIMULATION_TIME_STEP);

		//Create the libor tenor structure and the initial values
		TimeDiscretizationFromArray liborPeriodDiscretization = new TimeDiscretizationFromArray(0.0, (int) (LIBOR_TIME_HORIZON / LIBOR_PERIOD_LENGTH), LIBOR_PERIOD_LENGTH);

		// Create Brownian motions
		final BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(timeDiscretizationFromArray, NUMBER_OF_FACTORS, NUMBER_OF_PATHS, 31415 /* seed */);

		// Create a volatility model: Piecewise constant volatility
		//LIBORVolatilityModel volatilityModel = new LIBORVolatilityModelPiecewiseConstant(new RandomVariableFromArrayFactory(), timeDiscretizationFromArray, liborPeriodDiscretization, new TimeDiscretizationFromArray(0.00, 1.0, 2.0, 5.0, 10.0, 20.0, 30.0, 40.0), new TimeDiscretizationFromArray(0.00, 1.0, 2.0, 5.0, 10.0, 20.0, 30.0, 40.0), new double[]{0.50 / 100}, true);
		final LIBORVolatilityModel volatilityModel = new LIBORVolatilityModelPiecewiseConstant(timeDiscretizationFromArray, liborPeriodDiscretization, new TimeDiscretizationFromArray(0.00, 1.0, 2.0, 5.0, 10.0, 20.0, 30.0, 40.0), new TimeDiscretizationFromArray(0.00, 1.0, 2.0, 5.0, 10.0, 20.0, 30.0, 40.0), 0.50 / 100);

		// Create a correlation model
		final LIBORCorrelationModel correlationModel = new LIBORCorrelationModelExponentialDecay(timeDiscretizationFromArray, liborPeriodDiscretization, NUMBER_OF_FACTORS, 0.05, false);

		// Create a covariance model
		final AbstractLIBORCovarianceModelParametric covarianceModelParametric = new LIBORCovarianceModelFromVolatilityAndCorrelation(timeDiscretizationFromArray, liborPeriodDiscretization, volatilityModel, correlationModel);

		// Create blended local volatility model with fixed parameter (0=lognormal, > 1 = almost a normal model).
		//AbstractLIBORCovarianceModelParametric covarianceModelBlended = new BlendedLocalVolatilityModel(new RandomVariableFromArrayFactory(), covarianceModelParametric, 4.0, true);
		final AbstractLIBORCovarianceModelParametric covarianceModelDisplaced = new DisplacedLocalVolatilityModel(covarianceModelParametric, 1.0/0.25, false /* isCalibrateable */);
		
		// Set model properties
		final Map<String, Object> properties = new HashMap<>();
		// Choose the simulation measure
		properties.put("measure", LIBORMarketModelFromCovarianceModel.Measure.SPOT.name());
		// Choose normal state space for the Euler scheme (the covariance model above carries a linear local volatility model, such that the resulting model is log-normal).
		properties.put("stateSpace", LIBORMarketModelFromCovarianceModel.StateSpace.NORMAL.name());

		// Set calibration properties (should use our brownianMotion for calibration - needed to have to right correlation).
		final Double accuracy = 1E-7;	// Lower accuracy to reduce runtime of the unit test
		final int maxIterations = 200;
		final int numberOfThreads = 1;
		final double lambda = 0.1;
		final OptimizerFactory optimizerFactory = new OptimizerFactoryLevenbergMarquardt(
				RegularizationMethod.LEVENBERG, lambda,
				maxIterations, accuracy, numberOfThreads);

		final double[] parameterStandardDeviation = new double[covarianceModelDisplaced.getParameterAsDouble().length];
		final double[] parameterLowerBound = new double[covarianceModelDisplaced.getParameterAsDouble().length];
		final double[] parameterUpperBound = new double[covarianceModelDisplaced.getParameterAsDouble().length];
		Arrays.fill(parameterStandardDeviation, 0.20/100.0);
		Arrays.fill(parameterLowerBound, 0.0);
		Arrays.fill(parameterUpperBound, Double.POSITIVE_INFINITY);

		// Set calibration properties (should use our brownianMotion for calibration - needed to have to right correlation).
		final Map<String, Object> calibrationParameters = new HashMap<>();
		calibrationParameters.put("accuracy", accuracy);
		calibrationParameters.put("brownianMotion", brownianMotion);
		calibrationParameters.put("optimizerFactory", optimizerFactory);
		calibrationParameters.put("parameterStep", 1E-4);
		properties.put("calibrationParameters", calibrationParameters);

		/*
		 * Create corresponding LIBOR Market Model
		 */
		final CalibrationProduct[] calibrationItemsLMM = new CalibrationProduct[calibrationItemNames.size()];
		for(int i=0; i<calibrationItemNames.size(); i++) {
			calibrationItemsLMM[i] = new CalibrationProduct(calibrationProducts.get(i).getProduct(),calibrationProducts.get(i).getTargetValue(),calibrationProducts.get(i).getWeight());
		}
		final LIBORMarketModel liborMarketModelCalibrated = LIBORMarketModelFromCovarianceModel.of(
				liborPeriodDiscretization,
				curveModel,
				curveModel.getForwardCurve(InterestRateAnalyticCalibrator.FORWARD_EUR_6M),
				curveModel.getDiscountCurve(InterestRateAnalyticCalibrator.DISCOUNT_EUR_OIS),
				new RandomVariableFromArrayFactory(),
				covarianceModelDisplaced,
				calibrationItemsLMM, properties);


		System.out.println("\nCalibrated parameters are:");
		final double[] param = ((AbstractLIBORCovarianceModelParametric)((LIBORMarketModelFromCovarianceModel) liborMarketModelCalibrated).getCovarianceModel()).getParameterAsDouble();
		System.out.println(Arrays.toString(param));

		
		final EulerSchemeFromProcessModel process = new EulerSchemeFromProcessModel(liborMarketModelCalibrated, brownianMotion);
		final LIBORModelMonteCarloSimulationModel simulationCalibrated = new LIBORMonteCarloSimulationFromLIBORModel(process);

		System.out.println("\nValuation on calibrated model:");
		double deviationSum			= 0.0;
		double deviationSquaredSum	= 0.0;
		for (int i = 0; i < calibrationProducts.size(); i++) {
			final AbstractTermStructureMonteCarloProduct calibrationProduct = calibrationProducts.get(i).getProduct();
			try {
				final double valueModel = calibrationProduct.getValue(simulationCalibrated);
				final double valueTarget = calibrationProducts.get(i).getTargetValue().getAverage();
				final double error = valueModel-valueTarget;
				deviationSum += error;
				deviationSquaredSum += error*error;
				System.out.println(calibrationItemNames.get(i) + "\t" + "Model: " + formatterValue.format(valueModel) + "\t Target: " + formatterValue.format(valueTarget) + "\t Deviation: " + formatterDeviation.format(valueModel-valueTarget));// + "\t" + calibrationProduct.toString());
			}
			catch(final Exception e) {
			}
		}
		
		final double averageDeviation = deviationSum/calibrationProducts.size();
		System.out.println("Mean Deviation:" + formatterValue.format(averageDeviation));
		System.out.println("RMS Error.....:" + formatterValue.format(Math.sqrt(deviationSquaredSum/calibrationProducts.size())));
		System.out.println("__________________________________________________________________________________________\n");
	}
	
	private static CalibrationProduct createCalibrationItem(final double weight, final double exerciseDate, final double swapPeriodLength, final int numberOfPeriods, final double moneyness, final double targetVolatility, final String targetVolatilityType, final ForwardCurve forwardCurve, final DiscountCurve discountCurve) throws CalculationException {

		final double[]	fixingDates			= new double[numberOfPeriods];
		final double[]	paymentDates		= new double[numberOfPeriods];
		final double[]	swapTenor			= new double[numberOfPeriods + 1];

		for (int periodStartIndex = 0; periodStartIndex < numberOfPeriods; periodStartIndex++) {
			fixingDates[periodStartIndex] = exerciseDate + periodStartIndex * swapPeriodLength;
			paymentDates[periodStartIndex] = exerciseDate + (periodStartIndex + 1) * swapPeriodLength;
			swapTenor[periodStartIndex] = exerciseDate + periodStartIndex * swapPeriodLength;
		}
		swapTenor[numberOfPeriods] = exerciseDate + numberOfPeriods * swapPeriodLength;

		// Swaptions swap rate
		final double swaprate = moneyness + getParSwaprate(forwardCurve, discountCurve, swapTenor);

		// Set swap rates for each period
		final double[] swaprates = new double[numberOfPeriods];
		Arrays.fill(swaprates, swaprate);

		/*
		 * We use Monte-Carlo calibration on implied volatility.
		 * Alternatively you may change here to Monte-Carlo valuation on price or
		 * use an analytic approximation formula, etc.
		 */
		final SwaptionSimple swaptionMonteCarlo = new SwaptionSimple(swaprate, swapTenor, SwaptionSimple.ValueUnit.valueOf(targetVolatilityType));
		//		double targetValuePrice = AnalyticFormulas.blackModelSwaptionValue(swaprate, targetVolatility, fixingDates[0], swaprate, getSwapAnnuity(discountCurve, swapTenor));
		return new CalibrationProduct(swaptionMonteCarlo, targetVolatility, weight);
	}
	
	
	private static double getParSwaprate(final ForwardCurve forwardCurve, final DiscountCurve discountCurve, final double[] swapTenor) {
		return net.finmath.marketdata.products.Swap.getForwardSwapRate(new TimeDiscretizationFromArray(swapTenor), new TimeDiscretizationFromArray(swapTenor), forwardCurve, discountCurve);
	}
	
	
}
