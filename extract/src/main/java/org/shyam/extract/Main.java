package org.shyam.extract;

import org.shyam.extract.producer.Producer;
import org.shyam.extract.producer_config.KafkaProducerConfig;

public class Main {
    public static void main(String[] args) {
        KafkaProducerConfig config = new KafkaProducerConfig();
        Producer producer = new Producer(config);
        producer.startProducing();
    }
}