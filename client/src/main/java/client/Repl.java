package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ServerFacade client;

    public Repl(String serverUrl) {
        this.client = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to Chess :)");
        System.out.print(client.help());

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
        System.out.print("\n" + "LOGGED IN DUMMY" + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}
