package stepdefinitions.fxmarket.validate;

import helpers.kafka.KafkaTestConsumer;
import io.cucumber.java.en.Then;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import stepdefinitions.SharedScenarioContext;
import testvisa.KafkaTestConfig;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import stepdefinitions.TestSettings;

@SpringBootTest(classes = KafkaTestConfig.class)
public class KafkaValidateSteps {
    TestSettings settings = TestSettings.getInstance();

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Test
    @Then("FX Rates landed on kafka")
    public void kafkaSteps() throws Exception {
        String expectedTimestamp = (String)SharedScenarioContext.getInstance().get("timestamp");
        boolean timestampMatched = pollAndValidateKafkaMessages(expectedTimestamp);
        assertTrue(timestampMatched, "No matching timestamp found in Kafka messages");
    }

    @Then("FX Rates NOT landed on kafka")
    public void kafkaStepsNegative() throws Exception {
        String expectedTimestamp = (String)SharedScenarioContext.getInstance().get("timestamp");
        boolean timestampMatched = pollAndValidateKafkaMessagesNegative(expectedTimestamp);
        assertFalse(timestampMatched, "Matching timestamp found in Kafka messages");
    }


    private boolean pollAndValidateKafkaMessages(String expectedTimestamp) {
        Consumer<String, byte[]> consumer = KafkaTestConsumer.getTestKafkaConsumer();
        consumer.subscribe(Collections.singletonList("fx_rates"));

        ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(10000));

        if (records.isEmpty()) {
            consumer.close();
            throw new AssertionError("No message from Kafka");
        }

        boolean timestampMatched = false;

        for (ConsumerRecord<String, byte[]> record : records) {
            try {
                com.fx.market.kafka.message.FxRateEventProto receivedEvent =
                        com.fx.market.kafka.message.FxRateEventProto.parseFrom(record.value());

                if (expectedTimestamp.equals(receivedEvent.getTimestamp())) {
                    timestampMatched = true;
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException("Deserialization error", e);
            }
        }

        consumer.close();
        return timestampMatched;
    }


    private boolean pollAndValidateKafkaMessagesNegative(String expectedTimestamp) {
        Consumer<String, byte[]> consumer = KafkaTestConsumer.getTestKafkaConsumer();
        consumer.subscribe(Collections.singletonList("fx_rates"));

        ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(10000));

        boolean timestampMatched = false;

        for (ConsumerRecord<String, byte[]> record : records) {
            try {
                com.fx.market.kafka.message.FxRateEventProto receivedEvent =
                        com.fx.market.kafka.message.FxRateEventProto.parseFrom(record.value());

                if (expectedTimestamp.equals(receivedEvent.getTimestamp())) {
                    timestampMatched = true;
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException("Deserialization error", e);
            }
        }

        consumer.close();
        return timestampMatched;
    }



}
