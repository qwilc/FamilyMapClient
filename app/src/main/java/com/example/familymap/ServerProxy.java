package com.example.familymap;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import logger.LoggerConfig;
import com.google.gson.Gson;

import request.LoginRequest;
import request.RegisterRequest;
import result.AllEventsResult;
import result.AllPeopleResult;
import result.EventResult;
import result.LoginRegisterResult;
import result.PersonResult;

public class ServerProxy {
    private final String serverHost;
    private final String serverPort;
    private final Logger logger = Logger.getLogger("ServerProxy");

    public ServerProxy (String serverHost, String serverPort) {
        LoggerConfig.configureLogger(logger, Level.FINE);
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public LoginRegisterResult login (LoginRequest request) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            logger.finer(url.toString());
            HttpURLConnection http = setUpHttpConnection(url, "POST");

            Gson gson = new Gson();
            String reqData = gson.toJson(request);
            logger.finer(reqData);

            writeToRequestBody(reqData, http);

            LoginRegisterResult result = gson.fromJson(getResponseString(http), LoginRegisterResult.class);

            storeSessionData(result);

            return result;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //TODO: Exact same as login except for request parameter and URL. How do I fix this? Parent Request class?
    public LoginRegisterResult register (RegisterRequest request) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            HttpURLConnection http = setUpHttpConnection(url, "POST");

            Gson gson = new Gson();
            String reqData = gson.toJson(request);
            logger.finer(reqData);

            writeToRequestBody(reqData, http);

            LoginRegisterResult result = gson.fromJson(getResponseString(http), LoginRegisterResult.class);

            storeSessionData(result);

            return result;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void storeSessionData(LoginRegisterResult result) {
        if(result.isSuccess()) {
            DataCache.setAuthtoken(result.getAuthtoken());
            DataCache.setUserID(result.getPersonID());
            DataCache.fillData(this);
        }
    }

    public AllPeopleResult getPeople () {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");
            HttpURLConnection http = setUpHttpConnection(url, "GET");

            Gson gson = new Gson();
            return gson.fromJson(getResponseString(http), AllPeopleResult.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO: Unused so I probably can delete
//    public PersonResult getPerson(String personId) {
//        try {
//            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person/" + personId);
//            HttpURLConnection http = setUpHttpConnection(url, "GET");
//
//            Gson gson = new Gson();
//            return gson.fromJson(getResponseString(http), PersonResult.class);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public AllEventsResult getEvents () {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");
            HttpURLConnection http = setUpHttpConnection(url, "GET");

            Gson gson = new Gson();
            return gson.fromJson(getResponseString(http), AllEventsResult.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO: unused and can probably delete
//    public EventResult getEvent(String eventId) {
//        try {
//            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event/" + eventId);
//            HttpURLConnection http = setUpHttpConnection(url, "GET");
//
//            Gson gson = new Gson();
//            return gson.fromJson(getResponseString(http), EventResult.class);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private HttpURLConnection setUpHttpConnection(URL url, String method) throws IOException {
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod(method);
        logger.finest(http.getRequestMethod());

        http.setDoOutput(method.equals("POST"));

        if(DataCache.getAuthtoken() != null) {
            http.addRequestProperty("Authorization", DataCache.getAuthtoken());
        }

        logger.finest("About to call http.connect()");
        http.connect();
        logger.finest("Called http.connect()");
        return http;
    }

    private void writeToRequestBody(String reqData, HttpURLConnection http) throws IOException {
        OutputStream reqBody = http.getOutputStream();
        writeString(reqData, reqBody);
        reqBody.close();
    }

    private String getResponseString(HttpURLConnection http) throws IOException {
        Gson gson = new Gson();
        String respData;
        if(http.getResponseCode() == HttpURLConnection.HTTP_OK) {
            logger.fine("Successful login");
            InputStream respBody = http.getInputStream();
            respData = readString(respBody);
        }
        else {
            logger.fine("ERROR: " + http.getResponseMessage());
            InputStream respBody = http.getErrorStream();
            respData = readString(respBody);
        }
        logger.fine(respData);
        return respData;
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
