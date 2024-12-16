package com.fx.market.flinkorchestrator.vendor.flink.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "flink")
@EnableConfigurationProperties
public class FlinkProperties {
    private String url;
    private List<JobProperties> jobs;

    //JarId contains jar name and job name is equivalent to jar name
    public Map<String, String> getJobArguments(String jarId) {
        return
                jobs.stream()
                        .filter(jobProperties -> jarId.contains(jobProperties.getName()))
                        .map(JobProperties::getArguments)
                        .findAny()
                .orElse(Collections.emptyMap());
    }
}
