package com.fx.market.fxmarketredisadapter.service;

import com.fx.market.fxmarketredisadapter.model.FxRate;
import com.fx.market.fxmarketredisadapter.vendor.redis.RedisService;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class FxMarketRealTimeRatesService {

    @Autowired
    private RedisService redisService;

    public FxRate getMostRecentFxRateByPair(String ccyPair) {
        try {
            return FxRate.fromFxRateProtoBytes(
                    redisService.getValueByByteKey(
                            mapCurrencyPair(ccyPair).getBytes(StandardCharsets.UTF_8)
                    )
            );
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e); // todo; handle it properly
        }
        return FxRate.builder().build();
    }

    public static String mapCurrencyPair(String input) {
        return input.substring(0, 3) + "/" + input.substring(3, 6);
    }
}
