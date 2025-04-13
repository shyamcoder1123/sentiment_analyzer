package org.shyam.load.db;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.shyam.load.consumer.SentimentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class MongoService {
    private static final Logger logger = LoggerFactory.getLogger(MongoService.class);
    private final MongoCollection<Document> collection;

    public MongoService(){
        MongoDatabase database = MongoConfig.getDatabase();
        String collectionName = "product_reviews";
        this.collection = database.getCollection(collectionName);
    }


    public void updateSentiment(String authId, SentimentDTO sentiment){
        try{
            float score = sentiment.score();
            float magnitude = sentiment.magnitude();
            logger.info("This is the current sentiment: {}", sentiment);
            Document sentimentDoc = new Document("score", score).append("magnitude", magnitude);

            String SENTIMENT = "sentiment";
            String AUTH_ID = "authId";

            Bson filter = Filters.and(
                    Filters.eq(AUTH_ID, authId),
                    Filters.eq(SENTIMENT, null)
            );

            System.out.println("Filter used: " + filter.toBsonDocument(Document.class, MongoClientSettings.getDefaultCodecRegistry()));

            UpdateResult  updateResult = collection.updateOne(
                    and(eq(AUTH_ID, authId)),set(SENTIMENT, sentimentDoc)
            );
            logger.info("Matched documents : {}",updateResult.getMatchedCount());
            logger.info("Modified documents : {}",updateResult.getModifiedCount());
        }catch (Exception e){
            logger.error("MongoDB update failed : {}", e);
        }
    }
}
