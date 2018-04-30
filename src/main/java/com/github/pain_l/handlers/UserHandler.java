package com.github.pain_l.handlers;

import com.github.pain_l.db.DataBase;
import com.github.pain_l.models.User;
import spark.Request;
import spark.Response;
import spark.Route;

import org.bson.Document;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class UserHandler
{
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

    public Route logHandler = new Route() {
        @Override
        public Object handle(Request request, Response response) throws Exception {
            Document body = Document.parse(request.body());
            String userString = body.getString("username");
            String passString = body.getString("password");
            Document status = new Document();
            status.append("status", "ERROR");
            status.append("error", "Username and password required.");
            if (userString != null && passString != null) {
                if (DataBase.getInstance().getUsersCollection().find(eq("username", userString)).first() == null)
                    status.append("error", "User does not exist.");
                else if (DataBase.getInstance().getUsersCollection().find(and(eq("username", userString),
                        eq("password", passString))).first() != null)
                {
                    response.status(200);
                    return new Document().append("status", "SUCCESS");
                }
                else
                    status.append("error", "Wrong password.");
            }
            response.status(400);
            return status;
        }
    };
}
