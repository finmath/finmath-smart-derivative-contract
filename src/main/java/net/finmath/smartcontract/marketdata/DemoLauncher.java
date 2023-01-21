package net.finmath.smartcontract.marketdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.neovisionaries.ws.client.WebSocket;

import net.finmath.smartcontract.marketdata.adapters.MarketDataWebSocketAdapter;
import net.finmath.smartcontract.marketdata.util.IRMarketDataItem;
import net.finmath.smartcontract.marketdata.adapters.WebSocketConnector;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;


import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DemoLauncher {


    // https://www.logicbig.com/tutorials/misc/reactive-programming/reactor/programmatically-generate-via-a-consumer-callback.html
    /*
     */

    public static void main(String[] args) throws Exception {
        /* Load RICS from FILE */
        /*final String path = Thread.currentThread().getContextClassLoader().getResource("C:\\Temp\\finmath-smart-derivative-contract-eip\\src\\main\\resources\\rics_spec.txt").getPath();
        CsvMapper mapper = new CsvMapper();
        CsvSchema csvSchema = new CsvMapper().typedSchemaFor(MarketdataItem.class).withHeader().withColumnSeparator('\t');
        final MappingIterator<MarketdataItem> iterator = mapper.readerFor(MarketdataItem.class).with(csvSchema).readValues(new FileReader(path));
        final List<MarketdataItem> mdItemList = iterator.readAll();*
         */
        String sdcXML = new String(DemoLauncher.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
        SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
        List<IRMarketDataItem> mdItemList = sdc.getMarketdataItemList();


        /* Load connection properties*/
        String connectionPropertiesFile = Thread.currentThread().getContextClassLoader().getResource("connect.properties").getPath();
        Properties properties = new Properties();
        properties.load(new FileInputStream(connectionPropertiesFile));

        /* Init Websockect Connection*/
        final WebSocketConnector connector = new WebSocketConnector(properties);
        final WebSocket socket = connector.getWebSocket();

        /* Init Adapter */
        final MarketDataWebSocketAdapter adapter = new MarketDataWebSocketAdapter(connector.getAuthJson(),connector.getPosition(), mdItemList );
        socket.addListener(adapter);
        socket.connect();

        /* When all quotes are retrieved, write to file and disconnect*/
        while(true) {
            long quotesRetrieved = adapter.getMarketDataItems().stream().filter(item -> item.getValue() != null).count();
            if (quotesRetrieved == adapter.getMarketDataItems().size()) {
                String json = writeToJSON(adapter.getMarketDataItems());
                LocalDateTime time = LocalDateTime.now();
                try {
                    Path path = Paths.get("C:\\Temp\\marketdata\\md_" + time.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".json");
                    Files.write(path,json.getBytes());
                    adapter.getMarketDataItems().forEach(item->{
                        item.setValue(null);
                    });
                }
                catch(Exception e){

                }

                /** TODO: Perform Valuation **/
                System.out.println("DONE");
                Thread.sleep(10*1000);
                //break;
            }
        }

       // socket.disconnect();


    }

    //@Todo: Bugfix
    static String  writeToJSON(Set<IRMarketDataItem> itemSet) {


        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        Map<String, Map<String,Map<String, Map<String, Double > > > > nestedMap = new HashMap<>();
        nestedMap.put(date,new HashMap<>());
        for (IRMarketDataItem item : itemSet){
            if ( !nestedMap.get(date).containsKey(item.getCurve()) )
                nestedMap.get(date).put(item.getCurve(),new HashMap<>());
            if ( !nestedMap.get(date).get(item.getCurve()).containsKey(item.getType()))
                nestedMap.get(date).get(item.getCurve()).put(item.getType(),new HashMap<>());
            // if ( !nestedMap.get(item.getCurve()).get(item.getType()).containsKey(item.getTenor()))
            //     nestedMap.get(item.getCurve()).get(item.getType()).put(item.getTenor(),new HashMap<String,Double>());
            nestedMap.get(date).get(item.getCurve()).get(item.getType()).put(item.getTenor(),item.getValue());
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(nestedMap);
            return json;
        }
        catch (Exception e){
            return null;
        }

    }

    static void  writeToCSV(Set<IRMarketDataItem> itemSet){
        String fileName = "C:\\Temp\\marketdata_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))+".csv";
        try {
            CsvSchema csvSchema = new CsvMapper().typedSchemaFor(IRMarketDataItem.class).withHeader().withColumnSeparator(',');
            CsvMapper mapper = new CsvMapper();
            ObjectWriter writer = mapper.writerFor(IRMarketDataItem.class).with(csvSchema);

            File csvOutputFile = new File(fileName);
            writer.writeValues(csvOutputFile).writeAll(itemSet.stream().collect(Collectors.toList()));
        }
        catch(Exception e){
            System.out.println(e);
        }

    }

}
