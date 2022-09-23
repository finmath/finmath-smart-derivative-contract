package net.finmath.smartcontract.valuation.scenariogeneration;

import com.google.gson.Gson;
import net.finmath.smartcontract.simulation.scenariogeneration.IRCurveData;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataScenario;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Scenario Generator generates IRScenarios from a given json file
 *
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class IRScenarioGenerator {

	/**
	 * Static method which parses a json file from its file name and converts it to a list of market data scenarios
	 *
	 * @param fileName      Name of the input file.
	 * @param dateFormatter Date formatter to be used.
	 * @return List of <code>IRMarketDataScenario</code>
	 * @throws IOException                  File not found
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 */
	public static final List<IRMarketDataScenario> getScenariosFromJsonFile(final String fileName, final DateTimeFormatter dateFormatter) throws UnsupportedEncodingException, IOException {
		final String content;

		try {
			content = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println("Please provide the market data file " + fileName);
			throw e;
		}

		return getScenariosFromJsonContent(content, dateFormatter);

	}

	/**
	 * Static method which parses a json file from its string content and converts it to a list of market data scenarios
	 *
	 * @param jsonString    Content of the json.
	 * @param dateFormatter Date formatter to be used.
	 * @return List of <code>IRMarketDataScenario</code>
	 * @throws IOException                  File not found
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 */
	public static final List<IRMarketDataScenario> getScenariosFromJsonString(final String jsonString, final DateTimeFormatter dateFormatter) throws UnsupportedEncodingException, IOException {
		final String content;

		content = jsonString;
		return getScenariosFromJsonContent(content, dateFormatter);

	}

	/**
	 * Static method which parses a json file from its string content and converts it to a list of market data scenarios
	 *
	 * @param content       Content of the json.
	 * @param dateFormatter Date formatter to be used.
	 * @return List of <code>IRMarketDataScenario</code>
	 * @throws IOException                  File not found
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 */
	private static final List<IRMarketDataScenario> getScenariosFromJsonContent(final String content, final DateTimeFormatter dateFormatter) throws UnsupportedEncodingException, IOException {


		final Gson gson = new Gson();

		final Map<String, Map<String, Map<String, Map<String, Double>>>> timeSeriesDatamap = gson.fromJson(content, new HashMap<String, Map<String, Map<String, Map<String, Double>>>>().getClass());

		final List<IRMarketDataScenario> scenarioList = timeSeriesDatamap.entrySet().stream()
				.map(
						scenarioData -> {
							final Map<String, IRCurveData> map = scenarioData.getValue().entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> new IRCurveData(entry.getKey(), entry.getValue())));
							final String dateString = scenarioData.getKey();
							final LocalDate date = LocalDate.parse(dateString, dateFormatter);
							final LocalDateTime dateTime = date.atTime(17, 0);
							final IRMarketDataScenario scenario = new IRMarketDataScenario(map, dateTime);

							return scenario;
						})
				.sorted((scenario1, scenario2) -> scenario1.getDate().compareTo(scenario2.getDate()))
				.collect(Collectors.toList());

		return scenarioList;

	}
}
