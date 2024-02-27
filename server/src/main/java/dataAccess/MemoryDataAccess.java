package dataAccess;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MemoryDataAccess implements DataAccess{
    private Map<String, UserData> users = new HashMap<>();
    private Map<Integer, GameData> games = new HashMap<>();
    private Map<String, AuthData> tokens = new HashMap<>();

    @Override
    public void Clear() throws DataAccessException {
        users.clear();
        games.clear();
        tokens.clear();
    }
    @Override
    public void CreateGame(GameData myGame) throws DataAccessException {
        games.put(myGame.gameID(), myGame);
    }
    @Override
    public void CreateUser(UserData myUser) throws DataAccessException {
        users.put(myUser.username(), myUser);
    }
    @Override
    public void CreateAuth(AuthData myAuth) throws DataAccessException {
        tokens.put(myAuth.authToken(), myAuth);
    }
    @Override
    public void updateGame(int gameId, String updatedGameData) throws DataAccessException {
        games.remove(gameId);
        Gson gson = new Gson();
        GameData updateData = gson.fromJson(updatedGameData, GameData.class);
        games.put(gameId, updateData);
    }
    @Override
    public UserData getUser(String userId) throws DataAccessException {
        return users.get(userId);
    }
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return tokens.get(authToken);
    }
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }
    @Override
    public String listGames() throws DataAccessException {
        return new Gson().toJson(games);
    }
}
