package webSocketMessages;

public class JoinPlayerCommand extends UserGameCommand {
    public JoinPlayerCommand(String authToken) {
        super(authToken);
        this.commandType = commandTypeMap.get(getClass());
    }

}
