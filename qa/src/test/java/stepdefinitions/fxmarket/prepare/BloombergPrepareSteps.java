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
        DecimalFormat decimalFormat4 = new DecimalFormat("#.####", DecimalFormatSymbols.getInstance(Locale.US));
        DecimalFormat decimalFormat2 = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.US));

        for (Map<String, String> row : ratesData) {
            RatesRequest.Rate rate = new RatesRequest.Rate();

            rate.setPair(getProcessedNullableValue(row.get("pair")));
            rate.setBaseCurrency(getProcessedNullableValue(row.get("baseCurrency")));
            rate.setQuoteCurrency(getProcessedNullableValue(row.get("quoteCurrency")));

            boolean isJPY = "JPY".equalsIgnoreCase(rate.getQuoteCurrency() != null ? rate.getQuoteCurrency() : "");
            DecimalFormat decimalFormat = isJPY ? decimalFormat2 : decimalFormat4;

            rate.setAsk(getProcessedAskBid(row.get("ask"), decimalFormat, random));
            rate.setBid(getProcessedAskBid(row.get("bid"), decimalFormat, random));

            ratesList.add(rate);
        }

        RatesRequest ratesRequest = new RatesRequest();
        ratesRequest.setRates(ratesList);
        ratesRequest.setTimestamp(timestamp);

        SharedScenarioContext.getInstance().set("request", ratesRequest);
    }

    private String getProcessedNullableValue(String value) {
        if (value == null) return null;
        if ("null".equals(value)) return null;
        return value;
    }

    private String getProcessedAskBid(String value, DecimalFormat decimalFormat, Random random) {
        if (value == null || "null".equals(value)) return null;
        if ("*".equals(value)) return decimalFormat.format(generateRandomRate(random));
        return value;
    }
    private double generateRandomRate(Random random) {
        return 1.0 + (2.0 - 1.0) * random.nextDouble();
    }



}
