package com.fx.market.fxmarketcamelconnector.routes;

import com.fx.market.kafka.message.FxRateEventProto;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FxMarketCamelRoute extends RouteBuilder {

    @Autowired
    private MarketDataStubProperties marketDataStubProperties;
    private static final String FX_MARKET_DATA_PATH = "/forex/rates";

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
    private static final String TOPIC = "fx_rates_camel";

    @Override
    public void configure() throws Exception {
        log.info("Configuring Camel Route for FX Market Data");
        log.info("url: {} ", marketDataStubProperties.getUrl() + FX_MARKET_DATA_PATH);
        log.info("bootstrapServers: {} ", bootstrapServers);
        log.info("topic: {} ", TOPIC);

        from("stream:http?httpUrl="+marketDataStubProperties.getUrl() + FX_MARKET_DATA_PATH)
                .to("log:INFO")
                //.unmarshal()
                //.protobuf(FxRateEventProto.getDefaultInstance(), "json")
                //.setHeader(KafkaConstants.KEY, "key") // Kafka Key of the message
                .to("kafka:"+TOPIC+"?brokers="+bootstrapServers+"&key=constantKey");
    }
}

/// ////////////////////////////// FOR CURRENT SCENARIO
/// JAVA - camelK java root
// + Simplicity - One file
// - Libs - Probably possible but may add additional complexity + needs time to learn how
// = Runner? <- Managed by KNative that autoscales
                            // we need 24/7 always 1 replica
// - Prereq -> installed on k8s camel-k + knative?


/// Spring Boot - Gradle as here
// - Additional - more setup + new service, scripts, deployment
// + Elasticity - Import libs and dependencies easily
// = Runner? <- spring boot application -> pod
                            // we need 24/7 always 1 replica
// + Prereq -> No prerequisites?


//////////////////////////////////////////////////////////////////
// 1St solution: Stub -> Connector(json->proto) -> Kafka -> Processor over Kafka

// 3rd solution: Stub -> Camel(json->proto) -> Kafka -> Same Processor over Kafka
                                //do we need knative? -> No

///////////////////////////////////////////////////////////////////