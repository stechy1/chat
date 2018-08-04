package cz.stechy.chat.net.message;

public class HelloMessage implements IMessage {

    public static final String MESSAGE_TYPE = "hello";

    private final String data;

    public HelloMessage(String data) {
        this.data = data;
    }

    @Override
    public String getType() {
        return MESSAGE_TYPE;
    }

    @Override
    public Object getData() {
        return data;
    }
}
