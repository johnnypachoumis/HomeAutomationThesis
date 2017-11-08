package com.smarthome.pachoumis.homeautomation.utilityPackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/*
The HTTP Requests class implements the two out of the four basic HTTP requests: the GET request and
the PUT request. Those are called throughout the app to exchange information with the REST server.
 */
public class HttpRequests {

    /*
    The getRequest method implements the GET HTTP Request. It accepts an url and tries to connect with
    that url via the HTTP protocol. Has a timeout of 2 seconds per request. Uses a inputStreamReader
    to read the response and returns it to the caller. It handles malformed url and i/o exceptions.
     */
    public static String getRequest(String urlText){
        String response = "";
        try {
            URL url = new URL("http://" + Constant.SERVER_IP + ":8080/restServer/resources/" + urlText);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(2000);

            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                response+= Character.toString(current);
                data = isw.read();
            }

            urlConnection.disconnect();
            return response;

        }catch(MalformedURLException e){
            System.out.println("MALFORMED URL ERROR");
            return "SERVER ERROR";

        }catch (IOException e){
            System.out.println("INPUT OUTPUT ERROR");
            return "SERVER ERROR";

        }catch (Exception e){
            System.out.println("SERVER NOT FOUND");
            return "SERVER ERROR";

        }

    }

    /*
    the putRequest method implements the PUT HTTP Request. It accepts an url and a message and tries
    to connect with that url and deliver the message via the HTTP protocol. Has a timeout of 2 seconds
    per request. Uses a outputStreamWriter to send the message to the REST server. Handles protocol,
    malformed url and i/o exceptions.
     */
    public static void putRequest(String urlText, String message){
        try {
            URL url = new URL("http://" + Constant.SERVER_IP + ":8080/restServer/resources/" + urlText);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setConnectTimeout(2000);
            urlConnection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
            urlConnection.connect();

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(message);

            out.close();
            System.out.println("Response: " + urlConnection.getResponseMessage());
            urlConnection.disconnect();

        }catch (ProtocolException e) {
            System.out.println("PROTOCOL ERROR");

        }catch(MalformedURLException e){
            System.out.println("MALFORMED URL ERROR");

        }catch (IOException e){
            System.out.println("INPUT OUTPUT ERROR");

        }

    }

}
