package com.fx.market.flinkorchestrator.vendor.flink.client;

import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlinkClientConfiguration {

        //Set OkHttp as Feign Client
        @Bean
        public OkHttpClient client() {
            return new OkHttpClient();
        }
}
