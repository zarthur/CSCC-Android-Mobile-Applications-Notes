package com.arthurneuman.mycontacts.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

public class HttpRequest {
    // set username and password for basic auth with HttpURLConnection
    public HttpRequest(final String username, final String password) {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });
    }

    // process the response from an HttpURLConnection and return a string
    private String processResponse(HttpURLConnection connection) throws IOException {
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ", url: "
                        + connection.getURL());
            }

            InputStreamReader inputStreamReader
                    = new InputStreamReader(connection.getInputStream());
            BufferedReader input = new BufferedReader(inputStreamReader);
            String response = "";
            String line;
            while ((line = input.readLine()) != null) {
                response += line;
            }
            return response;
        }
        finally {
            connection.disconnect();
        }
    }

    // http get
    public String get(URL url) throws IOException{
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        return processResponse(connection);
    }

    // http post
    public String post(URL url, String contentType, String content) throws IOException{
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType);

        OutputStream os = connection.getOutputStream();
        os.write(content.getBytes());
        os.flush();

        return processResponse(connection);
    }
}
