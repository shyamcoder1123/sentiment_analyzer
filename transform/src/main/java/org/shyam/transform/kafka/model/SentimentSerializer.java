package org.shyam.transform.kafka.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.cloud.language.v1.Sentiment;

import java.io.IOException;

public class SentimentSerializer extends JsonSerializer<Sentiment> {
    @Override
    public void serialize(Sentiment sentiment, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if(sentiment != null){
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("score", sentiment.getScore());
            jsonGenerator.writeNumberField("magnitude", sentiment.getMagnitude());
            jsonGenerator.writeEndObject();
        }
    }
}
