package org.testvisa;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
        BloombergFXRateReading rateReading = createSampleFXRateReading();
        HttpResponse<String> response = sendPostRequest("/emitEvent", rateReading);
        assertEquals(200, response.statusCode(), "Unexpected HTTP status code");
    }

    //temporary solution
    private BloombergFXRateReading createSampleFXRateReading() {
        return new BloombergFXRateReading(
                "USD/JPY",
                "USD",
                "JPY",
                110.45,
                108.45,
                System.currentTimeMillis()
        );
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