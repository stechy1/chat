package cz.stechy.chat.net.message;

import java.io.Serializable;

public class AuthMessage implements IMessage {

    private static final long serialVersionUID = 2410714674227462122L;

    public static final String MESSAGE_TYPE = "auth";

    private final AuthAction action;
    private final boolean success;
    private final AuthMessageData data;

    public AuthMessage(AuthAction action, AuthMessageData data) {
        this(action, true, data);
    }

    public AuthMessage(AuthAction action, boolean success, AuthMessageData data) {
        this.action = action;
        this.success = success;
        this.data = data;
    }

    @Override
    public String getType() {
        return MESSAGE_TYPE;
    }

    public AuthAction getAction() {
        return action;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    public enum AuthAction {
        REGISTER, LOGIN, LOGOUT
    }

    public static final class AuthMessageData implements Serializable {

        private static final long serialVersionUID = -9036266648628886210L;

        public final String id;
        public final String name;

        public AuthMessageData() {
            this("");
        }

        public AuthMessageData(String name) {
            this("", name );
        }

        public AuthMessageData(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}