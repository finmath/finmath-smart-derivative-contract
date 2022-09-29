package net.finmath.smartcontract.simulation.scenariogeneration;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import net.finmath.smartcontract.simulation.curvecalibration.CalibrationDatapoint;

import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

/**
 * Scenario Generator generates IRScenarios from a given json file
 *
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class IRMarketDataParser {

	/**
	 * Static method which parses a csv file - using jackson csv mapper - and converts it to a list of market data scenarios
	 *
	 * @param fileName Name of the input file.
	 * @return List of <code>IRMarketDataScenario</code>
	 * @throws IOException                  Thrown if market data file is not found.
	 */
	public static List<IRMarketDataSet> getScenariosFromCSVFile(final String fileName) throws IOException {
		CsvMapper mapper = new CsvMapper();
		CsvSchema csvSchema = mapper.typedSchemaFor(MarketDataItem.class).withHeader();
		MappingIterator<MarketDataItem> iterator = mapper.readerFor(MarketDataItem.class).with(csvSchema).readValues(new FileReader(fileName));
		List<MarketDataItem> asPojoList = iterator.readAll();

		Map<String, Map<String, Set<MarketDataItem>>> mapCalibDatapointsPerDate = asPojoList.stream().collect(groupingBy(MarketDataItem::getScenarioDate, groupingBy(MarketDataItem::getCurveKey, toSet())));

		final List<IRMarketDataSet> scenarioList = null;
		/*final List<IRMarketDataSet> scenarioList = mapCalibDatapointsPerDate.entrySet().stream()
				.map(
						scenarioData -> {
							final Map<String, Set<MarketDataItem>> rawMap = scenarioData.getValue();
							final Map<String, IRCurveData> map = rawMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> {
								return new IRCurveData(e.getKey(), e.getValue().stream().map(x -> x.toCalibrationDataPoint()).collect(Collectors.toSet()));
							}));
							//final String productKey = scenarioData
							final String dateTimeStampStr = scenarioData.getKey();
							final LocalDateTime dateTime = parseTimestampString(dateTimeStampStr);
							final IRMarketDataSet scenario = new IRMarketDataSet(map, dateTime);

							return scenario;
						})
				//.sorted((scenario1, scenario2) -> scenario1.getDate().compareTo(scenario2.getDate()))
				.collect(Collectors.toList());*/


		return scenarioList;
	}

	/**
	 * Static method which parses a json file from its file name and converts it to a list of market data scenarios
	 *
	 * @param fileName Name of the input file.
	 * @return List of <code>IRMarketDataScenario</code>
	 * @throws IOException                  File not found
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 */
	public static final List<IRMarketDataSet> getScenariosFromJsonFile(final String fileName) throws IOException {

		final String content;
		try {
			content = new String(IRMarketDataParser.class.getResourceAsStream(fileName).readAllBytes(), StandardCharsets.UTF_8);
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
	public static final List<IRMarketDataSet> getScenariosFromJsonString(final String jsonString) throws UnsupportedEncodingException, IOException {
		final String content;

		content = jsonString;
		return getScenariosFromJsonContent(content);

	}

	/**
	 * Static method which parses a json file from its string content and converts it to a list of market data scenarios
	 *
	 * @return List of <code>IRMarketDataScenario</code>
	 * @throws IOException                  File not found
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 */
	private static final List<IRMarketDataSet> getScenariosFromJsonContent(final String content) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final Map<String, Map<String, Map<String, Map<String, Double>>>> timeSeriesDatamap = mapper.readValue(content, new HashMap<String, Map<String, Map<String, Map<String, Double>>>>().getClass());

		final List<IRMarketDataSet> scenarioList = timeSeriesDatamap.entrySet().stream()
				.map(
						scenarioData -> {
							Set<CalibrationDatapoint> set = scenarioData.getValue().entrySet().stream().map(entry->getCalibrationDataPointSet(entry.getKey(),entry.getValue())).flatMap(Collection::stream).collect(Collectors.toSet());
							final String timeStampStr = scenarioData.getKey();
							final LocalDateTime dateTime = parseTimestampString(timeStampStr);
							final IRMarketDataSet scenario = new IRMarketDataSet(set, dateTime);
							return scenario;
						})
				.sorted((scenario1, scenario2) -> scenario1.getDate().compareTo(scenario2.getDate()))
				.collect(Collectors.toList());

		return scenarioList;
	}

	private static Set<CalibrationDatapoint> getCalibrationDataPointSet(final String curveKey, final Map<String, Map<String, Double>> typeCurveMap) {
		Set<CalibrationDatapoint> datapoints = typeCurveMap.entrySet().stream().flatMap(entry -> entry.getValue().entrySet().stream().map(
				curvePointEntry -> new CalibrationDatapoint(curveKey, entry.getKey(), curvePointEntry.getKey(), curvePointEntry.getValue()))).collect(Collectors.toSet());
		return datapoints;
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

}
