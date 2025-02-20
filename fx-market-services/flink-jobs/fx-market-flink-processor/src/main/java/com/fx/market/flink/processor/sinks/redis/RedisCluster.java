package com.fx.market.flink.processor.sinks.redis;

import com.fx.market.kafka.message.FxRateProto;
import org.apache.commons.lang3.tuple.Pair;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RedisCluster implements Serializable {
    private static final long serialVersionUID = 1L;
    private static JedisCluster jedisCluster;

    public RedisCluster(List<Pair<String, String>> redisNodes, String userName, String password) {
        // Configure Redis Cluster nodes
        Set<HostAndPort> nodes = new HashSet<>();
        redisNodes.forEach(
                node -> {
                    // Pair left: host // Pair right: port
                    nodes.add(new HostAndPort(node.getLeft(), Integer.parseInt(node.getRight())));
                }
        );

        jedisCluster = new JedisCluster(nodes, userName, password);
    }

    public void saveAsProtoObject(String key, FxRateProto value) {
        jedisCluster.set(key.getBytes(StandardCharsets.UTF_8),
                value.toBuilder()
                        .setSavedAt(LocalDateTime.now().toString())
                        .build().toByteArray());
    }

}
