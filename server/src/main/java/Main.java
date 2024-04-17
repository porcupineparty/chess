import chess.*;
import passoffTests.obfuscatedTestClasses.TestServerFacade;
import server.Server;
import server.websocket.WebSocketHandler;

public class Main {
    private static Server server;
    public static void main(String[] args) {

        server = new Server();
        var port = server.run(29586);
        System.out.println("Started test HTTP server on " + port);

    }
}