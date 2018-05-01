package com.github.pain_l.handlers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.pain_l.db.DataBase;
import com.github.pain_l.models.User;
import spark.Request;
import spark.Response;
import spark.Route;

import org.bson.Document;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class UserHandler
{
    private Algorithm algorithm;
    {
        try {
            algorithm = Algorithm.HMAC256("magic");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public Route registerHandler = (request, response) -> {
        Document body = Document.parse(request.body());
        String userString = body.getString("username");
        String passString = body.getString("password");
        Document status = new Document();
        status.append("status", "ERROR");
        status.append("error", "Username and password required.");
        if (userString != null && passString != null) {
            if (userString.length() < 4 || passString.length() < 4)
            {
                response.status(400);
                status.append("error", "Username and password have to be at least 4 characters.");
            }
            else if (DataBase.getInstance().getUsersCollection().find(eq("username", userString)).first() != null)
            {
                response.status(409);
                status.append("error", "Username already in use.");
            }
            else {
                new User(userString, passString);
                response.status(200);
                return new Document().append("status", "SUCCESS").append("username", userString);
            }
        }
        response.status(400);
        return status;
    };

    public Route loginHandler = new Route() {
        @Override
        public Object handle(Request request, Response response) throws Exception {
            Document status = new Document();
            status.append("status", "ERROR");
            String authorization = request.headers("Authorization");
            if (authorization != null) {
                if (!getVerification(authorization.replaceFirst(".* ", ""))) {
                    response.status(400);
                    return status.append("error", "Bad token.");
                }
                response.status(200);
                return status.append("status", "LOGIN SUCCESS");
            }
            Document body = Document.parse(request.body());
            String userString = body.getString("username");
            String passString = body.getString("password");
            status.append("error", "Username and password required.");
            if (userString != null && passString != null) {
                if (DataBase.getInstance().getUsersCollection().find(eq("username", userString)).first() == null)
                    status.append("error", "User does not exist.");
                else if (DataBase.getInstance().getUsersCollection().find(and(eq("username", userString),
                        eq("password", passString))).first() != null)
                {
                    response.status(200);
                    return new Document().append("status", "SUCCESS").append("access_token", setToken());
                }
                else
                    status.append("error", "Wrong password.");
            }
            response.status(400);
            return status;
        }
    };

    private boolean getVerification(String token)
    {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
        } catch (JWTVerificationException exception){
            return false;
        }
        return true;
    }

    private Document setToken()
    {
        String token = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 7);
        try {
            token = JWT.create()
                    .withExpiresAt(cal.getTime())
                    .withIssuer("auth0")
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            // Invalid Signing configuration / Couldn't convert Claims.
        }
        return new Document().append("token", token).append("expires", cal.getTime().getTime()).append("type", "Bearer");
    }
}
