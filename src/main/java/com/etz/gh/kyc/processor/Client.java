/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.etz.gh.kyc.processor;

import com.etz.gh.kyc.util.Config;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author sunkwa-arthur
 */
public class Client {

    private Client() {
        throw new IllegalStateException("Client class");
    }

    static class Data {

        Data(String reference, String amount, String terminalId, String merchantName, String narration) {
            this.reference = reference;
            this.amount = amount;
            this.terminalId = terminalId;
            this.merchantName = merchantName;
            this.narration = narration;
        }

        private String reference;
        private String amount;
        private String terminalId;
        private String merchantName;
        private String narration;

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getTerminalId() {
            return terminalId;
        }

        public void setTerminalId(String terminalId) {
            this.terminalId = terminalId;
        }

        public String getMerchantName() {
            return merchantName;
        }

        public void setMerchantName(String merchantName) {
            this.merchantName = merchantName;
        }

        public String getNarration() {
            return narration;
        }

        public void setNarration(String narration) {
            this.narration = narration;
        }

        @Override
        public String toString() {

            return "{\n"
                    + "  \"reference\": \"" + reference + "\",\n"
                    + "  \"amount\": \"" + amount + "\",\n"
                    + "  \"terminalId\": \"" + terminalId + "\",\n"
                    + "  \"narration\":\"" + narration + "\",\n"
                    + "  \"merchantName\":\"" + merchantName + "\"\n"
                    + "}";
        }

    }

    public static String notifyClientServer(String reference, String amount, String terminalId, String merchantName, String narration) {

        Data data = new Data(reference, amount, terminalId, merchantName, narration);
        String response = post(Config.NOTIFY_URL, data.toString());

        return response;
    }

    private static String post(String URL, String jsonData) {
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

            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            return response.toString();

        } catch (IOException ex) {
            System.err.println("Exception>>> " + ex.getMessage());
        }
        return response.toString();
    }

    public static String sendGet(String url) throws Exception {
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
}
