package net.simulation;

import com.google.gson.Gson;
import net.finmath.smartcontract.simulation.scenariogeneration.IRCurveData;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataScenario;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DataRetrieverTest {

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	@Ignore
	@Test
	public void testDataImport(){
		try(Scanner scanner = new Scanner(new File("timeseriesdatamap.json"))) {
			final String content = scanner.next();
			final Gson gson = new Gson();
			//Class aClass = new HashMap<String,Map<String,Map<String,Pair<String,Double>>>>().getClass();
			final Map<String,Map<String,Map<String,Map<String,Double>>>>  timeSeriesDatamap = gson.fromJson(content,new HashMap<String,Map<String,Map<String,Map<String,Double>>>>().getClass());

			final List<IRMarketDataScenario> scenarioSet = timeSeriesDatamap.entrySet().stream().map(scenarioData->{
				final Map<String,IRCurveData> map = scenarioData.getValue().entrySet().stream().collect(Collectors.toMap(entry->entry.getKey(), entry->new IRCurveData(entry.getKey(),entry.getValue())));
				final String dateTime = scenarioData.getKey();
				final LocalDateTime time = LocalDate.parse(dateTime,formatter).atTime(17,0);
				final IRMarketDataScenario scenario = new IRMarketDataScenario(map, time);
				return scenario;
			}).sorted((S1, S2) -> S1.getDate().compareTo(S2.getDate())).collect(Collectors.toList());

			Assert.assertNotNull(timeSeriesDatamap);
		}
		catch(final Exception e){
			System.out.println(e);
		}
	}
}
