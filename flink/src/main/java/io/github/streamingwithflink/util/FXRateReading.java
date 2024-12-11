package io.github.streamingwithflink.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FXRateReading {

    private String pair;
    private String baseCurrency;
    private String quoteCurrency;
    private double ask;
    private double bid;
    private long timestamp;
    private String url = "https://mds-api.forexfactory.com/instruments?instruments=EUR%2FUSD,GBP%2FUSD,USD%2FJPY,USD%2FCHF,USD%2FCAD,AUD%2FUSD,NZD%2FUSD,GBP%2FJPY,EUR%2FGBP,EUR%2FJPY";

    public static void main(String[] args) {
        HttpClient client = null;
        try {
            client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://mds-api.forexfactory.com/instruments?instruments=EUR%2FUSD,GBP%2FUSD,USD%2FJPY,USD%2FCHF,USD%2FCAD,AUD%2FUSD,NZD%2FUSD,GBP%2FJPY,EUR%2FGBP,EUR%2FJPY"))
                    .GET() // GET is default
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            System.out.println(response.statusCode());
            System.out.println(response.body());

            FXRateReading fxRateReading = new FXRateReading();
            List<FXRateReading> rates = fxRateReading.fetchFXRates();
            rates.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                //client.close();
            }
        }
    }
    public FXRateReading() { }

    public FXRateReading(String pair, String baseCurrency, String quoteCurrency, double ask, double bid, long timestamp) {
        this.pair = pair;
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.ask = ask;
        this.bid = bid;
        this.timestamp = timestamp;
    }

    public List<FXRateReading> fetchFXRates() throws Exception {
        HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
        httpClient.setRequestMethod("GET");

        int responseCode = httpClient.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Gson gson = new Gson();
            return gson.fromJson(response.toString(), new TypeToken<List<FXRateReading>>(){}.getType());
        } else {
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }
    }

    @Override
    public String toString() {
        return "GicaFXRateReading{" +
                "pair='" + pair + '\'' +
                ", baseCurrency='" + baseCurrency + '\'' +
                ", quoteCurrency='" + quoteCurrency + '\'' +
                ", ask=" + ask +
                ", bid=" + bid +
                ", timestamp=" + timestamp +
                '}';
    }

    // Getters and Setters
    public String getPair() { return pair; }
    public void setPair(String pair) { this.pair = pair; }
    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }
    public String getQuoteCurrency() { return quoteCurrency; }
    public void setQuoteCurrency(String quoteCurrency) { this.quoteCurrency = quoteCurrency; }
    public double getAsk() { return ask; }
    public void setAsk(double ask) { this.ask = ask; }
    public double getBid() { return bid; }
    public void setBid(double bid) { this.bid = bid; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}