package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;



public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public void add(String visitorName, Session session) {
        var connection = new Connection(visitorName, session);
        connections.put(visitorName, connection);
    }
    public void remove(String visitorName) {
        connections.remove(visitorName);
    }
    public void broadcast(String excludeVisitorName, ServerMessage notification) throws IOException {
        var gson = new Gson();
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName)) {
                    try{
                        String jsonMessage = gson.toJson(notification);
                        // Send the JSON message
                        c.send(jsonMessage);
                    }catch(IOException e) {
                        // Handle IOException if send fails
                        e.printStackTrace();
                    }

                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }
}
