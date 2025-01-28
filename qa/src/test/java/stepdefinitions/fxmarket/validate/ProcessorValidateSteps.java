package stepdefinitions.fxmarket.validate;

import com.google.gson.Gson;
import io.cucumber.java.en.Then;
import model.RatesRequest;
import model.RateResponse;
import model.RatesRequest.Rate;
import stepdefinitions.SharedScenarioContext;
import stepdefinitions.TestSettings;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class ProcessorValidateSteps {

    TestSettings settings = TestSettings.getInstance();
    private final String endpoint = settings.getProperty("processor_url");
    HttpClient client = HttpClient.newHttpClient();

    @Then("Rates successfully updated")
    public void ratesUpdated() throws Exception {
        var expectedTimestamp = SharedScenarioContext.getInstance().get("timestamp");

        RatesRequest expectedRates = (RatesRequest) SharedScenarioContext.getInstance().get("request");

        for (Rate expectedRate : expectedRates.getRates()) {

            String url = endpoint + "/fx/rates/" + expectedRate.getBaseCurrency() + expectedRate.getQuoteCurrency();
            RateResponse actualRateResponse = sendGetRequest(url);

            assertNotNull(actualRateResponse, "Parsed RateResponse is null");

            assertEquals(expectedRate.getPair(), actualRateResponse.getPair(), "Pair mismatch");
            assertEquals(expectedRate.getBaseCurrency(), actualRateResponse.getBaseCurrency(), "Base currency mismatch");
            assertEquals(expectedRate.getQuoteCurrency(), actualRateResponse.getQuoteCurrency(), "Quote currency mismatch");
            assertEquals(expectedRate.getAsk(), actualRateResponse.getAsk(), "Ask price mismatch");
            assertEquals(expectedRate.getBid(), actualRateResponse.getBid(), "Bid price mismatch");
            assertEquals(expectedRate.getBid(), actualRateResponse.getBid(), "Bid price mismatch");
            assertEquals(expectedTimestamp, actualRateResponse.getCreatedAt(), "CreatedAtmismatch");

        }
    }

    private RateResponse sendGetRequest(String URL) throws Exception {
        Gson gson = new Gson();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertNotNull(response, "HTTP Response is null");
        assertEquals(200, response.statusCode(), "Unexpected HTTP status code");

        String responseBody = response.body();
        assertNotNull(responseBody, "Response body is null");

        return gson.fromJson(responseBody, RateResponse.class);
    }
}
