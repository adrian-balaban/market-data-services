package com.fx.model;

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
}
