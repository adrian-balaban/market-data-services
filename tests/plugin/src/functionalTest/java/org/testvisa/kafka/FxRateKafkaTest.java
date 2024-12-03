package org.testvisa.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@ContextConfiguration
@ExtendWith(SpringExtension.class)
@Import(KafkaTestConfig.class)
public class FxRateKafkaTest {


    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Test
    public void testReadFromFxRateTopic() {
        String topic = "fx_rates";

        // Get the configuration properties and copy them to a map
        Map<String, Object> consumerProps = new HashMap<>(consumerFactory.getConfigurationProperties());
        // Add or modify properties
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        // Create a consumer
        Consumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(topic));

        // Poll for messages
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
        assertFalse(records.isEmpty());
        records.forEach(record -> {
            System.out.println("Consumed message: " + record.value());
        });

        consumer.close();
    }
}