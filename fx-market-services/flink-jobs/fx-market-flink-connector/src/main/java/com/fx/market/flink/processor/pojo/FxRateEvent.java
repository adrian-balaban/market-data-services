package com.fx.market.flink.processor.pojo;

import java.util.List;

public class FxRateEvent {

    public String timestamp;
    public List<FxRate> rates;

    public String getTimestamp() {
        return timestamp;
    }

    public List<FxRate> getRates() {
        return rates;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setRates(List<FxRate> rates) {
        this.rates = rates;
    }
}
