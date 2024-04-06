package net.finmath.smartcontract.valuation.service.websocket.client;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import jakarta.websocket.*;
import net.finmath.smartcontract.model.ExceptionId;
import net.finmath.smartcontract.model.SDCException;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Client End point for demo purposes, currently just printing the text messages on console
 *
 * @author Peter Kohl-Landgraf
 */


@ClientEndpoint
public class WebSocketClientEndpoint extends Endpoint {

	private final PublishSubject<String> messageSubject = PublishSubject.create();

	private URI endpointURI;
	private Session userSession;
	private ClientEndpointConfig config;


	public WebSocketClientEndpoint(URI endpointURI, String user, String password) {
		this.endpointURI = endpointURI;
		this.config = ClientEndpointConfig.Builder.create().build();
		config.getUserProperties().put("Authorization", getBasicAuthHeader(user, password));
	}

	public Session getUserSession() {
		if (this.userSession == null)
			initSession();
		return userSession;
	}

	public Observable<String> asObservable() {
		if (this.userSession == null)
			initSession();
		return messageSubject;
	}

	private void initSession() {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			container.setDefaultMaxBinaryMessageBufferSize(1024 * 1024);
			this.userSession = container.connectToServer(this, config, endpointURI);
		} catch (Exception e) {
			throw new SDCException(ExceptionId.SDC_013, e.getMessage());
		}
	}

	public void sendTextMessage(String message) throws IOException {
		if (this.userSession == null)
			initSession();
		this.userSession.getBasicRemote().sendText(message);
	}

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		System.out.println("Opening websocket");
		session.addMessageHandler((MessageHandler.Whole<String>) message
			-> System.out.println("Received message: " + message));
	}


	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		System.out.println("Closing websocket");
		this.messageSubject.onComplete();
		this.userSession = null;
	}


	private String getBasicAuthHeader(String username, String password) {
		String auth = username + ":" + password;
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		return "Basic " + new String(encodedAuth);
	}

}
