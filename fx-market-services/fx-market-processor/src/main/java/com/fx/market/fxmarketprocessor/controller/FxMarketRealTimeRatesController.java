package com.fx.market.fxmarketprocessor.controller;

import com.fx.market.fxmarketprocessor.models.FxRate;
import com.fx.market.fxmarketprocessor.service.FxMarketRealTimeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@RestController
@RequestMapping("/fx/rates")
public class FxMarketRealTimeRatesController {

    @Autowired
    private FxMarketRealTimeRatesService fxMarketRealTimeRatesService;

    @GetMapping
    public HashMap<String, FxRate> getAllFxRate() {
        return fxMarketRealTimeRatesService.getMostRecentFxRates();
    }

    @GetMapping("/{ccyPair}")
    public Mono<FxRate> getFxRateByCcyPair(@PathVariable String ccyPair) {
        return fxMarketRealTimeRatesService.getMostRecentFxRateByPair(ccyPair);
    }


}
