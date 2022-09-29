package net.finmath.smartcontract.simulation.scenariogeneration;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import net.finmath.smartcontract.service.Application;

import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	 * @throws UnsupportedEncodingException Thrown if market data file has wrong encoding.
	 */
	public static final List<IRMarketDataSet> getScenariosFromCSVFile(final String fileName) throws UnsupportedEncodingException, IOException {
		CsvMapper mapper = new CsvMapper();
		CsvSchema csvSchema = mapper.typedSchemaFor(MarketDataObservationPojo.class).withHeader();
		MappingIterator<MarketDataObservationPojo> iterator = mapper.readerFor(MarketDataObservationPojo.class).with(csvSchema).readValues(new FileReader(fileName));
		List<MarketDataObservationPojo> asPojoList = iterator.readAll();

		Map<String, Map<String, Set<MarketDataObservationPojo>>> mapCalibDatapointsPerDate = asPojoList.stream().collect(groupingBy(MarketDataObservationPojo::getScenarioDate, groupingBy(MarketDataObservationPojo::getCurveKey, toSet())));

		final List<IRMarketDataSet> scenarioList = mapCalibDatapointsPerDate.entrySet().stream()
				.map(
						scenarioData -> {
							final Map<String, Set<MarketDataObservationPojo>> rawMap = scenarioData.getValue();
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
				.collect(Collectors.toList());


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
	public static final List<IRMarketDataSet> getScenariosFromJsonFile(final String fileName) throws UnsupportedEncodingException, IOException {

		final String content;
		try {
			content = Files.readString(Path.of((Application.class.getClassLoader().getResource(fileName).toURI()).getPath()), StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println("Please provide the market data file " + fileName);
			throw e;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
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
							final Map<String, IRCurveData> map = scenarioData.getValue().entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> new IRCurveData(entry.getKey(), entry.getValue())));
							final String timeStampStr = scenarioData.getKey();
							final LocalDateTime dateTime = parseTimestampString(timeStampStr);
							final IRMarketDataSet scenario = new IRMarketDataSet(map, dateTime);

							return scenario;
						})
				.sorted((scenario1, scenario2) -> scenario1.getDate().compareTo(scenario2.getDate()))
				.collect(Collectors.toList());

		return scenarioList;
	}

	private static LocalDateTime parseTimestampString(String timeStampString) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

		LocalDateTime localDateTime;
		try{
			localDateTime = LocalDateTime.parse(timeStampString, dateTimeFormatter);
		}
		catch (DateTimeParseException e) {
			// Fall back to 17:00
			final LocalDate date = LocalDate.parse(timeStampString, dateFormatter);
			localDateTime = date.atTime(17, 0);
		}

		return localDateTime;
	}

}
