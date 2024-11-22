package com.fx.market.fxmarketconnector.vendor.kafka;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.Map;

import static java.util.Map.entry;

@Slf4j
@Component
public class KafkaStreamProducer {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    public void produceStream(String topic, Flux<Object> stream) {
        KafkaSender.create(
                SenderOptions.create(Map.ofEntries(
                                entry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers),
                                entry(ProducerConfig.CLIENT_ID_CONFIG, "fx-market-connector-stream-producer"),
                                entry(ProducerConfig.ACKS_CONFIG, "all"),
                                entry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class),
                                entry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ProtMessageSerializer.class)
                        )))
                .send(
                        stream.map(messageProto -> SenderRecord.create(
                                new ProducerRecord<>(topic, messageProto.hashCode(), messageProto), messageProto))
                )
                .doOnError(e -> log.error("Message failed.", e))
                .subscribe(m -> log.info("Message sent: {}", m));
    }

    public void close() {
        //todo sender.close();
    }
}
