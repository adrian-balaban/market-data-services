package stepdefinitions;

import io.cucumber.java.en.Then;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import testvisa.KafkaTestConfig;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = KafkaTestConfig.class)
public class KafkaSteps {

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Test
    @Then("FX Rates landed on kafka")
    public void testReadFromFxRateTopic12() throws Exception {
        String topic = "fx_rates";
        var expectedTimestamp = SharedScenarioContext.getInstance().get("timestamp");

        // 3.  Consumer
        Map<String, Object> consumerProps = new HashMap<>(consumerFactory.getConfigurationProperties());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        Consumer<String, byte[]> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(topic));


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
