package com.fx.market.fxmarketredisadapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@SpringBootApplication
public class FxMarketRedisAdapterApplication {

	public static void main(String[] args) {
		SpringApplication.run(FxMarketRedisAdapterApplication.class, args);
	}

}
