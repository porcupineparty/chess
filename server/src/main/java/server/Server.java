package server;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.MySQLDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;
import dataAccess.DataAccessException;



import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private UserService userService = null;
    private GameService gameService = null;
    private ClearService clearService = null;
    private final AtomicInteger maxGameId = new AtomicInteger(1);

    public Server()  {


        try {
            // Attempt to initialize DataAccess and services
            DataAccess DAO = new MySQLDataAccess();
            userService = new UserService(DAO);
            gameService = new GameService(DAO);
            clearService = new ClearService(DAO);

        } catch (Exception e) {
            // Handle any exceptions that occur during initialization
            // For example, log the exception and gracefully handle the error
            System.out.print(e.getMessage());
            // You can also throw a custom exception or handle the error in a different way
        }


    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clearDatabase);
        Spark.post("/user", this::registerUser);
        Spark.get("/user", this::getUser);
        Spark.post("/session", this::loginUser);
        Spark.post("/game", this::createGame);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.put("/game", this::joinGame);
        Spark.exception(DataAccessException.class, this::exceptionHandler);



        Spark.awaitInitialization();
        return Spark.port();
    }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        System.out.print(ex.getMessage());
    }

    private Object joinGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        AuthData authData = userService.getAuth(authToken);
        if(authData == null){
            res.status(401);
            res.body("{\"message\": \"Error: Unauthorized\"}");
            return res.body();
        }
        Map<String, String> requestBody = new Gson().fromJson(req.body(), new TypeToken<Map<String, String>>() {}.getType());
        String playerColor = requestBody.get("playerColor");
        int gameId = Integer.parseInt(requestBody.get("gameID"));
        String username = authData.username();
        GameData game = gameService.getGameById(gameId);

        if(game == null){ // I don't know if you need to include gameID
            res.status(400);
            res.body("{\"message\": \"Error: bad request\"}");
            return res.body();
        }
        if(game.blackUsername() != null && Objects.equals(playerColor, "BLACK")){
            res.status(403);
            res.body("{\"message\": \"Error: bad request\"}");
            return res.body();
        }
        if(game.whiteUsername() != null && Objects.equals(playerColor, "WHITE")){
            res.status(403);
            res.body("{\"message\": \"Error: bad request\"}");
            return res.body();
        }
        else{
            gameService.joinGame(gameId, playerColor, username);
        }
        res.status(200);
        return "{\"message\": \"Join successful\"}";
    }


    private Object listGames(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        AuthData authData = userService.getAuth(authToken);
        if (authData == null) {
            res.status(401);
            res.body("{\"message\": \"Error: Unauthorized\"}");
            return res.body();
        }
        List<GameData> gamesList = gameService.listGames();

        // Create a map to hold the list of games
        Map<String, List<GameData>> responseMap = new HashMap<>();
        responseMap.put("games", gamesList);
        Gson gson = new Gson();
        return gson.toJson(responseMap);
    }

    private Object logout(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        AuthData authData = userService.getAuth(authToken);
        if (authData == null) {
            res.status(401);
            res.body("{\"message\": \"Error: unauthorized\"}");
            return res.body();
        }
        userService.logout(authToken);
        res.status(200);
        return "{\"message\": \"Logout successful\"}";
    }

    private Object createGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        AuthData authData = userService.getAuth(authToken);
        if (authData == null) {
            res.status(401);
            res.body("{\"message\": \"Error: Unauthorized\"}");
            return res.body();
        }
        var game = new Gson().fromJson(req.body(), GameData.class);
        if(game.gameName() == null){ // I don't know if you need to include gameID
            res.status(400);
            res.body("{\"message\": \"Error: bad request\"}");
            return res.body();
        }
        else{
            maxGameId.getAndIncrement();
            GameData gameData = gameService.createGame(game);
            return new Gson().toJson(gameData);
        }
    }

    private Object loginUser(Request req, Response res) throws DataAccessException{
        var user = new Gson().fromJson(req.body(), UserData.class);

        UserData userData = userService.getUser(user);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(userData != null && encoder.matches(user.password(), userData.password()) && Objects.equals(userData.username(), user.username())){
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authToken, user.username());
            userService.storeAuth(authData);
            return new Gson().toJson(authData);
        }
        else{
            res.status(401); // Unauthorized status code
            res.body("{\"message\": \"Error: unauthorized\"}");
            return res.body();
        }
    }

    private Object getUser(Request req, Response res) throws DataAccessException{
        var user = new Gson().fromJson(req.body(), UserData.class);
        return userService.getUser(user);
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
            res.status(200);
            return "";
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}
