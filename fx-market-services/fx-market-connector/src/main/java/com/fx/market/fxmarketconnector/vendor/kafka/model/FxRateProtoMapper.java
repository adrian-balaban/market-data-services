package com.fx.market.fxmarketconnector.vendor.kafka.model;

import com.fx.market.fxmarketconnector.vendor.stub.FxRateEvent;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.fx.market.kafka.message.FxRateEventProto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FxRateProtoMapper {

    private final ModelMapper modelMapper = ModelMapperFactory.getMapper();

    public FxRateEventProto toProto(FxRateEvent fxRateEvent) {
        //modelMapper.map(fxRateEvent, com.fx.market.kafka.message.FxRateEventProto.class);

        FxRateEventProto.Builder fxRateEventProto = FxRateEventProto.newBuilder()
                .setTimestamp(fxRateEvent.getTimestamp().toString());
        fxRateEvent.getRates().forEach(fxRate -> {
            fxRateEventProto.addRates(
                    com.fx.market.kafka.message.FxRateProto.newBuilder()
                            .setAsk(fxRate.getAsk())
                            .setBid(fxRate.getBid())
                            .setPair(fxRate.getPair())
                            .setBaseCurrency(fxRate.getBaseCurrency())
                            .setQuoteCurrency(fxRate.getQuoteCurrency())
                            .build());
        });

        return fxRateEventProto.build();
    }

    public FxRateEvent fromProto(FxRateEventProto fxRateEventProto) {
        return modelMapper.map(fxRateEventProto, FxRateEvent.class);
    }

}
