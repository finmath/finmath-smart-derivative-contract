package net.finmath.smartcontract.valuation.marketdata.curvecalibration;

import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.products.ForwardRateAgreement;
import net.finmath.marketdata.products.Swap;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.valuation.client.ValuationClient;
import net.finmath.time.FloatingpointDate;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static net.finmath.time.FloatingpointDate.getFloatingPointDateFromDate;

/**
 * Tests concerning the bootstrap procedure. Test relies on a valuation framework that can handle intraday data updates
 * (i.e. valuation time must not be forced at model-time 0).
 *
 * @author Luca Bressan
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CalibrationTests {
	private AnalyticModel calibratedModel;
	private CalibrationContext calibrationContext;
	private CalibratedCurves calibratedCurves;
	private CalibratedCurves.CalibrationSpec[] calibrationSpecs;
	private Set<CalibrationDataItem> calibrationDataItems;
	private LocalDateTime referenceDateTime;

	@BeforeAll
	void initializeTests() throws Exception {

		final String marketData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset_with_fixings.xml").readAllBytes(), StandardCharsets.UTF_8);
		final String productData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		SmartDerivativeContractDescriptor productDescriptor = SDCXMLParser.parse(productData);

		final CalibrationDataset calibrationDataset = CalibrationParserDataItems.getCalibrationDataSetFromXML(marketData, productDescriptor.getMarketdataItemList());

		/*
		16.06.2025: Until now the classes CalibrationContext.java and Calibrator.java stored the referenceDate as LocalDate type and when calibrating the interest rate curves the time points of the past fixings were calculated/stored as:
		"referenceDate.atStartOfDay() - quote.dateTime()", whereby quote.dateTime() represents the fixing date of the index AND the fixing time of that day, usually 09:00:00 or 13:00:00 UCT.
		By using referenceDate.atStartOfDay() for the Calibration's reference point , we set the time 0.0 of the calibrated interest rate curves to time 00:00:00 of the market data date.
		As a result, the historical fixings were measured relative to referenceDate.atStartOfDay(), leading to historical fixing times that account for intraday fractions, e.g.
			- the original market data time 05.05.2023 14:35:23 is within the Calibrator stored as 05.05.2023 and will be set by referenceDate.atStartOfDay() to 05.05.2023 00:00:00
			- the relative past fixing time for a historical fixing as of 02.05.2023 13:00:00 will be stored as -(2 days & 11 hours) or approximately -(2.4583/365.0)

		However, the class SwapLeg.java, specifically the corresponding swap schedule (ScheduleFromPeriods.java and Period.java) only uses LocalDates for the reference, fixing, period start, period end, and payment dates of the swap.
		Thus, the relative distance between these days and the reference date are always multiples of whole days (n * 1/365.0).
		As a result, when valuing a swap leg we always request forward rates and discount factors for times equal to whole day multiples. This also holds for periods, where the valuation date lies between the fixing and payment date, and we need to call the forward curve with a negative relative time (t<0) to obtain an already fixed forward rate.
		But since the historical fixings were calculated as the distance between referenceDate.atStartOfDay() and quote.dateTime(), which differ in hours:minutes:seconds, and, therefore, are NOT whole day multiples, we never request one of the stored past fixing times and wrongly interpolate the already fixed forward rate.

		To fix this issue, for now, we only use LocalDate for the Calibrator.java and set the referenceDateTime, i.e. the time 0.0 of the calibrated curves, to the marketDataTime.toLocalDate() and store the historical relative fixing times as:
		"referenceDateTime.toLocalDate() - quote.dateTime.toLocalTime()"
		With this approach the relative distance of the historical fixings are always measured in whole days and align exactly with the swap schedule, which does not care about the intraday fixing time.

		In the next step, we will work on revising the finmath-library and converting the classes ScheduleFromPeriods.java and Period.java to LocalDateTime, thereby also taking intraday discounting into account.
		 */

		calibrationContext = new CalibrationContextImpl(calibrationDataset.getDate(), 1E-9);

		/* Recover calibration products spec */
		Stream<CalibrationSpecProvider> calibrationSpecsDataPointStream =
				calibrationDataset.getDataAsCalibrationDataPointStream(
						new CalibrationParserDataItems());
		calibrationSpecs = calibrationSpecsDataPointStream.map(c -> c.getCalibrationSpec(calibrationContext))
				.toArray(CalibratedCurves.CalibrationSpec[]::new);

		calibrationDataItems = calibrationDataset.getDataPoints();
		referenceDateTime = calibrationDataset.getDate();


		/* Actual calibration to be tested. These instructions are the ones used throughout the rest of the library*/
		calibrationSpecsDataPointStream =
				calibrationDataset.getDataAsCalibrationDataPointStream(new CalibrationParserDataItems());
		List<CalibrationDataItem> fixings = calibrationDataItems.stream()
				.filter(cdi -> cdi.getSpec().getProductName().equals("Fixing") ||
						cdi.getSpec().getProductName().equals("Deposit")
				)
				.toList();
		Calibrator calibrator = new Calibrator(fixings, calibrationContext);
		calibratedModel = calibrator.calibrateModel(
						calibrationSpecsDataPointStream,
						calibrationContext)
				.orElseThrow()
				.getCalibratedModel();

		/* Get the test harness */
		calibratedCurves = calibrator.getCalibratedCurves();

	}

	@Test
	@DisplayName("The calibration Par-Swaps should have zero value.")
	void testSwapBootstrap_whenParSwapIsNotZeroValueFails() {
		Arrays.stream(calibrationSpecs)
				.filter(s -> s.getSymbol().matches("EUR-6M\\d+Y")) // this regex only yields EUR-EURIBOR6M swaps
				.map(s -> (Swap) calibratedCurves.getCalibrationProductForSpec(s))
				.forEach(
						s -> Assertions.assertEquals(0.0,
								s.getValue(calibratedModel),
								1E-6,
								"Error exceeded tolerance."));

	}

	@Test
	@DisplayName("The calibration Par-OIS-Swaps should have zero value.")
	void testOisBootstrap_whenParSwapIsNotZeroValueFails() {
		Arrays.stream(calibrationSpecs)
				.filter(s -> s.getSymbol().matches("EUR-OIS-\\d+[YMD]")) // this regex only yields EUR-ESTR swaps
				.map(s -> (Swap) calibratedCurves.getCalibrationProductForSpec(s))
				.forEach(
						s -> Assertions.assertEquals(0.0,
								s.getValue(calibratedModel),
								1E-6,
								"Error exceeded tolerance.")
				);

	}

	@Test
	@DisplayName("The calibration Par-FRAs should have zero value.")
	void testFraBootstrap_whenParFraIsNotZeroValueFails() {
		Arrays.stream(calibrationSpecs)
				.filter(f -> f.getSymbol().matches("EUR-6M([7-9])?(1\\d)?M")) // this regex only yields EURIBOR FRAs
				.map(f -> (ForwardRateAgreement) calibratedCurves.getCalibrationProductForSpec(f))
				.forEach(
						f -> Assertions.assertEquals(0.0,
								f.getValue(calibratedModel),
								1E-6,
								"Error exceeded tolerance.")
				);
	}


	@Test
	@DisplayName("The spot EURIBOR rate should match the calibration data.")
	void testSpotEuriborRateRetrieval_whenSpotDoesNotMatchDataFails() {

		Double spotRate = calibrationDataItems.stream().filter(i -> i.getSpec().getKey().equals("EUB6DEP6M"))
				.findFirst() //in the test dataset EUR6MD is more recent than EURIBOR6MD (as it is usually the case)
				.orElseThrow().getQuote();
		Assertions.assertEquals(spotRate, calibratedModel.getForwardCurve("forward-EUR-6M").getForward(
						calibratedModel,
						getFloatingPointDateFromDate(
								calibrationContext.getReferenceDate().atStartOfDay(),
								// get decimal representation of the valuation time
								calibrationContext.getReferenceDate().atTime(referenceDateTime.toLocalTime()))), 1E-6,
				"Error exceeded tolerance.");
	}

	@Test
	@DisplayName("The past EURIBOR fixings should be copied to the forward curve.")
	void testEuriborFixingsRetrieval_whenCurveDoesNotMatchDataFails() {

		CalibrationDataItem item = calibrationDataItems.stream().filter(i -> i.getProductName().equals("Fixing") && i.getCurveName().equals("Euribor6M")).findAny().get();

		double quote = calibratedModel.getForwardCurve("forward-EUR-6M")
				.getForward(
						calibratedModel, FloatingpointDate.getFloatingPointDateFromDate(
								calibrationContext.getReferenceDateTime(),
								item.getDateTime()));

		calibrationDataItems.stream()
				.filter(i -> i.getProductName().equals("Fixing") && i.getCurveName().equals("Euribor6M")).forEach(
						f -> Assertions.assertEquals(0.0, Math.min(
										Math.abs(calibratedModel.getForwardCurve("forward-EUR-6M")
												.getForward(
														calibratedModel, FloatingpointDate.getFloatingPointDateFromDate(
																calibrationContext.getReferenceDateTime(),
																f.getDateTime())) - f.getQuote()), Math.abs(
												calibratedModel.getForwardCurve("forward-EUR-6M").getForward(
														calibratedModel,
														FloatingpointDate.getFloatingPointDateFromDate(
																calibrationContext.getReferenceDateTime(),
																f.getDateTime().with(calibrationContext.getReferenceDateTime().toLocalTime()))) - f.getQuote())), 5E-5,
                                /* EURIBOR is fixed at 09:00 UTC (11:00 CEST), or refixed at 13:00 UTC (15:00 CEST)
                                So if testing at first fixing fails we check if the calibrator used a refixing.
                                 */
								"Error exceeded tolerance for fixing" + f.getDate()));

	}

	/*
	This test also checks that the forward-OIS-curve is recovered correctly.
	 */
	@Test
	@DisplayName("The past ESTR fixings should be copied to the discount curve.")
	void testEstrFixingsRetrieval_whenCurveDoesNotMatchDataFails() {
		calibrationDataItems.stream().sorted(Comparator.comparing(CalibrationDataItem::getDate))
				.filter(i -> i.getProductName().equals("Fixing") && i.getCurveName().equals("ESTR"))
				.filter(i -> i.getDate().isBefore(calibrationContext.getReferenceDate()))
				.forEach(
						f -> Assertions.assertEquals(
								f.getQuote(),
								calibratedModel.getForwardCurve("forward-EUR-OIS")
										.getForward(calibratedModel, getFloatingPointDateFromDate(
												calibrationContext.getReferenceDate().atStartOfDay(),
												f.getDate().atTime(13, 0, 0))),
								// €STR is fixed at 06:00 UTC (08:00 CET), this test does not account for refixings
								1E-5,
								"Error exceeded tolerance for fixing " + f.getDate() + "."));

	}

}
