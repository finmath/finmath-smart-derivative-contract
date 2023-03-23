package net.finmath.smartcontract.marketdata.curvecalibration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.finmath.smartcontract.demo.VisualiserSDC;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parses calibration data points and converts it to calibration specs
 */
public class CalibrationParserDataItems implements CalibrationParser {


	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CalibrationParserDataItems.class);
	private final Set<String> maturityGrid = new HashSet<>(Arrays.asList("1W", "2W", "3W", "1M", "2M", "3M", "4M", "5M", "6M", "7M", "8M", "9M", "10M", "11M", "12M", "15M", "18M", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", "40Y", "50Y"));

	@Override
	public Stream<CalibrationSpecProvider> parse(final Stream<CalibrationDataItem> datapoints) {
		return datapoints.map(this::parseDatapointIfPresent).filter(Optional::isPresent).map(Optional::get);
	}

	private Optional<CalibrationSpecProvider> parseDatapointIfPresent(final CalibrationDataItem datapoint) {
		if (!maturityGrid.contains(datapoint.getMaturity())) {
			return Optional.empty();
		}
		switch (datapoint.getCurveName()) {
			case "ESTR":
			case "EONIA":
				if (datapoint.getProductName().equals("Swap-Rate"))
					return Optional.of(new CalibrationSpecProviderOis(datapoint.getMaturity(), "annual", datapoint.getQuote() ));
				else
					return Optional.empty();
			case "Euribor6M":
				if (datapoint.getProductName().equalsIgnoreCase("Swap-Rate"))
					return Optional.of(new CalibrationSpecProviderSwap("6M", "semiannual", datapoint.getMaturity(), datapoint.getQuote() ));
				if (datapoint.getProductName().equalsIgnoreCase("Forward-Rate-Agreement")) {
					return Optional.of(new CalibrationSpecProviderFRA("6M", datapoint.getMaturity(), datapoint.getQuote()));
				}
				if (datapoint.getProductName().equalsIgnoreCase("Deposit") || datapoint.getProductName().equalsIgnoreCase("Deposit-Rate"))
					return Optional.of(new CalibrationSpecProviderDeposit("6M", datapoint.getMaturity(), datapoint.getQuote() ));
				else
					return Optional.empty();
			case "Euribor1M":
				return Optional.of(new CalibrationSpecProviderSwap("1M", "monthly", datapoint.getMaturity(), datapoint.getQuote() ));
			case "Euribor3M":
				return Optional.of(new CalibrationSpecProviderSwap("3M", "quarterly", datapoint.getMaturity(), datapoint.getQuote() ));
			default:
				logger.warn("Ignored data point.");
				return Optional.empty();
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
	public static final List<CalibrationDataset> getScenariosFromJsonFile(final String fileName) throws IOException {

		final String content;

		File startPoint = new File(VisualiserSDC.class.getResource(VisualiserSDC.class.getSimpleName()+".class").getPath());
		File file = new File (startPoint.getParentFile().getParentFile().getParentFile().getParentFile().getParent()+File.separator+fileName);
		try {
			content = new String((new FileInputStream(file)).readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println("Please provide the market data file " + fileName);
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
	public static final List<CalibrationDataset> getScenariosFromJsonString(final String jsonString) throws UnsupportedEncodingException, IOException {
		final String content;

		content = jsonString;
		return getScenariosFromJsonContent(content);

	}

	/**
	 * Static method which parses a csv file - using jackson csv mapper - and converts it to a list of market data scenarios
	 *
	 * @param fileName Name of the input file.
	 * @return List of <code>IRMarketDataScenario</code>
	 * @throws IOException                  Thrown if market data file is not found.
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
		final Map<String, Map<String, Map<String, Map<String, Map<String, Double>>>>> timeSeriesDatamap = mapper.readValue(content, new LinkedHashMap<String,  Map<String, Map<String, Map<String, Map<String, Double>>>>>().getClass());

		final List<CalibrationDataset> scenarioList = timeSeriesDatamap.entrySet().stream()
				.map(
						scenarioData -> {
							final String timeStampStr = scenarioData.getKey();
							final LocalDateTime dateTime = parseTimestampString(timeStampStr);
							Set<CalibrationDataItem> quotes = scenarioData.getValue().get("Quotes").entrySet().stream().map(entry->getCalibrationDataItemSet(entry.getKey(),entry.getValue(),dateTime)).flatMap(Collection::stream).collect(Collectors.toCollection( LinkedHashSet::new ));
							CalibrationDataset scenario = new CalibrationDataset(quotes, dateTime);
							if (scenarioData.getValue().containsKey("Fixings") ) {
								Set<CalibrationDataItem> fixings = scenarioData.getValue().get("Fixings").entrySet().stream().map(entry -> getFixingDataItemSet(entry.getKey(), entry.getValue(), dateTime)).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedHashSet::new));
								scenario = scenario.getClonedFixingsAdded(fixings);
							}

							return scenario;
						})
				.sorted((scenario1, scenario2) -> scenario1.getDate().compareTo(scenario2.getDate()))
				.collect(Collectors.toList());

		return scenarioList;

		// Luca 23/03/2023: this is how to handle the new(?) JSON format eg. timeseriesdatamap.json
		/*final ObjectMapper mapper = new ObjectMapper();
		final Map<String, Map<String, Map<String, Map<String, Double>>>> timeSeriesMap = mapper.readValue(content, new LinkedHashMap<String, Map<String, Map<String, Map<String, Double>>>>().getClass());
		final List<CalibrationDataset> scenarioList = new LinkedList<>();
		for (var scenarioData: timeSeriesMap.entrySet()){
			final String timestampKey = scenarioData.getKey();
			final LocalDateTime dateTime = parseTimestampString(timestampKey);
			final Set<CalibrationDataItem> calibrationDataItemSet = new LinkedHashSet<>();
			for(var curveData: scenarioData.getValue().entrySet()){
				final String curveKey = curveData.getKey();
				for(var productData: curveData.getValue().entrySet()){
					final String productKey = productData.getKey();
					for(var quoteData: productData.getValue().entrySet()){
						final String quoteKey = quoteData.getKey();
						final Double quoteValue = quoteData.getValue();
						final String uniqueKey =  timestampKey+curveKey+productKey+quoteKey+quoteValue;
						final CalibrationDataItem.Spec calibrationDataItemSpec = new CalibrationDataItem.Spec(uniqueKey,curveKey,productKey,quoteKey);
						final CalibrationDataItem calibrationDataItem = new CalibrationDataItem(calibrationDataItemSpec,quoteValue, dateTime);
						calibrationDataItemSet.add(calibrationDataItem);
					}
				}
			}
			scenarioList.add(new CalibrationDataset(calibrationDataItemSet,dateTime));
		}
		return scenarioList;*/
	}

	private static LocalDateTime parseTimestampString(String timeStampString) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

		LocalDateTime localDateTime;
		try{
			localDateTime = LocalDateTime.parse(timeStampString, dateTimeFormatter);
		}
		catch (Exception e) {
			// Fall back to 17:00
			final LocalDate date = LocalDate.parse(timeStampString, dateFormatter);
			localDateTime = date.atTime(17, 0);
		}

		return localDateTime;
	}


	private static Set<CalibrationDataItem> getCalibrationDataItemSet(final String curveKey, final Map<String, Map<String, Double>> typeCurveMap, final LocalDateTime timestamp) {
		Set<CalibrationDataItem> datapoints = typeCurveMap.entrySet().stream().flatMap(entry -> entry.getValue().entrySet().stream().map(
				curvePointEntry -> {
					String specKey = curveKey + "_" + entry.getKey() + "_" + curvePointEntry.getKey();
					CalibrationDataItem.Spec spec = new CalibrationDataItem.Spec(specKey,curveKey, entry.getKey(), curvePointEntry.getKey());
					CalibrationDataItem dataItem = new CalibrationDataItem(spec, curvePointEntry.getValue(),timestamp);
					return dataItem;
				})).collect(Collectors.toCollection( LinkedHashSet::new ));
		return datapoints;
	}

	private static Set<CalibrationDataItem> getFixingDataItemSet(final String curveKey, final Map<String, Map<String, Double>> typeCurveMap, final LocalDateTime timestamp) {

		Set<CalibrationDataItem> datapoints = typeCurveMap.entrySet().stream().flatMap(entry -> entry.getValue().entrySet().stream().map(
				curvePointEntry -> {
					LocalDate fixingDate = LocalDate.parse(curvePointEntry.getKey(),DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					String specKey = curveKey + "_" + entry.getKey() + "_" + curvePointEntry.getKey();
					CalibrationDataItem.Spec spec = new CalibrationDataItem.Spec(specKey,curveKey, entry.getKey(), "0D");
					CalibrationDataItem dataItem = new CalibrationDataItem(spec, curvePointEntry.getValue(),fixingDate.atStartOfDay());
					return dataItem;
				})).collect(Collectors.toCollection( LinkedHashSet::new ));
		return datapoints;
	}


}
