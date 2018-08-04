package cz.stechy.chat.core.connection;

import cz.stechy.chat.core.Client;
import cz.stechy.chat.core.event.Event;

public class ClientDisconnectedEvent implements Event {

    public static final String EVENT_TYPE = "client-disonnected";

    private final Client client;

    ClientDisconnectedEvent(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}
