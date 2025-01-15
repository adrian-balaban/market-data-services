package stepdefinitions.fxmarket.execute;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import stepdefinitions.SharedScenarioContext;
import stepdefinitions.TestSettings;
import testvisa.KafkaTestConfig;

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

@SpringBootTest(classes = KafkaTestConfig.class)
public class BloombergExecuteSteps {

    TestSettings settings = TestSettings.getInstance();

    private String endpoint = settings.getProperty("stub.blomberg_url");
    private HttpResponse<String> response;


    @When("the rates are sent by Bloomberg")
    public void sendRatesData() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers
                        .ofString(SharedScenarioContext.getInstance().get("requestBody").toString()))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertNotNull(response, "Response should not be null");
        Assertions.assertEquals(200, response.statusCode(), "Unexpected HTTP status code");

    }


}
