package net.finmath.smartcontract.service.websocket.client;

import java.net.URI;
import java.nio.charset.StandardCharsets;


/**
 * Starter for Websocket Demo
 *
 * @author Peter Kohl-Landgraf
 */


public class DemoStarter {

    public static void main(String[] args) throws Exception {

        String sdcXML = new String(DemoStarter.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/sdc2.xml").readAllBytes(), StandardCharsets.UTF_8);
        WebSocketClientEndpoint client = new WebSocketClientEndpoint(new URI("ws://localhost:443/valuationfeed"), "user1", "password1");
        long timeout = client.getUserSession().getMaxIdleTimeout();
        client.sendTextMessage(sdcXML);
        client.asObservable().subscribe(System.out::println);
        while (client.getUserSession().isOpen()){

        }
        System.out.println();


    }

}
