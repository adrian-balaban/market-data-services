package com.fx.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;

import java.util.Collections;
import java.util.Properties;

/*
  Creates the topic in Kafka with 9 partitions and replication factor of 1
  Necessary for KTable to work with multiple instances of processors, even on different topics
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KafkaAdminCreateTopic {

    public static void createTopic(String bootstrapServers, String topic) throws Exception {
        System.out.println("Creating topic "+ topic+" with bootstrap servers "+bootstrapServers);
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        try (Admin admin = Admin.create(properties)) {
            int partitions = 9;
            short replicationFactor = 1;
            NewTopic newTopic = new NewTopic(topic, partitions, replicationFactor);

            CreateTopicsResult result = admin.createTopics(Collections.singleton(newTopic));

            KafkaFuture<Void> future = result.values().get(topic);
            future.get();
        } catch (Exception ex) {
            System.out.println("Error creating topic: "+ ex.getMessage());
            throw ex;
        }

    }

}
