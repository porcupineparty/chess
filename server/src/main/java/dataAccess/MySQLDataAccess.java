package dataAccess;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.List;

public class MySQLDataAccess implements DataAccess{
    public MySQLDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @Override
    public void Clear() throws DataAccessException {

    }

    @Override
    public GameData CreateGame(GameData myGame) throws DataAccessException {
        return null;
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
            throw new DataAccessException("Error executing update: " + e.getMessage());
        }

    }



    @Override
    public void CreateAuth(AuthData myAuth) throws DataAccessException {

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
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void updateGame(GameData game, String playerColor, String username) throws DataAccessException {

    }
}
