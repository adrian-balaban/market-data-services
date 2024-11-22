package com.fx.market.fxmarketconnector.vendor.kafka;

import com.fx.market.kafka.message.FxRateEventProto;
import com.fx.market.fxmarketconnector.vendor.kafka.model.FxRateProtMessageSerializer;
import com.fx.market.fxmarketconnector.vendor.kafka.model.FxRateProtoMapper;
import com.fx.market.fxmarketconnector.vendor.stub.FxRateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    FxRateProtoMapper fxRateProtoMapper;

    private final KafkaSender<Integer, FxRateEventProto> sender = KafkaSender.create(
            SenderOptions.create(Map.ofEntries(
                    entry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"),
                    entry(ProducerConfig.CLIENT_ID_CONFIG, "fx-market-rates-stream-producer"),
                    entry(ProducerConfig.ACKS_CONFIG, "all"),
                    entry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class),
                    entry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, FxRateProtMessageSerializer.class)
            ))
    );

    public void sendMessages(String topic, Flux<FxRateEvent> fxStream) {
        sender.send(
                fxStream.map(fxRateEvent -> fxRateProtoMapper.toProto(fxRateEvent))
                        .map(fxRateEventProto -> SenderRecord.create(
                                        new ProducerRecord<>(topic, fxRateEventProto.hashCode(), fxRateEventProto),
                                fxRateEventProto))
                )
                .doOnError(e -> log.error("Message failed.", e))
                .subscribe(m -> log.info("Message sent: {}", m));
    }

    public void close() {
        sender.close();
    }
}
