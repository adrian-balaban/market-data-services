package testvisa;

import java.util.List;

public class ExchangeRates {
    private String timestamp;
    private List<Rate> rates;


    public ExchangeRates(String timestamp, List<Rate> rates) {
        this.timestamp = timestamp;
        this.rates = rates;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }

    @Override
    public String toString() {
        return "ExchangeRates{" +
                "timestamp='" + timestamp + '\'' +
                ", rates=" + rates +
                '}';
    }

    public static class Rate {
        private String pair;
        private String baseCurrency;
        private String quoteCurrency;
        private String ask;
        private String bid;

        public Rate(String pair, String baseCurrency, String quoteCurrency, String ask, String bid) {
            this.pair = pair;
            this.baseCurrency = baseCurrency;
            this.quoteCurrency = quoteCurrency;
            this.ask = ask;
            this.bid = bid;
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

        public String getAsk() {
            return ask;
        }

        public void setAsk(String ask) {
            this.ask = ask;
        }

        public String getBid() {
            return bid;
        }

        public void setBid(String bid) {
            this.bid = bid;
        }

        @Override
        public String toString() {
            return "Rate{" +
                    "pair='" + pair + '\'' +
                    ", baseCurrency='" + baseCurrency + '\'' +
                    ", quoteCurrency='" + quoteCurrency + '\'' +
                    ", ask='" + ask + '\'' +
                    ", bid='" + bid + '\'' +
                    '}';
        }
    }
}
