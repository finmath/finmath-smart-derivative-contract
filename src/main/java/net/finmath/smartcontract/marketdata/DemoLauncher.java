package net.finmath.smartcontract.marketdata;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.neovisionaries.ws.client.WebSocket;
import net.finmath.smartcontract.marketdata.adapters.AdapterMarketdataRetriever;
import net.finmath.smartcontract.marketdata.adapters.MarketdataItem;
import net.finmath.smartcontract.marketdata.connectors.WebSocketConnector;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DemoLauncher {

    /*
    @TODO: FILL src/main/resources/refinitiv_connect.properties with your credentials
     */

    public static void main(String[] args) throws Exception {
    	
    	

        /* Load RICS from FILE */
        final String path = Thread.currentThread().getContextClassLoader().getResource("rics_spec.txt").getPath();
        CsvMapper mapper = new CsvMapper();
        CsvSchema csvSchema = new CsvMapper().typedSchemaFor(MarketdataItem.class).withHeader().withColumnSeparator('\t');
        final MappingIterator<MarketdataItem> iterator = mapper.readerFor(MarketdataItem.class).with(csvSchema).readValues(new FileReader(path));
        final List<MarketdataItem> mdItemList = iterator.readAll();

        /* Load connection properties*/
        String connectionPropertiesFile = Thread.currentThread().getContextClassLoader().getResource("refinitiv_connect.properties").getPath();
        Properties properties = new Properties();
        properties.load(new FileInputStream(connectionPropertiesFile));

        /* Init Websockect Connection*/
        final WebSocketConnector connector = new WebSocketConnector(properties);
        final WebSocket socket = connector.getWebSocket();

        // @Todo:  Check Service Key = "ELEKTRON_DD";
        /* Init Adapter */
        final AdapterMarketdataRetriever adapter = new AdapterMarketdataRetriever(connector.getAuthJson(),connector.getPosition(), mdItemList );
        socket.addListener(adapter);
        socket.connect();

        /* When all quotes are retrieved, write to file and disconnect*/
        while(true) {
            long quotesRetrieved = adapter.getMarketDataItems().stream().filter(item -> item.getValue() != null).count();
            if (quotesRetrieved == adapter.getMarketDataItems().size()) {
                writeToCSV(adapter.getMarketDataItems());
                writeToJSON(adapter.getMarketDataItems());

                /** TODO: Perform Valuation **/

                System.out.println("DONE");
                break;
            }
        }

        socket.disconnect();


    }

    //@Todo: Bugfix
    static void  writeToJSON(Set<MarketdataItem> itemSet) {
        String fileName = "C:\\Temp\\marketdata_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))+".json";

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Map<String, Map<String,Map<String, Map<String, Double > > > > nestedMap = new HashMap<>();
        nestedMap.put(date,new HashMap<>());
        for (MarketdataItem item : itemSet){
            if ( !nestedMap.get(date).containsKey(item.getCurve()) )
                nestedMap.get(date).put(item.getCurve(),new HashMap<>());
            if ( !nestedMap.get(date).get(item.getCurve()).containsKey(item.getType()))
                nestedMap.get(date).get(item.getCurve()).put(item.getType(),new HashMap<>());
           // if ( !nestedMap.get(item.getCurve()).get(item.getType()).containsKey(item.getTenor()))
           //     nestedMap.get(item.getCurve()).get(item.getType()).put(item.getTenor(),new HashMap<String,Double>());
            nestedMap.get(date).get(item.getCurve()).get(item.getType()).put(item.getTenor(),item.getValue());
        }
        try {
          //  Files.write(Paths.get(fileName), json.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e){

        }

    }

    static void  writeToCSV(Set<MarketdataItem> itemSet){
        String fileName = "C:\\Temp\\marketdata_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))+".csv";
        try {
            CsvSchema csvSchema = new CsvMapper().typedSchemaFor(MarketdataItem.class).withHeader().withColumnSeparator(',');
            CsvMapper mapper = new CsvMapper();
            ObjectWriter writer = mapper.writerFor(MarketdataItem.class).with(csvSchema);

            File csvOutputFile = new File(fileName);
            writer.writeValues(csvOutputFile).writeAll(itemSet.stream().collect(Collectors.toList()));
        }
        catch(Exception e){
            System.out.println(e);
        }

    }

}
