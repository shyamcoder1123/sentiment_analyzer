package org.shyam.transform.service;

import com.google.cloud.language.v1.Sentiment;

public interface NaturalLanguageService {
    Sentiment getMessageSentiment(String text);
}
