package com.fx.market.flink.connector;
//flink run --target kubernetes-session -c com.fx.market.flink.connector.FxMarketFlinkConnectorWebsocket ./fxmarketflinkconnectorwebsocket-all.jar
import com.fx.model.FxRate;
import com.fx.model.FxRateEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AbstractRichFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.connector.source.Boundedness;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;

public class FxMarketFlinkConnectorWebsocket {

    private static final Logger log = LoggerFactory.getLogger(FxMarketFlinkConnectorWebsocket.class);
    public static final String FLINK_WEBSOCKET_CONSUMER_EXAMPLE = "Flink Websocket Consumer Example";
    public static void main(String[] args) throws Exception {
        //
        // On FLINK GUI Program Arguments enter: --stub_hostname fx-market-data-stub-ws-svc
        // On FLINK GUI Program Arguments enter: --stub_port 3081
        ParameterTool parameter = ParameterTool.fromArgs(args);
        String stub_hostname = parameter.get("stub_hostname", "fx-market-data-stub-ws-svc");
        int stub_port = parameter.getInt("stub_port", 3081);
        log.info("FX_INFO: stub_hostname set to {}", stub_hostname);
        log.info("FX_INFO: stub_port set to {}", stub_port);
        final String delimiter = "\n";
        final long max_retries = 100;
        try (StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();) {
            //env.addSource()


            HttpClient client = HttpClient.create()
                    .httpResponseDecoder(spec -> spec.maxInitialLineLength(16384))
                    .httpResponseDecoder(spec -> spec.maxHeaderSize(16384))
                    .doOnConnected(conn ->
                            conn.addHandlerFirst(new ReadTimeoutHandler(10, TimeUnit.SECONDS)))
                    .doOnChannelInit((observer, channel, remoteAddress) ->
                            channel.pipeline()
                                    .addFirst(new LoggingHandler("reactor.netty.examples")))
                    .doOnResponse((res, conn) -> {
                        if (res.status()
                                .code() == 500) {
                            System.err.println("Internal Server Error");
                        }
                    });
            client.warmup();

            Flux<String> response =
                    client.get()
                            .uri("http://localhost:3080/forex/rates")
                            .responseContent()
                            .asString();
            RichSourceFunction<String> source = new RichSourceFunction<String>() {
                @Override
                public void run(SourceContext<String> ctx) throws Exception {
                    response.subscribe(
                            ctx::collect
                    );
                }
                @Override
                public void cancel() {
                    response.subscribe().dispose();
                }
            };
            var stream = env.addSource(source)
                                        .map(new FxRatesMapper())
                    .flatMap(new FxRatesFlatMapper())
                    .keyBy(FxRate::getPair)
                    .reduce((FxRate aggValue, FxRate newValue) -> newValue);
            stream.print();

            //env.addSource(SourceFunction<OUT> function, String sourceName, @Nullable TypeInformation<OUT> typeInfo, Boundedness boundedness) {
            /*var streamOld =
                    env.socketTextStream(stub_hostname,stub_port, delimiter, max_retries)
                            .map(new FxRatesMapper())
                            .flatMap(new FxRatesFlatMapper())
                            .keyBy(FxRate::getPair)
                            //.window(TumblingProcessingTimeWindows.of(Duration.ofSeconds(5)))
                            .reduce((FxRate aggValue, FxRate newValue) -> newValue);
                            //.returns(FxRate.class);
            //stream.print();*/
            env.execute(FLINK_WEBSOCKET_CONSUMER_EXAMPLE);
        } catch (Exception e) {
            log.info("FX_INFO: {} ",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static class FxRatesMapper implements MapFunction<String, FxRateEvent> {
        private static final Logger log = LoggerFactory.getLogger(FxRatesMapper.class);
        private static final ObjectMapper MAPPER = new ObjectMapper();
        @Override
        public FxRateEvent map(String value) throws Exception {
            log.info("FX_RATES_MAPPER: value {}", value);
            return MAPPER.readValue(value, FxRateEvent.class);
        }
    }

    public static class FxRatesFlatMapper implements FlatMapFunction<FxRateEvent, FxRate> {
        private static final Logger log = LoggerFactory.getLogger(FxRatesFlatMapper.class);
        @Override
        public void flatMap(FxRateEvent fxRateEvent, org.apache.flink.util.Collector<FxRate> collector) {
            fxRateEvent.getRates().forEach(
                    collector::collect
            );
        }
    }
}


/*
mkdir ApiFieldsExample && unzip ApiFieldsExample-3.24.12-1.jar -o -d ApiFieldsExample
mkdir blpapi && unzip -o -d blpapi blpapi-3.24.12-1.jar && cd ..
mkdir blpapi && unzip -o -d blpapi blpapi-demoapps-3.24.12-1.jar && cd ..
mkdir BroadcastPublisherExample && unzip -o -d BroadcastPublisherExample BroadcastPublisherExample-3.24.12-1.jar && cd ..
mkdir ContributionsExample && unzip -o -d ContributionsExample ContributionsExample-3.24.12-1.jar && cd ..
mkdir EntitlementsVerificationRequestResponseExample && unzip -o -d EntitlementsVerificationRequestResponseExample EntitlementsVerificationRequestResponseExample-3.24.12-1.jar && cd ..
mkdir EntitlementsVerificationSubscriptionExample && unzip -o -d EntitlementsVerificationSubscriptionExample EntitlementsVerificationSubscriptionExample-3.24.12-1.jar && cd ..
mkdir GenerateTokenExample && unzip -o -d GenerateTokenExample GenerateTokenExample-3.24.12-1.jar && cd ..
mkdir InteractivePublisherExample && unzip -o -d InteractivePublisherExample InteractivePublisherExample-3.24.12-1.jar && cd ..
mkdir MultipleRequestsOverrideExample && unzip -o -d MultipleRequestsOverrideExample MultipleRequestsOverrideExample-3.24.12-1.jar && cd ..
mkdir RequestResponseExample && unzip -o -d RequestResponseExample RequestResponseExample-3.24.12-1.jar && cd ..
mkdir RequestServiceConsumerExample && unzip -o -d RequestServiceConsumerExample RequestServiceConsumerExample-3.24.12-1.jar && cd ..
mkdir RequestServiceProviderExample && unzip -o -d RequestServiceProviderExample RequestServiceProviderExample-3.24.12-1.jar && cd ..
mkdir SecurityLookupExample && unzip -o -d SecurityLookupExample SecurityLookupExample-3.24.12-1.jar && cd ..
mkdir SnapshotRequestTemplateExample && unzip -o -d SnapshotRequestTemplateExample SnapshotRequestTemplateExample-3.24.12-1.jar && cd ..
mkdir SubscriptionExample && unzip -o -d SubscriptionExample SubscriptionExample-3.24.12-1.jar && cd ..
mkdir SubscriptionWithEventPollingExample && unzip -o -d SubscriptionWithEventPollingExample SubscriptionWithEventPollingExample-3.24.12-1.jar && cd ..
mkdir UserModeExample && unzip -o -d UserModeExample UserModeExample-3.24.12-1.jar && cd ..

 */