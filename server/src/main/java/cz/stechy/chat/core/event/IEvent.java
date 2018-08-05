package cz.stechy.chat.core.event;

/**
 * Rozhraní pro všechny události
 */
public interface IEvent {

    /**
     * Vrátí typ události
     *
     * @return Typ události
     */
    String getEventType();

}
