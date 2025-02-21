package com.fx.market.flink.source.http;
//flink run --target kubernetes-session -c com.fx.market.flink.connector.FxMarketFlinkConnectorWebsocket ./fxmarketflinkconnectorwebsocket-all.jar
import com.fx.model.FxRate;
import com.fx.model.FxRateEvent;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FxMarketFlinkSourceHttp {

    private static final Logger log = LoggerFactory.getLogger(FxMarketFlinkSourceHttp.class);
    public static final String FLINK_WEBSOCKET_CONSUMER_EXAMPLE = "Flink Websocket Consumer Example";
    public static void main(String[] args) throws Exception {
        //
        // On FLINK GUI Program Arguments enter: --stub_hostname fx-market-data-stub-ws-svc
        // On FLINK GUI Program Arguments enter: --stub_port 3081
        ParameterTool parameter = ParameterTool.fromArgs(args);
        String stub_hostname = parameter.get("stub_hostname", "localhost"); //fx-market-data-stub-svc
        int stub_port = parameter.getInt("stub_port", 13081);
        log.info("FX_INFO: stub_hostname set to {}", stub_hostname);
        log.info("FX_INFO: stub_port set to {}", stub_port);
        final String delimiter = "\n";
        final long max_retries = 1;
        try (StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());) { //getExecutionEnvironment();) createLocalEnvironment
            DataStream<String> dataStream = env
                    .socketTextStream(stub_hostname, stub_port,delimiter, max_retries);
            dataStream.print();

            /*HttpClient client = HttpClient.create()
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

            */

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
