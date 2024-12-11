package com.fx.market.fxmarketconnector.vendor.stub;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "vendor.market.data.stub")
@EnableConfigurationProperties
public class MarketDataStubProperties {
    private String url;
}
