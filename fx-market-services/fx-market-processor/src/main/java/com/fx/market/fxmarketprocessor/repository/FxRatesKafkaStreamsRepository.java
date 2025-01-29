package com.fx.market.fxmarketprocessor.repository;

import com.fx.market.fxmarketprocessor.models.FxRate;
import com.fx.market.fxmarketprocessor.models.FxRateSerde;
import com.fx.market.fxmarketprocessor.repository.client.FxMarketProcessorClient;
import com.fx.market.fxmarketprocessor.topology.FxMarketProcessorTopologyService;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.state.HostInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class FxRatesKafkaStreamsRepository extends GenericKafkaStreamsRepository<String, FxRate>{

    @Autowired
    private FxMarketProcessorClient fxMarketProcessorClient;

    public FxRatesKafkaStreamsRepository() {
        super(
                Serdes.String(),
                new FxRateSerde(),
                FxMarketProcessorTopologyService.FX_RATES_REALTIME_STORE);
    }

    @Override
    protected Mono<FxRate> findRemotely(HostInfo hostInfo, String key) {
        return fxMarketProcessorClient.getState(hostInfo, key);
    }
}
