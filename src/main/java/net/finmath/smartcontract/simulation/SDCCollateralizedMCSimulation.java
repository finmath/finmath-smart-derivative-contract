package net.finmath.smartcontract.simulation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

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
import net.finmath.montecarlo.interestrate.models.covariance.BlendedLocalVolatilityModel;
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
	private static String dataRowFormat = "%-22s %20.2f %20.2f %20.2f %20.2f %20.2f %20.2f %n";
	
	private static double NOTIONAL = 1E7;
	private static double FUNDING_SPREAD = 0.005; //50bp
	private static final LocalDate REFERENCE_DATE =  LiborMarketModelCalibration.REFERENCE_DATE;
	
	public static void main(String args[]) throws Exception {

		// Create a payer swap
		BusinessdayCalendar businessdayCalendar = new BusinessdayCalendarExcludingTARGETHolidays();
		final Schedule scheduleInterfaceRec = ScheduleGenerator.createScheduleFromConventions(REFERENCE_DATE, 2, "0D", "4Y", "semiannual","ACT/360", "first", "modfollow", businessdayCalendar, -2, 0);
		final Schedule scheduleInterfacePay = ScheduleGenerator.createScheduleFromConventions(REFERENCE_DATE, 2, "0D", "4Y", "annual", "E30/360", "first", "modfollow", businessdayCalendar, -2, 0);
		final Notional notional = new NotionalFromConstant(NOTIONAL);
		final AbstractIndex index = new LIBORIndex(2.0/365.0, 0.5);
		final Swap swap = new Swap(notional, scheduleInterfaceRec, index, 0.0, scheduleInterfacePay, null, 0.022825);
		
		TreeSet<LocalDate> paymentDates = getPaymentDates(scheduleInterfaceRec, scheduleInterfacePay);
		LocalDate lastPaymentDate = paymentDates.last();

		// Model has been calibrated in LiborMarketModelCalibration.java
		final LIBORModelMonteCarloSimulationModel model = getModel();
		
		// Result lists for Excel export
		int steps = 100;
		List<Double> marginLimitUpperResult = new ArrayList<>(steps);
		List<Double> marginLimitLowerResult = new ArrayList<>(steps);
		List<Double> mvaUpperResult			= new ArrayList<>(steps);
		List<Double> mvaLowerResult			= new ArrayList<>(steps);
		List<Double> cashFlowPaymentsResult	= new ArrayList<>(steps);
		
		for (int i = 1; i <= steps; i++) {    
			// TODO refine
		    double scaling = i / 100000.0;
			marginLimitUpperResult.add(NOTIONAL * scaling);
			marginLimitLowerResult.add((-1.0) * NOTIONAL * scaling);

			RandomVariable marginLimitUpper = new Scalar(NOTIONAL * scaling);
			RandomVariable marginLimitLower = new Scalar((-1.0) * NOTIONAL * scaling);
			
			LocalDate datePrevious = REFERENCE_DATE;
			double timePrevious = 0.0;
			RandomVariable valuePrevious = swap.getValue(timePrevious, model);
			RandomVariable valueCurrent, valueChange, cappedValueChange;
			RandomVariable accrual, gapAmount;
			// Trade has initially a non-zero value -> collateral needs to be set up by an up-front payment
			RandomVariable collateralAccount = valuePrevious; 
			RandomVariable gapAccount = new RandomVariableFromDoubleArray(0.0);
			
			// Sum over T_k of E[(A_k - D_k)/N_k]
			RandomVariable cashFlowPayments = new RandomVariableFromDoubleArray(0.0);
			RandomVariable mvaLower = new RandomVariableFromDoubleArray(0.0);
			RandomVariable mvaUpper = new RandomVariableFromDoubleArray(0.0);
			RandomVariable numerairePrevious = model.getNumeraire(timePrevious);
			RandomVariable numeraireCurrent, fundingNumeraire;
			
			System.out.printf(headerFormat, "Valuation Date", "Value V(t)", "Value Change Y_i", "Capped Change X_i", "Gap Amount Z_i", "Gap Account D_i", "Coll Account C_i");
			for (LocalDate dateCurrent = REFERENCE_DATE.plusDays(1); dateCurrent.isBefore(lastPaymentDate.plusDays(1)); dateCurrent = dateCurrent.plusDays(1)) {
				if (businessdayCalendar.isBusinessday(dateCurrent)) {
					double timeCurrent = FloatingpointDate.getFloatingPointDateFromDate(REFERENCE_DATE, dateCurrent);
					double dt = FloatingpointDate.getFloatingPointDateFromDate(datePrevious, dateCurrent);
					// V(t_i)
					valueCurrent = swap.getValue(timeCurrent, model);
					// (1 + r_{i-1}*(t_i - t_{i-1})) = 1 / P^OIS(T;t)
					accrual = model.getForwardRate(timePrevious, timePrevious, timeCurrent).mult(dt).add(1.0);
					//accrual = getForwardBondOIS(model, timeCurrent, timePrevious).invert();
					// Y_i = V(t_i) - V(t_{i-1}) * (1+r_{i-1}*(t_i-t_{i-1})
					valueChange = valueCurrent.sub(valuePrevious.mult(accrual)); 
					// X_i
					cappedValueChange = valueChange.floor(marginLimitLower).cap(marginLimitUpper);
					// Z_i = Y_i - X_i
					gapAmount = valueChange.sub(cappedValueChange);
					// N(t_i)
					numeraireCurrent = model.getNumeraire(timeCurrent);
					if (paymentDates.contains(dateCurrent)) {
						// C_k+ = C_k - (A_k - D_k) = C_{i-1}*(1+rt) + D_{i-1}(1+rt) + V(t_k+) - V(i-1)*(1+rt)
						collateralAccount = collateralAccount.mult(accrual).add(gapAccount.mult(accrual)).add(valueCurrent).sub(valuePrevious.mult(accrual));
						// (A_k - D_k)/N_k
						cashFlowPayments = cashFlowPayments.add((valueCurrent.sub(valuePrevious.mult(accrual)).sub(gapAccount)).div(numeraireCurrent));
						gapAccount = gapAccount.mult(0.0);
					} else {
						// C_i = C_{i-1}*(1+r_{i-1}*(t_i-t_{i-1})) + X_i
						collateralAccount = collateralAccount.mult(accrual).add(cappedValueChange);
						// D_i = D_{i-1}*(1+r_{i-1}*(t_i-t_{i-1})) + Z_i
						gapAccount = gapAccount.mult(accrual).add(gapAmount);
					}
					// d(1/N^fd(t)) = 1/N^fd(t_i+1) - 1/N^fd(t_i)
					fundingNumeraire = numeraireCurrent.mult(Math.exp((timeCurrent) * FUNDING_SPREAD)).invert();
					fundingNumeraire = fundingNumeraire.sub(numerairePrevious.mult(Math.exp(timePrevious * FUNDING_SPREAD)).invert());
					mvaLower = mvaLower.add(fundingNumeraire.mult(marginLimitLower));
					mvaUpper = mvaUpper.add(fundingNumeraire.mult(marginLimitUpper));

					// Simple control path
					System.out.printf(dataRowFormat, formatter.format(dateCurrent), valueCurrent.get(0), valueChange.get(0), cappedValueChange.get(0), gapAmount.get(0), gapAccount.get(0), collateralAccount.get(0));
					timePrevious  = timeCurrent;
					datePrevious = dateCurrent;
					valuePrevious = valueCurrent;
					numerairePrevious = numeraireCurrent;
				}
			}
			mvaUpperResult.add(-mvaUpper.getAverage());			
			mvaLowerResult.add(-mvaLower.getAverage());	
			cashFlowPaymentsResult.add(cashFlowPayments.getAverage());	
		}
		
		writeToExcel(marginLimitUpperResult, marginLimitLowerResult, mvaUpperResult, mvaLowerResult, cashFlowPaymentsResult, "D:/Papers/SDC Collateralized/Margin Analysis.xlsx");

	}
	
	public static void writeToExcel(List<Double> marginLimitsUpper, List<Double> marginLimitsLower, List<Double> mvaUpper, List<Double> mvaLower, List<Double> cashFlowPayments, String filePath) throws IOException {
			    
		try {
            // Check if file exists
            if (!Files.exists(Paths.get(filePath))) {
                System.out.println("File does not exist.");
                return;
            }
            FileInputStream fileIn = new FileInputStream(filePath);
            Workbook wb = WorkbookFactory.create(fileIn);
            Sheet sheet = wb.createSheet("Data");

            // Optional: numeric cell format (preserves up to ~15 decimals without scientific notation)
            CellStyle numeric = wb.createCellStyle();
            short fmt = wb.createDataFormat().getFormat("0.###############");
            numeric.setDataFormat(fmt);

            // Header row
            Row header = sheet.createRow(0);
            String[] headers = {
                    "marginLimitsUpper", "marginLimitsLower",
                    "mvaUpper", "mvaLower",
                    "cashFlowPayments"
            };
            for (int c = 0; c < headers.length; c++) {
                Cell cell = header.createCell(c, CellType.STRING);
                cell.setCellValue(headers[c]);
            }

            // Data rows
            for (int i = 0; i < marginLimitsUpper.size(); i++) {
                Row row = sheet.createRow(i + 1);

                Cell c0 = row.createCell(0);
                c0.setCellValue(marginLimitsUpper.get(i));
                c0.setCellStyle(numeric);

                Cell c1 = row.createCell(1);
                c1.setCellValue(marginLimitsLower.get(i));
                c1.setCellStyle(numeric);

                Cell c2 = row.createCell(2);
                c2.setCellValue(mvaUpper.get(i));
                c2.setCellStyle(numeric);

                Cell c3 = row.createCell(3);
                c3.setCellValue(mvaLower.get(i));
                c3.setCellStyle(numeric);

                Cell c4 = row.createCell(4);
                c4.setCellValue(cashFlowPayments.get(i));
                c4.setCellStyle(numeric);
            }

            // Autosize columns
            for (int c = 0; c < headers.length; c++) {
                sheet.autoSizeColumn(c);
            }

            // Write file
            try (FileOutputStream out = new FileOutputStream(filePath)) {
                wb.write(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }	
	}
	
	
	private static RandomVariable getForwardBondOIS(LIBORModelMonteCarloSimulationModel model, double T, double t) throws CalculationException {
		// P^OIS(T;t) = P^L(T;t)*a_t/a_T
		RandomVariable adjustment_t = getNumeraireOISAdjustmentFactor(model, t);
		RandomVariable adjustment_T = getNumeraireOISAdjustmentFactor(model, T);
		return getForwardBondLibor(model, T, t).mult(adjustment_t).div(adjustment_T);
	}
	
	
	// TODO double check
	private static RandomVariable getNumeraireOISAdjustmentFactor(LIBORModelMonteCarloSimulationModel model, double time) throws CalculationException {
		if(time == 0) return new Scalar(1.0);
		return getForwardBondLibor(model, time, 0).mult(time).add(1.0).mult(model.getModel().getDiscountCurve().getDiscountFactor(time));
	}
	
	
	public static RandomVariable getForwardBondLibor(LIBORModelMonteCarloSimulationModel model, double T, double t) throws CalculationException {
		if(t > T) return new Scalar(0);
		return model.getLIBOR(t, t, T).mult(T - t).add(1.0).invert();
	}
	
	
	private static TreeSet<LocalDate> getPaymentDates(Schedule... schedules) {
	    return Arrays.stream(schedules)
	            .flatMap(schedule -> schedule.getPeriods().stream())
	            .map(Period::getPayment)
	            .collect(Collectors.toCollection(TreeSet::new));
	}
	
	
	/* 	t = 0.0025, T = 5.0
	 * 	double[] volatility = new double[] {0.007591548384595914, 0.011095652816495716, 0.01200036830413657, 0.005, 0.012046448653940332, 0.00957408113098317, 0.014340517874257861, 0.013186967560500052, 0.01260050854197083, 0.006641826359237182, 0.005};
	 *	double displacementParameter =  0.5006196831925163;
	 *	t = 0.00125, T = 5.0
	 * 	double[] volatility = new double[] {0.0076171397477439815, 0.01115875158560738, 0.012315531067898795, 0.005, 0.011846880953767819, 0.009133345869718132, 0.013829599620604734, 0.012534418906836074, 0.01170494492395721, 0.006371646572199523, 0.005}; 
	 *	double displacementParameter =  0.5005569139318871;
	 *  t = 0.5, T = 5.0
	 *	double[] volatility = new double[] {0.00712435456254336, 0.00890703919721209, 0.011966564823574471, 0.0049999999999993505, 0.02119390974353589, 0.005303011727031433, 0.013936900686557618, -0.004543848662533013, 0.014729087694969436, 0.010290294585667078, 0.005};
	 *	double displacementParameter =  0.5007563782560908;
	 */
	public static LIBORModelMonteCarloSimulationModel getModel() throws CloneNotSupportedException, CalculationException {
		int numberOfPaths = LiborMarketModelCalibration.NUMBER_OF_PATHS;
		int numberOfFactors = LiborMarketModelCalibration.NUMBER_OF_FACTORS;
		double liborRateTimeHorizon = LiborMarketModelCalibration.LIBOR_TIME_HORIZON;
		double liborPeriodLength = LiborMarketModelCalibration.LIBOR_PERIOD_LENGTH;
		double simulationTimeStep = LiborMarketModelCalibration.SIMULATION_TIME_STEP;
		double[] volatility = new double[] {0.007917640152172153, 0.008444828514918408, 0.011903094386501008, 0.005000000000000346, 0.021676871474132164, 0.004841279913803049, 0.013640963631216477, -0.00863984710409209, 0.014914764783913457, 0.009332835587252468, 0.005000000000000001};
		double displacementParameter =  0.5006868746387496;
		final AnalyticModel curveModel = LiborMarketModelCalibration.calibrateCurves().orElseThrow().getCalibratedModel();
		return createLIBORMarketModel(numberOfPaths, numberOfFactors, liborRateTimeHorizon, liborPeriodLength, simulationTimeStep, curveModel, volatility, displacementParameter); 
	}

	public static LIBORModelMonteCarloSimulationModel createLIBORMarketModel(
			final int numberOfPaths, final int numberOfFactors, 
			final double liborRateTimeHorzion, final double liborPeriodLength, final double simulationTimeStep, 
			final AnalyticModel curveModel, double[] volatility, double displacementParameter) throws CalculationException {

		final ForwardCurve forwardCurve = curveModel.getForwardCurve(LiborMarketModelCalibration.FORWARD_EUR_6M);
		final DiscountCurve discountCurve = curveModel.getDiscountCurve(LiborMarketModelCalibration.DISCOUNT_EUR_OIS);
		
		/*
		 * Create a simulation time discretization
		 */
		// If simulation time is below libor time, exceptions will be hard to track.;
		TimeDiscretizationFromArray timeDiscretizationFromArray = new TimeDiscretizationFromArray(0.0, (int) (liborRateTimeHorzion / simulationTimeStep), simulationTimeStep);

		/*
		 * Create the libor tenor structure and the initial values
		 */
		TimeDiscretizationFromArray liborPeriodDiscretization = new TimeDiscretizationFromArray(0.0, (int) (liborRateTimeHorzion / liborPeriodLength), liborPeriodLength);

		/*
		 * Create Brownian motions
		 */
		final BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(timeDiscretizationFromArray, numberOfFactors, numberOfPaths, 31415 /* seed */);

		// Create a volatility model: Piecewise constant volatility
		LIBORVolatilityModel volatilityModel = new LIBORVolatilityModelPiecewiseConstant(new RandomVariableFromArrayFactory(), timeDiscretizationFromArray, liborPeriodDiscretization, new TimeDiscretizationFromArray(0.00, 1.0, 2.0, 5.0, 10.0, 20.0, 30.0, 40.0), new TimeDiscretizationFromArray(0.00, 1.0, 2.0, 5.0, 10.0, 20.0, 30.0, 40.0), volatility, false);

		// Create a correlation model
		LIBORCorrelationModel correlationModel = new LIBORCorrelationModelExponentialDecay(timeDiscretizationFromArray, liborPeriodDiscretization, numberOfFactors, 0.05, false);

		// Create a covariance model
		AbstractLIBORCovarianceModelParametric covarianceModelParametric = new LIBORCovarianceModelFromVolatilityAndCorrelation(timeDiscretizationFromArray, liborPeriodDiscretization, volatilityModel, correlationModel);

		// Create blended local volatility model with fixed parameter 0.0 (that is "lognormal").
		AbstractLIBORCovarianceModelParametric covarianceModelBlended = new BlendedLocalVolatilityModel(new RandomVariableFromArrayFactory(), covarianceModelParametric, displacementParameter, false);

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
				new RandomVariableFromArrayFactory(), covarianceModelBlended, calibrationItems, properties);

		final EulerSchemeFromProcessModel process = new EulerSchemeFromProcessModel(liborMarketModel, brownianMotion);

		return new LIBORMonteCarloSimulationFromLIBORModel(process);
	}
}
