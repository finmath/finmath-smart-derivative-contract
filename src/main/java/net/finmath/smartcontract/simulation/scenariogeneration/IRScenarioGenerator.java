package net.finmath.smartcontract.simulation.scenariogeneration;

import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.gson.Gson;
import net.finmath.smartcontract.simulation.curvecalibration.CalibrationDatapoint;
import org.apache.commons.math3.util.Pair;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

/**
 * Scenario Generator generates IRScenarios from a given json file
 *
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class IRScenarioGenerator {

	/**
	 * Static method which parses a csv file - using jackson csv mapper - and converts it to a list of market data scenarios
	 *
	 * @param fileName Name of the input file.
	 * @param dateFormatter Date formatter to be used.
	 * @return List of <code>IRMarketDataScenario</code>
	 * @throws IOException Thrown if market data file is not found.
	 */
	public static final List<IRMarketDataScenario> getScenariosFromCSVFile(final String fileName, final DateTimeFormatter dateFormatter) throws UnsupportedEncodingException, IOException {
		CsvMapper mapper = new CsvMapper();
		CsvSchema csvSchema = mapper.typedSchemaFor(MarketDataObservationPojo.class).withHeader();
		MappingIterator<MarketDataObservationPojo> iterator = mapper.readerFor(MarketDataObservationPojo.class).with(csvSchema).readValues(new FileReader("C:\\Temp\\marktdata.csv"));
		List<MarketDataObservationPojo> asPojoList = iterator.readAll();

		Map<String, Map<String, Set<MarketDataObservationPojo>> > mapCalibDatapointsPerDate = asPojoList.stream().collect(groupingBy(MarketDataObservationPojo::getScenarioDate,groupingBy(MarketDataObservationPojo::getCurveKey,toSet())));

		final List<IRMarketDataScenario> scenarioList = mapCalibDatapointsPerDate.entrySet().stream()
				.map(
						scenarioData->{
							final Map<String,Set<MarketDataObservationPojo> > rawMap = scenarioData.getValue();
							final Map<String,IRCurveData> map = rawMap.entrySet().stream().collect(Collectors.toMap(e->e.getKey(),e->{
								return new IRCurveData(e.getKey(),e.getValue().stream().map(x->x.toCalibrationDataPoint()).collect(Collectors.toSet()));
							}));
							final String dateString = scenarioData.getKey();
							final LocalDate date = LocalDate.parse(dateString,dateFormatter);
							final LocalDateTime dateTime = date.atTime(17,0);
							final IRMarketDataScenario scenario = new IRMarketDataScenario(map, dateTime);

							return scenario;
						})
				.sorted((scenario1, scenario2) -> scenario1.getDate().compareTo(scenario2.getDate()))
				.collect(Collectors.toList());


		return scenarioList;
	}

	/**
	 * Static method which parses a json file and converts it to a list of market data scenarios
	 *
	 * @param fileName Name of the input file.
	 * @param dateFormatter Date formatter to be used.
	 * @return List of <code>IRMarketDataScenario</code>
	 * @throws IOException Thrown if market data file is not found.
	 * @throws UnsupportedEncodingException Thrown if market data file is in wrong encoding.
	 */
	public static final List<IRMarketDataScenario> getScenariosFromJsonFile(final String fileName, final DateTimeFormatter dateFormatter) throws UnsupportedEncodingException, IOException {
		try {
			final String content = new String(Files.readAllBytes(Paths.get(fileName)), "UTF-8");
			final Gson gson = new Gson();

			final Map<String,Map<String,Map<String,Map<String,Double>>>>  timeSeriesDatamap = gson.fromJson(content, new HashMap<String,Map<String,Map<String,Map<String,Double>>>>().getClass());

			final List<IRMarketDataScenario> scenarioList = timeSeriesDatamap.entrySet().stream()
					.map(
							scenarioData->{
								final Map<String,IRCurveData> map = scenarioData.getValue().entrySet().stream().collect(Collectors.toMap(entry->entry.getKey(), entry->new IRCurveData(entry.getKey(),entry.getValue())));
								final String dateString = scenarioData.getKey();
								final LocalDate date = LocalDate.parse(dateString,dateFormatter);
								final LocalDateTime dateTime = date.atTime(17,0);
								final IRMarketDataScenario scenario = new IRMarketDataScenario(map, dateTime);

								return scenario;
							})
					.sorted((scenario1, scenario2) -> scenario1.getDate().compareTo(scenario2.getDate()))
					.collect(Collectors.toList());

			return scenarioList;
		}
		catch(IOException e) {
			System.out.println("Please provide the market data file " + fileName);
			throw e;
		}
	}
}
