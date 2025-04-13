package org.shyam.transform.service;

public class LanguageServiceFactory {
    private LanguageServiceFactory(){}

    public static NaturalLanguageService createLanguageService(LanguageServices serviceName){
        NaturalLanguageService naturalLanguageService;
        switch (serviceName){
            case GOOGLE_NATURAL_LANGUAGE:
                naturalLanguageService = new GoogleNaturalLanguageService();
            default:
                naturalLanguageService = new GoogleNaturalLanguageService();
        }
        return naturalLanguageService;
    }
}
