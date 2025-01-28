package stepdefinitions.fxmarket.prepare;

import io.cucumber.java.en.When;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import model.RatesRequest;
import org.springframework.boot.test.context.SpringBootTest;
import stepdefinitions.SharedScenarioContext;
import testvisa.KafkaTestConfig;

@SpringBootTest(classes = KafkaTestConfig.class)
public class BloombergPrepareSteps {

    @When("the following rates data is prepared:")
    public void prepareRatesData(io.cucumber.datatable.DataTable dataTable) {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), java.time.ZoneOffset.UTC);
        String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").format(now);

        SharedScenarioContext.getInstance().set("timestamp", timestamp);

        List<Map<String, String>> ratesData = dataTable.asMaps();
        List<RatesRequest.Rate> ratesList = new ArrayList<>();

        Random random = new Random();
        DecimalFormat decimalFormat = new DecimalFormat("#.####", DecimalFormatSymbols.getInstance(Locale.US));

        for (Map<String, String> row : ratesData) {
            RatesRequest.Rate rate = new RatesRequest.Rate();
            rate.setPair(row.get("pair"));
            rate.setBaseCurrency(row.get("baseCurrency"));
            rate.setQuoteCurrency(row.get("quoteCurrency"));
            rate.setAsk(row.get("ask").equals("*") ? decimalFormat.format(generateRandomRate(random)) : row.get("ask"));
            rate.setBid(row.get("bid").equals("*") ? decimalFormat.format(generateRandomRate(random)) : row.get("bid"));

            ratesList.add(rate);
        }

        RatesRequest ratesRequest = new RatesRequest();
        ratesRequest.setRates(ratesList);
        ratesRequest.setTimestamp(timestamp);

        SharedScenarioContext.getInstance().set("request", ratesRequest);
    }

    private double generateRandomRate(Random random) {
        return 1.0 + (2.0 - 1.0) * random.nextDouble();
    }



}
