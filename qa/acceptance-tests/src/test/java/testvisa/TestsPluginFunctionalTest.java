package test.java.testvisa;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeAll;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
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
         client = HttpClient.newHttpClient();
         gson = new GsonBuilder().setPrettyPrinting().create();

        ExchangeRates.Rate rate1 = new ExchangeRates.Rate("USD/JPY", "USD", "JPY", "110.45", "108.45");
        ExchangeRates.Rate rate2 = new ExchangeRates.Rate("EUR/USD", "EUR", "USD", "1.1111", "1.1101");

        ExchangeRates exchangeRates = new ExchangeRates(
                Instant.ofEpochMilli(timestamp).toString(),
                List.of(rate1, rate2)
        );

        String requestBody = gson.toJson(exchangeRates);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "/emitEvent"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Unexpected HTTP status code");
    }



}