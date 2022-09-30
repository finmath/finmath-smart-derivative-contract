package net.finmath.smartcontract.marketdata.refinitiv;//|-----------------------------------------------------------------------------
//|            This source code is provided under the Apache 2.0 license      --
//|  and is provided AS IS with no warranty or guarantee of fit for purpose.  --
//|                See the project's LICENSE.md for details.                  --
//|            Copyright (C) 2018-2021 Refinitiv. All rights reserved.        --
//|-----------------------------------------------------------------------------


import com.neovisionaries.ws.client.*;
import org.apache.commons.cli.*;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.System.exit;

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
 *
 *
 * Refnitiv Github Repo: https://github.com/Refinitiv/websocket-api/tree/master/Applications/Examples/RDP/java
 *
 */
public class MarketPriceRdpGwAuthentication {

    public static String server;
    public static String hostname = "eu-west-1-aws-1-sm.optimized-pricing-api.refinitiv.net";
    public static String port = "443";
    public static String user = "";  // PUT YOUR MACHINE ID
    public static String clientid = "";  // PUT YOUR APP_KEY
    public static String position = "";
    public static String appId = "256";
    public static WebSocket ws = null;
    public static String authToken = "";
    public static String password = "";     // PUT YOUR PASS
    public static String newPassword = "";
    public static String authUrl = "https://api.refinitiv.com:443/auth/oauth2/v1/token";
    public static String ric = "EURAB6E4Y=";
    public static String service = "ELEKTRON_DD";   // CHECK WHICH SERVICE IS ENABLED
    public static String scope = "";
    public static JSONObject authJson = null;



    final private static int passwordLengthMask               = 0x1;
    final private static int passwordUppercaseLetterMask      = 0x2;
    final private static int passwordLowercaseLetterMask      = 0x4;
    final private static int passwordDigitMask                = 0x8;
    final private static int passwordSpecialCharacterMask     = 0x10;
    final private static int passwordInvalidCharacterMask     = 0x20;


    // Default password policy
    final private static int passwordLengthMin                = 30;
    final private static int passwordUppercaseLetterMin       = 1;
    final private static int passwordLowercaseLetterMin       = 1;
    final private static int passwordDigitMin                 = 1;
    final private static int passwordSpecialCharacterMin      = 1;
    final private static String passwordSpecialCharacterSet   = "~!@#$%^&*()-_=+[]{}|;:,.<>/?";
    final private static int passwordMinNumberOfCategories    = 3;

    public static void main(String[] args) {



        Options options = new Options();

        options.addOption(Option.builder().longOpt("hostname").required().hasArg().desc("hostname").build());
        options.addOption(Option.builder().longOpt("port").hasArg().desc("port").build());
        options.addOption(Option.builder().longOpt("app_id").hasArg().desc("app_id").build());
        options.addOption(Option.builder().longOpt("user").required().hasArg().desc("user").build());
        options.addOption(Option.builder().longOpt("clientid").required().hasArg().desc("clientid").build());
        options.addOption(Option.builder().longOpt("position").hasArg().desc("position").build());
        options.addOption(Option.builder().longOpt("password").required().hasArg().desc("password").build());
        options.addOption(Option.builder().longOpt("newPassword").hasArg().desc("newPassword").build());
        options.addOption(Option.builder().longOpt("auth_url").hasArg().desc("auth_url").build());
        options.addOption(Option.builder().longOpt("ric").hasArg().desc("ric").build());
        options.addOption(Option.builder().longOpt("service").hasArg().desc("service").build());
        options.addOption(Option.builder().longOpt("scope").hasArg().desc("scope").build());
        options.addOption(Option.builder().longOpt("help").desc("help").build());


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (org.apache.commons.cli.ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("MarketPriceRdpGwAuthentication", options);
            exit(1);
            return;
        }

        if(cmd.hasOption("help"))
        {
            formatter.printHelp("MarketPriceRdpGwAuthentication", options);
            exit(0);
        }
        if(cmd.hasOption("hostname"))
            hostname = cmd.getOptionValue("hostname");
        if(cmd.hasOption("port"))
            port = cmd.getOptionValue("port");
        if(cmd.hasOption("app_id"))
            appId = cmd.getOptionValue("app_id");
        if(cmd.hasOption("user"))
            user = cmd.getOptionValue("user");
        if(cmd.hasOption("clientid"))
            clientid = cmd.getOptionValue("clientid");
        if(cmd.hasOption("password"))
            password = cmd.getOptionValue("password");
        if(cmd.hasOption("auth_url"))
            authUrl = cmd.getOptionValue("auth_url");
        if(cmd.hasOption("position"))
        {
            position = cmd.getOptionValue("position");
        }
        else
        {
            try {
                position = Inet4Address.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                // If localhost isn't defined, use 127.0.0.1.
                position = "127.0.0.1/net";
            }
        }
        if(cmd.hasOption("ric"))
            ric = cmd.getOptionValue("ric");
        if(cmd.hasOption("service"))
            service = cmd.getOptionValue("service");
        if(cmd.hasOption("scope"))
            scope = cmd.getOptionValue("scope");
        if(cmd.hasOption("newPassword")) {
            newPassword = cmd.getOptionValue("newPassword");
            if ((newPassword == null) || (newPassword.length() == 0)) {
                System.out.println("Value of the option newPassword cannot be empty");
                exit(1);
            }

            int result = checkPassword(newPassword);
            if ((result & passwordInvalidCharacterMask) != 0) {
                System.out.println("New password contains invalid symbol(s)");
                System.out.println("Valid symbols are [A-Z][a-z][0-9]" + passwordSpecialCharacterSet);
                exit(0);
            }

            if ((result & passwordLengthMask) != 0) {
                System.out.println("New password length should be at least "
                        + passwordLengthMin
                        + " characters");
                exit(0);
            }
            int countCategories = 0;
            for (int mask = passwordUppercaseLetterMask; mask <= passwordSpecialCharacterMask; mask <<= 1) {
                if ((result & mask) == 0) {
                    countCategories++;
                }
            }
            if (countCategories < passwordMinNumberOfCategories) {
                System.out.println("Password must contain characters belonging to at least three of the following four categories:\n"
                        + "uppercase letters, lowercase letters, digits, and special characters.\n");
                exit(0);
            }
            if (!changePassword(authUrl)) {
                exit(0);
            }
            password = newPassword;
            newPassword = "";
        }

        try {

            // Connect to Refinitiv Data Platform and authenticate (using our username and password)
            authJson = getAuthenticationInfo(null);
            if (authJson == null)
                exit(1);

            // Determine when the access token expires. We will re-authenticate before then.
            int expireTime = Integer.parseInt(authJson.getString("expires_in"));

            server = String.format("wss://%s:%s/WebSocket", hostname, port);
            System.out.println("Connecting to WebSocket " + server + " ...");
            ws = connect();

            while(true) {
                // Continue using current token until 90% of initial time before it expires.
                Thread.sleep(expireTime * 900);  // The value 900 means 90% of expireTime in milliseconds

                // Connect to Refinitiv Data Platform and re-authenticate, using the refresh token provided in the previous response
                authJson = getAuthenticationInfo(authJson);
                if (authJson == null)
                    exit(1);

                // If expiration time returned by refresh request is less then initial expiration time,
                // re-authenticate using password
                int refreshingExpireTime = Integer.parseInt(authJson.getString("expires_in"));
                if (refreshingExpireTime != expireTime) {
                    System.out.println("expire time changed from " + expireTime + " sec to " + refreshingExpireTime +
                            " sec; retry with password");
                    authJson = getAuthenticationInfo(null);
                    if (authJson == null)
                        exit(1);
                    expireTime = Integer.parseInt(authJson.getString("expires_in"));
                }

                // Send the updated access token over the WebSocket.
                sendLoginRequest(ws, authJson.getString("access_token"), false);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebSocketException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static int checkPassword(String pwd) {
        int result = 0;

        if (pwd.length() < passwordLengthMin) {
            result |= passwordLengthMask;
        }

        int countUpper = 0;
        int countLower = 0;
        int countDigit = 0;
        int countSpecial = 0;

        for (int i = 0; i < pwd.length(); i++) {
            char c = pwd.charAt(i);
            StringBuffer currentSymbol = new StringBuffer(1);
            currentSymbol.append(c);

            String charAsString = new String(currentSymbol);
            if ((!charAsString.matches("[A-Za-z0-9]")) && (!passwordSpecialCharacterSet.contains(currentSymbol))) {
                result |= passwordInvalidCharacterMask;
            }

            if (Character.isUpperCase(c)) {
                countUpper++;
            }
            if (Character.isLowerCase(c)) {
                countLower++;
            }
            if (Character.isDigit(c)) {
                countDigit++;
            }
            if (passwordSpecialCharacterSet.contains(currentSymbol))  {
                countSpecial++;
            }
        }

        if (countUpper < passwordUppercaseLetterMin) {
            result |= passwordUppercaseLetterMask;
        }
        if (countLower < passwordLowercaseLetterMin) {
            result |= passwordLowercaseLetterMask;
        }
        if (countDigit < passwordDigitMin) {
            result |= passwordDigitMask;
        }
        if (countSpecial < passwordSpecialCharacterMin) {
            result |= passwordSpecialCharacterMask;
        }

        return result;
    }

    /**
     * Send a request to change password and receive an answer.
     */
    public static boolean changePassword(String authServer) {
        boolean result = false;
        try
        {
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(new SSLContextBuilder().build());

            HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            HttpPost httppost = new HttpPost(authServer);
            HttpParams httpParams = new BasicHttpParams();

            // Disable redirect
            httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS, false);

            // Set request parameters.
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("client_id", clientid));
            params.add(new BasicNameValuePair("username", user));
            params.add(new BasicNameValuePair("grant_type", "password"));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("newPassword", newPassword));
            params.add(new BasicNameValuePair("scope", scope));
            params.add(new BasicNameValuePair("takeExclusiveSignOnControl", "true"));
            System.out.println("Sending password change request to " + authUrl);

            httppost.setParams(httpParams);
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);

            int statusCode = response.getStatusLine().getStatusCode();

            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
            switch ( statusCode ) {
                case HttpStatus.SC_OK:                  // 200
                    // Password change was successful.
                    System.out.println("Password was successfully changed:");
                    result = true;
                    break;

                case HttpStatus.SC_MOVED_PERMANENTLY:              // 301
                case HttpStatus.SC_MOVED_TEMPORARILY:              // 302
                case HttpStatus.SC_TEMPORARY_REDIRECT:             // 307
                case 308:                                          // 308 HttpStatus.SC_PERMANENT_REDIRECT
                    // Perform URL redirect
                    System.out.println("Password change HTTP code: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
                    Header header = response.getFirstHeader("Location");
                    if( header != null )
                    {
                        String newHost = header.getValue();
                        if ( newHost != null )
                        {
                            System.out.println("Perform URL redirect to " + newHost);
                            result = changePassword(newHost);
                        } else {
                            result = false;
                        }
                    }
                    break;
                case HttpStatus.SC_FORBIDDEN:              // 403
                case HttpStatus.SC_NOT_FOUND:              // 404
                case HttpStatus.SC_GONE:                   // 410
                case 451:                                  // 451 Unavailable For Legal Reasons
                    // Error during change password attempt
                    System.out.println("Password change failure\n"
                            + response.getStatusLine().getStatusCode() + " "
                            + response.getStatusLine().getReasonPhrase());
                    System.out.println(responseJson.toString(2));
                    result = false;
                    break;
                default:
                    // Retry the request to the API gateway
                    System.out.println("Changing password response HTTP code: "
                            + response.getStatusLine().getStatusCode() + " "
                            + response.getStatusLine().getReasonPhrase());
                    Thread.sleep(5000);
                    // CAUTION: This is sample code with infinite retries.
                    System.out.println("Retry change request");
                    result = changePassword(authServer);
            }
        } catch (Exception e) {
            System.out.println("Password change failure:");
            e.printStackTrace();
            result = false;
        }
        return result;
    }


    /**
     * Connect to the Realtime Service over a WebSocket.
     */
    public static WebSocket connect() throws IOException, WebSocketException, NoSuchAlgorithmException
    {
        WebSocketFactory factory = new WebSocketFactory();
        boolean verity = factory.getVerifyHostname();

        SSLParameters params = new SSLParameters();
        params.setProtocols(new String[] {"TLSv1.2"});

        /* @Todo: PUT YOUR PROXY SETTINGS HERE */
        ProxySettings settings = factory.getProxySettings();
        settings.setHost("").setPort(8080);
        settings.setCredentials("","");


        WebSocket webSocket = factory
                .createSocket(server)
                .addProtocol("tr_json2")
                .addListener(new WebSocketAdapter() {

                    int count = 0;
                    JSONArray jsonArraySTore = new JSONArray();

                    /**
                     * Called when message received, parse message into JSON for processing
                     */
                    public void onTextMessage(WebSocket websocket, String message) throws JSONException {
                        if(!message.isEmpty()) {
                            System.out.println("RECEIVED:");

                            JSONArray jsonArray = new JSONArray(message);
                            List<Object> list = jsonArraySTore.toList();
                            list.addAll(jsonArray.toList());
                            jsonArraySTore = new JSONArray(list);

                           //System.out.println(jsonArray.toString(2));

                            for (int i = 0; i < jsonArray.length(); ++i)
                                processMessage(websocket, jsonArray.getJSONObject(i));

                            count++;
                            if (jsonArraySTore.toList().size()  > 73) {
                                try {

                                    Path p = Paths.get("C:\\Temp\\md_"+System.currentTimeMillis()+".txt");
                                    Files.createFile(p);
                                    FileChannel.open(p, StandardOpenOption.WRITE).truncate(0).close();
                                    for (int i = 0; i<jsonArraySTore.toList().size();i++){
                                        if (jsonArraySTore.getJSONObject(i).has("Fields") ){
                                            String name = jsonArraySTore.getJSONObject(i).getJSONObject("Key").getString("Name");
                                            JSONObject fields = (JSONObject) jsonArraySTore.getJSONObject(i).get("Fields");
                                            double BID = fields.getDouble("BID");
                                            double ASK = fields.has("ASK") ? fields.getDouble("ASK") : 0.0;
                                            String TS = fields.getString("VALUE_DT1") + "_" + fields.getString("VALUE_TS1");
                                            String output = name + "\t" + BID + "\t" + ASK + "\t" + TS + "\n";
                                            Files.write(p, output.getBytes(), StandardOpenOption.APPEND);
                                            System.out.println(output);
                                        }
                                    }
                                   // JSONArray fields = (JSONArray) jsonArraySTore.getJSONObject(0).get("Fields");
                                   // System.out.println(fields.get(0));
                                    Thread.sleep(60*1000);
                                }
                                catch(Exception e){
                                    System.out.println(e);
                                }

                                //ws.disconnect();
                                //exit(0);
                            }
                           // System.out.println(count);
                        }
                    }

                    /**
                     * Called when handshake is complete and websocket is open, send login
                     */
                    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws JSONException {
                        System.out.println("WebSocket successfully connected!");
                        sendLoginRequest(websocket, authJson.getString("access_token"), true);
                    }


                })
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();

        return webSocket;
    }

    private JSONArray concatArray(
            JSONArray jsArr1,
                                  JSONArray jsArr2) {
        List<Object> list = jsArr1.toList();
        list.addAll(jsArr2.toList());
        return new JSONArray(list);
    }

    /**
     * Authenticate to Refinitiv Data Platform via an HTTP post request.
     * Initially authenticates using the specified password. If information from a previous authentication response is provided, it instead authenticates using
     * the refresh token from that response. Uses authUrl as url.
     * @param previousAuthResponseJson Information from a previous authentication, if available
     * @return A JSONObject containing the authentication information from the response.
     */
    public static JSONObject getAuthenticationInfo(JSONObject previousAuthResponseJson) {
        String url = authUrl;
        return getAuthenticationInfo(previousAuthResponseJson, url);
    }

    /**
     * Authenticate to Refinitiv Data Platform via an HTTP post request.
     * Initially authenticates using the specified password. If information from a previous authentication response is provided, it instead authenticates using
     * the refresh token from that response.
     * @param previousAuthResponseJson Information from a previous authentication, if available
     * @param url The HTTP post url
     * @return A JSONObject containing the authentication information from the response.
     */
    public static JSONObject getAuthenticationInfo(JSONObject previousAuthResponseJson, String url) {
        try
        {
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(new SSLContextBuilder().build());

            HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            HttpPost httppost = new HttpPost(url);
            HttpParams httpParams = new BasicHttpParams();

            // Disable redirect
            httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS, false);

            // Set request parameters.
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("client_id", clientid));
            params.add(new BasicNameValuePair("username", user));

            if (previousAuthResponseJson == null)
            {
                // First time through, send password.
                params.add(new BasicNameValuePair("grant_type", "password"));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("scope", scope));
                params.add(new BasicNameValuePair("takeExclusiveSignOnControl", "true"));
                System.out.println("Sending authentication request with password to " + url + "...");

            }
            else
            {
                // Use the refresh token we got from the last authentication response.
                params.add(new BasicNameValuePair("grant_type", "refresh_token"));
                params.add(new BasicNameValuePair("refresh_token", previousAuthResponseJson.getString("refresh_token")));
                System.out.println("Sending authentication request with refresh token to " + url + "...");
            }

            httppost.setParams(httpParams);
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);

            int statusCode = response.getStatusLine().getStatusCode();

            switch ( statusCode ) {
                case HttpStatus.SC_OK:                  // 200
                    // Authentication was successful. Deserialize the response and return it.
                    JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
                    System.out.println("Refinitiv Data Platform Authentication succeeded. RECEIVED:");
                    System.out.println(responseJson.toString(2));
                    return responseJson;
                case HttpStatus.SC_MOVED_PERMANENTLY:              // 301
                case HttpStatus.SC_MOVED_TEMPORARILY:              // 302
                case HttpStatus.SC_TEMPORARY_REDIRECT:             // 307
                case 308:                                          // 308 HttpStatus.SC_PERMANENT_REDIRECT
                    // Perform URL redirect
                    System.out.println("Refinitiv Data Platform authentication HTTP code: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
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
                    System.out.println("Refinitiv Data Platform authentication HTTP code: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
                    if (previousAuthResponseJson != null)
                    {
                        System.out.println("Retry with username and password");
                        return getAuthenticationInfo(null);
                    }
                    return null;
                case HttpStatus.SC_FORBIDDEN:                      // 403
                case HttpStatus.SC_NOT_FOUND:                      // 404
                case HttpStatus.SC_GONE:                           // 410
                case 451:                                          // 451 Unavailable For Legal Reasons
                    // Stop retrying with the request
                    System.out.println("Refinitiv Data Platform authentication HTTP code: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
                    System.out.println("Stop retrying with the request");
                    return null;
                default:
                    // Retry the request to Refinitiv Data Platform
                    System.out.println("Refinitiv Data Platform authentication HTTP code: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
                    Thread.sleep(5000);
                    // CAUTION: This is sample code with infinite retries.
                    System.out.println("Retry the request to Refinitiv Data Platform");
                    return getAuthenticationInfo(previousAuthResponseJson);
            }
        } catch (Exception e) {
            System.out.println("Refinitiv Data Platform authentication failure:");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate a login request from command line data (or defaults) and send
     * Used for both the initial login and subsequent logins that send updated access tokens.
     * @param websocket Websocket to send the request on
     * @param authToken Token to use
     * @param isFirstLogin Whether this is our first login
     * @throws JSONException
     */
    public static void sendLoginRequest(WebSocket websocket, String authToken, boolean isFirstLogin) throws JSONException {
        String loginJsonString = "{\"ID\":1,\"Domain\":\"Login\",\"Key\":{\"Elements\":{\"ApplicationId\":\"\",\"Position\":\"\",\"AuthenticationToken\":\"\"},\"NameType\":\"AuthnToken\"}}";
        JSONObject loginJson = new JSONObject(loginJsonString);
        loginJson.getJSONObject("Key").getJSONObject("Elements").put("AuthenticationToken", authToken);
        loginJson.getJSONObject("Key").getJSONObject("Elements").put("ApplicationId", appId);
        loginJson.getJSONObject("Key").getJSONObject("Elements").put("Position", position);

        if (!isFirstLogin) // If this isn't our first login, we don't need another refresh for it.
            loginJson.put("Refresh", false);

        websocket.sendText(loginJson.toString());
        System.out.println("SENT:\n" + loginJson.toString(2));
    }

    /**
     * Process a message received over the WebSocket
     * @param websocket Websocket the message was received on
     * @param messageJson Deserialized JSON message
     * @throws JSONException
     */
    public static void processMessage(WebSocket websocket, JSONObject messageJson) throws JSONException {
        String messageType = messageJson.getString("Type");

        switch(messageType)
        {
            case "Refresh":
            case "Status":
                if(messageJson.has("Domain")) {
                    String messageDomain = messageJson.getString("Domain");
                    if(messageDomain.equals("Login")) {
                        // Check message state to see if login succeeded. If so, send item request. Otherwise stop.
                        JSONObject messageState = messageJson.optJSONObject("State");
                        if (messageState != null)
                        {
                            if (!messageState.getString("Stream").equals("Open") || !messageState.getString("Data").equals("Ok"))
                            {
                                System.out.println("Login failed.");
                                exit(1);
                            }

                            // Login succeeded, send item request.
                            sendRequest(websocket);


                        }

                    }
                }
                break;

            case "Ping":
                String pongJsonString = "{\"Type\":\"Pong\"}";
                JSONObject pongJson = new JSONObject(pongJsonString);
                websocket.sendText(pongJsonString);
                System.out.println("SENT:\n" + pongJson.toString(2));
                break;
            default:
                break;
        }
    }

    /**
     * Create and send simple Market Price request
     * @param websocket Websocket to send the message on
     * @throws JSONException
     */
    public static void sendRequest(WebSocket websocket) throws JSONException {
        String requestJsonString;
        //requestJsonString = "{\"ID\":2,\"Key\":{\"Name\":[\"EURAB6E2Y=\",\"EURAB6E3Y=\",\"EUROSTR=\"]},\"Service\":\"" + service + "\"}}"; // \"View\":[\"BID\",\"ASK\"]}";

        //\"Key\":{\"Name\":\"" + ric + "\"

        //String keyString = "\"Key\":{\"Name\":[\"EURAB6E2Y=\",\"EURAB6E3Y=\",\"EUREST21M=\"]}";
        String keyString = "\"Key\":{\"Name\":[\"EUROSTR=\",\"EUROND=\",\"EURSWD=\",\"EUR6MD=\",\"EUR1X7F=\",\"EUR2X8F=\",\"EUR3X9F=\",\"EUR4X10F=\",\"EUR6X12F=\",\"EUR9X15F=\",\"EUR12X18F=\",\"EURAB6E2Y=\",\"EURAB6E3Y=\",\"EURAB6E4Y=\",\"EURAB6E5Y=\",\"EURAB6E6Y=\",\"EURAB6E7Y=\",\"EURAB6E8Y=\",\"EURAB6E9Y=\",\"EURAB6E10Y=\",\"EURAB6E11Y=\",\"EURAB6E12Y=\",\"EURAB6E13Y=\",\"EURAB6E14Y=\",\"EURAB6E15Y=\",\"EURAB6E16Y=\",\"EURAB6E17Y=\",\"EURAB6E18Y=\",\"EURAB6E19Y=\",\"EURAB6E20Y=\",\"EURAB6E21Y=\",\"EURAB6E22Y=\",\"EURAB6E23Y=\",\"EURAB6E24Y=\",\"EURAB6E25Y=\",\"EURAB6E26Y=\",\"EURAB6E27Y=\",\"EURAB6E28Y=\",\"EURAB6E29Y=\",\"EURAB6E30Y=\",\"EURAB6E40Y=\",\"EURAB6E50Y=\"]}";
        //String keyString = "\"Key\":{\"Name\":[\"EURAB6E14Y=\"]}";
        String keyString2 = "\"Key\":{\"Name\":[\"EUROSTR=\",\"EUROND=\",\"EURSWD=\",\"EUR6MD=\",\"EUR1X7F=\",\"EUR2X8F=\",\"EUR3X9F=\",\"EUR4X10F=\",\"EUR6X12F=\",\"EUR9X15F=\",\"EUR12X18F=\",\"EURAB6E2Y=\",\"EURAB6E3Y=\",\"EURAB6E4Y=\",\"EURAB6E5Y=\",\"EURAB6E6Y=\",\"EURAB6E7Y=\",\"EURAB6E8Y=\",\"EURAB6E9Y=\",\"EURAB6E10Y=\",\"EURAB6E11Y=\",\"EURAB6E12Y=\",\"EURAB6E13Y=\",\"EURAB6E14Y=\",\"EURAB6E15Y=\",\"EURAB6E16Y=\",\"EURAB6E17Y=\",\"EURAB6E18Y=\",\"EURAB6E19Y=\",\"EURAB6E20Y=\",\"EURAB6E21Y=\",\"EURAB6E22Y=\",\"EURAB6E23Y=\",\"EURAB6E24Y=\",\"EURAB6E25Y=\",\"EURAB6E26Y=\",\"EURAB6E27Y=\",\"EURAB6E28Y=\",\"EURAB6E29Y=\",\"EURAB6E30Y=\",\"EURAB6E40Y=\",\"EURAB6E50Y=\",\"EURESTSW=\",\"EUREST2W=\",\"EUREST3W=\",\"EUREST1M=\",\"EUREST2M=\",\"EUREST3M=\",\"EUREST4M=\",\"EUREST5M=\",\"EUREST6M=\",\"EUREST7M=\",\"EUREST8M=\",\"EUREST9M=\",\"EUREST1Y=\",\"EUREST15M=\",\"EUREST18M=\",\"EUREST21M=\",\"EUREST2Y=\",\"EUREST3Y=\",\"EUREST4Y=\",\"EUREST5Y=\",\"EUREST6Y=\",\"EUREST7Y=\",\"EUREST8Y=\",\"EUREST9Y=\",\"EUREST10Y=\",\"EUREST11Y=\",\"EUREST12Y=\",\"EUREST15Y=\",\"EUREST20Y=\",\"EUREST25Y=\",\"EUREST30Y=\"]}";

        requestJsonString = "{\"ID\":2,"+keyString2+",\"View\":[\"MID\",\"BID\",\"ASK\",\"VALUE_DT1\",\"VALUE_TS1\"]}";
        JSONObject mpRequestJson = new JSONObject(requestJsonString);
        websocket.sendText(requestJsonString);
        System.out.println("SENT:\n" + mpRequestJson.toString(2));


    }
}
