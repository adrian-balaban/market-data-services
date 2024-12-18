package com.fx.market.flinkorchestrator.vendor.flink.client;

import lombok.Data;

import java.util.Map;

@Data
public class JobProperties {
    private String name;
    private Map<String, String> arguments;
}
