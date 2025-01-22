package com.fx.market.fxmarketprocessor.topology;

import com.fx.market.fxmarketprocessor.models.FxRate;
import com.fx.market.fxmarketprocessor.models.FxRateSerde;
import com.fx.market.kafka.message.FxRateEventProto;
import com.fx.utils.KafkaAdminCreateTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FxMarketProcessorTopologyService {

    public final static String FX_RATES_REALTIME_STORE = "FX_RATES_REALTIME_STORE";

    @Value("${kafka.topic}")
    private String topic;

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Autowired
    public void fxRatesRealtimeTopology(StreamsBuilder kStreamBuilder) {

        try  {
            KafkaAdminCreateTopic.createTopic(bootstrapServers, topic);
        } catch (Exception ex) {
            log.error("Error creating topic: ", ex);
        }

        KStream<Integer, FxRateEventProto> stream = kStreamBuilder.stream(topic);

        KTable<String, FxRate> realtimeFxRateStream = stream.flatMapValues(
                        FxRateEventProto::getRatesList)
                .mapValues(FxRate::fromFxRateProto)
                .groupBy((key, rate) -> rate.getPair().replace("/", ""), Grouped.with(Serdes.String(), new FxRateSerde())) // Triggers repartition and Pair is a new KEY
                .reduce((aggValue, newValue) -> newValue,
                        Materialized.<String, FxRate, KeyValueStore<Bytes, byte[]>>as(FX_RATES_REALTIME_STORE)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new FxRateSerde())); // Triggers repartition tracks changelog

    }


}