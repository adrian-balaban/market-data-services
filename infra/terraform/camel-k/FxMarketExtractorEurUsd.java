// camel-k: language=java

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

// kamel run FxMarketExtractorEurUsd.java
// Extracts EURUSD part of the message and sends it to a new event
public class FxMarketExtractorEurUsd extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        System.out.println("Configuring Camel K Route for FX Market Data");
        ExtractDataBean extractDataBean = new ExtractDataBean();
        from("knative:event/market.eurusd-usdjpy")
                //.to("log:EURUSD_USDJPY")
                .bean(extractDataBean, "extractDataEurUsd")
                .to("log:EURUSD")
                .to("knative:event/market.eurusd");
    }

    /**
     * Extracts EURUSD part of the message
     * from a message like this:
     *   {"timestamp":"2025-01-16T15:39:03.418Z","rates":[{"pair":"USD/JPY","baseCurrency":"USD","quoteCurrency":"JPY","ask":"110.02711","bid":"108.03673"},{"pair":"EUR/USD","baseCurrency":"EUR","quoteCurrency":"USD","ask":"1.02785","bid":"1.01679"}]}]
     * and sends it to a message like this:
     *   {"timestamp":"2025-01-16T15:39:03.418Z","pair":"EUR/USD","baseCurrency":"EUR","quoteCurrency":"USD","ask":"1.02785","bid":"1.01679"}
     */
    public class ExtractDataBean {
        public String extractDataEurUsd(String body) {
            return body.substring(0, 41) + body.substring(147, 238);
        }
    }
}


