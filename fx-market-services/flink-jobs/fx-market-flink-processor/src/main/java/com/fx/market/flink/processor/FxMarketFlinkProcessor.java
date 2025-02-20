package com.fx.market.flink.processor;

import com.fx.market.flink.processor.helpers.FxRateEventProtoMessageDeserializer;
import com.fx.market.flink.processor.pojo.FxRate;
import com.fx.market.flink.processor.pojo.FxRateEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FxMarketFlinkProcessor {

    private static final Logger log = LoggerFactory.getLogger(FxMarketFlinkProcessor.class);

    public static void main(String[] args) throws Exception {
        ParameterTool parameter = ParameterTool.fromArgs(args);
        String bootstrapServers = "broker:29092"; // default
        String topic = "fx_rates"; // default

        // On FLINK GUI Program Arguments enter: --bootstrapServers kafka:9091
        if (parameter.has("bootstrapServers")) {
            bootstrapServers = parameter.get("bootstrapServers");
        }
        if (parameter.has("topic")) {
            topic = parameter.get("topic");
        }

        log.info("FX_INFO: bootstrapServers set to {}", bootstrapServers);
        log.info("FX_INFO: topic set to {}", topic);

        Configuration config = new Configuration();

        //config.setString(
        //        PipelineOptions.SERIALIZATION_CONFIG.key(),
        //        "[com.fx.market.flink.processor.model.FxRateEventProto: {type: kryo, kryo-type: registered, class: com.twitter.chill.protobuf.ProtobufSerializer}}]");

        StreamExecutionEnvironment env = StreamExecutionEnvironment. //getExecutionEnvironment(config);
        //env.getConfig().registerKryoType(MyCustomType.class);
        //env.getConfig().disableGenericTypes();
//        env.getConfig().registerTypeWithKryoSerializer(FxRateEventProto.class, ProtobufSerializer.class);

        KafkaSource<FxRateEvent> source = KafkaSource.<FxRateEvent>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(topic)
                .setGroupId("flink-consumer-group")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new FxRateEventProtoMessageDeserializer())
                .build();

        DataStream<FxRate> stream = env.fromSource(
                        source,
                        WatermarkStrategy.noWatermarks(),
                        "Kafka Source"
                )
                .flatMap(new FxRatesFlatMapper())
                .keyBy(FxRate::getPair)
                .reduce((FxRate aggValue, FxRate newValue) -> newValue);

        stream.print();

        env.execute("Flink Kafka Consumer Example");
    }

    public static class FxRatesFlatMapper implements FlatMapFunction<FxRateEvent, FxRate> {

        @Override
        public void flatMap(FxRateEvent fxRateEvent, org.apache.flink.util.Collector<FxRate> collector) throws Exception {
            fxRateEvent.getRates().forEach(
                    collector::collect
            );
        }
    }
}
