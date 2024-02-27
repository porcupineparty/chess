package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.List;

public interface DataAccess {
    void Clear() throws DataAccessException;
    void CreateGame(GameData myGame) throws DataAccessException;
    void CreateUser(UserData myUser) throws DataAccessException;
    void CreateAuth(AuthData myAuth) throws DataAccessException;
    void updateGame(int gameId, String updatedGameData) throws DataAccessException;
    UserData getUser(String userId) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    String listGames() throws DataAccessException;
}
