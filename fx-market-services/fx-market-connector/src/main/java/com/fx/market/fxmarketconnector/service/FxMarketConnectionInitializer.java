package com.fx.market.fxmarketconnector.service;

import com.fx.utils.KafkaAdminCreateTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Properties;

@Slf4j
@Service
public class FxMarketConnectionInitializer {

    @Autowired
    private FxMarketConnectorService fxMarketConnectorService;

    @Value("${kafka.topic}")
    private String topic;

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeFxMarketConnection() {
        try  {
            KafkaAdminCreateTopic.createTopic(bootstrapServers, topic);
        } catch (Exception ex) {
            log.error("Error creating topic: ", ex);
        }

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
