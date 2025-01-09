package com.fx.market.flink.processor.mappers;


import com.fx.market.flink.processor.model.FxRateEventProto;
import com.fx.market.flink.processor.pojo.FxRate;
import com.fx.market.flink.processor.pojo.FxRateEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class FxRateProtoMapper {

    public static FxRateEvent fromProto(FxRateEventProto fxRateEventProto) {
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
