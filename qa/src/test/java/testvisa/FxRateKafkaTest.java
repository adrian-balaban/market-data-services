package testvisa;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

import com.fx.market.kafka.message.FxRateEventProto;
import com.fx.market.kafka.message.FxRateProto;
import helpers.TestsPluginFunctionalTest;
import org.apache.kafka.clients.consumer.*;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

@SpringBootTest(classes = KafkaTestConfig.class)
@ExtendWith(SpringExtension.class)
public class FxRateKafkaTest {
  //  private helpers.TestContext context;


    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

   // public FxRateKafkaTest(TestContext testContext) {
   //     this.context = testContext;
   // }
   public FxRateKafkaTest( ) {
   }
    @Autowired
    private ApplicationContext context;

    @Test
    public void verifyBeans() {
        assertNotNull(context.getBean(ConsumerFactory.class), "ConsumerFactory не найден в контексте!");
        System.out.println("ConsumerFactory бин успешно загружен.");
    }

    @Test
    public void testReadFromFxRateTopic() throws Exception {
        TestsPluginFunctionalTest emit = new TestsPluginFunctionalTest();
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), java.time.ZoneOffset.UTC);
        String timestamp =  DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").format(now);


        System.out.println("----------------------------"+ timestamp);

        emit.testEmitEventEndpoint(timestamp);
        String topic = "fx_rates";

        Map<String, Object> consumerProps = new HashMap<>(consumerFactory.getConfigurationProperties());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        Consumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(topic));

        await()
                .atMost(10, SECONDS) //  время ожидания
                .untilAsserted(() -> {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000)); // Polling

               //     var a = new byte['a'];
                 //   var x = com.fx.market.kafka.message.FxRateEventProto.parseFrom(a);

                    assertTrue(
                            StreamSupport.stream(records.records(topic).spliterator(), false)
                                    .anyMatch(record -> record.value().contains(timestamp)),
                            " timestamp !"
                    );
                });

        consumer.close();
    }





}