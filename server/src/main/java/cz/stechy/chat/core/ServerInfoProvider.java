package cz.stechy.chat.core;

import cz.stechy.chat.net.message.ServerStatusMessage;

/**
 * Rozhraní pro poskytování informací o serveru
 */
public interface ServerInfoProvider {

    /**
     * Vytvoří a vrátí novou zprávu s informacemi o stavu serveru
     *
     * @return {@link ServerStatusMessage}
     */
    ServerStatusMessage getServerStatusMessage();

}
