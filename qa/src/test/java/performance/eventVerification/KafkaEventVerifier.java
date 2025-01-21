package performance.eventVerification;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.*;
import java.time.Duration;
import java.util.*;
import org.json.JSONObject;

public class KafkaEventVerifier {

    private static final String TOPIC = "test-topic";
    private static final String GROUP_ID = "test-group";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String EXPECTED_EVENTS_FILE = "expected-events.txt";

    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = createConsumer();
        Set<String> expectedEvents = loadExpectedEvents();

        System.out.println("Loaded " + expectedEvents.size() + " expected events from file.");

        consumer.subscribe(Collections.singletonList(TOPIC));
        System.out.println("Consumer connected. Checking received events...");

        int receivedEventsCount = 0;
        int verifiedEventsCount = 0;

        try {
            while (!expectedEvents.isEmpty()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
                if (records.isEmpty()) {
                    System.out.println("\nTimeout reached. No more messages to process.");
                    break;
                }

                for (ConsumerRecord<String, String> record : records) {
                    receivedEventsCount++;
                    String receivedValue = record.value();
                    String parsedEvent;

                    try {
                        JSONObject jsonObject = new JSONObject(receivedValue);
                        if (jsonObject.has("message")) {
                            parsedEvent = jsonObject.getString("message");
                        } else {
                            parsedEvent = jsonObject.toString();
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to parse message: " + receivedValue);
                        System.err.println("Error: " + e.getMessage());
                        continue;
                    }

                    System.out.println("📥 Received event: " + parsedEvent);

                    if (expectedEvents.contains(parsedEvent)) {
                        System.out.println(" Event verified: " + parsedEvent);
                        expectedEvents.remove(parsedEvent);
                        verifiedEventsCount++;
                    } else {
                        System.err.println(" Unexpected event: " + parsedEvent);
                    }
                }
            }

            System.out.println("\n Final Summary:");
            System.out.println("- Total received messages: " + receivedEventsCount);
            System.out.println("  - Verified events: " + verifiedEventsCount);
            System.out.println("   - Remaining expected events count: " + expectedEvents.size());
            if (!expectedEvents.isEmpty()) {
                System.out.println("   - Remaining expected events:");
                expectedEvents.forEach(event -> System.out.println("     " + event));
            }

        } finally {
            consumer.close();
            System.out.println("Consumer closed.");
        }
    }

    private static KafkaConsumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000");

        return new KafkaConsumer<>(props);
    }

    private static Set<String> loadExpectedEvents() {
        Set<String> expectedEvents = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(EXPECTED_EVENTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                expectedEvents.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error loading expected events from file: " + e.getMessage());
            System.exit(1);
        }
        return expectedEvents;
    }
}
