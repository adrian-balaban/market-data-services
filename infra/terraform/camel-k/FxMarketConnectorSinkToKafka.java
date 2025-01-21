// camel-k: language=java

// camel-k: dependency=camel:stream
// camel-k: dependency=camel:jackson
// camel-k: dependency=camel:kafka
// camel-k: dependency=camel:debug
// camel-k: dependency=camel:protobuf
// camel-k: dependency=mvn:org.apache.kafka:kafka-clients:3.9.0
// camel-k: dependency=mvn:com.google.protobuf:protobuf-java:3.25.5
// camel-k: dependency=mvn:org.projectlombok:lombok:1.18.22
//project(":model-fx-proto")
//project(":model-bloomberg-stub")

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.List;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import com.fx.market.fxmarketcamelconnector.mappers.FxRateProtoMapper;

// kamel run FxMarketConnectorSinkToKafka.java
public class FxMarketConnectorSinkToKafka extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        com.fx.market.fxmarketcamelconnector.vendor.stub.MarketDataStubProperties marketDataStubProperties;
        FxRateProtoMapper fxRateProtoMapper;
        ExtractSseDataBean sseMapper;
        static final String FX_MARKET_DATA_PATH = "/forex/rates";
        String bootstrapServers;
        String topic;

        ExtractSseDataBean sseMapper = new ExtractSseDataBean();
        System.out.println("Configuring Camel K Route for FX Market Data");

        System.out.println("url: {} ", marketDataStubProperties.getUrl() + FX_MARKET_DATA_PATH);
        System.out.println("bootstrapServers: {} ", bootstrapServers);
        System.out.println("topic: {} ", topic);

/*
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

 */
        from("stream:http?httpUrl=http://fx-market-data-stub-svc.fxmarket:3080/forex/rates")
                .filter().method(sseMapper, "messageHasData")
                  .bean(sseMapper, "extractSseData")
                .to("log:INFO_SSE_STREAM")
                .to("knative:event/market.eurusd-usdjpy");
    }

    public class ExtractSseDataBean {
        public String extractSseData(String body) {
            return body.substring(6);
        }
        public boolean messageHasData(String body) {
            return !body.isEmpty();
        }
    }
}
