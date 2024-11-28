package com.fx.market.flink.processor.pojo;


import com.fx.market.flink.processor.model.FxRateProto;

import java.time.LocalDateTime;

public class FxRate {

    public String pair;
    public String baseCurrency;
    public String quoteCurrency;
    public String ask; // keep as string to avoid conversion
    public String bid; // keep as string to avoid conversion

    public String getPair() {
        return pair;
    }

    public String getBid() {
        return bid;
    }

    public String getAsk() {
        return ask;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public static FxRate fromFxRateProto(FxRateProto fxRateProto) {
        var newFxRate = new FxRate();
        newFxRate.pair = fxRateProto.getPair();
        newFxRate.baseCurrency = fxRateProto.getBaseCurrency();
        newFxRate.quoteCurrency = fxRateProto.getQuoteCurrency();
        newFxRate.ask = fxRateProto.getAsk();
        newFxRate.bid = fxRateProto.getBid();
        return newFxRate;
    }

    @Override
    public String toString() {
        return "Timestamp:" + LocalDateTime.now() + "FxRate - Pair:"+ this.pair + " ASK:" + this.ask + " BID:" + this.bid;
    }
}
