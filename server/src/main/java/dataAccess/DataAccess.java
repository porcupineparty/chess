package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.List;

public interface DataAccess {
    void Clear() throws DataAccessException;
    GameData CreateGame(GameData myGame) throws DataAccessException;
    void CreateUser(UserData myUser) throws DataAccessException;
    void CreateAuth(AuthData myAuth) throws DataAccessException;
    UserData getUser(String userId) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void updateGame(GameData game, String playerColor, String username) throws DataAccessException;
}
