package com.github.pain_l.handlers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.pain_l.db.DataBase;
import com.github.pain_l.models.StatEntry;
import org.bson.Document;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.exclude;

public class StatsHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        Document status = new Document();
        DecodedJWT token;
        status.append("status", "ERROR");
        String authorization = request.headers("Authorization");
        if (authorization != null) {
            if ((token = getVerification(authorization.replaceFirst(".* ", ""))) == null) {
                response.status(400);
                return status.append("error", "Bad token.");
            }
        }
        else
        {
            response.status(401);
            return status.append("error", "Unauthorized.");
        }
        String username = token.getClaim("username").asString();

        switch (request.requestMethod()) {
            case "POST":
                Document body = Document.parse(request.body());
                List<String> villagers = (List<String>)body.get("villagers");
                List<String> werewolves = (List<String>)body.get("werewolves");
                String side = body.getString("winningside");
                if (villagers == null || villagers.size() < 1 || werewolves == null || werewolves.size() < 1
                        || (!side.equals("WEREWOLVES") && !side.equals("VILLAGERS")))
                    return status.append("error", "Error in request body.");
                StatEntry stats = new StatEntry(villagers, werewolves, side, username);
                response.status(200);
                status.append("status", "SUCCESS");
                return status.append("stats", stats);

            case "GET":
                ArrayList<Document> docs = new ArrayList<>();
                int i = 0;
                for (Document doc : DataBase.getInstance().getStatsCollection().find(eq("username", username)).projection(exclude("_id", "username")))
                    docs.add(doc.append("id", ++i));
                response.status(200);
                return status.append("status", "SUCESS").append("stats", docs);
        }
        return status;
    }

    private DecodedJWT getVerification(String token)
    {
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256("magic");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException exception){
            return null;
        }
    }
}
