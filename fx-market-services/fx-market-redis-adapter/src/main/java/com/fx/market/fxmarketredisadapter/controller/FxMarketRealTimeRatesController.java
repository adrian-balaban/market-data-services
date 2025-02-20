package com.fx.market.fxmarketredisadapter.controller;

import com.fx.market.fxmarketredisadapter.model.FxRate;
import com.fx.market.fxmarketredisadapter.service.FxMarketRealTimeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fx/rates")
public class FxMarketRealTimeRatesController {

    @Autowired
    private FxMarketRealTimeRatesService fxMarketRealTimeRatesService;

    @GetMapping("/{ccyPair}")
    public FxRate getFxRateByCcyPair(@PathVariable String ccyPair) {
        return fxMarketRealTimeRatesService.getMostRecentFxRateByPair(ccyPair);
    }


}
