// camel-k: language=java

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

// kamel run FxMarketOutputStream.java
public class FxMarketOutputStream extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("knative:event/market.eurusd")
                .to("log:eurusd")
                // Route messages to the standard output. Message will be followed by the newline.
                .to("stream:out"); // see https://camel.apache.org/components/4.8.x/stream-component.html#_examples
                // Send byte[] payload to the standard output. No newline will be added after the message.
                // template.sendBody("direct:in", "Hello Bytes World".getBytes());
    }
}
