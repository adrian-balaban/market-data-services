package com.fx.market.fxmarketcamelconnector.routes;

import com.fx.utils.KafkaAdminCreateTopic;
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
    private ExtractSseDataBean sseMapper;

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

        try  {
            KafkaAdminCreateTopic.createTopic(bootstrapServers, topic);
        } catch (Exception ex) {
            log.error("Error creating topic: ", ex);
        }

        from("stream:http?httpUrl="+marketDataStubProperties.getUrl() + FX_MARKET_DATA_PATH)
                .to("log:INFO_SSE_STREAM")
                .filter().method(sseMapper, "messageHasData")
                    .bean(sseMapper, "extractSseData")
                    .unmarshal().json(JsonLibrary.Jackson, com.fx.model.FxRateEvent.class)
                    .bean(fxRateProtoMapper, "toProto")
                    .to("log:INFO_PROTOBUF")
                    .to("kafka:"+topic+"?brokers="+bootstrapServers)
                    .end();
    }

}