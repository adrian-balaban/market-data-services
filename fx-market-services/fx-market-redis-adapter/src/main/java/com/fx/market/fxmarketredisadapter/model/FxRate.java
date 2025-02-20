package com.fx.market.fxmarketredisadapter.model;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FxRate {

    String pair;
    String baseCurrency;
    String quoteCurrency;
    String ask; // keep as string to avoid conversion
    String bid; // keep as string to avoid conversion
    String createdAt;
    String savedAt;

    public static FxRate fromFxRateProtoBytes(byte[] fxRateProto) throws InvalidProtocolBufferException {
        return fromFxRateProto(
                com.fx.market.kafka.message.FxRateProto.parseFrom(fxRateProto));
    }

    public static FxRate fromFxRateProto(com.fx.market.kafka.message.FxRateProto fxRateProto) {
        return FxRate.builder()
                .pair(fxRateProto.getPair())
                .baseCurrency(fxRateProto.getBaseCurrency())
                .quoteCurrency(fxRateProto.getQuoteCurrency())
                .ask(fxRateProto.getAsk())
                .bid(fxRateProto.getBid())
                .createdAt(fxRateProto.getCreatedAt())
                .savedAt(fxRateProto.getSavedAt())
                .build();
    }
}
