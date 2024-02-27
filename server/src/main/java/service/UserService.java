package service;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;


public class UserService {
    private final MemoryDataAccess dao;
    public UserService(MemoryDataAccess dao) {
        this.dao = dao;
    }
    public AuthData register(UserData user) throws DataAccessException {
        dao.CreateUser(user);
        AuthData authData = new AuthData("dummyToken", user.username());
        return authData;
    }
    public UserData getUser(UserData user) throws DataAccessException {
        UserData myUser = dao.getUser(user.username());
        return myUser;
    }
    public AuthData login(UserData user) {
        return null;
    }
    public void logout(UserData user) {

    }
}
