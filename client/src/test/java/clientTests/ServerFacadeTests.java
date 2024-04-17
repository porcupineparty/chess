package clientTests;

import client.Repl;
import client.ServerFacade;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;
import chess.ChessBoard;

import java.io.*;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static final String username = "newUser";
    private static final String email = "emailUser";
    private static final String password = "passwordUser";
    private ByteArrayOutputStream mockOutput;
    private final String validGameName = "Test Game";
    private static String url; // Declare url as a static variable


    @BeforeEach
    public void clearDatabase(){serverFacade.clear();}



    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(29586);
        url = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(url, null);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void testHelp() {
        String helpResponse = serverFacade.helpPrelogin();
        assertNotNull(helpResponse);
        assertFalse(helpResponse.isEmpty());

    }
    @Test
    public void testHelpTwo() {
        String helpResponse = serverFacade.helpPostlogin();
        assertNotNull(helpResponse);
        assertFalse(helpResponse.isEmpty());
    }
    @Test
    public void testLogin() {
        serverFacade.register(username, password, email);
        serverFacade.logout();
        String response = serverFacade.login(username, password);
        assertNotNull(response);

        JsonObject jsonResponse = new Gson().fromJson(response, JsonObject.class);

        String returnedUsername = jsonResponse.get("username").getAsString();

        assertEquals(username, returnedUsername);
    }
    @Test
    public void testLoginNegative() {

        assertThrows(RuntimeException.class, () -> serverFacade.login(username, password));
    }
    @Test
    public void testJoinObserver(){
        serverFacade.register(username, password, email);
        String userInput = validGameName + "\n";
        InputStream mockInput = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(mockInput);
        String createGameResponse = serverFacade.createGame();

        JsonObject createGameJsonResponse = new Gson().fromJson(createGameResponse, JsonObject.class);
        int gameId = createGameJsonResponse.get("gameID").getAsInt();

        // Mock user input for joining as an observer
        String userInputJoinObserver = gameId + "\n";
        InputStream mockInputJoinObserver = new ByteArrayInputStream(userInputJoinObserver.getBytes());
        System.setIn(mockInputJoinObserver);

        String joinObserverResponse = serverFacade.joinObserver();
        assertTrue(joinObserverResponse.contains("Join successful"));

    }
    @Test
    public void testListGamesPositive() {

        serverFacade.register(username, password, email);


        String listGamesResponse = serverFacade.listGames();


        assertNotNull(listGamesResponse);
        assertTrue(listGamesResponse.contains("game"));
    }
    @Test
    public void testListGamesNegative() {
        //Not authenticated.
        String listGamesResponse = serverFacade.listGames();


        assertNotNull(listGamesResponse);
        assertTrue(listGamesResponse.contains("Error: Unable to list games"));
    }


    @Test
    public void testJoinGame() {
        serverFacade.register(username, password, email);
        String userInput = validGameName + "\n";
        InputStream mockInput = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(mockInput);
        String createGameResponse = serverFacade.createGame();

        JsonObject createGameJsonResponse = new Gson().fromJson(createGameResponse, JsonObject.class);
        int gameId = createGameJsonResponse.get("gameID").getAsInt();


        // Mock user input for joining a game
        String userInputJoinGame = gameId + "\nwhite\n";
        InputStream mockInputJoinGame = new ByteArrayInputStream(userInputJoinGame.getBytes());
        System.setIn(mockInputJoinGame);
        String stringNumber = String.valueOf(gameId);


        String response = serverFacade.joinGameParser(stringNumber, "white");
        assertTrue(response.contains("Join successful"));
    }


    @Test
    public void testJoinGameWithInvalidGameID() {
        serverFacade.register(username, password, email);
        int gameId = -1;


        // Mock user input for joining a game
        String stringNumber = String.valueOf(gameId);


        RuntimeException exception = assertThrows(RuntimeException.class, () -> serverFacade.joinGameParser(stringNumber, "white"));
        assertInstanceOf(IOException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Server returned HTTP response code: 400"));
    }



    @Test
    public void testJoinObserverWithInvalidGameID() {
        serverFacade.register(username, password, email);

        // Mock user input with an invalid game ID (such as a negative value)
        String userInputJoinObserver = "-1\n"; // Assuming -1 is an invalid game ID
        InputStream mockInputJoinObserver = new ByteArrayInputStream(userInputJoinObserver.getBytes());
        System.setIn(mockInputJoinObserver);

        // Assert that joining with an invalid game ID throws the expected exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> serverFacade.joinObserver());
        assertInstanceOf(IOException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Server returned HTTP response code: 400"));
    }




    @Test
    public void testRegister() {

        String response = serverFacade.register(username, password, email);
        assertNotNull(response);

        JsonObject jsonResponse = new Gson().fromJson(response, JsonObject.class);
        System.out.print(jsonResponse);

        String returnedUsername = jsonResponse.get("username").getAsString();

        assertEquals(username, returnedUsername);
    }
    @Test
    public void testRegisterNegative(){

        assertThrows(RuntimeException.class, () -> serverFacade.register("", "", ""));
    }


    @Test
    public void testLogout() {
        serverFacade.register(username, password, email);
        String newResponse = serverFacade.logout();

        // Check if the response indicates successful logout
        assertEquals("Logout successful", newResponse);
    }
    @Test
    public void testLogoutWithoutLogin() {
        // Attempt to log out without registering or logging in
        String response = serverFacade.logout();

        // Check if the response indicates a failure to logout
        assertEquals("Error: Logout failed", response);
    }

    @Test
    public void testClear() {
        // Mock the authToken to simulate an authenticated session

        // Call the clear method
        String response = serverFacade.clear();
        // Check if the response indicates successful clearance
        assertEquals("Clearance successful", response);
    }
    @Test
    public void testCreateGameWithValidInput() {
        serverFacade.register(username, password, email);
        String userInput = validGameName + "\n";
        InputStream mockInput = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(mockInput);

        String response = serverFacade.createGame();


        assertTrue(response.contains("\"gameID\":"));
        assertTrue(response.contains("\"gameName\":"));
        assertTrue(response.contains(validGameName));
    }
    @Test
    public void testCreateGameUnauthorized() {
        String userInput = validGameName + "\n";
        InputStream mockInput = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(mockInput);

        // Now, test the createGame method

        assertThrows(RuntimeException.class, () -> serverFacade.createGame());
    }

}
