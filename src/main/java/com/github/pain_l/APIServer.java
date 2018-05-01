package com.github.pain_l;

import static spark.Spark.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pain_l.handlers.UserHandler;

public class APIServer
{
    private static ObjectMapper jsonObjectMapper = new ObjectMapper();

    public APIServer()
    {
    }

    public static void main(String[] args)
    {
        Integer portNumber =  1357;
        try {
            portNumber = Integer.valueOf(System.getenv("PORT"));
        }
        catch (Exception e) {
            System.err.println("There was an error retrieving PORT env var using the default one (1357)");
        }
        port(portNumber);

        get("/", (req, res) -> "BlueGarou API");

        post("/register", new UserHandler().registerHandler, jsonObjectMapper::writeValueAsString);
        post("/login", new UserHandler().loginHandler, jsonObjectMapper::writeValueAsString);

        options("*", (request, response) -> "");
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
            response.header("Access-Control-Allow-Headers", "*");
            response.type("application/json");
        });
    }
}
