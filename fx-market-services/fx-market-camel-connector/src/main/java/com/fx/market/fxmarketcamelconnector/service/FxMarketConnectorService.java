package com.fx.market.fxmarketcamelconnector.service;

import com.fx.market.fxmarketcamelconnector.mappers.FxRateProtoMapper;
import com.fx.market.fxmarketcamelconnector.vendor.kafka.KafkaStreamProducer;
import com.fx.market.fxmarketcamelconnector.vendor.stub.MarketDataStubClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FxMarketConnectorService {

    private static final String TOPIC = "fx_rates";

    @Autowired
    private MarketDataStubClient marketDataStubClient;

    @Autowired
    private KafkaStreamProducer kafkaStreamProducer;

    @Autowired
    FxRateProtoMapper fxRateProtoMapper;

    public void processFxMarketRates() {
        log.info("Processing of FX Market Rates started");

        try {
            kafkaStreamProducer.produceStream(TOPIC,
                    marketDataStubClient.consumeServerSentEvent()
                            .map(ServerSentEvent::data)
                            .map(fxRateEvent -> fxRateProtoMapper.toProto(fxRateEvent)));
        } finally {
            kafkaStreamProducer.close();
        }

        log.warn("Processing of FX Market Rates unexpectedly finished");
    }

}
