package dataAccess;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MySQLDataAccess implements DataAccess{
    private AtomicInteger nextGameId = new AtomicInteger(1);
    public MySQLDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @Override
    public void Clear() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            // Disable foreign key checks
            try (var disableForeignKey = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0")) {
                disableForeignKey.executeUpdate();
            }

            // Clear AUTH table
            try (var clearAuth = connection.prepareStatement("DELETE FROM AUTH")) {
                clearAuth.executeUpdate();
            }

            // Clear USER table
            try (var clearUser = connection.prepareStatement("DELETE FROM USER")) {
                clearUser.executeUpdate();
            }

            // Clear GAME table
            try (var clearGame = connection.prepareStatement("DELETE FROM GAME")) {
                clearGame.executeUpdate();
            }

            // Re-enable foreign key checks
            try (var enableForeignKey = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1")) {
                enableForeignKey.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error executing clear: " + e.getMessage());
        }
    }




    @Override
    public GameData CreateGame(GameData myGame) throws DataAccessException {
        GameData gameWithID;
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement("INSERT INTO GAME (whiteusername, blackusername, gamename, implementation) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {


            statement.setString(1, myGame.whiteUsername());
            statement.setString(2, myGame.blackUsername());
            statement.setString(3, myGame.gameName());



            ChessBoard myNewBoard = new ChessBoard();
            myNewBoard.resetBoard();
            System.out.print(myNewBoard);
            String chessBoardJson = new Gson().toJson(myNewBoard);
            statement.setString(4, chessBoardJson);




            // Execute the update
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int gameId = generatedKeys.getInt(1); // Assuming the game ID is in the first column
                gameWithID = new GameData(gameId, myGame.whiteUsername(), myGame.blackUsername(), myGame.gameName(), myGame.implementation());
            } else {
                throw new DataAccessException("Failed to retrieve generated game ID.");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error executing create game: " + e.getMessage());
        }
        return gameWithID;
    }




    @Override
    public void CreateUser(UserData myUser) throws DataAccessException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(myUser.password());

        try(var connection = DatabaseManager.getConnection();
            var statement = connection.prepareStatement("INSERT INTO USER (username, password, email) VALUES (?, ?, ?)")){
            statement.setString(1, myUser.username());
            statement.setString(2, hashedPassword);
            statement.setString(3, myUser.email());

            // Execute the update
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error executing create user: " + e.getMessage());
        }

    }



    @Override
    public void CreateAuth(AuthData myAuth) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement("INSERT INTO AUTH (authtoken, username) VALUES (?, ?)")) {

            statement.setString(1, myAuth.authToken());
            statement.setString(2, myAuth.username());

            // Execute the update
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error executing create auth: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String userId) throws DataAccessException {
        UserData userData = null;
        try (var connection = DatabaseManager.getConnection();
        var statement = connection.prepareStatement("SELECT * FROM USER WHERE username = ?")){
            statement.setString(1, userId);
            try(var resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    var username = resultSet.getString("username");
                    var password = resultSet.getString("password");
                    var email = resultSet.getString("email");

                    userData = new UserData(username, password, email);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get User: " + e.getMessage());
        }
        return userData;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if(authToken == null){
            throw new DataAccessException("AuthTokenNull");
        }
        AuthData authData = null;
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement("SELECT * FROM AUTH WHERE authtoken = ?")) {
            statement.setString(1, authToken);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var retrievedAuthToken = resultSet.getString("authtoken");
                    var username = resultSet.getString("username");

                    authData = new AuthData(retrievedAuthToken, username);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get AuthData: " + e.getMessage());
        }
        return authData;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData gameData = null;
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement("SELECT * FROM GAME WHERE gameid = ?")) {
            statement.setInt(1, gameID);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String whiteUsername = resultSet.getString("whiteusername");
                    String blackUsername = resultSet.getString("blackusername");
                    String gameName = resultSet.getString("gamename");

                    // Deserialize the implementation from JSON
                    String implementationJson = resultSet.getString("implementation");
                    Gson gson = new Gson();
                    ChessGame implementation = gson.fromJson(implementationJson, ChessGame.class);

                    gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, implementation);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get game: " + e.getMessage());
        }
        return gameData;
    }



    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement("SELECT * FROM GAME")) {
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Retrieve game data from the result set
                    int gameID = resultSet.getInt("gameid");
                    String whiteUsername = resultSet.getString("whiteusername");
                    String blackUsername = resultSet.getString("blackusername");
                    String gameName = resultSet.getString("gamename");
                    String implementation = resultSet.getString("implementation");
                    //look at the implementation in the future.
                    // Create a new GameData object with the retrieved data
                    GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, null); // Replace null with the chess implementation
                    games.add(gameData);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to list games: " + e.getMessage());
        }
        return games;
    }


    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement("DELETE FROM AUTH WHERE authtoken = ?")) {

            statement.setString(1, authToken);

            // Execute the delete
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error executing delete: " + e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData game, String playerColor, String username) throws DataAccessException {
        if(username == null){
            throw new DataAccessException("The Username was null");
        }
        try (var connection = DatabaseManager.getConnection()) {
            // Update the game with the provided username based on playerColor
            if ("WHITE".equals(playerColor) || "White".equals(playerColor) || "white".equals(playerColor)) {
                try (var statement = connection.prepareStatement("UPDATE GAME SET whiteusername = ? WHERE gameid = ?")) {
                    statement.setString(1, username);
                    statement.setInt(2, game.gameID());
                    statement.executeUpdate();
                }
            } else if ("BLACK".equals(playerColor) || "black".equals(playerColor) || "Black".equals(playerColor)) {
                try (var statement = connection.prepareStatement("UPDATE GAME SET blackusername = ? WHERE gameid = ?")) {
                    statement.setString(1, username);
                    statement.setInt(2, game.gameID());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error executing update: " + e.getMessage());
        }
    }
}
