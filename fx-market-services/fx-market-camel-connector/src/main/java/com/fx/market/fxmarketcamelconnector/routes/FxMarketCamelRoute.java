package com.fx.market.fxmarketcamelconnector.routes;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FxMarketCamelRoute extends RouteBuilder {

    @Autowired
    private com.fx.market.fxmarketcamelconnector.vendor.stub.MarketDataStubProperties marketDataStubProperties;

    @Autowired
    private com.fx.market.fxmarketcamelconnector.mappers.FxRateProtoMapper fxRateProtoMapper;

    @Autowired
    private com.fx.market.fxmarketcamelconnector.camel.beans.ExtractSseData sseMapper;

    private static final String FX_MARKET_DATA_PATH = "/forex/rates";

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topic}")
    private String topic;

    @Override
    public void configure()  {
        log.info("Configuring Camel Route for FX Market Data");
        log.info("url: {} ", marketDataStubProperties.getUrl() + FX_MARKET_DATA_PATH);
        log.info("bootstrapServers: {} ", bootstrapServers);
        log.info("topic: {} ", topic);

        from("stream:http?httpUrl="+marketDataStubProperties.getUrl() + FX_MARKET_DATA_PATH)
                .to("log:INFO_SSE_STREAM")
                .bean(sseMapper, "extractSseData")
                .unmarshal().json(JsonLibrary.Jackson, com.fx.model.FxRateEvent.class)
                .bean(fxRateProtoMapper, "toProto")
                .to("log:INFO_PROTOBUF")
                .to("kafka:"+topic+"?brokers="+bootstrapServers);
    }

}
