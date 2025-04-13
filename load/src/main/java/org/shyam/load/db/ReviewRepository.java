package org.shyam.load.db;

import org.shyam.load.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review,String> {
    Optional<Review> findByAuthIdAndSentimentIsNull(String authId);
}
