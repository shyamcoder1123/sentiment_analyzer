package org.shyam.transform.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.language.v1.Sentiment;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.bson.Document;
import org.shyam.transform.kafka.model.ProcessedMessage;
import org.shyam.transform.kafka.producer.config.KafkaProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    private final ConfigLoader configLoader = new ConfigLoader();
    private final KafkaProducer<String, String> kafkaProducer;
    private final String topic;
    private final long produceIntervalMs;

    public Producer(KafkaProducerConfig config) {
        this.topic = config.getTopic();
        this.produceIntervalMs = config.getProduceIntervalMs();
        this.kafkaProducer = new KafkaProducer<>(config.getKafkaProducerProperties());
    }

    public void sentToTopic(ProcessedMessage processedMessage) {
        logger.info("Processing one random record...");

        if (processedMessage != null) {
            logger.info("sent one ProcessedMessage: {}", processedMessage);
            produce(processedMessage);
        } else {
            logger.info("No ProcessedMessages received");
        }
    }

    private void produce(ProcessedMessage processedMessage) {

        if (processedMessage.getAuthId() == null) {
            logger.error("Can't fetch authId, not producing");
            return;
        }

        String key = processedMessage.getAuthId();

        try {
            // Jackson to convert to JSON string
            String json = new ObjectMapper().writeValueAsString(processedMessage);
            kafkaProducer.send(new ProducerRecord<>(topic, key, json));
            logger.info("ProcessedMessage sent to Kafka topic: {}", topic);
        } catch (Exception e) {
            logger.error("Failed to send ProcessedMessage to Kafka", e);
        }
    }

    public void close() {
        if (kafkaProducer != null) {
            kafkaProducer.close();
            logger.info("KafkaProducer closed.");
        }
    }
}
