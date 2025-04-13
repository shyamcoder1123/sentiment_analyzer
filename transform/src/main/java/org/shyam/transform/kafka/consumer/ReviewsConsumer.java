package org.shyam.transform.kafka.consumer;

import com.google.cloud.language.v1.Sentiment;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.shyam.transform.kafka.model.ProcessedMessage;
import org.shyam.transform.kafka.producer.Producer;
import org.shyam.transform.kafka.producer.config.KafkaProducerConfig;
import org.shyam.transform.service.LanguageServiceFactory;
import org.shyam.transform.service.LanguageServices;
import org.shyam.transform.service.NaturalLanguageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReviewsConsumer extends ConsumerTemplate {

    private static final Logger logger = LoggerFactory.getLogger(ReviewsConsumer.class);

    public ReviewsConsumer() {
        super("test-topic", "contacts-test-group", 1000L);
    }

    @Override
    protected void consume(ConsumerRecord<String, String> record) {
        // processing logic
        try  {
            processAndSend(record.value(), record.key());
        }
        catch (Exception e) {
            logger.error("Could not send for processing", e);
        }
    }

    @Override
    protected void reject(ConsumerRecord<String, String> record, Exception e) {
        logger.error("Rejecting` record: key = {}, value = {}", record.key(), record.value());
    }

    private void processAndSend(String jsonMessage, String authId){
        logger.info("ProcessedMessage key {}", authId);
        Sentiment sentiment = sendToProcessing(jsonMessage);
        ProcessedMessage processedMessage = new ProcessedMessage(authId, sentiment);
        sendToKafka(processedMessage);
    }
    private void sendToKafka(ProcessedMessage processedMessage){
        KafkaProducerConfig config = new KafkaProducerConfig();
        Producer producer = new Producer(config);
        producer.sentToTopic(processedMessage);
    }

    public Sentiment sendToProcessing(String jsonMessage){
        NaturalLanguageService languageService = LanguageServiceFactory.createLanguageService(LanguageServices.GOOGLE_NATURAL_LANGUAGE);
        Sentiment sentiment = languageService.getMessageSentiment(jsonMessage);
        logger.info("Sentiment of the data {}", sentiment);
        return sentiment;
    }

}