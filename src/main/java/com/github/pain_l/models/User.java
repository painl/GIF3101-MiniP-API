package com.github.pain_l.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pain_l.db.DataBase;
import org.bson.Document;

public class User {

    @JsonProperty
    private String username;
    @JsonProperty
    private String password;

    private DataBase db = DataBase.getInstance();

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
        db.getUsersCollection().insertOne(Document.parse(this.toJson()));
    }

    public User()
    {
    }

    public String toJson() {
        String toJson = null;
        try {
            toJson = new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return toJson;
    }
}
