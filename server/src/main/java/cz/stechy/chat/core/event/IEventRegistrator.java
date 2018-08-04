package cz.stechy.chat.core.event;

import cz.stechy.chat.core.IEventHandler;

/**
 * Rozhraní definující metody pro přihlášení/odhlášení příjmu zpráv daného typu od klienta
 */
public interface IEventRegistrator {

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

}
