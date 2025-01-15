package com.fx.market.fxmarketcamelconnector.mappers;

import com.fx.market.kafka.message.FxRateEventProto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FxRateProtoMapper {

    public byte[] toProto(com.fx.model.FxRateEvent fxRateEvent) {
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

        return fxRateEventProto.build().toByteArray();
    }

}
