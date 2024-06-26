package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.GameData;

import java.util.List;

public class GameService {
    private final DataAccess dao;
    public GameService(DataAccess dao) {
        this.dao = dao;
    }
    public GameData createGame(GameData newGame) throws DataAccessException {
        if(newGame.gameName() == null){
            throw new DataAccessException("No Game Name Specified");
        }
        return dao.CreateGame(newGame);
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

        return dao.getGame(gameId);
    }
}
