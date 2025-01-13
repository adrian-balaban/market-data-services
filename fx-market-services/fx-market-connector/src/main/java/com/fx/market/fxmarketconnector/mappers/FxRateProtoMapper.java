package com.fx.market.fxmarketconnector.mappers;

import com.fx.market.kafka.message.FxRateEventProto;
import com.fx.model.FxRate;
import com.fx.model.FxRateEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        FxRateEvent event = new FxRateEvent();
        event.setTimestamp(fxRateEventProto.getTimestamp().toString());
        List<FxRate> list = new ArrayList<>();
        fxRateEventProto.getRatesList().forEach(protoRate -> {
                    FxRate fxRate = new FxRate();
                    fxRate.setAsk(protoRate.getAsk());
                    fxRate.setBid(protoRate.getBid());
                    fxRate.setPair(protoRate.getPair());
                    fxRate.setQuoteCurrency(protoRate.getQuoteCurrency());
                    fxRate.setBaseCurrency(protoRate.getBaseCurrency());
                    list.add(fxRate);
                    }
                );

        event.setRates(list);
        return event;
    }

}
