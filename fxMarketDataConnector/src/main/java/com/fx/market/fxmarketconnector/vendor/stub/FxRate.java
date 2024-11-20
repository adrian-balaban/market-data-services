package com.fx.market.fxmarketconnector.vendor.stub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FxRate {

    private String pair;
    private String baseCurrency;
    private String quoteCurrency;
    private String ask; // keep as string to avoid conversion
    private String bid; // keep as string to avoid conversion
}
