package testvisa.kafka;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

import helpers.TestContext;
import org.apache.kafka.clients.consumer.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testvisa.TestsPluginFunctionalTest;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;


@ContextConfiguration(classes = KafkaTestConfig.class)
@ExtendWith(SpringExtension.class)
@Import(KafkaTestConfig.class)
public class FxRateKafkaTest {
    private helpers.TestContext context;


    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    public FxRateKafkaTest(TestContext testContext) {
        this.context = testContext;
    }

    @Test
    public void testReadFromFxRateTopic() throws Exception {
        TestsPluginFunctionalTest emit = new TestsPluginFunctionalTest();

        long timestamp = System.currentTimeMillis();
       System.out.println("----------------------------"+ timestamp);

        emit.testEmitEventEndpoint(timestamp);
        String topic = "fx_rates_kafka";

        Map<String, Object> consumerProps = new HashMap<>(consumerFactory.getConfigurationProperties());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        Consumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(topic));

        await()
                .atMost(10, SECONDS) //  время ожидания
                .untilAsserted(() -> {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000)); // Polling

                    //var a = new byte['a'];
                  //   x = com.fx.market.kafka.message.FxRateEventProto.parseFrom(a);

                    assertTrue(
                            StreamSupport.stream(records.records(topic).spliterator(), false)
                                    .anyMatch(record -> record.value().contains("\"timestamp\":" + timestamp)),
                            " timestamp !"
                    );
                });

        consumer.close();
    }
}