package stepdefinitions.fxmarket.execute;

import com.google.gson.Gson;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import stepdefinitions.SharedScenarioContext;
import stepdefinitions.TestSettings;
import testvisa.KafkaTestConfig;
import model.RatesRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootTest(classes = KafkaTestConfig.class)
public class BloombergExecuteSteps {

    TestSettings settings = TestSettings.getInstance();
    private String stubEndpoint = settings.getProperty("stub.blomberg_url");
    private HttpResponse<String> response;

    @When("the rates are sent by Bloomberg")
    public void sendRatesData() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

      //  if ((System.getenv("JENKINS_RUN")).equalsIgnoreCase("true")) {
        //    System.out.println("Running on Jenkins.");
          //  stubEndpoint = settings.getProperty("stub.blomberg_url");
      //  }

        RatesRequest ratesRequest = (RatesRequest) SharedScenarioContext.getInstance().get("request");

        if (ratesRequest == null) {
            throw new IllegalStateException("RatesRequest is not set in SharedScenarioContext!");
        }

        String requestBody = new Gson().toJson(ratesRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(stubEndpoint +"/emitEvent"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertNotNull(response, "Response should not be null");
        Assertions.assertEquals(200, response.statusCode(), "Unexpected HTTP status code");
    }
}