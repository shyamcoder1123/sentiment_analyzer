package org.shyam.transform;

import org.shyam.transform.kafka.consumer.ConsumerManager;

public class Main {
    public static void main(String[] args) {
        ConsumerManager manager = new ConsumerManager(6);
        manager.startConsumers();
        Runtime.getRuntime().addShutdownHook(new Thread(manager::shutdownConsumers));
    }
}