package webSocketMessages;

public class JoinPlayerCommand extends UserGameCommand {
    public JoinPlayerCommand(String authToken, CommandType commandType) {
        super(authToken);
        this.commandType = commandTypeMap.get(getClass());
    }

}
