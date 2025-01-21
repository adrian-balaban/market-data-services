package performance;

import com.fx.market.kafka.message.FxRateEventProto;
import helpers.kafka.KafkaTestConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class DeltaCalculationTest {
    int durationPoll = 10000;
    String topic = "fx_rates";

    @Test
    public void deltaCalculation() throws Exception {

        Consumer<String, byte[]> consumer = KafkaTestConsumer.getTestKafkaConsumer();
        consumer.subscribe(Collections.singletonList(topic));
        Map<Long, Long> summary = new HashMap<>();
        int totalRecordsCount = 0;

        try {
            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(durationPoll));

                if (records.isEmpty()) {
                    System.out.println("No more messages in Kafka, exiting loop.");
                    break;
                }

                totalRecordsCount += records.count();

                System.out.printf("📥 Processing %d new messages...%n", records.count());

                for (ConsumerRecord<String, byte[]> record : records) {
                    try {
                        FxRateEventProto receivedEvent = FxRateEventProto.parseFrom(record.value());

                        Instant instant = Instant.parse(receivedEvent.getTimestamp());
                        long epochMillis = instant.toEpochMilli();
                        long recordTimestamp = record.timestamp();
                        long delta = Math.abs(recordTimestamp - epochMillis);

                        summary.merge(delta, 1L, Long::sum);

                    } catch (Exception e) {
                        System.err.println("Deserialization error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Kafka consumer error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.printf("✅ Total records processed: %d%n", totalRecordsCount);
            System.out.println("📊 Delta summary: " + summary);
            consumer.close();
        }

}}
