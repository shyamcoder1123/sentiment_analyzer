package org.shyam.extract.producer;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class UnprocessedReviewProviderFactory {
    private UnprocessedReviewProviderFactory(){}
    public static UnprocessedReviewProvider createReviewProvider(ReviewProducerType type, MongoCollection<Document> collection){
        UnprocessedReviewProvider reviewProducer;
        switch (type){
            case LATEST -> reviewProducer = new LatestUnprocessedReviewProvider(collection);
            case OLDEST -> reviewProducer = new OldestUnprocessedReviewProvider(collection);
            default -> reviewProducer = new LatestUnprocessedReviewProvider(collection);
        }
        return reviewProducer;
    }
}
