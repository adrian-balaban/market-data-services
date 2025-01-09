package com.fx.market.fxmarketcamelconnector.camel.beans;

public class ExtractSseData {
    public String extractSseData(String body) {
        return body.substring(6);
    }
}