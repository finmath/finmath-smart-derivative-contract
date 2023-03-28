package net.finmath.smartcontract.websocket;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

import javax.websocket.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SimpleWebSocketClient {
    private final PublishSubject<String> messageSubject = PublishSubject.create();

    public void connect(String uri, String username, String password) throws Exception {
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().build();
        config.getUserProperties().put("Authorization", getBasicAuthHeader(username, password));
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig config) {
                session.addMessageHandler(new MessageHandler.Whole<String>() {
                    @Override
                    public void onMessage(String message) {
                        messageSubject.onNext(message);
                    }
                });
            }

            @Override
            public void onError(Session session, Throwable throwable) {
                messageSubject.onError(throwable);
            }
        }, config, new URI(uri));
    }

    public Observable<String> getMessageObservable() {
        return messageSubject;
    }

    private String getBasicAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth);
    }


    public static void main(String[] args) throws Exception {
        SimpleWebSocketClient client = new SimpleWebSocketClient();
        client.connect("ws://localhost:8080/feed","user","password");

        client.getMessageObservable().subscribe(System.out::println);

        while(true){

        }

        //Thread.sleep(5000); // Wait for 5 seconds before closing the connection
       // client.close();
    }

    public void close() throws Exception {
        messageSubject.onComplete();
    }
}
