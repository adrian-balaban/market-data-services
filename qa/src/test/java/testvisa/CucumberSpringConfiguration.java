package testvisa;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = KafkaTestConfig.class)
public class CucumberSpringConfiguration {
}