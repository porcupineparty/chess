package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.Scanner;



public class ServerFacade {

    private String authToken; // Store the authentication token

    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            // Split the input into command and arguments
            String[] parts = input.trim().split("\\s+", 3);
            String command = parts[0].toLowerCase();

            // Determine which server request to make based on the command
            switch (command) {
                case "help":
                    return help();
                case "login":
                    return loginPrompt();
                case "register":
                    return registerPrompt();
                case "quit":
                    return quit();
                default:
                    // Handle unrecognized command
                    return help();
            }
        } catch (Exception e) {
            return handleException(e);
        }


    }

    public String loginPrompt() {
        // Prompt the user for username and password
        String username = promptInput("Enter username: ");
        String password = promptInput("Enter password: ");

        // Call the login method with the provided username and password
        return login(username, password);
    }

    public String registerPrompt() {
        // Prompt the user for username, password, and email
        String username = promptInput("Enter username: ");
        String password = promptInput("Enter password: ");
        String email = promptInput("Enter email: ");

        // Call the register method with the provided information
        return register(username, password, email);
    }


    public String help() {
        // Implement method to retrieve help information from the server
        //Command	Description
        //Help	Displays text informing the user what actions they can take.
        //Quit	Exits the program.
        //Login	Prompts the user to input login information. Calls the server login API to login the user. When successfully logged in, the client should transition to the Postlogin UI.
        //Register	Prompts the user to input registration information. Calls the server register API to register and login the user. If successfully registered, the client should be logged in and transition to the Postlogin UI.
        return "Command\tDescription\n" +
                "        Help\tDisplays text informing the user what actions they can take.\n" +
                "        Quit\tExits the program.\n" +
                "        Login\tPrompts the user to input login information. Calls the server login API to login the user. When successfully logged in, the client should transition to the Postlogin UI.\n" +
                "        Register\tPrompts the user to input registration information. Calls the server register API to register and login the user. If successfully registered, the client should be logged in and transition to the Postlogin UI."; // Replace with actual implementation
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

    public String quit() {
        if (authToken == null) {
            return "You are not logged in.";
        }

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
                return "Logout successful";
            } else {
                // Return an error message
                return "Error: Logout failed";
            }

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }






    // Helper method to handle exceptions
    private String handleException(Throwable e) {
        return e.getMessage(); // Simple handling for demonstration purposes
    }
}
