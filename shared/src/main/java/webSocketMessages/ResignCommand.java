package webSocketMessages;

public class ResignCommand extends UserGameCommand{
    public ResignCommand(String authToken, UserGameCommand.CommandType commandType) {
        super(authToken);
        this.commandType = commandTypeMap.get(getClass());
    }
}
