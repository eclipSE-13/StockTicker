package org.example;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static void main(String[] args) {
        try {
            String stockName = args.length==0?"^NSEI":args[0];
            StringBuffer response = new StringBuffer();
            URL urlForGetRequest = new URL("https://query1.finance.yahoo.com/v8/finance/chart/"+stockName);
            String readLine;
            HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
            conection.setRequestMethod("GET");
            int responseCode = conection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conection.getInputStream()));

                while ((readLine = in.readLine()) != null) {
                    response.append(readLine);
                }
                in.close();

            } else {
                System.out.println("Invalid Stock Symbol");
                return;
            }
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject jsonObject1= (JSONObject) jsonObject.getJSONObject("chart").getJSONArray("result").get(0);
            float currentPrice = jsonObject1.getJSONObject("meta").getFloat("regularMarketPrice");
            float prePrice = jsonObject1.getJSONObject("meta").getFloat("previousClose");
            float diff = currentPrice - prePrice;
            float percentage = (100*Math.abs(diff))/prePrice;
            String color = diff>=0?ANSI_GREEN:ANSI_RED;
            System.out.println(stockName+"\t\t"+currentPrice+"\t\t"+color+String.format("%.2f",diff)+"\t\t"+String.format("%.2f%%",percentage)+ANSI_RESET);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}