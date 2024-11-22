package com.fx.market.fxmarketconnector.vendor.kafka;

import com.fx.market.fxmarketconnector.vendor.stub.FxRateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;

import java.util.concurrent.CompletableFuture;

@Component
public class FxMarketDataKafkaProducer {

    @Autowired
    KafkaStreamProducer kafkaStreamProducer;

    private static final Logger logger = LoggerFactory.getLogger(FxMarketDataKafkaProducer.class);
    private static final String TOPIC = "mocked_fx_rates";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String key, String value) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, key, value);
        future.whenComplete((sr, ex) -> System.out.println(future + ": " + sr.getRecordMetadata()));
    }

    public void streamFxRates(Flux<FxRateEvent> fxRateFlux) {
        kafkaStreamProducer.sendMessages("mocked_stream_fx_rates_3", fxRateFlux);
    }

}
