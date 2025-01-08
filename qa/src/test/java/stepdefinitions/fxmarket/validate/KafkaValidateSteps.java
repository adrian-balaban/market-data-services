package stepdefinitions.fxmarket.validate;

import helpers.kafka.KafkaTestConsumer;
import io.cucumber.java.en.Then;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import stepdefinitions.SharedScenarioContext;
import testvisa.KafkaTestConfig;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import stepdefinitions.TestSettings;

@SpringBootTest(classes = KafkaTestConfig.class)
public class KafkaValidateSteps {
    TestSettings settings = TestSettings.getInstance();

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;


    @Test
    @Then("FX Rates landed on kafka")
    public void testReadFromFxRateTopic12() throws Exception {
        var expectedTimestamp = SharedScenarioContext.getInstance().get("timestamp");

        Consumer<String, byte[]> consumer = KafkaTestConsumer.getTestKafkaConsumer();

        consumer.subscribe(Collections.singletonList("fx_rates"));

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(1000));
                    assertTrue(records.count() > 0, "No message from Kafka");

                    records.forEach(record -> {
                        try {
                            // Deserialization
                            com.fx.market.kafka.message.FxRateEventProto receivedEvent = com.fx.market.kafka.message.FxRateEventProto.parseFrom(record.value());
                            System.out.println("Received message " + receivedEvent);

                            assertEquals(expectedTimestamp, receivedEvent.getTimestamp(), "Timestamp not the same");

                        } catch (Exception e) {
                            throw new RuntimeException("Deserialization error", e);
                        }
                    });
                });

        consumer.close();
    }

}
