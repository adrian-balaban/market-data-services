package com.fx.market.flinkorchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FlinkOrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlinkOrchestratorApplication.class, args);
	}

}
