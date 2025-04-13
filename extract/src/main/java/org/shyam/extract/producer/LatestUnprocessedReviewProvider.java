package org.shyam.extract.producer;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class LatestUnprocessedReviewProvider implements UnprocessedReviewProvider {
    private final MongoCollection<Document> collection;
    public LatestUnprocessedReviewProvider(MongoCollection<Document> collection) {
        this.collection = collection;
    }
    @Override
    public List<Document> fetchDocuments() {
        return collection.find(eq("sentiment", null))
                .sort(Sorts.descending("datetime"))
                .limit(10)
                .into(new ArrayList<>());
    }
}
