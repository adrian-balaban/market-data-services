package com.fx.market.flink.processor.pojo;

import java.time.LocalDateTime;
import java.util.List;

public class FxRateEvent {

    public LocalDateTime timestamp;
    public List<FxRate> rates;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<FxRate> getRates() {
        return rates;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setRates(List<FxRate> rates) {
        this.rates = rates;
    }
}
