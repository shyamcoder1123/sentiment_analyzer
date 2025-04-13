package org.shyam.load.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("reviews")
public class Review {

    @Id
    private String id;

    private String review;
    private String authId;
    private String datetime;

    private SentimentDTO sentiment;


    // Getters, setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public SentimentDTO getSentiment() {
        return sentiment;
    }

    public void setSentiment(SentimentDTO sentiment) {
        this.sentiment = sentiment;
    }
}
