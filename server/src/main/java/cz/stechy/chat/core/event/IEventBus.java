package cz.stechy.chat.core.event;

/**
 * Rozhraní obsahující metodu pro
 */
public interface IEventBus {

    /**
     * Přihlásí odběr daného typu zprávy
     *
     * @param messageType Typ zprávy
     * @param listener {@link IEventHandler}
     */
    void registerEventHandler(String messageType, IEventHandler listener);

    /**
     * Odhlásí odběr daného typu zprávy
     *
     * @param messageType Typ zprávy
     * @param listener {@link IEventHandler}
     */
    void unregisterEventHandler(String messageType, IEventHandler listener);

    /**
     * Zveřejní event
     *
     * @param event {@link IEvent} IEvent, který se má zveřejnit
     */
    void publishEvent(IEvent event);

}
