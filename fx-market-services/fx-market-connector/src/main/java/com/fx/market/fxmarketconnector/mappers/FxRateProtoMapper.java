package com.fx.market.fxmarketconnector.mappers;

import com.fx.market.fxmarketconnector.vendor.stub.model.FxRateEvent;
import org.springframework.stereotype.Component;
import com.fx.market.kafka.message.FxRateEventProto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FxRateProtoMapper {

    public FxRateEventProto toProto(FxRateEvent fxRateEvent) {
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
        // TODO NotUsed; Placeholder to be implemented if needed
        return new FxRateEvent();
    }

}
