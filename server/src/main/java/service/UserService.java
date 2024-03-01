package service;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.UUID;


public class UserService {
    private final MemoryDataAccess dao;
    public UserService(MemoryDataAccess dao) {
        this.dao = dao;
    }
    public AuthData register(UserData user) throws DataAccessException {
        if(user.username() == null || user.email() == null || user.password() == null){
            throw new DataAccessException("Invalid Register Info");
        }

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
        if(authData == null){
            throw new DataAccessException("Auth Data Null");
        }
        return authData;
    }
    public void storeAuth(AuthData authData) throws DataAccessException {
        dao.CreateAuth(authData);
    }
}
