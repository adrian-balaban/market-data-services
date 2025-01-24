package com.fx.market.fxmarketprocessor.vendor.kafka;

import com.fx.market.kafka.FxRateEventProtoSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.HostInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.*;

@Slf4j
@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${kafka.application-id-config}")
    private String applicationIdConfig;
    @Value("${kafka.application-self-host}")
    private String applicationSelfHost;
    @Value("${kafka.application-self-port}")
    private String getApplicationSelfPort;
    @Value("${kafka.application-state-dir-config}")
    private String applicationStateDirConfig;

    @Bean(name =
            KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs() {
        return new KafkaStreamsConfiguration(Map.of(
                APPLICATION_ID_CONFIG, applicationIdConfig,
                APPLICATION_SERVER_CONFIG, String.format("%s:%s", applicationSelfHost, getApplicationSelfPort),
                STATE_DIR_CONFIG, applicationStateDirConfig,
                BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName(),
                DEFAULT_VALUE_SERDE_CLASS_CONFIG, FxRateEventProtoSerde.class.getName(),
                DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName(),
                StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 0, // Set commit interval to 1000 ms
                StreamsConfig.RESTORE_CONSUMER_PREFIX, 0,
                AUTO_OFFSET_RESET_CONFIG, "latest"
        ));
    }

    @Bean
    public HostInfo hostInfo() {
        final String selfUrl = String.format("%s:%s", applicationSelfHost, getApplicationSelfPort);
        log.info("Creating host info: {}", selfUrl);
        return new HostInfo(applicationSelfHost, Integer.parseInt(getApplicationSelfPort));
    }
}