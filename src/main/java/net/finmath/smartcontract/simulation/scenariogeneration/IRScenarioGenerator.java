package net.finmath.smartcontract.simulation.scenariogeneration;

import com.google.gson.Gson;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;


/**
 * Scenario Generator generates IRScenarios from a given json file
 *
 * @author Peter Kohl-Landgraf
 */
public class IRScenarioGenerator {

    /**
     * Static method which parses a json file and converts it to a list of market data scenarios
     *
     */
    public final static List<IRMarketDataScenario> getScenariosFromJsonFile(String fileName, DateTimeFormatter dateFormatter) throws Exception {
        String content = new Scanner(new File(fileName)).next();
        Gson gson = new Gson();

        Map<String,Map<String,Map<String,Map<String,Double>>>>  timeSeriesDatamap = gson.fromJson(content,new HashMap<String,Map<String,Map<String,Map<String,Double>>>>().getClass());

        List<IRMarketDataScenario> scenarioList = timeSeriesDatamap.entrySet().stream().map(scenarioData->{
            Map<String,IRCurveData> map = scenarioData.getValue().entrySet().stream().collect(Collectors.toMap(entry->entry.getKey(), entry->new IRCurveData(entry.getKey(),entry.getValue())));
            String dateString = scenarioData.getKey();
            LocalDate date = LocalDate.parse(dateString,dateFormatter);
            LocalDateTime dateTime = date.atTime(17,0);
            IRMarketDataScenario scenario = new IRMarketDataScenario(map, dateTime);
            return scenario;
        }).sorted((S1, S2) -> S1.getDate().compareTo(S2.getDate())).collect(Collectors.toList());

        return scenarioList;

    }

}
