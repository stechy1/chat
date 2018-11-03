package cz.stechy.chat.net.message;

public class ResponceMessage implements IMessage {

    public static final String MESSAGE_TYPE = "responce";

    private final boolean success;
    private final Object[] responceData;

    ResponceMessage(boolean success, Object[] responceData) {
        this.success = success;
        this.responceData = responceData;
    }

    @Override
    public String getType() {
        return MESSAGE_TYPE;
    }

    @Override
    public Object getData() {
        return responceData;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public boolean isResponce() {
        return true;
    }
}