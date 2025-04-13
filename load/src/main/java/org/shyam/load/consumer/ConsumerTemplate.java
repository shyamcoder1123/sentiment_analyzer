package org.shyam.load.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.shyam.load.consumer.config.KafkaConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ConsumerTemplate {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerTemplate.class);
    private static final int MAX_RETRIES = 3;
    private static final Duration POLL_TIMEOUT = Duration.ofMillis(1000);

    private final String topic;
    private final KafkaConsumer<String, String> consumer;
    private final long pollInterval;
    private final AtomicBoolean running;

    public ConsumerTemplate(String topic, String groupId, long pollInterval) {
        this.topic = topic;
        Properties properties = KafkaConsumerConfig.getKafkaConsumerProperties(groupId);
        this.consumer = new KafkaConsumer<>(properties);
        this.pollInterval = pollInterval;
        this.running = new AtomicBoolean(false);
    }

    protected abstract void consume(ConsumerRecord<String, String> record) throws Exception;

    protected abstract void reject(ConsumerRecord<String, String> record, Exception e);

    protected void processRecords(ConsumerRecords<String, String> records) {
        for (ConsumerRecord<String, String> record : records) {
            try {
                logger.info("Processing message - key: {}, value: {}, offset: {}",
                        record.key(), record.value(), record.offset());

                try {
                    consume(record);
//                    Thread.sleep(500);
                } catch (Exception e) {
                    reject(record, e);
                }
            } catch (Exception e) {
                logger.error("Error processing record: {}", record, e);
            }
        }
    }

    public void startConsuming() {
        Thread shutdownHook = registerShutdownHook();

        consumer.subscribe(Collections.singletonList(topic));
        logger.info("Consuming messages from topic: {}", topic);
        running.set(true);

        try {
            while (running.get()) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(POLL_TIMEOUT);

                    if (!records.isEmpty()) {
                        processRecords(records);
                        commitOffsets();
                    }

                    if (pollInterval > 0L) {
                        Thread.sleep(pollInterval);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Consumer interrupted, initiating shutdown");
                    break;
                } catch (Exception e) {
                    handleConsumerError(e);
                }
            }
        } catch (WakeupException e) {
            if (running.get()) {
                throw e;
            }
        } finally {
            cleanup(shutdownHook);
        }
    }

    public void close() {
        if (running.compareAndSet(true, false)) {
            consumer.wakeup();
        }
    }

    private void commitOffsets() {
        int retries = 0;
        boolean committed = false;

        while (!committed && retries < MAX_RETRIES) {
            try {
                consumer.commitSync();
                committed = true;
            } catch (Exception e) {
                retries++;
                if (retries >= MAX_RETRIES) {
                    logger.error("Failed to commit offsets after {} retries", MAX_RETRIES, e);
                    throw e;
                }
                logger.warn("Failed to commit offsets, retry {}/{}", retries, MAX_RETRIES, e);
                try {
                    Thread.sleep(1000L * retries);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while retrying commit", ie);
                }
            }
        }
    }

    private void handleConsumerError(Exception e) {
        logger.error("Error in consumer loop", e);
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            close();
        }
    }

    private Thread registerShutdownHook() {
        Thread shutdownHook = new Thread(this::close);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        return shutdownHook;
    }

    private void cleanup(Thread shutdownHook) {
        try {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        } catch (IllegalStateException e) {
            logger.error("Error removing shutdown hook", e);;
        }

        try {
            consumer.close();
            logger.info("Kafka consumer closed.");
        } catch (Exception e) {
            logger.error("Error closing Kafka consumer", e);
        }
    }


}
