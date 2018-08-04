package cz.stechy.chat.core.event;

/**
 * Značkovací rozhraní
 */
public interface IEventProcessor extends IEventRegistrator {

    /**
     * Zveřejní event
     *
     * @param event {@link Event} Event, který se má zveřejnit
     */
    void publishEvent(Event event);

}
