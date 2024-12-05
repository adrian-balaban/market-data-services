package com.fx.market.flink.processor.helpers;

import com.fx.market.flink.processor.model.FxRateEventProto;
import com.fx.market.flink.processor.pojo.FxRate;
import com.fx.market.flink.processor.pojo.FxRateEvent;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;


import java.io.IOException;
import java.util.Arrays;

import static com.fx.market.flink.processor.mappers.FxRateProtoMapper.fromProto;

public class FxRateEventProtoMessageDeserializer implements DeserializationSchema<FxRateEvent> {

    @Override
    public FxRateEvent deserialize(byte[] bytes) throws IOException {
        try{
            //System.out.println("MJ DEBUG deserialize:" + Arrays.toString(bytes));
            //System.out.println("MJ1 DEBUG deserialize:" + FxRateEventProto.parseFrom(bytes).toString());
            var fxProto = FxRateEventProto.parseFrom(bytes);
            return fromProto(fxProto);
        } catch (Exception e) {
            System.out.println("InvalidProtocolBufferException:" + Arrays.toString(bytes));
        }
        return null;
    }

    @Override
    public boolean isEndOfStream(FxRateEvent fxRateEventProto) {
        return false;
    }

    @Override
    public TypeInformation<FxRateEvent> getProducedType() {
        return TypeInformation.of(FxRateEvent.class);
    }
}