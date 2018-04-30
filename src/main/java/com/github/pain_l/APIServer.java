package com.github.pain_l;

import static spark.Spark.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import javaslang.control.Option;
import javaslang.control.Try;

public class APIServer
{
    private static ObjectMapper jsonObjectMapper = new ObjectMapper();

    public APIServer()
    {
    }

    public static void main(String[] args)
    {
        Integer portNumber = Try.of(() -> Integer.valueOf(System.getenv("PORT"))).orElseGet((t) -> {
            System.err.println("There was an error retrieving PORT env var using the default one (8080)");
            return 8080;
        });

        port(portNumber);

        get("/", (req, res) -> "BlueGarou API");

        get("/ping", (req, res) -> "pong");

//        get("/schedules/:startOfWeek", new UserHandler(), jsonObjectMapper::writeValueAsString);

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
