package org.testvisa;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestsPluginFunctionalTest {

    public static final String BASE_URL = "http://localhost:3080";
    public   HttpClient client;
    public Gson gson;

    @BeforeAll
    static void setup() {

    }

  //  @Test
    public void testEmitEventEndpoint(long timestamp) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        Gson gson = new Gson();
        BloombergFXRateReading reading1 = new BloombergFXRateReading(
                "USD/JPY", "USD", "JPY", 110.45, 108.45, timestamp
        );

        BloombergFXRateReading reading2 = new BloombergFXRateReading(
                "EUR/USD", "EUR", "USD", 1.1111, 1.1101, timestamp
        );

        List<BloombergFXRateReading> readings = new ArrayList<>();
        readings.add(reading1);
        readings.add(reading2);

        String requestBody = gson.toJson(readings);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "/emitEvent"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();




        HttpResponse response =  client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Unexpected HTTP status code");
    }



}