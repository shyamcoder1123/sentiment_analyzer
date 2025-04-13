package org.shyam.transform.kafka.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.cloud.language.v1.Sentiment;

public class ProcessedMessage {
    private final String authId;
    @JsonSerialize(using = SentimentSerializer.class)// custom serializer for Sentiment
    private final Sentiment sentiment;
    public ProcessedMessage(String authId, Sentiment sentiment){
        this.authId = authId;
        this.sentiment = sentiment;
    }

    public String getAuthId() {
        return authId;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    @Override
    public String toString() {
        return "ProcessedMessage{" +
                "authId='" + authId + '\'' +
                ", sentiment=" + sentiment +
                '}';
    }
}
