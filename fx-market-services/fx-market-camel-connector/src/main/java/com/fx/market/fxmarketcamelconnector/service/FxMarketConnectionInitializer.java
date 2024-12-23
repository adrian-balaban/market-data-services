package com.fx.market.fxmarketcamelconnector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FxMarketConnectionInitializer {

    @Autowired
    private FxMarketConnectorService fxMarketConnectorService;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeFxMarketConnection() {
        log.info("Initializing Fx Market Connection");
        try {
            fxMarketConnectorService.processFxMarketRates();
        } catch (Exception ex) {
            log.error("Critical Error: ", ex);
        } finally {
            log.error("TODO: Shutdown!");
        }

        log.error("Unexpected finish of FxMarketConnection");
    }
}
