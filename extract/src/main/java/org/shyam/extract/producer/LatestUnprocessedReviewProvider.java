package org.shyam.extract.producer;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class LatestUnprocessedReviewProvider implements UnprocessedReviewProvider {
    private final MongoCollection<Document> collection;
    public LatestUnprocessedReviewProvider(MongoCollection<Document> collection) {
        this.collection = collection;
    }
    @Override
    public List<Document> fetchDocuments() {
        List<Document> documents = collection.find(
                        and(eq("sentiment", null), eq("status", "unprocessed"))
                )
                .sort(Sorts.descending("datetime"))
                .limit(10)
                .into(new ArrayList<>());

        for (Document doc : documents) {
            // Assuming you have the unique identifier (_id) to update each document
            ObjectId id = doc.getObjectId("_id");

            // Update the status to 'processing'
            collection.updateOne(
                    eq("_id", id),  // Filter by document _id
                    new Document("$set", new Document("status", "processing"))  // Set status field to 'processing'
            );
        }
        return documents;
    }
}
