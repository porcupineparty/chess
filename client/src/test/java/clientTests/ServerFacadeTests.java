package clientTests;

import client.Repl;
import client.ServerFacade;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static final String username = "newUser";
    private static final String email = "emailUser";
    private static final String password = "passwordUser";
    private static String url; // Declare url as a static variable


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        url = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(url);
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
    public void testLogin() {
        String response = serverFacade.login(username, password);
        assertNotNull(response);

        // Parse the JSON response
        JsonObject jsonResponse = new Gson().fromJson(response, JsonObject.class);

        // Extract the username field from the JSON response
        String returnedUsername = jsonResponse.get("username").getAsString();

        // Assert that the returned username matches the expected username
        assertEquals(username, returnedUsername);
    }


    @Test
    public void testRegister() {
        //need to delete the user first.
        String response = serverFacade.register(username, password, email);
        assertNotNull(response);

        // Parse the JSON response
        JsonObject jsonResponse = new Gson().fromJson(response, JsonObject.class);
        System.out.print(jsonResponse);

        // Extract the username field from the JSON response
        String returnedUsername = jsonResponse.get("username").getAsString();

        // Assert that the returned username matches the expected username
        assertEquals(username, returnedUsername);
    }


    @Test
    public void testQuit() {
        String response = serverFacade.register(username, password, email);
        JsonObject jsonResponse = new Gson().fromJson(response, JsonObject.class);

        String newResponse = serverFacade.quit();

        // Check if the response indicates successful logout
        assertEquals("Logout successful", newResponse);
    }

}
