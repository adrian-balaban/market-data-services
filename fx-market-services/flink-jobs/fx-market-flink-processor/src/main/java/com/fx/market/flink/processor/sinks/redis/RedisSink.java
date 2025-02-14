package com.fx.market.flink.processor.sinks.redis;

import com.fx.market.kafka.message.FxRateProto;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.List;

public class RedisSink extends RichSinkFunction<FxRateProto> {
    RedisCluster redisCluster;

    @Override
    public void invoke(FxRateProto value, Context context) throws Exception {
        redisCluster.saveAsProtoObject(value.getPair(), value);
    }

    @Override
    public void writeWatermark(Watermark watermark) throws Exception {
        super.writeWatermark(watermark);
    }

    @Override
    public void finish() throws Exception {
        super.finish();
    }

    @Override
    public void open(OpenContext openContext) throws Exception {
        System.out.println("FX_DEBUG - RedisSink - openContext: " + openContext.toString());
        final ParameterTool parameters = ParameterTool.fromMap(getRuntimeContext().getGlobalJobParameters());
        final String redisHost = parameters.get("redisHost");
        final String redisPort = parameters.get("redisPort");
        final String redisUsername = parameters.get("redisUsername");
        final String redisPassword = parameters.get("redisPassword");

        this.redisCluster = new RedisCluster(
                List.of(
                        Pair.of(redisHost, redisPort)
                ), redisUsername, redisPassword);

        super.open(openContext);
    }
}