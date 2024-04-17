package webSocketMessages;

public class LeaveCommand extends UserGameCommand{
    public LeaveCommand(String authToken) {
        super(authToken);
        this.commandType = commandTypeMap.get(getClass());
    }
}
