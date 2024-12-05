package com.fx.market.flinkorchestrator.vendor.flink.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "flink")
@EnableConfigurationProperties
public class FlinkProperties {
    private String url;
}
