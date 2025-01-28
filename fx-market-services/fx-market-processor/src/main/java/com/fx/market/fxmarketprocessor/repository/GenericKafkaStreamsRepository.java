package com.fx.market.fxmarketprocessor.repository;

import com.fx.market.fxmarketprocessor.repository.client.FxMarketProcessorClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public abstract class GenericKafkaStreamsRepository<K, V> {

    private final Serde<K> keySerde;
    private final Serde<V> valueSerde;

    @Autowired
    private StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    @Autowired
    private FxMarketProcessorClient fxMarketProcessorClient;

    @Autowired
    private HostInfo hostInfo;

    private final String storeName;

    public GenericKafkaStreamsRepository(Serde<K> keySerde, Serde<V> valueSerde, String storeName) {
        this.keySerde = keySerde;
        this.valueSerde = valueSerde;
        this.storeName = storeName;
    }

    public Mono<V> find(K key) {
        var metadata = streamsBuilderFactoryBean.getKafkaStreams().queryMetadataForKey(
                storeName, key, keySerde.serializer()
        );
        log.info("Record is on : {}", metadata.activeHost());
        var recordHost = metadata.activeHost();
        if (hostInfo.equals(recordHost)) {
            log.info("Searching locally for key: {}", key);
            return findLocally(key);
        } else {
            log.info("Searching remotely for key: {}", key);
            return findRemotely(recordHost, key);
        }
    }

    private Mono<V> findLocally(K key) {
        return Mono.just(getStore().get(key));
    }

    protected abstract Mono<V> findRemotely(HostInfo hostInfo, K key);

    private ReadOnlyKeyValueStore<K, V> getStore() {
        KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        return kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(
                        storeName,
                        QueryableStoreTypes.keyValueStore()
                )
        );
    }

}
