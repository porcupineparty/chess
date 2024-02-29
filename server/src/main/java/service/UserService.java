package service;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;

import java.util.UUID;


public class UserService {
    private final MemoryDataAccess dao;
    public UserService(MemoryDataAccess dao) {
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
        UserData myUser = dao.getUser(user.username());
        return myUser;
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData myAuth = dao.getAuth(authToken);
        if (myAuth != null) {
            dao.deleteAuth(authToken);
        }
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData myAuth = dao.getAuth(authToken);
        if(myAuth != null){
            return myAuth;
        }
        else{
            return null;
        }
    }
    public void storeAuth(AuthData authData) throws DataAccessException {
        dao.CreateAuth(authData);
    }
}
