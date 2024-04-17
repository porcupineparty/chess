package client.websocket;

import webSocketMessages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage notification);
}