package com.github.pain_l.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pain_l.db.DataBase;
import org.bson.Document;

import java.util.List;

public class StatEntry {

    @JsonProperty
    public List<String> villagers;

    @JsonProperty
    public List<String> werewolves;

    @JsonProperty
    public String winningside;

    @JsonProperty
    public String username;

    public StatEntry(List<String> villagers, List<String> werewolves, String winningside, String username)
    {
        this.villagers = villagers;
        this.werewolves = werewolves;
        this.winningside = winningside;
        this.username = username;
        DataBase.getInstance().getStatsCollection().insertOne(Document.parse(this.toJson()));
    }

    private StatEntry()
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
