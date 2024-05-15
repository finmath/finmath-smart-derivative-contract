package net.finmath.smartcontract.valuation.marketdata.curvecalibration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.finmath.smartcontract.model.ExceptionId;
import net.finmath.smartcontract.model.MarketDataList;
import net.finmath.smartcontract.model.SDCException;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parses calibration data points and converts it to calibration specs
 */
@SuppressWarnings("java:S125")
public class CalibrationParserDataItems implements CalibrationParser {


	private static final Logger logger = LoggerFactory.getLogger(CalibrationParserDataItems.class);

	@Override
	public Stream<CalibrationSpecProvider> parse(final Stream<CalibrationDataItem> datapoints) {
		return datapoints.map(this::parseDatapointIfPresent).filter(Optional::isPresent).map(Optional::get);
	}

	private Optional<CalibrationSpecProvider> parseDatapointIfPresent(final CalibrationDataItem datapoint) {

		switch (datapoint.getCurveName()) {
			case "ESTR", "EONIA" -> {
				if (datapoint.getProductName().equals("Swap-Rate"))
					return Optional.of(new CalibrationSpecProviderOis(datapoint.getMaturity(), "annual", datapoint.getQuote()));
				else
					return Optional.empty();
			}
			case "Euribor6M" -> {
				if (datapoint.getProductName().equalsIgnoreCase("Swap-Rate"))
					return Optional.of(new CalibrationSpecProviderSwap("6M", "semiannual", datapoint.getMaturity(), datapoint.getQuote()));
				if (datapoint.getProductName().equalsIgnoreCase("Forward-Rate-Agreement")) {
					return Optional.of(new CalibrationSpecProviderFRA("6M", datapoint.getMaturity(), datapoint.getQuote()));
				}
				if (datapoint.getProductName().equalsIgnoreCase("Deposit") || datapoint.getProductName().equalsIgnoreCase("Deposit-Rate"))
					return Optional.of(new CalibrationSpecProviderDeposit("6M", datapoint.getMaturity(), datapoint.getQuote()));
				else
					return Optional.empty();
			}
			case "Euribor1M" -> {
				return Optional.of(new CalibrationSpecProviderSwap("1M", "monthly", datapoint.getMaturity(), datapoint.getQuote()));
			}
			case "Euribor3M" -> {
				return Optional.of(new CalibrationSpecProviderSwap("3M", "quarterly", datapoint.getMaturity(), datapoint.getQuote()));
			}
			default -> {
				logger.warn("Ignored data point.");
				return Optional.empty();
			}
		}
	}


	/**
	 * Static method which parses a json file from its file name and converts it to a list of market data scenarios
	 *
	 * @param fileName Name of the input file.
	 * @return List of <code>IRMarketDataScenario</code>
	 * @throws IOException                  File not found
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 */
	public static List<CalibrationDataset> getScenariosFromJsonFile(final String fileName) throws IOException {

		final String content;
		try (InputStream inputStream = CalibrationParserDataItems.class.getResourceAsStream(fileName)) {
			content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Please provide the market data file: {}.", fileName, e);
			throw e;
		}

		return getScenariosFromJsonContent(content);

	}


	/**
	 * Static method which parses a json file from its string content and converts it to a list of market data scenarios
	 *
	 * @param jsonString Content of the json.
	 * @return List of <code>IRMarketDataScenario</code>
	 * @throws IOException                  File not found
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 */
	public static List<CalibrationDataset> getScenariosFromJsonString(final String jsonString) throws IOException {
		final String content;

		content = jsonString;
		return getScenariosFromJsonContent(content);

	}

	public static  CalibrationDataset getCalibrationDataSetFromXML(final String xmlString, List<CalibrationDataItem.Spec> dataSpecs) {
		//StringReader reader = new StringReader(xmlString);
		//JAXBContext jaxbContext = JAXBContext.newInstance(MarketDataList.class);
		MarketDataList marketDataList =  SDCXMLParser.unmarshalXml(xmlString, MarketDataList.class);

		Set<CalibrationDataItem> calibrationDataItems = new LinkedHashSet<>();

		dataSpecs.stream().forEach(spec-> {
			/* Can be more than one, if we have data points of type fixing*/
			Set<CalibrationDataItem> calibrationDataItemSet = marketDataList.getPoints().stream().filter(marketDataPoint -> marketDataPoint.getId().equals(spec.getKey())).map(point-> new CalibrationDataItem(spec, point.getValue(), point.getTimeStamp())).collect(Collectors.toSet());
			calibrationDataItems.addAll(calibrationDataItemSet);
		});

		if(calibrationDataItems.isEmpty())
			throw new SDCException(ExceptionId.SDC_CALIBRATION_DATA_EMPTY, "No calibration items detected.");

		return new CalibrationDataset(calibrationDataItems, marketDataList.getRequestTimeStamp());
	}

	/**
	 * Static method which parses a csv file - using jackson csv mapper - and converts it to a list of market data scenarios
	 *
	 * @param fileName Name of the input file.
	 * @return List of <code>IRMarketDataScenario</code>
	 * @throws IOException Thrown if market data file is not found.
	 */
	public static List<CalibrationDataset> getScenariosFromCSVFile(final String fileName) throws IOException {
		throw new IOException("to be implemented");
	}


	/**
	 * Static method which parses a json file from its string content and converts it to a list of market data scenarios
	 *
	 * @return List of <code>IRMarketDataScenario</code>
	 * @throws IOException                  File not found
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 */
	private static final List<CalibrationDataset> getScenariosFromJsonContent(final String content) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final Map<String, Map<String, Map<String, Map<String, Map<String, Double>>>>> timeSeriesDatamap = mapper.readValue(content, new LinkedHashMap<String, Map<String, Map<String, Map<String, Map<String, Double>>>>>().getClass());

		return timeSeriesDatamap.entrySet().stream()
				.map(
						scenarioData -> {
							final String timeStampStr = scenarioData.getKey();
							final LocalDateTime dateTime = parseTimestampString(timeStampStr);
							Set<CalibrationDataItem> quotes = scenarioData.getValue().get("Quotes").entrySet().stream().map(entry -> getCalibrationDataItemSet(entry.getKey(), entry.getValue(), dateTime)).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedHashSet::new));
							CalibrationDataset scenario = new CalibrationDataset(quotes, dateTime);
							if (scenarioData.getValue().containsKey("Fixings")) {
								Set<CalibrationDataItem> fixings = scenarioData.getValue().get("Fixings").entrySet().stream().map(entry -> getFixingDataItemSet(entry.getKey(), entry.getValue(), dateTime)).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedHashSet::new));
								scenario = scenario.getClonedFixingsAdded(fixings);
							}

							return scenario;
						})
				.sorted((scenario1, scenario2) -> scenario1.getDate().compareTo(scenario2.getDate()))
				.toList();
	}

	private static LocalDateTime parseTimestampString(String timeStampString) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

		LocalDateTime localDateTime;
		try {
			localDateTime = LocalDateTime.parse(timeStampString, dateTimeFormatter);
		} catch (Exception e) {
			// Fall back to 17:00
			final LocalDate date = LocalDate.parse(timeStampString, dateFormatter);
			localDateTime = date.atTime(17, 0);
		}

		return localDateTime;
	}


	private static Set<CalibrationDataItem> getCalibrationDataItemSet(final String curveKey, final Map<String, Map<String, Double>> typeCurveMap, final LocalDateTime timestamp) {
		return typeCurveMap.entrySet().stream().flatMap(entry -> entry.getValue().entrySet().stream().map(
				curvePointEntry -> {
					String specKey = curveKey + "_" + entry.getKey() + "_" + curvePointEntry.getKey();
					CalibrationDataItem.Spec spec = new CalibrationDataItem.Spec(specKey, curveKey, entry.getKey(), curvePointEntry.getKey());
					return new CalibrationDataItem(spec, curvePointEntry.getValue(), timestamp);
				})).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private static Set<CalibrationDataItem> getFixingDataItemSet(final String curveKey, final Map<String, Map<String, Double>> typeCurveMap, final LocalDateTime timestamp) {

		return typeCurveMap.entrySet().stream().flatMap(entry -> entry.getValue().entrySet().stream().map(
				curvePointEntry -> {
					LocalDate fixingDate = LocalDate.parse(curvePointEntry.getKey(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					String specKey = curveKey + "_" + entry.getKey() + "_" + curvePointEntry.getKey();
					CalibrationDataItem.Spec spec = new CalibrationDataItem.Spec(specKey, curveKey, entry.getKey(), "1D");
					return new CalibrationDataItem(spec, curvePointEntry.getValue(), fixingDate.atStartOfDay());
				})).collect(Collectors.toCollection(LinkedHashSet::new));
	}


}
