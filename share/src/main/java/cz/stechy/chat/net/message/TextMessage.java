package cz.stechy.chat.net.message;

/**
 * Jednoduchá textová zpráva
 */
public class TextMessage implements IMessage {

    public static final String MESSAGE_TYPE = "text";

    private final String data;

    public TextMessage(String data) {
        this.data = data;
    }

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public Object getData() {
        return data;
    }
}
