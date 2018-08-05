package cz.stechy.chat.core.connection;

import cz.stechy.chat.core.event.IEvent;
import cz.stechy.chat.net.message.IMessage;

public class MessageReceivedEvent implements IEvent {

    private final IMessage receivedMessage;
    private final Client client;

    MessageReceivedEvent(IMessage receivedMessage, Client client) {
        this.receivedMessage = receivedMessage;
        this.client = client;
    }

    @Override
    public String getEventType() {
        return receivedMessage.getType();
    }

    public IMessage getReceivedMessage() {
        return receivedMessage;
    }

    public Client getClient() {
        return client;
    }
}
