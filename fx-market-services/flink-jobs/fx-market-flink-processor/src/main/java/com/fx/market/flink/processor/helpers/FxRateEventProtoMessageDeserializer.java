package com.fx.market.flink.processor.helpers;

import com.fx.market.kafka.message.FxRateEventProto;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.util.Arrays;

public class FxRateEventProtoMessageDeserializer implements DeserializationSchema<FxRateEventProto> {

    @Override
    public FxRateEventProto deserialize(byte[] bytes) throws IOException {
        try{
            return FxRateEventProto.parseFrom(bytes);
        } catch (Exception e) {
            System.out.println("InvalidProtocolBufferException:" + Arrays.toString(bytes));
        }
        return null;
    }

    @Override
    public boolean isEndOfStream(FxRateEventProto fxRateEventProto) {
        return false;
    }

    @Override
    public TypeInformation<FxRateEventProto> getProducedType() {
        return TypeInformation.of(FxRateEventProto.class);
    }
}