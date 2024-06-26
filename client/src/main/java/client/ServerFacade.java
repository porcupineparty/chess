package client;

import chess.ChessBoard;
import client.websocket.WebSocketFacade;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import client.websocket.NotificationHandler;
import com.mysql.cj.xdevapi.JsonValue;
import exception.ResponseException;

import static ui.EscapeSequences.*;


public class ServerFacade {

    private String authToken; // Store the authentication token

    private final String serverUrl;
    private boolean inGame;
    private String username;

    private WebSocketFacade ws;
    private final NotificationHandler notificationHandler;



    public ServerFacade(String serverUrl, NotificationHandler notificationHandler) {
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;

    }

    public String eval(String input) {
        String[] parts = input.trim().split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String arguments = parts.length > 1 ? parts[1] : ""; // Get the arguments if they exist
        if (authToken == null) {
            // Prelogin UI commands
            switch (command) {
                case "help":
                    return helpPrelogin();
                case "quit":
                    quit();
                case "login":
                    return loginPrompt();
                case "register":
                    return registerPrompt();
                default:
                    return helpPrelogin();
            }
        } else {
            if(inGame == false){
                switch (command) {
                    case "help":
                        return helpPostlogin();
                    case "logout":
                        return logout();
                    case "create":
                        if ("game".equalsIgnoreCase(arguments)) {
                            return createGame();
                        } else {
                            return helpPostlogin(); // Return help message if the argument is incorrect
                        }
                    case "list":
                        if ("games".equalsIgnoreCase(arguments)) {
                            return listGames();
                        } else {
                            return helpPostlogin(); // Return help message if the argument is incorrect
                        }
                    case "join":
                        if ("game".equalsIgnoreCase(arguments)) {
                            return joinGame();
                        } else if("observer".equalsIgnoreCase(arguments)){
                            return joinObserver(); // Return help message if the argument is incorrect
                        } else {
                            return helpPostlogin(); // Return help message if the argument is incorrect
                        }
                    default:
                        return helpPostlogin();
                }
            }
            else {
                switch (command) {
                    case "help":
                        return helpInGame();
                    case "redraw":
                        if ("chess board".equalsIgnoreCase(arguments)){
                            return redrawChessBoard();
                        }
                        else {
                            return helpInGame();
                        }
                    case "leave":
                        return leave();
                    case "make":
                        if ("move".equalsIgnoreCase(arguments)) {
                            return makeMove();
                        } else {
                            return helpInGame(); // Return help message if the argument is incorrect
                        }
                    case "resign":
                        return resign();
                    case "highlight":
                        if("legal moves".equalsIgnoreCase(arguments)){
                            return highlightLegalMoves();
                        }
                        else{
                            return helpInGame();
                        }
                    default:
                        return helpInGame();

                }
            }
            // Postlogin UI commands

        }
    }

    public String redrawChessBoard() {
        return null;
    }

    public String leave() {
        return null;
    }

    public String makeMove() {
        return null;
    }

    public String resign() {
        return null;
    }

    public String highlightLegalMoves() {
        return null;
    }
    private Map<Integer, Integer> dummyToActualIdMap = new HashMap<>();

    public String helpInGame() {
        return SET_TEXT_COLOR_BLUE + "Command\t\t\tDescription\n" +
                "-------\t\t\t-----------\n" +
                "Help\t\t\tDisplays text informing the user what actions they can take.\n" +
                "Redraw Chess Board\tRedraws the chess board upon the user’s request.\n" +
                "Leave\t\t\tRemoves the user from the game (whether they are playing or observing the game). The client transitions back to the Post-Login UI.\n" +
                "Make Move\t\tAllow the user to input what move they want to make. The board is updated to reflect the result of the move, and the board automatically updates on all clients involved in the game.\n" +
                "Resign\t\t\tPrompts the user to confirm they want to resign. If they do, the user forfeits the game and the game is over. Does not cause the user to leave the game.\n" +
                "Highlight Legal Moves\tAllows the user to input what piece for which they want to highlight legal moves. The selected piece’s current square and all squares it can legally move to are highlighted. This is a local operation and has no effect on remote users’ screens.";

    }

    public String joinObserver() {
        // Prompt the user to input the game ID they want to observe
        String gameId = promptInput("Enter the ID of the game you want to observe: ");
        String actualGameId = String.valueOf(dummyToActualIdMap.get(Integer.parseInt(gameId)));

        try {
            // Construct the request body
            String requestBody = "{ \"gameID\": \"" + actualGameId + "\"}";

            // Construct the URL for the join observer endpoint
            URI uri = new URI(serverUrl + "/game");

            // Create an HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            // Set the request method to PUT
            connection.setRequestMethod("PUT");

            // Set the content type header
            connection.setRequestProperty("Content-Type", "application/json");

            // Set the Authorization header with the authToken
            connection.setRequestProperty("Authorization", authToken);

            // Enable output for sending request body
            connection.setDoOutput(true);



            // Write the request body to the connection output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response from the server
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            // Close the connection
            connection.disconnect();
            InitialChessBoard.printInitialChessBoard();
            InitialChessBoard.printInitialChessBoardReverse();
            inGame = true;


            // Return the response from the server
            return response.toString();

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String joinGameParser(String gameId, String playerColor){
        try {
            // Construct the request body
            String requestBody = "{ \"gameID\": \"" + gameId + "\", \"playerColor\": \"" + playerColor + "\"}";

            // Construct the URL for the join game endpoint
            URI uri = new URI(serverUrl + "/game");

            // Create an HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            // Set the request method to PUT
            connection.setRequestMethod("PUT");

            // Set the content type header
            connection.setRequestProperty("Content-Type", "application/json");

            // Set the Authorization header with the authToken
            connection.setRequestProperty("Authorization", authToken);

            // Enable output for sending request body
            connection.setDoOutput(true);

            connection.connect();

            // Write the request body to the connection output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response from the server
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }


            // Print the ChessBoard state
            System.out.println("ChessBoard State:");


            // Close the connection
            connection.disconnect();
            InitialChessBoard.printInitialChessBoard();
            InitialChessBoard.printInitialChessBoardReverse();

            inGame = true;
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.joinPlayer(username);


            // Return the response from the server
            return response.toString();

        } catch (URISyntaxException | IOException | ResponseException e) {
            throw new RuntimeException(e);
        }
    }
    public String joinGame() {
        // Prompt the user to input the game ID and color
        String gameId = promptInput("Enter the ID of the game you want to join: ");
        String actualGameId = String.valueOf(dummyToActualIdMap.get(Integer.parseInt(gameId)));
        String playerColor = promptInput("Enter the color you want to play: ");
        return joinGameParser(actualGameId, playerColor);
    }



    public String listGames() {
        try {
            // Construct the URL for the list games endpoint
            URI uri = new URI(serverUrl + "/game");

            // Create an HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set the Authorization header with the authToken
            connection.setRequestProperty("Authorization", authToken);

            // Check the response code to ensure successful request
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Read the response from the server
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
                // Close the connection
                connection.disconnect();

                System.out.println("Raw JSON response: " + response);

                // Parse JSON and format output
                return formatGameList(response.toString());
            } else {
                // Return an error message
                return "Error: Unable to list games";
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatGameList(String json) {
        StringBuilder formattedOutput = new StringBuilder();
        JsonObject gamesObject = JsonParser.parseString(json).getAsJsonObject();
        JsonArray gamesArray = gamesObject.getAsJsonArray("games");

        if (gamesArray != null) {
            int dummyIdCounter = 1;
            int actualIdCounter = 1;
            formattedOutput.append("ID\tGame Name\t\tWhite Player\t\tBlack Player\n");
            for (JsonElement element : gamesArray) {
                JsonObject game = element.getAsJsonObject();
                int actualId = game.get("gameID").getAsInt();
                String gameName = game.get("gameName").getAsString();
                // Check if whiteUsername is present before retrieving its value
                String whitePlayer = game.has("whiteUsername") ? game.get("whiteUsername").getAsString() : "N/A";
                // Check if blackUsername is present before retrieving its value
                String blackPlayer = game.has("blackUsername") ? game.get("blackUsername").getAsString() : "N/A";
                // Store the mapping of dummy ID to actual ID
                dummyToActualIdMap.put(dummyIdCounter, actualId);
                formattedOutput.append(String.format("%-3s\t%-15s\t%-20s\t%-15s\n", dummyIdCounter, gameName, whitePlayer, blackPlayer));
                dummyIdCounter++;
                actualIdCounter++;
            }
        } else {
            formattedOutput.append("No games available.\n");
        }

        return formattedOutput.toString();
    }







    public String createGame() {
        // Prompt the user to input the name for the new game
        String gameName = promptInput("Enter the name for the new game: ");

        try {
            // Construct the request body
            String requestBody = "{ \"gameName\": \"" + gameName + "\"}";

            // Construct the URL for the create game endpoint
            URI uri = new URI(serverUrl + "/game");

            // Create an HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Set the content type header
            connection.setRequestProperty("Content-Type", "application/json");

            // Set the Authorization header with the authToken
            connection.setRequestProperty("Authorization", authToken);

            // Enable output for sending request body
            connection.setDoOutput(true);

            // Write the request body to the connection output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response from the server
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            // Close the connection
            connection.disconnect();

            // Return the response from the server

            return response.toString();


// Set the initial chessboard state as part of the game implementation


        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void quit() {
        System.out.print("Exiting Application. . .");
        System.exit(0);
    }

    public String helpPostlogin() {
        return SET_TEXT_COLOR_BLUE + "Command\t\t\tDescription\n" +
                "-------\t\t\t-----------\n" +
                "Help\t\t\tDisplays text informing the user what actions they can take.\n" +
                "Logout\t\t\tLogs out the user. Calls the server logout API to logout the user. After logging out with the server, the client should transition to the Prelogin UI.\n" +
                "Create Game\t\tAllows the user to input a name for the new game. Calls the server create API to create the game. This does not join the player to the created game; it only creates the new game in the server.\n" +
                "List Games\t\tLists all the games that currently exist on the server. Calls the server list API to get all the game data, and displays the games in a numbered list, including the game name and players (not observers) in the game. The numbering for the list should be independent of the game IDs.\n" +
                "Join Game\t\tAllows the user to specify which game they want to join and what color they want to play. They should be able to enter the number of the desired game. Your client will need to keep track of which number corresponds to which game from the last time it listed the games. Calls the server join API to join the user to the game.\n" +
                "Join Observer\tAllows the user to specify which game they want to observe. They should be able to enter the number of the desired game. Your client will need to keep track of which number corresponds to which game from the last time it listed the games. Calls the server join API to verify that the specified game exists.";
    }




    public String loginPrompt() {
        // Prompt the user for username and password
        String username = promptInput("Enter username: ");
        String password = promptInput("Enter password: ");

        // Call the login method with the provided username and password
        return login(username, password);
    }
    public String clear() {
        try {
            // Construct the URL for the clear endpoint
            URI uri = new URI(serverUrl + "/db");

            // Create an HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            // Set the request method to DELETE
            connection.setRequestMethod("DELETE");

            // Check the response code to ensure successful clearance
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Return a success message
                return "Clearance successful";
            } else {
                // Return an error message
                return "Error: Clearance failed";
            }

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public String registerPrompt() {
        // Prompt the user for username, password, and email
        String username = promptInput("Enter username: ");
        String password = promptInput("Enter password: ");
        String email = promptInput("Enter email: ");

        // Call the register method with the provided information
        return register(username, password, email);
    }


    public String helpPrelogin() {
        return SET_TEXT_COLOR_BLUE + "Command\t\tDescription\n" +
                "-------\t\t-----------\n" +
                "Help\t\tDisplays text informing the user what actions they can take.\n" +
                "Quit\t\tExits the program.\n" +
                "Login\t\tPrompts the user to input login information. Calls the server login API to login the user. When successfully logged in, the client should transition to the Postlogin UI.\n" +
                "Register\tPrompts the user to input registration information. Calls the server register API to register and login the user. If successfully registered, the client should be logged in and transition to the Postlogin UI.";
    }
    public String login(String username, String password) {
        // Construct the login request body
        String requestBody = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        // Send the login request to the server
        // You can use libraries like HttpClient or OkHttp for making HTTP requests
        // Here's a simplified example using HttpURLConnection
        try {

            URI uri = new URI(serverUrl + "/session");

            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response from the server
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            connection.disconnect();
            JsonObject jsonResponse = new Gson().fromJson(response.toString(), JsonObject.class);
            this.authToken = jsonResponse.get("authToken").getAsString();
            this.username = username;

            // Return the response from the server
            return response.toString();

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String register(String username, String password, String email) {

        // Construct the registration request body
        String requestBody = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\", \"email\": \"" + email + "\"}";

        // Send the registration request to the server
        // Here's a simplified example using HttpURLConnection
        try {
            // Construct the URL for the registration endpoint
            URI uri = new URI(serverUrl + "/user");

            // Create an HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Set the content type header
            connection.setRequestProperty("Content-Type", "application/json");

            // Enable output for sending request body
            connection.setDoOutput(true);

            // Write the request body to the connection output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response from the server
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            // Close the connection
            connection.disconnect();
            JsonObject jsonResponse = new Gson().fromJson(response.toString(), JsonObject.class);
            this.authToken = jsonResponse.get("authToken").getAsString();
            System.out.print(authToken);
            // Return the response from the server
            this.username = username;
            return response.toString();


        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String promptInput(String prompt) {
        System.out.print(prompt);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public String logout() {

        try {
            // Construct the URL for the session endpoint
            URI uri = new URI(serverUrl + "/session");

            // Create an HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            // Set the request method to DELETE
            connection.setRequestMethod("DELETE");

            // Set the Authorization header with the authToken
            connection.setRequestProperty("Authorization", authToken);

            // Check the response code to ensure successful logout
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Return a success message
                authToken = null;
                username = null;
                return "Logout successful";
            } else {
                // Return an error message
                return "Error: Logout failed";
            }

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getAuthToken() {
        return this.authToken != null;
    }
}
