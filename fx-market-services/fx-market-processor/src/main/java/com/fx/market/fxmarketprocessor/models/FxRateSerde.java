package com.fx.market.fxmarketprocessor.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class FxRateSerde implements Serde<FxRate> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Serializer<FxRate> serializer() {
        return (topic, data) -> {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize Rate", e);
            }
        };
    }

    @Override
    public Deserializer<FxRate> deserializer() {
        return (topic, data) -> {
            try {
                return objectMapper.readValue(data, FxRate.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize Rate", e);
            }
        };
    }

}
