package org.shyam.extract.producer;

import com.mongodb.client.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.bson.Document;
import org.shyam.extract.ConfigLoader;
import org.shyam.extract.producer_config.KafkaProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Producer{

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    private final MongoCollection<Document> collection;
    private final UnprocessedReviewProvider unprocessedReviewProvider;
    private final MongoClient mongoClient;
    private final ConfigLoader configLoader = new ConfigLoader();
    private final KafkaProducer<String, String> kafkaProducer;
    private final String topic;
    private final long produceIntervalMs;

    public Producer(KafkaProducerConfig config) {
        String mongoUri = configLoader.getProperty("mongo.uri");
        this.mongoClient = MongoClients.create(mongoUri);
        String dbName = configLoader.getProperty("mongo.database");
        MongoDatabase database = mongoClient.getDatabase(dbName);
        String collectionName = configLoader.getProperty("mongo.collection");
        this.collection = database.getCollection(collectionName);

        this.topic = config.getTopic();
        this.produceIntervalMs = config.getProduceIntervalMs();
        this.kafkaProducer = new KafkaProducer<>(config.getKafkaProducerProperties());

        this.unprocessedReviewProvider = UnprocessedReviewProviderFactory.createReviewProvider(ReviewProducerType.LATEST, collection);
    }
    public void startProducing() {
        String produceIntervalMsStr = configLoader.getProperty("producer.interval.ms");
        long produceIntervalMs = Long.parseLong(produceIntervalMsStr);

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                processDocuments();
            }
        }, 0, produceIntervalMs);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gracefully...");
            close();
        }));

        // Keep the program running
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processDocuments() {
        logger.info("Processing one random record...");

        // Fetch one document based on strategy
        List<Document> documents = unprocessedReviewProvider.fetchDocuments();
        for(Document document : documents){
            if (document != null) {
                logger.info("Produced one Document: {}", document.toJson());
                produce(document);
            } else {
                logger.info("No documents found in the collection.");
            }
        }
        logger.info("{} documents produced", documents.size());
        logger.info("--------------------------------------------------------------------------");
    }

    private void produce(Document document) {
        String message = document.toJson();

        if (document.get("authId") == null) {
            logger.error("Can't fetch authId, not producing");
            return;
        }

        String key = document.get("authId").toString();

        try {
            kafkaProducer.send(new ProducerRecord<>(topic, key, message));
            logger.info("Document sent to Kafka topic: {}", topic);
        } catch (Exception e) {
            logger.error("Failed to send document to Kafka", e);
        }
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            logger.info("MongoClient closed.");
        }
        if (kafkaProducer != null) {
            kafkaProducer.close();
            logger.info("KafkaProducer closed.");
        }
    }
}
