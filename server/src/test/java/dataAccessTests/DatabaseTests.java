package dataAccessTests;

import chess.*;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.MySQLDataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTests {


    DataAccess DAO = new MySQLDataAccess();

    public DatabaseTests() throws DataAccessException {
        try {
            // Attempt to initialize DataAccess and services
            DAO = new MySQLDataAccess();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    @BeforeEach
    public void clearDatabase() {

        try {
            DAO.Clear();


        } catch (DataAccessException e) {

            System.out.println("Failed to clear the database: " + e.getMessage());
        }
    }

    @Test
    public void testCreateUserPositive() throws DataAccessException {

        String username = "testUser";
        String password = "testPassword";
        String email = "test@example.com";
        UserData user = new UserData(username, password, email);

        DAO.CreateUser(user);

        UserData retrievedUser = DAO.getUser(username);
        assertNotNull(retrievedUser);
        assertEquals(username, retrievedUser.username());
        assertEquals(email, retrievedUser.email());
    }

    @Test
    public void testCreateUserNegative() {

        String username = null; // Empty username
        String password = "testPassword";
        String email = "test@example.com";
        UserData user = new UserData(username, password, email);


        assertThrows(DataAccessException.class, () -> DAO.CreateUser(user));
    }

    @Test
    public void testClearPositive() {

        UserData user1 = new UserData("user1", "password1", "user1@example.com");
        UserData user2 = new UserData("user2", "password2", "user2@example.com");


        assertDoesNotThrow(() -> {
            DAO.CreateUser(user1);
            DAO.CreateUser(user2);
        });

        assertDoesNotThrow(() -> DAO.Clear());

    }




    @Test
    public void positiveTestChessBoardInitialization() throws DataAccessException {
        // Arrange
        ChessBoard myBoard = new ChessBoard();
        myBoard.resetBoard();

        ChessGame chessGame = new ChessGame();
        chessGame.setBoard(myBoard);

        GameData game = new GameData(1, "chimichanga", "username", "myGame", chessGame);


        DAO.CreateGame(game);

        System.out.print(DAO.listGames());
        System.out.print(game.implementation().toString());


        GameData retrievedGame = DAO.getGame(game.gameID());
        System.out.print(retrievedGame.implementation().getBoard().toString());

        assertNotNull(retrievedGame.implementation()); // Ensure the ChessGame implementation is not null
        assertEquals(chessGame.getBoard().toString(), retrievedGame.implementation().getBoard().toString()); // Check if the initial board state matches
    }
    @Test
    public void negativeTestChessBoardInitialization() throws DataAccessException, InvalidMoveException {

        ChessBoard myBoard = new ChessBoard();
        myBoard.resetBoard();

        ChessPosition startPosition = new ChessPosition(2, 1);
        ChessPosition endPosition = new ChessPosition(3, 1);
        ChessMove myMove = new ChessMove(startPosition, endPosition, null);

        // Create separate instances of ChessGame for game and game2
        ChessGame chessGame1 = new ChessGame();
        chessGame1.setTeamTurn(ChessGame.TeamColor.WHITE);
        chessGame1.setBoard(myBoard);

        ChessGame chessGame2 = new ChessGame();
        chessGame2.setTeamTurn(ChessGame.TeamColor.WHITE);
        chessGame2.setBoard(new ChessBoard());


        GameData game = new GameData(1, "chimichanga", "username", "myGame", chessGame1);
        GameData game2 = new GameData(2, "chimichanga", "username", "myGame2", chessGame2);


        chessGame1.makeMove(myMove);


        DAO.CreateGame(game);
        DAO.CreateGame(game2);

        System.out.print(DAO.listGames());
        System.out.print(game.implementation().toString());


        GameData retrievedGame1 = DAO.getGame(1);
        GameData retrievedGame2 = DAO.getGame(2);

        assertNotNull(retrievedGame1.implementation()); // Ensure the ChessGame implementation is not null
        assertNotNull(retrievedGame2.implementation()); // Ensure the ChessGame implementation is not null
        assertNotEquals(retrievedGame2.implementation().getBoard().toString(), retrievedGame1.implementation().getBoard().toString()); // Check if the initial board state matches
    }

    @Test
    public void testJoinGamePositive() throws DataAccessException {

        ChessBoard myBoard = new ChessBoard();
        myBoard.resetBoard();

        ChessGame chessGame = new ChessGame();
        chessGame.setBoard(myBoard);


        GameData game = new GameData(1, "chimichanga", null, "myGame", chessGame);
        DAO.CreateGame(game);


        DAO.updateGame(game, "BLACK", "viewer");


        GameData retrievedGame = DAO.getGame(1);

        assertEquals("viewer", retrievedGame.blackUsername());
    }
    @Test
    public void testJoinGameNegative() throws DataAccessException {

        ChessBoard myBoard = new ChessBoard();
        myBoard.resetBoard();

        ChessGame chessGame = new ChessGame();
        chessGame.setBoard(myBoard);


        GameData game = new GameData(1, "chimichanga", null, "myGame", chessGame);
        DAO.CreateGame(game);


        assertThrows(DataAccessException.class, () -> DAO.updateGame(game, "BLACK", null));


        GameData retrievedGame = DAO.getGame(1);
        assertNull(retrievedGame.blackUsername());
    }
    @Test
    public void testCreateAuthPositive() throws DataAccessException {

        String authToken = "randomAuthToken";
        String username = "testUser";
        UserData user = new UserData(username, "password", "email");
        DAO.CreateUser(user);


        AuthData authData = new AuthData(authToken, username);
        DAO.CreateAuth(authData);


        AuthData retrievedAuth = DAO.getAuth(authToken);
        assertNotNull(retrievedAuth);
        assertEquals(authToken, retrievedAuth.authToken());
        assertEquals(username, retrievedAuth.username());
    }
    @Test
    public void testCreateAuthNegative() throws DataAccessException {

        String authToken = "randomAuthToken";
        String username = "testUser";


        assertThrows(DataAccessException.class, () -> {
            AuthData authData = new AuthData(authToken, username);
            DAO.CreateAuth(authData);
        });
    }

    @Test
    public void testGetAuthNegativeInvalidToken() throws DataAccessException {

        String authToken = null;


        assertThrows(DataAccessException.class, () -> DAO.getAuth(authToken));
    }
    @Test
    public void testGetAuthPositive() throws DataAccessException {

        String authToken = "randomAuthToken";
        String username = "testUser";
        UserData user = new UserData("testUser", "password", "email");
        DAO.CreateUser(user);
        AuthData authData = new AuthData(authToken, username);
        DAO.CreateAuth(authData);


        AuthData retrievedAuth = DAO.getAuth(authToken);


        assertNotNull(retrievedAuth);
        assertEquals(authToken, retrievedAuth.authToken());
        assertEquals(username, retrievedAuth.username());
    }
    @Test
    public void testGetGamePositive() throws DataAccessException {

        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(1, "chimichanga", "username", "myGame", chessGame);
        DAO.CreateGame(game);


        GameData retrievedGame = DAO.getGame(1);


        assertNotNull(retrievedGame);
    }

    @Test
    public void testGetGameNegativeNonExistentID() throws DataAccessException {

        int nonExistentGameID = 999;


        assertNull(DAO.getGame(nonExistentGameID));
    }

    @Test
    public void testListGamesPositive() throws DataAccessException {

        GameData game1 = new GameData(1, "whiteUser1", "blackUser1", "Game1", null);
        GameData game2 = new GameData(2, "whiteUser2", "blackUser2", "Game2", null);

        DAO.CreateGame(game1);
        DAO.CreateGame(game2);


        var games = DAO.listGames();

        assertNotNull(games);
        assertEquals(2, games.size());
        assertTrue(games.contains(game1));
        assertTrue(games.contains(game2));
    }
    @Test
    public void testListGamesNegative() throws DataAccessException {

        List<GameData> games = DAO.listGames();
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }
    @Test
    public void testDeleteAuthNegativeNonexistentAuthToken() throws DataAccessException {

        String nonexistentAuthToken = "nonexistentAuthToken";

       DAO.deleteAuth(nonexistentAuthToken);
        AuthData retrievedAuth = DAO.getAuth(nonexistentAuthToken);
        assertNull(retrievedAuth);
    }
    @Test
    public void testDeleteAuthPositive() throws DataAccessException {

        String authToken = "randomAuthToken";
        String username = "testUser";
        DAO.CreateUser(new UserData(username, "password", "email"));
        AuthData authData = new AuthData(authToken, username);
        DAO.CreateAuth(authData);

        DAO.deleteAuth(authToken);

        assertNull(DAO.getAuth(authToken));
    }
    @Test
    public void testCreateGameNegativeNullParameters() {
        GameData game = new GameData(1, null, null, null, null);
        assertThrows(DataAccessException.class, () -> DAO.CreateGame(game));
    }

    @Test
    public void testUpdateGamePositive() throws DataAccessException {
        GameData game = new GameData(1, "whiteUser", null, "Game1", null);
        DAO.CreateGame(game);

        DAO.updateGame(game, "BLACK", "blackUser");
        GameData updatedGame = DAO.getGame(1);

        assertEquals("blackUser", updatedGame.blackUsername());
    }
    @Test
    public void testUpdateGameNegativeNullParameters() {
        GameData game = new GameData(1, "whiteUser", null, "Game1", null);
        assertThrows(DataAccessException.class, () -> DAO.updateGame(game, null, null));
    }
    @Test
    public void testJoinGameNegativeNullParameters() {
        assertThrows(DataAccessException.class, () -> DAO.updateGame(null, null, null));
    }
    @Test
    public void testCreateUserNegativeDuplicateUsername() throws DataAccessException {
        String username = "existingUser";
        String password = "testPassword";
        String email = "test@example.com";
        UserData existingUser = new UserData(username, password, email);


        DAO.CreateUser(existingUser);


        UserData newUser = new UserData(username, "newPassword", "new@example.com");
        assertThrows(DataAccessException.class, () -> DAO.CreateUser(newUser));
    }



}
