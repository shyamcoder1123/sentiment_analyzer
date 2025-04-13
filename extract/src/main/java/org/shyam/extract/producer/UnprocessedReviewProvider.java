package org.shyam.extract.producer;
import org.bson.Document;

import java.util.List;

public interface UnprocessedReviewProvider {
    List<Document> fetchDocuments();
}
