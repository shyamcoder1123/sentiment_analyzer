package org.shyam.extract.producer_config;

import org.apache.kafka.common.serialization.StringSerializer;
import org.shyam.extract.ConfigLoader;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class KafkaProducerConfig {

    private final String topic;
    private final long produceIntervalMs;
    private final Properties kafkaProducerProperties;


    public KafkaProducerConfig() {
        ConfigLoader configLoader = new ConfigLoader();
        this.topic = configLoader.getProperty("producer.topic");
        this.produceIntervalMs = Long.parseLong(configLoader.getProperty("producer.interval.ms"));

        this.kafkaProducerProperties = new Properties();
        this.kafkaProducerProperties.put(BOOTSTRAP_SERVERS_CONFIG, configLoader.getProperty("producer.bootstrap.servers"));
        this.kafkaProducerProperties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        this.kafkaProducerProperties.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    }

    public String getTopic() {
        return topic;
    }

    public long getProduceIntervalMs() {
        return produceIntervalMs;
    }

    public Properties getKafkaProducerProperties() {
        return kafkaProducerProperties;
    }
}

