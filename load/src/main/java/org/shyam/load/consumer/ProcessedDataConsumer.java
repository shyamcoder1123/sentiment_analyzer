package org.shyam.load.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.shyam.load.db.ReviewRepository;
import org.shyam.load.model.SentimentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class ProcessedDataConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProcessedDataConsumer.class);
    @Autowired
    private ReviewRepository reviewRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "processed-data-topic", groupId = "processed-data-group")
    protected void consume(ConsumerRecord<String, String> record) {
        // processing logic
        try  {
            processAndUpdateDB(record.value(), record.key());
        }
        catch (Exception e) {
            logger.error("Could not process", e);
        }
    }

    private void processAndUpdateDB(String jsonMessage, String authId){
        logger.info("ProcessedMessage key {}", authId);
        SentimentDTO sentiment = getSentiment(jsonMessage);
        updateDB(authId, sentiment);
    }

    private SentimentDTO getSentiment(String jsonMessage)  {
        SentimentDTO sentiment;
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
        reviewRepository.findByAuthIdAndSentimentIsNull(authId)
                .ifPresent(review -> {
                    review.setSentiment(sentiment);
                    reviewRepository.save(review);
                    logger.info("Updated sentiment for authId: {}", authId);
                });
    }

}