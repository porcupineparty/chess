import chess.*;
import client.Repl;

public class Main {
    public static void main(String[] args) {
        String serverUrl = "http://localhost:29586"; // Replace with your server URL
        Repl repl = new Repl(serverUrl);
        repl.run();
    }
}
