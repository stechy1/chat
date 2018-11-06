package cz.stechy.chat.plugins.auth.event;

import cz.stechy.chat.core.connection.IClient;
import cz.stechy.chat.core.event.IEvent;
import cz.stechy.chat.plugins.auth.User;

public class LoginEvent implements IEvent {

    public static final String EVENT_TYPE = "login";

    public final IClient client;
    public final User user;

    public LoginEvent(IClient client, User user) {
        this.client = client;
        this.user = user;
    }

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}
