package client;

import java.util.Objects;
import java.util.Scanner;
import client.websocket.NotificationHandler;
import webSocketMessages.ServerMessage;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler{
    private final ServerFacade client;


    public Repl(String serverUrl) {
        this.client = new ServerFacade(serverUrl, this);

    }

    public void run() {
        System.out.println(BLACK_PAWN + " " + BLACK_KING + " Welcome to Chess :) " + BLACK_KING + " " + BLACK_PAWN);
        System.out.print(client.helpPrelogin());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(ServerMessage serverMessage) {
        System.out.println(SET_TEXT_COLOR_RED + serverMessage.getMessage());
        printPrompt();
    }

    private void printPrompt() {
        if (client.getAuthToken()) {
            System.out.print("\n" + SET_TEXT_COLOR_BLUE + "LOGGED IN >>> " + SET_TEXT_COLOR_GREEN);
        } else {
            System.out.print("\n" + SET_TEXT_COLOR_RED + "LOGGED OUT >>> " + SET_TEXT_COLOR_GREEN);
        }
    }
}
