package performance;

import helpers.kafka.KafkaTestConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeltaCalculationTest {
    int maxRetries = 5;
    int retryIntervalMillis = 2000;
    int durationPoll = 2000;
    String topic = "fx_rates";

    @Test
    public void calculateDelta() throws Exception {
        Consumer<String, byte[]> consumer = KafkaTestConsumer.getTestKafkaConsumer();
        consumer.subscribe(Collections.singletonList(topic));

        ConsumerRecords<String, byte[]> records = ConsumerRecords.empty();

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            records = consumer.poll(Duration.ofMillis(durationPoll));

            if (!records.isEmpty()) {
                break;
            }

            System.out.println("No messages yet, retrying... (" + (attempt + 1) + "/" + maxRetries + ")");
            Thread.sleep(retryIntervalMillis);
        }

        assertTrue(records.count() > 0, "No message from Kafka after retries");

        for (ConsumerRecord<String, byte[]> record : records) {
            try {
                com.fx.market.kafka.message.FxRateEventProto receivedEvent =
                        com.fx.market.kafka.message.FxRateEventProto.parseFrom(record.value());

                Instant instant = Instant.parse(receivedEvent.getTimestamp());
                long epochMillis = instant.toEpochMilli();
                long recordTimestamp = record.timestamp();
                long delta = Math.abs(recordTimestamp - epochMillis);

                System.out.printf("Delta between Kafka timestamp and message timestamp: %d ms%n", delta);

            } catch (Exception e) {
                throw new RuntimeException("Deserialization error", e);
            }
        }
        System.out.printf("Count of records - %d%n", records.count());
        consumer.close();
    }


}
