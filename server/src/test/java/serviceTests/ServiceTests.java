package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private MemoryDataAccess dao;
    private ClearService clearService;
    private GameService gameService;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        dao = new MemoryDataAccess();
        clearService = new ClearService(dao);
        gameService = new GameService(dao);
        userService = new UserService(dao);
    }

    // ClearService Tests
    @Test
    public void testClearDatabase() {
        assertDoesNotThrow(() -> clearService.deleteDatabase());
    }

    // GameService Tests
    @Test
    public void testCreateGame() throws DataAccessException {
        GameData newGame = new GameData(344,null, null, "megaGame", null);
        assertNotNull(gameService.createGame(newGame));

        assertThrows(DataAccessException.class, () -> {
            GameData newGameTwo = new GameData(344, null, null, null, null);
            gameService.createGame(newGameTwo);
        });
    }

    @Test
    public void testJoinGame() throws DataAccessException {
        GameData newGame = new GameData(1, null, null, "myGame", null);
        GameData newGameTwo = gameService.createGame(newGame);
        UserData newUser = new UserData("myUser", "myPassword", "myEmail@gmail.com");
        userService.register(newUser);
        assertDoesNotThrow(() -> gameService.joinGame(newGameTwo.gameID(), "BLACK", "myUser"));
        assertThrows(DataAccessException.class, () -> gameService.joinGame(999, "BLACK", "myUser"));
    }
    @Test
    public void testGetGame() throws DataAccessException {
        GameData newGame = new GameData(1, null, null, "myGame", null);
        GameData createdGame = gameService.createGame(newGame);
        GameData retrievedGame = gameService.getGameById(createdGame.gameID());
        assertNull(gameService.getGameById(-1));
        assertEquals(createdGame, retrievedGame);
    }
    @Test
    public void testGetUser() throws DataAccessException {
        UserData userData = new UserData("myUser", "myPassword", "@gmail.com");
        AuthData authData = userService.register(userData);
        assertEquals(authData.username(), userService.getUser(userData).username());
        assertNotEquals("badUsername", userService.getUser(userData).username());
    }


    @Test
    public void testRegisterUser() throws DataAccessException {
        UserData newUser = new UserData("myUser", "myPass", "myEmail@gmail.com");
        assertNotNull(userService.register(newUser));
        assertThrows(DataAccessException.class, () -> {
            UserData badUser = new UserData(null, null, null);
            userService.register(badUser);
        });
    }

    @Test
    public void testLogout() throws DataAccessException {


        AuthData authData = new AuthData("validAuth", "myUsername");
        UserData userData = new UserData("myUsername", "myPassword", "email@mgail.com");
        userService.register(userData);
        userService.storeAuth(authData);


        assertDoesNotThrow(() -> userService.logout(authData.authToken()));


        assertNull(userService.getAuth(authData.authToken()));
    }

    @Test
    public void testLogoutWithInvalidAuthToken() {
        assertThrows(DataAccessException.class, () -> userService.logout("invalidAuthToken"));
    }
    @Test
    public void testListGames() throws DataAccessException {

        GameData game1 = new GameData(1, null, null, "game1", null);
        GameData game2 = new GameData(2, null, null, "game2", null);
        GameData game3 = new GameData(3, null, null, "game3", null);
        gameService.createGame(game1);
        gameService.createGame(game2);
        gameService.createGame(game3);


        List<GameData> gamesList = gameService.listGames();


        assertTrue(gamesList.contains(game1));
        assertTrue(gamesList.contains(game2));
        assertTrue(gamesList.contains(game3));

        clearService.deleteDatabase();
        gamesList = gameService.listGames();


        assertFalse(gamesList.contains(new GameData(3, null, null, "game3", null)));
    }
    @Test
    public void testStoreAuth() throws DataAccessException {
        // Store a new authentication token in the database
        String authToken = "newAuthToken";
        AuthData authData = new AuthData(authToken, "myUser");
        userService.storeAuth(authData);
        AuthData badAuth = new AuthData(authToken, "notMyUser");

        // Retrieve the stored authentication token using getAuth()
        AuthData retrievedAuthData = userService.getAuth(authToken);

        // Assert that the retrieved authentication token matches the stored one
        assertEquals(authData, retrievedAuthData);
        assertNotEquals(retrievedAuthData, badAuth);
    }
    @Test
    public void testGetAuth() throws DataAccessException{

        assertNull(userService.getAuth("invalidAuthToken"));

        String authToken = "validAuthToken";
        AuthData authData = new AuthData(authToken, "myUser");
        userService.storeAuth(authData);

        AuthData retrievedAuthData = userService.getAuth(authToken);

        // Assert that the retrieved authentication token matches the stored one
        assertEquals(authData, retrievedAuthData);
    }

}
