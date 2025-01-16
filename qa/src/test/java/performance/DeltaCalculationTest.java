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
    @Test
    public void deltaCalculation23() throws Exception {
        Consumer<String, byte[]> consumer = KafkaTestConsumer.getTestKafkaConsumer();
        consumer.subscribe(Collections.singletonList("fx_rates"));

        ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(1000));
        assertTrue(records.count() > 0, "No message from Kafka");

        for (ConsumerRecord<String, byte[]> record : records) {
            try {
                com.fx.market.kafka.message.FxRateEventProto receivedEvent =
                        com.fx.market.kafka.message.FxRateEventProto.parseFrom(record.value());

                Instant instant =  Instant.parse(receivedEvent.getTimestamp()+ "Z");
                long epochMillis = instant.toEpochMilli();

                long recordTimestamp = record.timestamp();

                long delta = Math.abs(recordTimestamp - epochMillis);

                System.out.printf("Delta between Kafka timestamp and message timestamp: %d ms%n", delta);

            } catch (Exception e) {
                throw new RuntimeException("Deserialization error", e);
            }
        }
        System.out.printf("Count of records - ", records.count());

        consumer.close();
    }

}
