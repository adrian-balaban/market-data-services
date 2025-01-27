package stepdefinitions.fxmarket.validate;

import com.google.gson.Gson;
import io.cucumber.java.en.Then;
import model.Rate;
import stepdefinitions.SharedScenarioContext;
import stepdefinitions.TestSettings;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProcessorValidateSteps {

    TestSettings settings = TestSettings.getInstance();
    private final String endpoint = settings.getProperty("processor_url");
    HttpClient client = HttpClient.newHttpClient();

    @Then("Rates available on endpoint")
    public void kafkaSteps() throws Exception {
        List<Rate> rates = (List<Rate>) SharedScenarioContext.getInstance().get("rates");

        Gson gson = new Gson();

        for (Rate expectedRate : rates) {
            String responseBody = sendGetRequest(client, endpoint + expectedRate.getBaseCurrency() + expectedRate.getQuoteCurrency());
            Rate actualRate = gson.fromJson(responseBody, Rate.class);

            assertEquals(expectedRate.getPair(), actualRate.getPair(), "Pair mismatch");
            assertEquals(expectedRate.getBaseCurrency(), actualRate.getBaseCurrency(), "Base currency mismatch");
            assertEquals(expectedRate.getQuoteCurrency(), actualRate.getQuoteCurrency(), "Quote currency mismatch");
            assertEquals(expectedRate.getAsk(), actualRate.getAsk(), "Ask price mismatch");
            assertEquals(expectedRate.getBid(), actualRate.getBid(), "Bid price mismatch");
            assertEquals(expectedRate.getCreatedAt(), actualRate.getCreatedAt(), "CreatedAt timestamp mismatch");
        }

    }

    private String sendGetRequest(HttpClient client, String URL) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
