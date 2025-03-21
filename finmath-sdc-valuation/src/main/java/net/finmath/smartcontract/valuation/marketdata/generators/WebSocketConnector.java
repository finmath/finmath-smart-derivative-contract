package net.finmath.smartcontract.valuation.marketdata.generators;//|-----------------------------------------------------------------------------
//|            This source code is provided under the Apache 2.0 license      --
//|  and is provided AS IS with no warranty or guarantee of fit for purpose.  --
//|                See the project's LICENSE.md for details.                  --
//|            Copyright (C) 2018-2021 Refinitiv. All rights reserved.        --
//|-----------------------------------------------------------------------------

import com.neovisionaries.ws.client.*;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.json.JSONObject;

import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.Inet4Address;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/*
 * This example demonstrates authenticating via Refinitiv Data Platform, using an
 * authentication token and a Refinitiv Real-Time service endpoint to retrieve
 * market content.
 *
 * This example maintains a session by proactively renewing the authentication
 * token before expiration.
 *
 * It performs the following steps:
 * - Authenticating via HTTP Post request to Refinitiv Data Platform
 * - Opening a WebSocket to a specified Refinitiv Real-Time Service endpoint (host/port)
 * - Sending Login into the Real-Time Service using the token retrieved
 *   from Refinitiv Data Platform.
 * - Requesting market-price content.
 * - Printing the response content.
 * - Periodically proactively re-authenticating to Refinitiv Data Platform, and
 *   providing the updated token to the Real-Time endpoint before token expiration.
 */
public class WebSocketConnector {
	Properties connectionProperties;
	public JSONObject authJson;

	public String position;
	public String scope = "";
	public String server = "";
	public static WebSocket ws = null;


	public WebSocketConnector(Properties connectionProperties) throws Exception {
		this.connectionProperties = connectionProperties;
		this.position = Inet4Address.getLocalHost().getHostAddress();
	}

	public WebSocket getWebSocket() throws Exception{
		if (ws == null) {
			return initAuthJson().initWebSocketConnection();
		}
		return ws;
	}

	public JSONObject getAuthJson() {
		return authJson;
	}

	public String getPosition(){
		return this.position;
	}

	public WebSocketConnector initAuthJson() {
		try {
			// Connect to Live Market Data Platform and authenticate (using our username and password)
			this.authJson = getAuthenticationInfo(null, connectionProperties.get("AUTHURL").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}



	/**
	 * Connect to the Realtime Service over a WebSocket.
	 */
	public WebSocket initWebSocketConnection() throws IOException, WebSocketException, NoSuchAlgorithmException {
		server = String.format("wss://%s:%s/WebSocket", connectionProperties.get("HOSTNAME"), connectionProperties.get("PORT"));
		System.out.println("Connecting to WebSocket " + server + " ...");
		WebSocketFactory factory = new WebSocketFactory();

		SSLParameters params = new SSLParameters();
		params.setProtocols(new String[]{"TLSv1.2"});

		ProxySettings settings = factory.getProxySettings();
		if(connectionProperties.get("USEPROXY").equals("TRUE")) {
			settings.setHost(connectionProperties.get("PROXYHOST").toString()).setPort(Integer.parseInt(connectionProperties.get("PROXYPORT").toString()));
			settings.setCredentials(connectionProperties.get("PROXYUSER").toString(), connectionProperties.get("PROXYPASS").toString());
		}
		WebSocket webSocket = factory
				.createSocket(server)
				.addProtocol("tr_json2")
				.addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);

		return webSocket;
	}




	/**
	 * Authenticate to Refinitiv Data Platform via an HTTP post request.
	 * Initially authenticates using the specified password. If information from a previous authentication response is provided, it instead authenticates using
	 * the refresh token from that response.
	 * @param previousAuthResponseJson Information from a previous authentication, if available
	 * @param url The HTTP post url
	 * @return A JSONObject containing the authentication information from the response.
	 */
	public JSONObject getAuthenticationInfo(JSONObject previousAuthResponseJson, String url) {
		try
		{
			PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
					.setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create().build())
					.build();

			HttpClient httpclient = HttpClientBuilder.create()
					.setConnectionManager(connectionManager).build();
			HttpPost httppost = new HttpPost(url);
           /* HttpParams httpParams = new BasicHttpParams();

            // Disable redirect
            httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS, false);*/

			// Set request parameters.
			List<NameValuePair> params = new ArrayList<>(2);
			params.add(new BasicNameValuePair("client_id", connectionProperties.get("CLIENTID").toString()));
			params.add(new BasicNameValuePair("username", connectionProperties.get("USER").toString()));

			if (previousAuthResponseJson == null) {
				// First time through, send password.
				params.add(new BasicNameValuePair("grant_type", "password"));
				params.add(new BasicNameValuePair("password", connectionProperties.get("PASSWORD").toString()));
				params.add(new BasicNameValuePair("scope", scope));
				params.add(new BasicNameValuePair("takeExclusiveSignOnControl", "true"));
				System.out.println("Sending authentication request with password to " + url + "...");

			} else {
				// Use the refresh token we got from the last authentication response.
				params.add(new BasicNameValuePair("grant_type", "refresh_token"));
				params.add(new BasicNameValuePair("refresh_token", previousAuthResponseJson.getString("refresh_token")));
				System.out.println("Sending authentication request with refresh token to " + url + "...");
			}

			//httppost.setParams(httpParams);
			httppost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

			//Execute and get the response.
			ClassicHttpResponse response = httpclient.executeOpen(null, httppost, null);

			int statusCode = response.getCode();

			switch ( statusCode ) {
				case HttpStatus.SC_OK:                  // 200
					// Authentication was successful. Deserialize the response and return it.
					JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
//                    System.out.println("Refinitiv Data Platform Authentication succeeded. RECEIVED:");
//                    System.out.println(responseJson.toString(2));
					return responseJson;
				case HttpStatus.SC_MOVED_PERMANENTLY:              // 301
				case HttpStatus.SC_MOVED_TEMPORARILY:              // 302
				case HttpStatus.SC_TEMPORARY_REDIRECT:             // 307
				case 308:                                          // 308 HttpStatus.SC_PERMANENT_REDIRECT
					// Perform URL redirect
//                    System.out.println("Refinitiv Data Platform authentication HTTP code: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
					Header header = response.getFirstHeader("Location");
					if( header != null )
					{
						String newHost = header.getValue();
						if ( newHost != null )
						{
							System.out.println("Perform URL redirect to " + newHost);
							return getAuthenticationInfo(previousAuthResponseJson, newHost);
						}
					}
					return null;
				case HttpStatus.SC_BAD_REQUEST:                    // 400
				case HttpStatus.SC_UNAUTHORIZED:                   // 401
					// Retry with username and password
					System.out.println("Refinitiv Data Platform authentication HTTP code: " + response.getCode() + " " + response.getReasonPhrase());
					if (previousAuthResponseJson != null) {
						System.out.println("Retry with username and password");
						return getAuthenticationInfo(null, connectionProperties.get("AUTHURL").toString());
					}
					return null;
				case HttpStatus.SC_FORBIDDEN:                      // 403
				case HttpStatus.SC_NOT_FOUND:                      // 404
				case HttpStatus.SC_GONE:                           // 410
				case 451:                                          // 451 Unavailable For Legal Reasons
					// Stop retrying with the request
					System.out.println("Refinitiv Data Platform authentication HTTP code: " + response.getCode() + " " + response.getReasonPhrase());
					System.out.println("Stop retrying with the request");
					return null;
				default:
					// Retry the request to Refinitiv Data Platform
					System.out.println("Refinitiv Data Platform authentication HTTP code: " + response.getCode() + " " + response.getReasonPhrase());
					Thread.sleep(5000);
					// CAUTION: This is sample code with infinite retries.
					System.out.println("Retry the request to Refinitiv Data Platform");
					return getAuthenticationInfo(previousAuthResponseJson,connectionProperties.get("AUTHURL").toString());
			}
		} catch (Exception e) {
			System.out.println("Refinitiv Data Platform authentication failure:");
			e.printStackTrace();
			return null;
		}
	}


}
