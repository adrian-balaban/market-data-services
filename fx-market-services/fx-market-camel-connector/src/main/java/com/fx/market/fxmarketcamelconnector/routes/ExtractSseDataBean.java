package com.fx.market.fxmarketcamelconnector.routes;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtractSseDataBean {
    public String extractSseData(String body) {
        return body.substring(6);
    }
    public boolean messageHasData(String body) {return !body.isEmpty();}
}