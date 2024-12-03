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

class TestsPluginFunctionalTest {

    private static final String BASE_URL = "http://localhost:3080";
    private static HttpClient client;
    private static Gson gson;

    @BeforeAll
    static void setup() {
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    @Test
    void testEmitEventEndpoint() throws Exception {
        BloombergFXRateReading reading1 = new BloombergFXRateReading(
                "USD/JPY", "USD", "JPY", 110.45, 108.45, System.currentTimeMillis()
        );

        BloombergFXRateReading reading2 = new BloombergFXRateReading(
                "EUR/USD", "EUR", "USD", 1.1111, 1.1101, System.currentTimeMillis()
        );

        List<BloombergFXRateReading> readings = new ArrayList<>();
        readings.add(reading1);
        readings.add(reading2);

        HttpResponse<String> response = sendPostRequest("/emitEvent", readings);
        assertEquals(200, response.statusCode(), "Unexpected HTTP status code");
    }

    private HttpResponse<String> sendPostRequest(String endpoint, Object body) throws Exception {
        String requestBody = gson.toJson(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }


}