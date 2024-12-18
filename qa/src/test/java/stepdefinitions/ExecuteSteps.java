package stepdefinitions;

import com.google.gson.Gson;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fx.market.kafka.message.FxRateEventProto;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import testvisa.KafkaTestConfig;

@SpringBootTest(classes = KafkaTestConfig.class)
public class ExecuteSteps {

    private String endpoint = "http://localhost:3080/emitEvent";;
    private JsonObject requestBody;
    private HttpResponse<String> response;
    String timestamp;

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Given("Service is started")
    public void i_have_an_example()  {
        System.out.println("Given step");
        FxRateEventProto proto = null;
    }

    @When("the following rates data is prepared:")
    public void prepareRatesData(io.cucumber.datatable.DataTable dataTable) {
        // Create the root JSON object
        requestBody = new JsonObject();

        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), java.time.ZoneOffset.UTC);
         timestamp =  DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").format(now);

        SharedScenarioContext.getInstance().set("timestamp",timestamp);

        requestBody.addProperty("timestamp", timestamp);

        // Create the "rates" array
        List<Map<String, String>> ratesData = dataTable.asMaps();
        List<JsonObject> ratesJsonArray = new ArrayList<>();

        Random random = new Random();
        DecimalFormat decimalFormat = new DecimalFormat("#.####", DecimalFormatSymbols.getInstance(Locale.US)); // Локаль с точкой

        List<Map<String, String>> updatedRatesData = new ArrayList<>();
        for (Map<String, String> row : ratesData) {
            Map<String, String> mutableRow = new HashMap<>(row); // Создаём изменяемую копию
            mutableRow.put("ask", mutableRow.get("ask").equals("*") ? decimalFormat.format(generateRandomRate(random)) : mutableRow.get("ask"));
            mutableRow.put("bid", mutableRow.get("bid").equals("*") ? decimalFormat.format(generateRandomRate(random)) : mutableRow.get("bid"));
            updatedRatesData.add(mutableRow);
        }

        for (Map<String, String> rate : updatedRatesData) {
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

    private double generateRandomRate(Random random) {
        return 1.0 + (2.0 - 1.0) * random.nextDouble();
    }

    @When("the rates are sent by Bloomberg")
    public void sendRatesData() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertNotNull(response, "Response should not be null");
        Assertions.assertEquals(200, response.statusCode(), "Unexpected HTTP status code");

    }

    @Then("the response status code should be {int}")
    public void verifyStatusCode(int expectedStatusCode) {
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
