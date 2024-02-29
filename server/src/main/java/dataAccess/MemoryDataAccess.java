package dataAccess;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class MemoryDataAccess implements DataAccess{
    private Map<String, UserData> users = new HashMap<>();
    private Map<Integer, GameData> games = new HashMap<>();
    private Map<String, AuthData> tokens = new HashMap<>();
    private AtomicInteger nextGameId = new AtomicInteger(1);

    @Override
    public void Clear() throws DataAccessException {
        users.clear();
        games.clear();
        tokens.clear();
    }
    @Override
    public GameData CreateGame(GameData myGame) throws DataAccessException {
        int gameId = nextGameId.getAndIncrement();
        GameData gameWithId = new GameData(gameId, myGame.whiteUsername(), myGame.blackUsername(), myGame.gameName(), myGame.implementation());
        games.put(gameId, gameWithId);
        return gameWithId;
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
    public void updateGame(GameData game, String playerColor, String username) throws DataAccessException {
        if ("WHITE".equals(playerColor) && game.whiteUsername() == null) {
            GameData updateGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.implementation());
            games.remove(game.gameID());
            games.put(game.gameID(), updateGame);
        } else if ("BLACK".equals(playerColor) && game.blackUsername() == null) {
            GameData updateGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.implementation());
            games.remove(game.gameID());
            games.put(game.gameID(), updateGame);
        } else if(playerColor == null){
            GameData updateGame = new GameData(game.gameID(), game.whiteUsername(),  game.blackUsername(), game.gameName(), game.implementation());
            games.remove(game.gameID());
            games.put(game.gameID(), updateGame);
        }
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
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }
    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        tokens.remove(authToken);
    }
}
