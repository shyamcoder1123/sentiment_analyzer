package org.shyam.transform.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ConsumerManager {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerManager.class);
    private final int consumerCount;
    private final List<Thread> consumerThreads;
//    private final List<ContactConsumer> consumers;
    private final List<ReviewsConsumer> consumers;

    public ConsumerManager(int consumerCount) {
        this.consumerCount = consumerCount;
        this.consumerThreads = new ArrayList<>();
        this.consumers = new ArrayList<>();
    }

    public void startConsumers() {

        logger.info("Starting {} consumers for the 'contacts' topic...", consumerCount);

        for (int i = 0; i < consumerCount; i++) {
//            ContactConsumer consumer = new ContactConsumer();
            ReviewsConsumer consumer = new ReviewsConsumer();
            consumers.add(consumer);

            Thread thread = new Thread(consumer::startConsuming, "Worker-"+(i+1));
            consumerThreads.add(thread);
            thread.start();
        }

        logger.info("Started {} consumers for the 'contacts' topic.", consumerCount);

    }

    public void shutdownConsumers() {
        logger.info("Shutting down consumers...");

//        consumers.forEach(ContactConsumer::close);
        consumers.forEach(ReviewsConsumer::close);

        for (Thread thread : consumerThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread interrupted during shutdown: {}", e.getMessage());
            }
        }

        logger.info("All consumers have been shut down.");
    }
}

