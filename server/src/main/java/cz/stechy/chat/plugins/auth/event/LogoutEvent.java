package cz.stechy.chat.plugins.auth.event;

import cz.stechy.chat.core.event.IEvent;
import cz.stechy.chat.plugins.auth.User;

public class LogoutEvent implements IEvent {

    public static final String EVENT_TYPE = "logout";

    public final User user;

    public LogoutEvent(User user) {
        this.user = user;
    }

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}
