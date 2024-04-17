package webSocketMessages;

public class ErrorMessage extends ServerMessage{
    public ErrorMessage() {
        super(ServerMessageType.ERROR);
    }
}
