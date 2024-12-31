package com.fx.market.fxmarketcamelprocessor.routes;

import com.fx.market.kafka.message.FxRateEventProto;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FxMarketCamelRoute extends RouteBuilder {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
    private static final String TOPIC = "fx_rates_camel";

    @Override
    public void configure() throws Exception {
        log.info("Configuring Camel Route for FX Market Data");
        log.info("bootstrapServers: {} ", bootstrapServers);
        log.info("topic: {} ", TOPIC);

        from("kafka:"+TOPIC+"?brokers="+bootstrapServers)//groupId=fx-market-camel-processor&groupInstanceId=fx-market-camel-processor-1
                //.unmarshal()
                //.protobuf(FxRateEventProto.getDefaultInstance())
                .log("Message received from Kafka : ${body}")
                .log("    on the topic ${headers[kafka.TOPIC]}")
                .log("    on the partition ${headers[kafka.PARTITION]}")
                .log("    with the offset ${headers[kafka.OFFSET]}")
                .log("    with the key ${headers[kafka.KEY]}")
                .to("log:INFO2");
                /*.process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        String payload = exchange.getIn().getBody(String.class);
                        // do something with the payload and/or exchange here
                        exchange.getIn().setBody("Changed body");
                    }
                }).to("activemq:myOtherQueue")*/
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