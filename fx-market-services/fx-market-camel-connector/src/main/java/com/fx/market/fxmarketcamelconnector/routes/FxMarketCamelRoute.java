package com.fx.market.fxmarketcamelconnector.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FxMarketCamelRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        //CamelContext context = new DefaultCamelContext();

        // Transform the body of received items and log
        from("stream:http?httpUrl=http://localhost:3080/forex/rates")
                .setBody().simple("BasicReactorToCamel - Camel received ${body}")
                .to("log:INFO");
        // Logic to Extract Rates
        // Logic to Group By Rates
        // Logic to Save the latest Rate for each ccy pair

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