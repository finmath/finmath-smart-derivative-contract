package net.finmath.smartcontract.marketdata.adapters;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import io.reactivex.rxjava3.core.Observable;

import java.io.IOException;

/**
 * Class to be implemented by adapters that emit market data in a reactive manner.
 * @param <MarketDataFormat> the format of the output data
 * @author Luca Bressan
 */
public abstract class LiveFeedAdapter<MarketDataFormat> extends WebSocketAdapter {

    /**
     * Requests that the remote data source closes all channels
     * and logs off the user from the server.
     * @param webSocket the Websocket onto which the request must be sent.
     *                  Implementation of adapters with local sources should
     *                  be able to handle a null parameter.
     */
    public abstract void closeStreamsAndLogoff(WebSocket webSocket);

    /**
     * Get the reactive source as an observable stream.
     * @return the observable stream.
     */
    public abstract Observable<MarketDataFormat> asObservable();

    /**
     * Serializes a dataset to a file.
     * @param writeDir the target folder for the serialization file.
     * @param outputDataset the dataset to be serialized.
     * @param isOvernightFixing must be set to true if the date for overnight fixings
     *                          should be rolled during serialization.
     *                          Adapters implementing this class that perform rolling at
     *                          the emission level should ignore this parameter.
     * @throws IOException if the write operation fails.
     */
    public abstract void writeDataset(String writeDir,
                                      MarketDataFormat outputDataset,
                                      boolean isOvernightFixing) throws IOException;

}
