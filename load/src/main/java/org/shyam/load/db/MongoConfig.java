package org.shyam.load.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConfig {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "product_reviews";
    private static MongoClient mongoclient;

    private MongoConfig(){}

    public static MongoDatabase getDatabase(){
        if(mongoclient==null){
            mongoclient = MongoClients.create(CONNECTION_STRING);
        }
        return mongoclient.getDatabase(DATABASE_NAME);
    }
}
