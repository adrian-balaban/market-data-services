package com.fx.market.fxmarketconnector.mappers;

import com.fx.market.fxmarketconnector.vendor.stub.model.FxRate;
import com.fx.market.fxmarketconnector.vendor.stub.model.FxRateEvent;
import org.springframework.stereotype.Component;
import com.fx.market.kafka.message.FxRateEventProto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
        event.setTimestamp(LocalDateTime.parse(fxRateEventProto.getTimestamp()));
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
