package com.fx.market.flink.processor.helpers;

import com.fx.market.flink.processor.pojo.FxRate;

import java.time.LocalDateTime;
import java.util.List;

public class FxRateEventProto {

    LocalDateTime timestamp;
    List<FxRate> rates;

}
