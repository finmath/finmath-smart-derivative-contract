package net.finmath.smartcontract.valuation.marketdata.curvecalibration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.products.ForwardRateAgreement;
import net.finmath.marketdata.products.Swap;
import net.finmath.smartcontract.model.MarketDataSet;
import net.finmath.smartcontract.model.MarketDataSetValuesInner;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.time.FloatingpointDate;
import org.junit.jupiter.api.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

/**
 * Tests concerning the bootstrap procedure. Test relies on a valuation framework that can handle intraday data updates
 * (i.e. valuation time must not be forced at model-time 0).
 *
 * @author Luca Bressan
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CalibrationTests {
	private AnalyticModel calibratedModel;
	private CalibrationContext calibrationContext;
	private CalibratedCurves calibratedCurves;
	private CalibratedCurves.CalibrationSpec[] calibrationSpecs;
	private HashSet<CalibrationDataItem> calibrationDataItems;
	private LocalDateTime referenceDateTime;

	@BeforeAll
	void initializeTests() throws IOException, ParserConfigurationException, SAXException, CloneNotSupportedException {
		MarketDataSet marketData;
		SmartDerivativeContractDescriptor product;
		try (
				InputStream marketDataMessageStream = CalibrationTests.class.getClassLoader().getResourceAsStream(
						"net/finmath/smartcontract/valuation/client/nf_md_20230711-123529.json");
				// data obtained from the new livefeed implementation. Contains several fixings.
				InputStream sdcmlStream = CalibrationTests.class.getClassLoader()
						.getResourceAsStream(
								"net.finmath.smartcontract.product.xml/smartderivativecontract.xml") // standard plain swap template
		) {
			final String marketDataMessageContents = new String(
					Objects.requireNonNull(marketDataMessageStream).readAllBytes(), StandardCharsets.UTF_8);
			marketData = new ObjectMapper().registerModule(new JavaTimeModule())
					.readValue(marketDataMessageContents, MarketDataSet.class);
			final String sdcmlFileContents = new String(
					Objects.requireNonNull(sdcmlStream).readAllBytes(),
					StandardCharsets.UTF_8);
			product = SDCXMLParser.parse(sdcmlFileContents);
		}

		final LocalDate referenceDate = marketData.getRequestTimestamp().toLocalDate(); // strip time info from request, model time 0.0 is at midnight, dataset day
		referenceDateTime = marketData.getRequestTimestamp()
				.toLocalDateTime(); // upgrade variable type for decimal time calculations
		final List<CalibrationDataItem.Spec> marketdataItemSpecList = product.getMarketdataItemList();
		final List<MarketDataSetValuesInner> marketDataValues = marketData.getValues();
		calibrationDataItems = new HashSet<>();
		marketdataItemSpecList.forEach(marketDataItemSpec -> marketDataValues.stream()
				.filter(marketDataValue -> marketDataValue.getSymbol().equals(marketDataItemSpec.getKey()))
				.map(marketDataValue -> new CalibrationDataItem(marketDataItemSpec, marketDataValue.getValue(),
						marketDataValue.getDataTimestamp().toLocalDateTime()))
				.forEach(calibrationDataItems::add));
		final CalibrationDataset calibrationDataset = new CalibrationDataset(calibrationDataItems, referenceDateTime);
		calibrationContext = new CalibrationContextImpl(referenceDate, 1E-9);

		/* Recover calibration products spec */
		Stream<CalibrationSpecProvider> calibrationSpecsDataPointStream =
				calibrationDataset.getDataAsCalibrationDataPointStream(
						new CalibrationParserDataItems());
		calibrationSpecs = calibrationSpecsDataPointStream.map(c -> c.getCalibrationSpec(calibrationContext))
				.toArray(CalibratedCurves.CalibrationSpec[]::new);


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

		Double spotRate = calibrationDataItems.stream().filter(i -> i.getSpec().getKey().equals("EUR6MD="))
				.findFirst() //in the test dataset EUR6MD is more recent than EURIBOR6MD (as it is usually the case)
				.orElseThrow().getQuote();
		Assertions.assertEquals(spotRate, calibratedModel.getForwardCurve("forward-EUR-6M").getForward(
						calibratedModel,
						FloatingpointDate.getFloatingPointDateFromDate(
								calibrationContext.getReferenceDate().atStartOfDay(),
								// get decimal representation of the valuation time
								calibrationContext.getReferenceDate().atTime(referenceDateTime.toLocalTime()))), 1E-6,
				"Error exceeded tolerance.");
	}

	@Test
	@DisplayName("The past EURIBOR fixings should be copied to the forward curve.")
	void testEuriborFixingsRetrieval_whenCurveDoesNotMatchDataFails() {
		calibrationDataItems.stream()
				.filter(i -> i.getProductName().equals("Fixing") && i.getCurveName().equals("Euribor6M")).forEach(
						f -> Assertions.assertEquals(0.0, Math.min(
										Math.abs(calibratedModel.getForwardCurve("forward-EUR-6M")
												.getForward(
														calibratedModel, FloatingpointDate.getFloatingPointDateFromDate(
																calibrationContext.getReferenceDate().atStartOfDay(),
																f.getDate().atTime(9, 0, 0))) - f.getQuote()), Math.abs(
												calibratedModel.getForwardCurve("forward-EUR-6M").getForward(
														calibratedModel,
														FloatingpointDate.getFloatingPointDateFromDate(
																calibrationContext.getReferenceDate().atStartOfDay(),
																f.getDate().atTime(13, 0, 0))) - f.getQuote())), 5E-7,
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
										.getForward(calibratedModel, FloatingpointDate.getFloatingPointDateFromDate(
												calibrationContext.getReferenceDate().atStartOfDay(),
												f.getDate().atTime(6, 0, 0))),
								// €STR is fixed at 06:00 UTC (08:00 CET), this test does not account for refixings
								1E-5,
								"Error exceeded tolerance for fixing " + f.getDate() + "."));

	}


}
