package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.NotificationMessage;
import webSocketMessages.ServerMessage;
import webSocketMessages.UserGameCommand;

import java.io.IOException;
@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(userGameCommand.getAuthString(), session);
            case JOIN_OBSERVER -> joinObserver(userGameCommand.getAuthString(), session);
            case MAKE_MOVE -> makeMove(userGameCommand.getAuthString(), session);
            case LEAVE -> leave(userGameCommand.getAuthString(), session);
            case RESIGN -> resign(userGameCommand.getAuthString(), session);
        }
    }
    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        // Handle WebSocket error here
        System.err.println("WebSocket error occurred: " + throwable.getMessage());
    }

    private void joinPlayer(String username, Session session) throws IOException {
        connections.add(username, session);
        var message = String.format("%s just joined the game", username);
        var notificationMessage = new NotificationMessage();
        notificationMessage.setMessage(message);
        connections.broadcast(username, notificationMessage);
    }
    private void joinObserver(String username, Session session) throws IOException {
        connections.add(username, session);
        var message = String.format("%s just joined the game", username);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        serverMessage.setMessage(message);
        connections.broadcast(username, serverMessage);
    }
    private void makeMove(String username, Session session) throws IOException {
        connections.add(username, session);
        var message = String.format("%s just joined the game", username);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        serverMessage.setMessage(message);
        connections.broadcast(username, serverMessage);
    }
    private void leave(String username, Session session) throws IOException {
        connections.add(username, session);
        var message = String.format("%s just joined the game", username);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        serverMessage.setMessage(message);
        connections.broadcast(username, serverMessage);
    }
    private void resign(String username, Session session) throws IOException {
        connections.add(username, session);
        var message = String.format("%s just joined the game", username);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        serverMessage.setMessage(message);
        connections.broadcast(username, serverMessage);
    }
}
