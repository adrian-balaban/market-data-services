package com.fx.market.fxmarketredisadapter.vendor.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "vendor.redis")
@EnableConfigurationProperties
public class RedisProperties {
    private List<NodeProperty> nodes;
    private String username;
    private String password;
    private Integer poolMaxTotal;
    private Integer poolMaxIdle;
    private Integer poolMinIdle;
    private Integer connectionTimeout;
    private Integer soTimeout;
    private Integer maxAttempts;

    @Data
    public static class NodeProperty {
        String host;
        Integer port;
    }
}
