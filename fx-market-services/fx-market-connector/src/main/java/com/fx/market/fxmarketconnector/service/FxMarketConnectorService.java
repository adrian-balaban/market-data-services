package com.fx.market.fxmarketconnector.service;

import com.fx.market.fxmarketconnector.mappers.FxRateProtoMapper;
import com.fx.market.fxmarketconnector.vendor.kafka.KafkaStreamProducer;
import com.fx.market.fxmarketconnector.vendor.stub.MarketDataStubClient;
import com.fx.utils.KafkaAdminCreateTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FxMarketConnectorService {

    @Autowired
    private MarketDataStubClient marketDataStubClient;

    @Autowired
    private KafkaStreamProducer kafkaStreamProducer;

    @Autowired
    FxRateProtoMapper fxRateProtoMapper;

    @Value("${kafka.topic}")
    private String topic;

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    public void processFxMarketRates() {
        log.info("Processing of FX Market Rates started");
        try {
            kafkaStreamProducer.produceStream(topic,
                    marketDataStubClient.consumeServerSentEvent()
                            .map(ServerSentEvent::data)
                            .map(fxRateEvent -> fxRateProtoMapper.toProto(fxRateEvent)));
        } finally {
            kafkaStreamProducer.close();
        }

        log.warn("Processing of FX Market Rates unexpectedly finished");
    }

}
