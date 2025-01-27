package stepdefinitions.fxmarket.prepare;

import io.cucumber.java.en.When;

import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import model.Rate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import stepdefinitions.SharedScenarioContext;
import stepdefinitions.TestSettings;
import testvisa.KafkaTestConfig;

@SpringBootTest(classes = KafkaTestConfig.class)
public class BloombergPrepareSteps {
    TestSettings settings = TestSettings.getInstance();

    @When("the following rates data is prepared:")
    public void prepareRatesData(io.cucumber.datatable.DataTable dataTable) {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), java.time.ZoneOffset.UTC);
        String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").format(now);

        SharedScenarioContext.getInstance().set("timestamp", timestamp);

        List<Map<String, String>> ratesData = dataTable.asMaps();
        List<Rate> ratesList = new ArrayList<>();

        Random random = new Random();
        DecimalFormat decimalFormat = new DecimalFormat("#.####", DecimalFormatSymbols.getInstance(Locale.US));

        for (Map<String, String> row : ratesData) {
            Rate rate = new Rate();
            rate.setPair(row.get("pair"));
            rate.setBaseCurrency(row.get("baseCurrency"));
            rate.setQuoteCurrency(row.get("quoteCurrency"));
            rate.setAsk(row.get("ask").equals("*") ? decimalFormat.format(generateRandomRate(random)) : row.get("ask"));
            rate.setBid(row.get("bid").equals("*") ? decimalFormat.format(generateRandomRate(random)) : row.get("bid"));
            rate.setCreatedAt(timestamp);

            ratesList.add(rate);
        }

        SharedScenarioContext.getInstance().set("rates", ratesList);
    }

    private double generateRandomRate(Random random) {
        return 1.0 + (2.0 - 1.0) * random.nextDouble();
    }



}
