package com.fx.market.kafka;

import com.fx.market.kafka.message.FxRateEventProto;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class FxRateEventProtoSerde implements Serde<FxRateEventProto> {
    @Override
    public Serializer<com.fx.market.kafka.message.FxRateEventProto> serializer() {
        return new FxRateEventProtoSerializer();
    }

    @Override
    public Deserializer<com.fx.market.kafka.message.FxRateEventProto> deserializer() {
        return new FxRateEventProtoDeserializer();
    }

    public static class FxRateEventProtoSerializer implements Serializer<com.fx.market.kafka.message.FxRateEventProto> {
        @Override
        public byte[] serialize(String topic, com.fx.market.kafka.message.FxRateEventProto data) {
            return data.toByteArray();
        }
    }

    public static class FxRateEventProtoDeserializer implements Deserializer<com.fx.market.kafka.message.FxRateEventProto> {
        @Override
        public com.fx.market.kafka.message.FxRateEventProto deserialize(String topic, byte[] data) {
            try {
                return com.fx.market.kafka.message.FxRateEventProto.parseFrom(data);
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException("Failed to deserialize Protobuf message", e);
            }
        }
    }
}
