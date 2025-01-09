package stepdefinitions.fxmarket.prepare;

import com.google.gson.Gson;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
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
import stepdefinitions.SharedScenarioContext;
import stepdefinitions.TestSettings;
import testvisa.KafkaTestConfig;

@SpringBootTest(classes = KafkaTestConfig.class)
public class BloombergPrepareSteps {
    TestSettings settings = TestSettings.getInstance();

    private String endpoint = settings.getProperty("stub.blomberg_url");
    private JsonObject requestBody;
    private HttpResponse<String> response;
    String timestamp;

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @When("the following rates data is prepared:")
    public void prepareRatesData(io.cucumber.datatable.DataTable dataTable) {
        requestBody = new JsonObject();
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), java.time.ZoneOffset.UTC);
        timestamp =  DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").format(now);

        SharedScenarioContext.getInstance().set("timestamp",timestamp);

        requestBody.addProperty("timestamp", timestamp);

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
        SharedScenarioContext.getInstance().set("requestBody", requestBody);
        requestBody.add("rates", new Gson().toJsonTree(ratesJsonArray));
    }

    private double generateRandomRate(Random random) {
        return 1.0 + (2.0 - 1.0) * random.nextDouble();
    }



}
