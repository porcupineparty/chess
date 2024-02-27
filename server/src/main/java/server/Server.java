package server;


import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;
import dataAccess.DataAccess;

import java.util.Map;

public class Server {
    private final MemoryDataAccess DAO = new MemoryDataAccess();
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    private DataAccess dataAccess;

    public Server() {
        userService = new UserService(DAO);
        gameService = new GameService();
        clearService = new ClearService(DAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clearDatabase);
        Spark.post("/user", this::registerUser);
        Spark.get("/user", this::getUser);


        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object getUser(Request req, Response res) throws DataAccessException{
        var user = new Gson().fromJson(req.body(), UserData.class);
        UserData userData = userService.getUser(user);
        return userData;
    }


    private Object registerUser(Request req, Response res) throws DataAccessException{
        var user = new Gson().fromJson(req.body(), UserData.class);
        UserData userData = userService.getUser(user);
        if(userData != null && userData.username() != null){
            res.status(403);
            res.body("{\"message\": \"Error: already taken\"}");
            return res.body();
        }
        if(user.password() == null || user.email() == null || user.username() == null){
            res.status(400);
            res.body("{\"message\": \"Error: bad request\"}");
            return res.body();
        }
        AuthData authData = userService.register(user);
        return new Gson().toJson(authData);
    }

    private Object clearDatabase(Request req, Response res) throws DataAccessException {
            clearService.deleteDatabase();
            res.status(204);
            return "";
    }




    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}
