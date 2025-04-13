package org.shyam.transform.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;
import com.google.cloud.language.v1.Sentiment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GoogleNaturalLanguageService implements NaturalLanguageService{
    private static final Logger logger = LoggerFactory.getLogger(GoogleNaturalLanguageService.class);
    private int NUMBER_OF_MAXIMUM_CALLS_ALLOWED = 100;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private LanguageServiceClient languageClient;

    public GoogleNaturalLanguageService(){
        try{
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(System.getenv("GOOGLE_APPLICATION_CREDENTIALS")))
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            LanguageServiceSettings settings = LanguageServiceSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            languageClient = LanguageServiceClient.create(settings);
            registerShutdownHook();
        }catch (Exception e){
            logger.error("Error in creating  google client for sentiment : ", e);
        }
    }



    @Override
    public Sentiment getMessageSentiment(String text) {
        Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        // Detects the sentiment of the text
        Sentiment sentiment = null;
        if(NUMBER_OF_MAXIMUM_CALLS_ALLOWED-->0){
            sentiment = languageClient.analyzeSentiment(doc).getDocumentSentiment();
        }

        logger.info("Text: {}%n", text);
        logger.info("Sentiment: = {}", sentiment);
        return sentiment;
    }

    private void registerShutdownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }
    private void shutdown(){
        if(running.compareAndSet(true, false)){
            try{
                languageClient.shutdown();
                logger.info("Google Natural Language Service client shut down successfully.");
            }catch (Exception e){
                logger.error("Error shutting down Google Natural Language Service client", e);
            }
        }
    }
    // To be called when the service is no longer needed
    public void stop() {
        if (running.compareAndSet(true, false)) {
            shutdown();
        }
    }
}
