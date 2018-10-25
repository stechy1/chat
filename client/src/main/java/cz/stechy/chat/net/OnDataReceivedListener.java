package cz.stechy.chat.net;

import cz.stechy.chat.net.message.IMessage;

@FunctionalInterface
public interface OnDataReceivedListener {

    /**
     * Metoda je zavolána vždy, když dorazí nějaká data
     *
     * @param message {@link IMessage} Přijatá zpráva
     */
    void onDataReceived(IMessage message);
}