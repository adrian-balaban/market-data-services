package com.fx.market.fxmarketconnector.controller;

import com.fx.market.fxmarketconnector.vendor.kafka.FxMarketDataKafkaProducer;
import com.fx.market.fxmarketconnector.vendor.stub.FxRateEvent;
import com.fx.market.fxmarketconnector.vendor.stub.MarketDataStubClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping(path = "/market/forex/")
public class FxMarketDataController {

    @Autowired
    private MarketDataStubClient marketDataStubClient;

    @Autowired
    private FxMarketDataKafkaProducer fxMarketDataKafkaProducer;

    @GetMapping(path = "/rates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<FxRateEvent>> fxMarketRate() {
        log.info("Received request for fx-market stream");

        return marketDataStubClient.consumeServerSentEvent()
                .doOnNext(
                    event -> fxMarketDataKafkaProducer.sendMessage(event.id(), event.data().toString())
                );
    }

    @GetMapping(path = "/stream-rates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void fxMarketRateStream() {
        log.info("Received request for fx-market stream");

        fxMarketDataKafkaProducer.streamFxRates(
                marketDataStubClient.consumeServerSentEvent()
                        .map(sseEvent -> sseEvent.data())
        );
    }
}
