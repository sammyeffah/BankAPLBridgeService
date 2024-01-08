/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.etz.gh.kyc.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 *
 * @author samuel.onwona
 */
public class HttpClient {

    public String sendGet(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuffer response = new StringBuffer();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    public String post(String jsonData, String urlString, String authToken) throws IOException {
//        StringBuilder response = new StringBuilder();
        StringBuffer response = new StringBuffer();

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
//            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + authToken);
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(60000);
            con.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(jsonData);
                wr.flush();
            }

            int responseCode = con.getResponseCode();

//            System.out.println("Response Code : " + responseCode);
            BufferedReader in;
            if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

//            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
//                String inputLine;
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine);
//                }
//            }
            return response.toString();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return response.toString();
    }

    public String postURLEncoded(String endPointURL, String username, String password, String grant_type) throws MalformedURLException, IOException {
        String urlParameters = "username=" + username + "&password=" + password + "&grant_type=" + grant_type;
        System.out.println("URL PARAMETERS>>> " + urlParameters.replace(username, "*****").replace(password, "*****").replace(grant_type, "*****"));

//        StringBuilder response = new StringBuilder();
        StringBuffer response = new StringBuffer();

        try {

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            URL url = new URL(endPointURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            conn.setConnectTimeout(60000);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }
            int responseCode = conn.getResponseCode();

//            System.out.println("Authentication Response Code : " + responseCode);
            BufferedReader in;
            if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
//            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
//                String inputLine;
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine);
//                }
//            }
            return response.toString();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return response.toString();
    }

    public String postNoAuth(String jsonData, String urlString) throws IOException {
        StringBuffer response = new StringBuffer();

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(60000);
            con.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(jsonData);
                wr.flush();
            }

            int responseCode = con.getResponseCode();

            System.out.println("Response Code : " + responseCode);
            BufferedReader in;
            if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return response.toString();
    }

    public String sendPostSSL(String url, String function, String truststore, String keystore, String payload, String tsPassword, String ksPassword) throws MalformedURLException, IOException {
        String resp;
        System.out.println("BASE URL:: " + "172.17.102");
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession sslSession) {
                return hostname.equals("172.17.102");
            }
        });
        System.out.println("KEYSTORE PATH:: " + keystore);
        System.setProperty("javax.net.ssl.keyStore", keystore);
        System.setProperty("javax.net.ssl.keyStorePassword", ksPassword);
        System.setProperty("javax.net.ssl.trustStore", truststore);
        System.setProperty("javax.net.ssl.trustStorePassword", tsPassword);

        URL obj = new URL(url + function);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setDoOutput(true);
        con.setDoInput(true);
        con.setConnectTimeout(60000);
        con.setRequestMethod("POST");

        con.setRequestProperty("Content-Type", "application/json");
//        con.setRequestProperty("SOAPAction", url);
        OutputStream reqStream = con.getOutputStream();
        reqStream.write(payload.getBytes());
        reqStream.flush();
        reqStream.close();

        String response = "";
        String inputLine;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        while ((inputLine = in.readLine()) != null) {
            response = response + inputLine;
        }
        System.out.println("RESPONSE PAYLOAD::: " + response);
        resp = response;
        in.close();

        return resp;
    }

    public String sendGetSSL(String url, String function, String truststore, String keystore, String tsPassword, String ksPassword) throws MalformedURLException, IOException {
        String resp;
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession sslSession) {
                return hostname.equals("172.17.102");
            }
        });
//https://172.17.102:88/api/
        System.setProperty("javax.net.ssl.keyStore", keystore);
        System.setProperty("javax.net.ssl.keyStorePassword", ksPassword);
        System.setProperty("javax.net.ssl.trustStore", truststore);
        System.setProperty("javax.net.ssl.trustStorePassword", tsPassword);

        URL obj = new URL(url + function);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setDoOutput(true);
        con.setDoInput(true);
        con.setConnectTimeout(60000);
        con.setRequestMethod("GET");

        con.setRequestProperty("Content-Type", "application/json");
//        con.setRequestProperty("SOAPAction", url);
        OutputStream reqStream = con.getOutputStream();
//        reqStream.write(payload.getBytes());
        reqStream.flush();
        reqStream.close();

        String response = "";
        String inputLine;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        while ((inputLine = in.readLine()) != null) {
            response = response + inputLine;
        }
        System.out.println("RESPONSE PAYLOAD::: " + response);
        resp = response;
        in.close();

        return resp;
    }

    public String getHttp(String URL, String APIKey, String APISecret) {
//        StringBuilder response = new StringBuilder();
        StringBuffer response = new StringBuffer();
        try {
            URL url = new URL(URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("x-api-key", APIKey);
            con.setRequestProperty("x-api-secret", APISecret);
            con.setConnectTimeout(30000);
            con.setDoOutput(true);

            int responseCode = con.getResponseCode();

            System.out.println("Response Code : " + responseCode);

            BufferedReader in;
            if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            System.out.println("INPUTSTREAM>>> " + in);
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            System.out.println("RESPONSE>>> " + response);

            return response.toString();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return response.toString();
    }

    public static String postData(String url, String data, String SOAPAction) {
        StringBuilder response = new StringBuilder();
        String responseData = null;
        try {

            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setDoOutput(true);

            conn.setRequestProperty("Accept", "text/xml,application/xml");
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);

            conn.setRequestProperty("SOAPAction", SOAPAction);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                System.out.println("Response Code: " + responseCode + ". Connection not successful");
                System.out.println();

                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Output: \n" + response);
                }
            } else {

                System.out.println("Response Code: " + responseCode);

                try (BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    responseData = response.toString();
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }

        return responseData;
    }
    
     public String postGaRural(String URL, String jsonData) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(jsonData);
                wr.flush();
            }

            int responseCode = con.getResponseCode();

            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return response.toString();
    }

}
