package client;

import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ServerFacade client;
    private final String serverUrl;

    public Repl(String serverUrl) {
        this.client = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
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

//    public void notify(Notification notification) {
//        System.out.println(RED + notification.message());
//        printPrompt();
//    }

    private void printPrompt() {
        if (client.getAuthToken()) {
            System.out.print("\n" + SET_TEXT_COLOR_BLUE + "LOGGED IN >>> " + SET_TEXT_COLOR_GREEN);
        } else {
            System.out.print("\n" + SET_TEXT_COLOR_RED + "LOGGED OUT >>> " + SET_TEXT_COLOR_GREEN);
        }
    }
}
