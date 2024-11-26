package com.fx.market.fxmarketprocessor.vendor.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import com.fx.market.kafka.message.FxRateEventProto;

public class ProtoMessageSerde implements Serde<FxRateEventProto> {
    @Override
    public Serializer<FxRateEventProto> serializer() {
        return new FxRateEventProtoSerializer();
    }

    @Override
    public Deserializer<FxRateEventProto> deserializer() {
        return new FxRateEventProtoDeserializer();
    }

    public static class FxRateEventProtoSerializer implements Serializer<FxRateEventProto> {
        @Override
        public byte[] serialize(String topic, FxRateEventProto data) {
            return data.toByteArray();
        }
    }

    public static class FxRateEventProtoDeserializer implements Deserializer<FxRateEventProto> {
        @Override
        public FxRateEventProto deserialize(String topic, byte[] data) {
            try {
                return FxRateEventProto.parseFrom(data);
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException("Failed to deserialize Protobuf message", e);
            }
        }
    }
}