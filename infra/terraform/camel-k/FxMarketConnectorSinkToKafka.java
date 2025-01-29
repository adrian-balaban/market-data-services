// camel-k: language=java

// camel-k: dependency=camel:stream
// camel-k: dependency=camel:jackson
// camel-k: dependency=camel:kafka
// camel-k: dependency=camel:debug
// camel-k: dependency=camel:protobuf

//import com.fx.market.fxmarketconnector.mappers.FxRateProtoMapper;
//import com.fx.market.fxmarketconnector.vendor.kafka.KafkaStreamProducer;
//import com.fx.utils.KafkaAdminCreateTopic;
import lombok.extern.slf4j.Slf4j;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.List;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

// kamel run FxMarketConnectorSinkToKafka.java
public class FxMarketConnectorSinkToKafka extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        String url = "http://fx-market-data-stub-svc.adrian-fxmarket:3080";
        //FxRateProtoMapper fxRateProtoMapper;
        //ExtractSseDataBean sseMapper;
        String FX_MARKET_DATA_PATH = "/forex/rates";
        String bootstrapServers = "PLAINTEXT://kafka.fxmarket:9071";
        String topic = "fx_rates_camel_k";

        //ExtractSseDataBean sseMapper = new ExtractSseDataBean();
        System.out.println("Configuring Camel K Route for FX Market Data");

        System.out.println("url "+ url + FX_MARKET_DATA_PATH);
        System.out.println("bootstrapServers "+bootstrapServers);
        System.out.println("topic "+ topic);

        /*try  {
            KafkaAdminCreateTopic.createTopic(bootstrapServers, topic);
        } catch (Exception ex) {
            log.error("Error creating topic: ", ex);
        }

        /*from("stream:http?httpUrl="+marketDataStubProperties.getUrl() + FX_MARKET_DATA_PATH)
                .to("log:INFO_SSE_STREAM")
                .filter().method(sseMapper, "messageHasData")
                    .bean(sseMapper, "extractSseData")
                    .unmarshal().json(JsonLibrary.Jackson, com.fx.model.FxRateEvent.class)
                    .bean(fxRateProtoMapper, "toProto")
                    .to("log:INFO_PROTOBUF")
                    .to("kafka:"+topic+"?brokers="+bootstrapServers)
                    .end();


        from("stream:http?httpUrl=http://fx-market-data-stub-svc.fxmarket:3080/forex/rates")
                .filter().method(sseMapper, "messageHasData")
                  .bean(sseMapper, "extractSseData")
                .to("log:INFO_SSE_STREAM")
                .to("knative:event/market.eurusd-usdjpy");*/
    }

    /*public class ExtractSseDataBean {
        public String extractSseData(String body) {
            return body.substring(6);
        }
        public boolean messageHasData(String body) {
            return !body.isEmpty();
        }
    }*/
}
