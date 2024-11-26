package com.fx.market.fxmarketprocessor.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fx.market.kafka.message.FxRateEventProto;

import java.time.Instant;
import java.util.stream.Collectors;

import static com.fx.market.fxmarketprocessor.service.FxRate.fromFxRateProto;

@Slf4j
@Configuration
public class FxMarketProcessorService {
    private static final String TOPIC_INPUT = "fx_rates";


    @Bean
    public KStream<Integer, FxRateEventProto> kStream(StreamsBuilder kStreamBuilder) {
        KStream<Integer, FxRateEventProto> stream = kStreamBuilder.stream(TOPIC_INPUT);

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
