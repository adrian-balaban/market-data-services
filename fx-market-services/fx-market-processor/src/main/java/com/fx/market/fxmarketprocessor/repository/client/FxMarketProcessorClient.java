package com.fx.market.fxmarketprocessor.repository.client;

import com.fx.market.fxmarketprocessor.models.FxRate;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.state.HostInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class FxMarketProcessorClient {

    private final static WebClient client = WebClient.create();

    public Mono<FxRate> getState(HostInfo hostInfo, String ccyPair) {
        log.info("Performing call on host: {} port: {}, for key: {}", hostInfo.host(), hostInfo.port(), ccyPair);

       return client.get()
               .uri(String.format("http://%s:%s/fx/rates/%s",
                       hostInfo.host(),
                       hostInfo.port(),
                       ccyPair))
               .retrieve()
               .bodyToMono(FxRate.class);
    }
}
