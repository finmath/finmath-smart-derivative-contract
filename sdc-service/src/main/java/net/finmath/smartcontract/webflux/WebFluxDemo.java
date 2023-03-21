package net.finmath.smartcontract.webflux;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Base64;

public class WebFluxDemo {

    public static void main(String[] args) throws Exception {

        //HttpClients.custom().

        WebSocketClient client = new ReactorNettyWebSocketClient();
        URI uri = URI.create("ws://localhost:8080/uppercase");

        String username = "default";
        String password = "9ec07ca2-16d4-4768-8df7-9cca13e5ed6e";

        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);

        // create auth credentials
        String authString = "user:password";
        String base64Creds = Base64.getEncoder().encodeToString(authString.getBytes());
        headers.add("Authorization", "Basic " + base64Creds);
        //headers.setContentType(MediaType.APPLICATION_JSON);


        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1));
        client.execute(uri, headers,  webSocketSession ->
                // send msg
                webSocketSession.send(
                        longFlux.map(i -> webSocketSession.textMessage("vinsguru" + i))
                ).and(
                        // receive message
                        webSocketSession.receive()
                                .map(WebSocketMessage::getPayloadAsText)
                                .doOnNext(System.out::println)
                ).then()
        ).block(Duration.ofSeconds(5));
    }

}
