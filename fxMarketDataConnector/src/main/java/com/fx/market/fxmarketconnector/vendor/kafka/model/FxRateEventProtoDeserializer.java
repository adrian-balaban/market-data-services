package com.fx.market.fxmarketconnector.vendor.kafka.model;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;
import com.fx.market.kafka.message.FxRateEventProto;

public class FxRateEventProtoDeserializer implements Deserializer<FxRateEventProto>{
    @Override
    public FxRateEventProto deserialize(String topic, byte[] data) {
        try {
            return FxRateEventProto.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            throw new RuntimeException("excepiton while parsing");
        }
    }
}