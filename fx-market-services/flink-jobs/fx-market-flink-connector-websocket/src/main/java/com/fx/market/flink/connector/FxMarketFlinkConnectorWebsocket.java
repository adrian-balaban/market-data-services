package com.fx.market.flink.connector;

import com.fx.model.FxRate;
import com.fx.model.FxRateEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FxMarketFlinkConnectorWebsocket {

    private static final Logger log = LoggerFactory.getLogger(FxMarketFlinkConnectorWebsocket.class);

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
            var stream =
                    env.socketTextStream(stub_hostname,stub_port, delimiter, max_retries)
                            .map(new FxRatesMapper())
                            .flatMap(new FxRatesFlatMapper())
                            .keyBy(FxRate::getPair)
                            //.window(TumblingProcessingTimeWindows.of(Duration.ofSeconds(5)))
                            .reduce((FxRate aggValue, FxRate newValue) -> newValue)
                            .returns(FxRate.class);
            stream.print();
            env.execute("Flink Websocket Consumer Example");
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
