package com.fx.market.fxmarketprocessor.service;

import com.fx.market.kafka.message.FxRateEventProto;
import com.fx.utils.KafkaAdminCreateTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.fx.market.fxmarketprocessor.service.FxRate.fromFxRateProto;

@Slf4j
@Configuration
public class FxMarketProcessorService {

    @Value("${kafka.topic}")
    private String topic;

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KStream<Integer, FxRateEventProto> kStream(StreamsBuilder kStreamBuilder) {

        try  {
            KafkaAdminCreateTopic.createTopic(bootstrapServers, topic);
        } catch (Exception ex) {
            log.error("Error creating topic: ", ex);
        }

        KStream<Integer, FxRateEventProto> stream = kStreamBuilder.stream(topic);

        KTable<String, FxRate> table = stream.flatMap(
                        (key, fxRateEventProto) ->
                                fxRateEventProto.getRatesList().stream()
                                        .map(rate -> new KeyValue<>(key, rate))
                                        .collect(Collectors.toList()))
                .map((key, fxRateProto) -> new KeyValue<>(key, fromFxRateProto(fxRateProto)))
                .groupBy((key, rate) -> rate.getPair(), Grouped.with(Serdes.String(), new FxRateSerde()))
                .reduce((aggValue, newValue) -> newValue, Materialized.with(Serdes.String(), new FxRateSerde()));

        table.toStream()
                .foreach((key, value) ->
                        System.out.println("Time: " + Instant.now() + "Pair: " + key + ", Rate: " + value));
        return stream;
    }


}
/*

        @Value("${kafka.topic}")
        private String topic;

        @Value("${kafka.bootstrap-servers}")
        private String bootstrapServers;
        try {
            log.info("Creating topic {}", topic);
            Properties properties = new Properties();
            properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

            try (Admin admin = Admin.create(properties)) {
                int partitions = 9;
                short replicationFactor = 1;
                NewTopic newTopic = new NewTopic(topic, partitions, replicationFactor);

                CreateTopicsResult result = admin.createTopics(Collections.singleton(newTopic));

                KafkaFuture<Void> future = result.values().get(topic);
                future.get();
            } catch (Exception ex) {
                log.error("Error creating topic: ", ex);
            }

 */