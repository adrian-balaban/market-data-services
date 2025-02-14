package com.fx.market.fxmarketredisadapter.vendor.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

@Slf4j
@Service
public class RedisService {

    @Autowired
    private JedisCluster jedisCluster;

    public String getValueByKey(String key) {
        log.info("Searching Redis Cluster for key: {}", key);
        return jedisCluster.get(key);
    }

    public byte[] getValueByByteKey(byte[] key) {
        log.info("Searching Redis Cluster for byte key: {}", key);
        return jedisCluster.get(key);
    }

}
