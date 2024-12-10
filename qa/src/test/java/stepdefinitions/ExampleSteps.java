package stepdefinitions;

import com.google.gson.Gson;
import helpers.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fx.market.kafka.message.FxRateEventProto;
import org.junit.jupiter.api.Assertions;
import testvisa.kafka.FxRateKafkaTest;

public class ExampleSteps {
    private TestContext context;
    private String endpoint;
    private JsonObject requestBody;
    private HttpResponse<String> response;

    public ExampleSteps() {
        this.context = new TestContext();
    }

    @Given("Service is started")
    public void i_have_an_example()  {
        System.out.println("Given step");
        FxRateEventProto proto = null;
    }

    @Given("the following rates data is prepared:")
    public void prepareRatesData(io.cucumber.datatable.DataTable dataTable) {
        // Create the root JSON object
        requestBody = new JsonObject();
        context.set("timestamp",System.currentTimeMillis());
        System.out.println("----------------------------" + context.get("timestamp"));
        requestBody.addProperty("timestamp", context.get("timestamp").toString());

        // Create the "rates" array
        List<Map<String, String>> ratesList = dataTable.asMaps();
        List<JsonObject> ratesJsonArray = new ArrayList<>();

        for (Map<String, String> rate : ratesList) {
            JsonObject rateJson = new JsonObject();
            rateJson.addProperty("pair", rate.get("pair"));
            rateJson.addProperty("baseCurrency", rate.get("baseCurrency"));
            rateJson.addProperty("quoteCurrency", rate.get("quoteCurrency"));
            rateJson.addProperty("ask", rate.get("ask"));
            rateJson.addProperty("bid", rate.get("bid"));
            ratesJsonArray.add(rateJson);
        }

        // Add the "rates" array to the root JSON object
        requestBody.add("rates", new Gson().toJsonTree(ratesJsonArray));
    }

    @When("the rates data is sent to the API")
    public void sendRatesData() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Then("the response status code should be {int}")
    public void verifyStatusCode(int expectedStatusCode) {
        Assertions.assertNotNull(response, "Response should not be null");
        Assertions.assertEquals(expectedStatusCode, response.statusCode(), "Unexpected HTTP status code");
    }

    @When("message in kafka verified")
    public void i_run_the_example() throws Exception {
        new FxRateKafkaTest(context).testReadFromFxRateTopic();
    }

    @Given("the API endpoint is {string}")
    public void setApiEndpoint(String apiEndpoint) {
        this.endpoint = apiEndpoint;
    }

    @Then("I should see the example result")
    public void i_should_see_the_example_result() {
        System.out.println("Then step");
    }
}
