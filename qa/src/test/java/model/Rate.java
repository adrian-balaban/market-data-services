package model;

public class Rate {
    private String pair;
    private String baseCurrency;
    private String quoteCurrency;
    private String ask;
    private String bid;
    private String createdAt;

    public Rate() {}

    public String getPair() { return pair; }
    public void setPair(String pair) { this.pair = pair; }

    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }

    public String getQuoteCurrency() { return quoteCurrency; }
    public void setQuoteCurrency(String quoteCurrency) { this.quoteCurrency = quoteCurrency; }

    public String getAsk() { return ask; }
    public void setAsk(String ask) { this.ask = ask; }

    public String getBid() { return bid; }
    public void setBid(String bid) { this.bid = bid; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Rate{" +
                "pair='" + pair + '\'' +
                ", baseCurrency='" + baseCurrency + '\'' +
                ", quoteCurrency='" + quoteCurrency + '\'' +
                ", ask='" + ask + '\'' +
                ", bid='" + bid + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}