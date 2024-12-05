package com.fx.market.flink.processor.helpers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fx.market.flink.processor.model.FxRateEventProto;

import java.io.IOException;

public class MyCustomSerializer extends Serializer<FxRateEventProto> {
    @Override
    public void write(Kryo kryo, Output output, FxRateEventProto object) {
        output.write(object.toByteArray());
    }

    @Override
    public FxRateEventProto read(Kryo kryo, Input input, Class<FxRateEventProto> type) {
        // Deserialization logic
        try {
            return FxRateEventProto.parseFrom(input.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
