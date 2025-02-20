package com.fx.market.fxmarketredisadapter.vendor.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Connection;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
public class RedisClusterClient {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public JedisCluster jedisCluster() {
        log.info("Initializing Redis CLuster client with the following config: {}",
                redisProperties.toString()
        );

        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        redisProperties.getNodes().forEach(
                nodeProperty -> jedisClusterNodes.add(
                        new HostAndPort(nodeProperty.getHost(), nodeProperty.getPort()))
        );

        GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(redisProperties.getPoolMaxTotal());
        poolConfig.setMaxIdle(redisProperties.getPoolMaxIdle());
        poolConfig.setMinIdle(redisProperties.getPoolMinIdle());

        return new JedisCluster(
                jedisClusterNodes,
                redisProperties.getConnectionTimeout(),
                redisProperties.getSoTimeout(),
                redisProperties.getMaxAttempts(),
                redisProperties.getPassword(),
                redisProperties.getUsername(),
                poolConfig);
    }
}
