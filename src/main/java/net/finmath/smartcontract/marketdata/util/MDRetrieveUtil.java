package net.finmath.smartcontract.marketdata.util;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import net.finmath.marketdata.adapters.AdapterMarketdataRetriever;
import net.finmath.marketdata.adapters.MarketdataItem;
import net.finmath.marketdata.connectors.WebSocketConnector;
import org.jfree.util.Log;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MDRetrieveUtil {
	
	
	public static Set<MarketdataItem> getRICS(String rics, String credentials) {
		
		


        /* Load RICS from FILE */
        final String path = Thread.currentThread().getContextClassLoader().getResource(rics).getPath();
        CsvMapper mapper = new CsvMapper();
        CsvSchema csvSchema = new CsvMapper().typedSchemaFor(MarketdataItem.class).withHeader().withColumnSeparator('\t');
        List<MarketdataItem> mdItemList = null;
		try {
			final MappingIterator<MarketdataItem> iterator = mapper.readerFor(MarketdataItem.class).with(csvSchema).readValues(new FileReader(path));
			mdItemList = iterator.readAll();
		} catch (IOException e3) {
			Log.error("Could not read file: " + rics);
			e3.printStackTrace();
		}

        /* Load connection properties*/
        String connectionPropertiesFile = Thread.currentThread().getContextClassLoader().getResource(credentials).getPath();
        Properties properties = new Properties();
        try {
			properties.load(new FileInputStream(connectionPropertiesFile));
		} catch (IOException e2) {
			Log.error("Could not load connectionPropertiesFile");
			e2.printStackTrace();
		}

        /* Init Websockect Connection*/
        WebSocketConnector connector = null;
		WebSocket socket = null;
		try {
			connector = new WebSocketConnector(properties);
			socket = connector.getWebSocket();
		} catch (Exception e1) {
			Log.error("Web Socket Error");
			e1.printStackTrace();
		}

        
        /* Init Adapter */
        final AdapterMarketdataRetriever adapter = new AdapterMarketdataRetriever(connector.getAuthJson(),connector.getPosition(), mdItemList );
        socket.addListener(adapter);
        try {
			socket.connect();
		} catch (WebSocketException e) {
			Log.error("Could not connect to Market Data Source");
			e.printStackTrace();
		}

        /* When all quotes are retrieved, write to file and disconnect*/
        while(true) {
            long quotesRetrieved = adapter.getMarketDataItems().stream().filter(item -> item.getValue() != null).count();
            if (quotesRetrieved == adapter.getMarketDataItems().size()) {
            	// @Todo:  Timeout
            	break;
            	
            }
        }

        socket.disconnect();
        return adapter.getMarketDataItems();
		
	}
	
	
	public static String constructJSON(Set<MarketdataItem> mdItems) {
		
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Map<String, Map<String,Map<String, Map<String, Double > > > > nestedMap = new HashMap<>();
        nestedMap.put(date,new HashMap<>());
        for (MarketdataItem item : mdItems){
            if ( !nestedMap.get(date).containsKey(item.getCurve()) )
                nestedMap.get(date).put(item.getCurve(),new HashMap<>());
            if ( !nestedMap.get(date).get(item.getCurve()).containsKey(item.getType()))
                nestedMap.get(date).get(item.getCurve()).put(item.getType(),new HashMap<>());
                nestedMap.get(date).get(item.getCurve()).get(item.getType()).put(item.getTenor(),item.getValue());
        }
        Gson gson = new Gson();
        String json = gson.toJson(nestedMap);
        return json;
	}

}
