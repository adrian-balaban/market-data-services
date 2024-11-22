package com.fx.market.fxmarketconnector.vendor.kafka.model;

import com.fx.market.kafka.message.FxRateEventProto;
import org.apache.kafka.common.serialization.Serializer;

public class FxRateProtMessageSerializer implements Serializer<FxRateEventProto>{
    @Override
    public byte[] serialize(String topic, FxRateEventProto data) {
        return data.toByteArray();
    }
}