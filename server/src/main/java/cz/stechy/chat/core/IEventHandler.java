package cz.stechy.chat.core;

import cz.stechy.chat.core.event.Event;

public interface IEventHandler {

    void handleEvent(Event event);

}
