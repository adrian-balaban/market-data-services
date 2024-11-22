package com.fx.market.fxmarketconnector.vendor.stub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FxRateEvent {

    LocalDateTime timestamp;
    List<FxRate> rates;

}
