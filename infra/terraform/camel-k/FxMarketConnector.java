// camel-k: language=java

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.List;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

// kamel run FxMarketConnector.java
public class FxMarketConnector extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        ExtractSseDataBean sseMapper = new ExtractSseDataBean();
        System.out.println("Configuring Camel K Route for FX Market Data");

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


