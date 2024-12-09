/*
 * Copyright 2015 Fabian Hueske / Vasia Kalavri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * POJO to hold sensor reading data.
 */
// describe what is doing this code
public class BloombergFXRateReading {

    public String pair;//: "USD/JPY",
    public String baseCurrency;//": "USD",
    public String quoteCurrency;//": "JPY",
    public double ask;//": 110.45,
    public double bid;//": 108.45
    public long timestamp;

    /**
     * Empty default constructor to satify Flink's POJO requirements.
     */
    public BloombergFXRateReading() { }

    public BloombergFXRateReading(String pair, String baseCurrency, String quoteCurrency, double ask, double bid, long timestamp) {
        this.pair = pair;
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.ask = ask;
        this.bid = bid;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "BloombergFXRateReading{" +
                "pair='" + pair + '\'' +
                ", baseCurrency='" + baseCurrency + '\'' +
                ", quoteCurrency='" + quoteCurrency + '\'' +
                ", ask=" + ask +
                ", bid=" + bid +
                ", timestamp=" + LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS")) +
                '}';
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
