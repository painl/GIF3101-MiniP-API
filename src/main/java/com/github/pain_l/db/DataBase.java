package com.github.pain_l.db;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DataBase {
    private MongoDatabase database;
    private static DataBase instance = new DataBase();

    private DataBase()
    {
        try {
            MongoClient mongoclient = new MongoClient(new MongoClientURI("mongodb://admin:bluegarou@ds163769.mlab.com:63769/bluegarou-db"));
            database = mongoclient.getDatabase("bluegarou-db");
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public static DataBase getInstance()
    {
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getUsersCollection () {
        return database.getCollection("users");
    }

    public MongoCollection<Document> getStatsCollection () {
        return database.getCollection("stats");
    }
}