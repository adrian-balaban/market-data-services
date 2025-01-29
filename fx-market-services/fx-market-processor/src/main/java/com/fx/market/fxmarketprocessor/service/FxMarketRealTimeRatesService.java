package com.fx.market.fxmarketprocessor.service;

import com.fx.market.fxmarketprocessor.models.FxRate;
import com.fx.market.fxmarketprocessor.repository.FxRatesKafkaStreamsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FxMarketRealTimeRatesService {

    @Autowired
    private FxRatesKafkaStreamsRepository fxRatesKafkaStreamsRepository;


    public Mono<FxRate> getMostRecentFxRateByPair(String ccyPair) {
        return fxRatesKafkaStreamsRepository.find(ccyPair);
    }


}
