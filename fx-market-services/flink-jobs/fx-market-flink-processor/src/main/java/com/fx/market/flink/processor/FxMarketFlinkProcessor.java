package com.fx.market.flink.processor;

import com.fx.market.flink.processor.helpers.FxRateEventProtoMessageDeserializer;
import com.fx.market.flink.processor.sinks.redis.RedisSink;
import com.fx.market.kafka.message.FxRateEventProto;
import com.fx.market.kafka.message.FxRateProto;
import com.twitter.chill.protobuf.ProtobufSerializer;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class FxMarketFlinkProcessor {

    private static final Logger log = LoggerFactory.getLogger(FxMarketFlinkProcessor.class);

    public static void main(String[] args) throws Exception {
        // On FLINK GUI Program Arguments enter for example: --bootstrapServers kafka:9071
        ParameterTool argParameter = ParameterTool.fromArgs(args);

        ParameterTool defaultParameters =
                ParameterTool.fromMap(Map.of(
                        "bootstrapServers", "kafka:9071",
                        "topic", "fx_rates",
                        "redisHost","fx-redis-cluster",
                        "redisPort","6379",
                        "redisUsername","default",
                        "redisPassword","default"
                ));

        ParameterTool finalParameters = defaultParameters.mergeWith(argParameter); // Default are overridden by Arg

        log.info("FX_INFO: bootstrapServers set to {}", finalParameters.get("bootstrapServers"));
        log.info("FX_INFO: topic set to {}", finalParameters.get("topic"));
        log.info("FX_INFO: Redis set to {}:{}", finalParameters.get("redisHost"), finalParameters.get("redisPort"));

        Configuration config = new Configuration();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
        env.addDefaultKryoSerializer(FxRateEventProto.class, ProtobufSerializer.class);
        env.getConfig().setGlobalJobParameters(finalParameters);

        KafkaSource<FxRateEventProto> source = KafkaSource.<FxRateEventProto>builder()
                .setBootstrapServers(finalParameters.get("bootstrapServers"))
                .setTopics(finalParameters.get("topic"))
                .setGroupId("flink-consumer-group")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new FxRateEventProtoMessageDeserializer())
                .build();

        var stream = env.fromSource(
                        source,
                        WatermarkStrategy.noWatermarks(),
                        "Kafka Source"
                )
                .flatMap(new FxRatesFlatMapper())
                .keyBy(FxRateProto::getPair);

        //redis-cli -h fx-redis-redis-cluster -p 6379 -a password
        stream.addSink(new RedisSink());

        env.execute("FlinkFxRateKafkaToRedisProcessor");
    }

    public static class FxRatesFlatMapper implements FlatMapFunction<FxRateEventProto, FxRateProto> {

        @Override
        public void flatMap(FxRateEventProto fxRateEvent, org.apache.flink.util.Collector<FxRateProto> collector) throws Exception {
            fxRateEvent.getRatesList().forEach(
                    collector::collect
            );
        }
    }
}
