// camel-k: language=java

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

// kamel run FxMarketLogEurUsd.java
// Logs eurusd events
public class FxMarketLogEurUsd extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("knative:event/market.eurusd")
                .to("log:eurusd");
    }
}


