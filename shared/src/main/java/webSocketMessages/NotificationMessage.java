package webSocketMessages;

public class NotificationMessage extends ServerMessage{
    public NotificationMessage() {
        super(ServerMessage.ServerMessageType.NOTIFICATION);

    }
}
