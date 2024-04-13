package service;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.UUID;


public class UserService {
    private final DataAccess dao;
    public UserService(DataAccess dao) {
        this.dao = dao;
    }
    public AuthData register(UserData user) throws DataAccessException {

        String authToken = UUID.randomUUID().toString();
        dao.CreateUser(user);
        AuthData authData = new AuthData(authToken, user.username());
        dao.CreateAuth(authData);
        return authData;
    }
    public UserData getUser(UserData user) throws DataAccessException {
        return dao.getUser(user.username());
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData myAuth = dao.getAuth(authToken);
        if (myAuth != null) {
            dao.deleteAuth(authToken);
        }
        else{
            throw new DataAccessException("Error logging out");
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData authData = dao.getAuth(authToken);
        return authData;
    }
    public void storeAuth(AuthData authData) throws DataAccessException {
        dao.CreateAuth(authData);
    }
}
