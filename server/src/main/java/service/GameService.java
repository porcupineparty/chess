package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.GameData;

import java.util.List;

public class GameService {
    private final MemoryDataAccess dao;
    public GameService(MemoryDataAccess dao) {
        this.dao = dao;
    }
    public GameData createGame(GameData newGame, int gameId) throws DataAccessException {
        GameData myGame = dao.CreateGame(newGame);
        return myGame;
    }
    public GameData getGame(GameData myGame) throws DataAccessException {
        GameData gottenGame = dao.getGame(myGame.gameID());
        return gottenGame;
    }
    public List<GameData> listGames() throws DataAccessException{
        return dao.listGames();
    }
    public void joinGame(int gameId, String playerColor, String username) throws DataAccessException {
        GameData game = dao.getGame(gameId);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        dao.updateGame(getGameById(gameId), playerColor, username);
    }

    public GameData getGameById(int gameId) throws DataAccessException {
        GameData gottenGame = dao.getGame(gameId);
        return gottenGame;
    }
}
