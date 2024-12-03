package org.testvisa.kafka;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.kafka.clients.consumer.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testvisa.TestsPluginFunctionalTest;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;


@ContextConfiguration
@ExtendWith(SpringExtension.class)
@Import(KafkaTestConfig.class)
public class FxRateKafkaTest {


    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Test
    public void testReadFromFxRateTopic() throws Exception {
        TestsPluginFunctionalTest emit = new TestsPluginFunctionalTest();

        // 1. Генерация текущего timestamp
        long timestamp = System.currentTimeMillis();
       System.out.println("----------------------------"+ timestamp);

        emit.testEmitEventEndpoint(timestamp);
        String topic = "fx_rates";

        // Get the configuration properties and copy them to a map
        Map<String, Object> consumerProps = new HashMap<>(consumerFactory.getConfigurationProperties());
        // Add or modify properties
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        // Create a consumer
        Consumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(topic));

        // Poll for messages
   //     ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
   //     assertFalse(records.isEmpty());
   //     records.forEach(record -> {
    //        System.out.println("Consumed message: " + record.value());
   //     });


        // 5. Использование Awaitility для ожидания сообщения
        await()
                .atMost(10, SECONDS) // Максимальное время ожидания
                .untilAsserted(() -> {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500)); // Polling

                    var a = new byte['a'];
                    // x = com.fx.market.kafka.message.FxRateEventProto.parseFrom(a);


                    // Проверка, что хотя бы одно сообщение содержит нужный timestamp
                    assertTrue(
                            StreamSupport.stream(records.records(topic).spliterator(), false)
                                    .anyMatch(record -> record.value().contains("\"timestamp\":" + timestamp)),
                            "Сообщение с указанным timestamp не найдено!"
                    );
                });

        // 6. Закрытие Consumer
        consumer.close();
    }
}