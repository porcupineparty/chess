package webSocketMessages;

public class JoinObserverCommand extends UserGameCommand{
    public JoinObserverCommand(String authToken, CommandType commandType) {
        super(authToken);
        this.commandType = commandTypeMap.get(getClass());
    }
}
