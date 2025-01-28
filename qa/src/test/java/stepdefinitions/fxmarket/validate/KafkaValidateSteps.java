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
import static org.junit.jupiter.api.Assertions.assertTrue;

import stepdefinitions.TestSettings;

@SpringBootTest(classes = KafkaTestConfig.class)
public class KafkaValidateSteps {
    TestSettings settings = TestSettings.getInstance();

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Test
    public void kafkaSteps() throws Exception {
        var expectedTimestamp = SharedScenarioContext.getInstance().get("timestamp");

        Consumer<String, byte[]> consumer = KafkaTestConsumer.getTestKafkaConsumer();
        consumer.subscribe(Collections.singletonList("fx_rates"));

        ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(10000));
        assertTrue(records.count() > 0, "No message from Kafka");
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

        assertTrue(timestampMatched, "No matching timestamp found in Kafka messages");

        consumer.close();
}}
