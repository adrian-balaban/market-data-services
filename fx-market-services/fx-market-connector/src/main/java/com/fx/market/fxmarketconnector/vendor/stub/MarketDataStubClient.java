package com.fx.market.fxmarketconnector.vendor.stub;

import com.fx.market.fxmarketconnector.vendor.stub.model.FxRateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class MarketDataStubClient {
    private static final String FX_MARKET_DATA_PATH = "/forex/rates";

    @Autowired
    private MarketDataStubProperties marketDataStubProperties;

    private final static  WebClient client = WebClient.create();

    private final static ParameterizedTypeReference<ServerSentEvent<FxRateEvent>> sseType
            = new ParameterizedTypeReference<>() {
    };

    public Flux<ServerSentEvent<FxRateEvent>> consumeServerSentEvent() {
        log.info("Requested GET of SSE from {}", marketDataStubProperties.getUrl());
        return client.get()
                .uri(marketDataStubProperties.getUrl() + FX_MARKET_DATA_PATH)
                .retrieve()
                .bodyToFlux(sseType);
    }
}
