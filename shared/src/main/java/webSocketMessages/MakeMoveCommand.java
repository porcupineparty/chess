package webSocketMessages;

public class MakeMoveCommand extends UserGameCommand{
    public MakeMoveCommand(String authToken, CommandType commandType) {
        super(authToken);
        this.commandType = commandTypeMap.get(getClass());
    }

}
