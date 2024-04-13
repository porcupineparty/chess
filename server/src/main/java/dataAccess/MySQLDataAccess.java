package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
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
             var statement = connection.prepareStatement("INSERT INTO GAME (whiteUsername, blackUsername, gameName, GAMEID, Implementation) VALUES (?, ?, ?, ?, ?)")) {
            int gameId = nextGameId.getAndIncrement();

            statement.setString(1, myGame.whiteUsername());
            statement.setString(2, myGame.blackUsername());
            statement.setString(3, myGame.gameName());
            statement.setInt(4, gameId);

            // Check if implementation is null
            ChessGame implementation = myGame.implementation();
            if (implementation != null) {
                statement.setString(5, implementation.toString());
            } else {
                // Handle null implementation
                statement.setNull(5, Types.VARCHAR);
            }
            gameWithID = new GameData(gameId, myGame.whiteUsername(), myGame.blackUsername(), myGame.gameName(), myGame.implementation());

            // Execute the update
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error executing create game: " + e.getMessage());
        }
        return gameWithID;
    }




    @Override
    public void CreateUser(UserData myUser) throws DataAccessException {

        try(var connection = DatabaseManager.getConnection();
            var statement = connection.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?)")){
            statement.setString(1, myUser.username());
            statement.setString(2, myUser.password());
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
             var statement = connection.prepareStatement("INSERT INTO AUTH (authToken, Username) VALUES (?, ?)")) {

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
        var statement = connection.prepareStatement("SELECT * FROM user WHERE username = ?")){
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
        AuthData authData = null;
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement("SELECT * FROM AUTH WHERE authToken = ?")) {
            statement.setString(1, authToken);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var retrievedAuthToken = resultSet.getString("authToken");
                    var username = resultSet.getString("Username");

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
             var statement = connection.prepareStatement("SELECT * FROM GAME WHERE GAMEID = ?")) {
            statement.setInt(1, gameID);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {

                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");

                    // I don't think I need the implementation because it is already stored.
                    //NOTICE make sure this is the case later on.

                    gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, null); // Replace null with the chess implementation
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
                    int gameID = resultSet.getInt("GAMEID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");
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
             var statement = connection.prepareStatement("DELETE FROM AUTH WHERE authToken = ?")) {

            statement.setString(1, authToken);

            // Execute the delete
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error executing delete: " + e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData game, String playerColor, String username) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            // Update the game with the provided username based on playerColor
            if ("WHITE".equals(playerColor)) {
                try (var statement = connection.prepareStatement("UPDATE GAME SET whiteUsername = ? WHERE GAMEID = ?")) {
                    statement.setString(1, username);
                    statement.setInt(2, game.gameID());
                    statement.executeUpdate();
                }
            } else if ("BLACK".equals(playerColor)) {
                try (var statement = connection.prepareStatement("UPDATE GAME SET blackUsername = ? WHERE GAMEID = ?")) {
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
