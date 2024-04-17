package webSocketMessages;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    public UserGameCommand(String authToken) {
        this.authToken = authToken;
    }

    public enum CommandType {
        JOIN_PLAYER,
        JOIN_OBSERVER,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    protected CommandType commandType;

    private final String authToken;

    public String getAuthString() {
        return authToken;
    }

    public CommandType getCommandType() {
        return this.commandType;
    }

    public void setCommandType(CommandType commandType){
        this.commandType = commandType;
    }
    protected static final Map<Class<? extends UserGameCommand>, CommandType> commandTypeMap = new HashMap<>();

    static {
        commandTypeMap.put(JoinPlayerCommand.class, CommandType.JOIN_PLAYER);
        commandTypeMap.put(JoinObserverCommand.class, CommandType.JOIN_OBSERVER);
        commandTypeMap.put(MakeMoveCommand.class, CommandType.MAKE_MOVE);
        commandTypeMap.put(LeaveCommand.class, CommandType.LEAVE);
        commandTypeMap.put(ResignCommand.class, CommandType.RESIGN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserGameCommand))
            return false;
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() && Objects.equals(getAuthString(), that.getAuthString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthString());
    }
}
