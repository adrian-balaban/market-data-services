package com.fx.market.fxmarketconnector.vendor.stub;

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

    public Flux<ServerSentEvent<FxRateEvent>> consumeServerSentEvent() {
        log.info("Starting consuming SSE from {}", marketDataStubProperties.getUrl());

        WebClient client = WebClient.create(marketDataStubProperties.getUrl());
        ParameterizedTypeReference<ServerSentEvent<FxRateEvent>> sseType
                = new ParameterizedTypeReference<>() {
        };

        return client.get()
            .uri(FX_MARKET_DATA_PATH)
            .retrieve()
            .bodyToFlux(sseType);
    }
}
