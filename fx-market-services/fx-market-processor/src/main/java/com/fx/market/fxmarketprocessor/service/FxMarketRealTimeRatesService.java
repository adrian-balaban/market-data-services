package com.fx.market.fxmarketprocessor.service;

import com.fx.market.fxmarketprocessor.models.FxRate;
import com.fx.market.fxmarketprocessor.topology.FxMarketProcessorTopologyService;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class FxMarketRealTimeRatesService {

    @Autowired
    private StreamsBuilderFactoryBean streamsBuilderFactoryBean;


    public HashMap<String, FxRate> getMostRecentFxRates() {
        KeyValueIterator<String, FxRate> keyValueIterator = getStore().all();
        HashMap<String, FxRate> result = new HashMap<>();

        while (keyValueIterator.hasNext()) {
            String nexKey = keyValueIterator.peekNextKey();
            KeyValue<String, FxRate> keyValue = keyValueIterator.next();
            result.putIfAbsent(nexKey, keyValue.value);
        }

        keyValueIterator.close();
        return result;
    }

    public FxRate getMostRecentFxRateByPair(String ccyPair) {
        return getStore().get(ccyPair);
    }


    private ReadOnlyKeyValueStore<String, FxRate> getStore() {
        KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        return kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(
                        FxMarketProcessorTopologyService.FX_RATES_REALTIME_STORE,
                        QueryableStoreTypes.keyValueStore()
                )
        );
    }

}
