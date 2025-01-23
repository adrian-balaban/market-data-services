package com.fx.market.fxmarketprocessor.service;

import com.fx.market.fxmarketprocessor.models.FxRate;
import com.fx.market.fxmarketprocessor.repository.FxRatesKafkaStreamsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Service
public class FxMarketRealTimeRatesService {

    @Autowired
    private StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    @Autowired
    private FxRatesKafkaStreamsRepository fxRatesKafkaStreamsRepository;

    public HashMap<String, FxRate> getMostRecentFxRates() {
        return new HashMap<>();
        //KeyValueIterator<String, FxRate> keyValueIterator = getStore().all();
        //HashMap<String, FxRate> result = new HashMap<>();

        //while (keyValueIterator.hasNext()) {
        //    String nexKey = keyValueIterator.peekNextKey();
        //    KeyValue<String, FxRate> keyValue = keyValueIterator.next();
        //    result.putIfAbsent(nexKey, keyValue.value);
        //}

        //keyValueIterator.close();
        //return result;
    }

    public Mono<FxRate> getMostRecentFxRateByPair(String ccyPair) {
        return fxRatesKafkaStreamsRepository.find(ccyPair);
    }


}
