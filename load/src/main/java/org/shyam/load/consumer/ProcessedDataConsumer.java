package org.shyam.load.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;;
import org.shyam.load.db.MongoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessedDataConsumer extends ConsumerTemplate {

    private static final Logger logger = LoggerFactory.getLogger(ProcessedDataConsumer.class);

    public ProcessedDataConsumer() {
        super("processed-data-topic", "processed-data-group", 1000L);
    }

    @Override
    protected void consume(ConsumerRecord<String, String> record) {
        // processing logic
        try  {
            processAndUpdateDB(record.value(), record.key());
        }
        catch (Exception e) {
            logger.error("Could not process", e);
        }
    }

    @Override
    protected void reject(ConsumerRecord<String, String> record, Exception e) {
        logger.error("Rejecting` record: key = {}, value = {}", record.key(), record.value());
    }

    private void processAndUpdateDB(String jsonMessage, String authId){
        logger.info("ProcessedMessage key {}", authId);
        SentimentDTO sentiment = getSentiment(jsonMessage);
        updateDB(authId, sentiment);
    }

    private SentimentDTO getSentiment(String jsonMessage)  {
        SentimentDTO sentiment;
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            JsonNode root = objectMapper.readTree(jsonMessage);
            JsonNode sentimentNode = root.get("sentiment");
            sentiment = objectMapper.treeToValue(sentimentNode, SentimentDTO.class);
            logger.info("Sentiment of the current document : {}", sentiment);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return sentiment;
    }

    private void updateDB(String authId, SentimentDTO sentiment){
        MongoService service = new MongoService();
        service.updateSentiment(authId, sentiment);
    }

}