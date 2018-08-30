package cz.stechy.chat.core.multicaster;

import cz.stechy.chat.core.ServerInfoProvider;

/**
 * Továrna instancí rozhraní {@link IMulticastSender}
 */
public interface IMulticastSenderFactory {

    /**
     * Vytvoří novou instanci rozhraní {@link IMulticastSender}
     *
     * @param serverInfoProvider {@link ServerInfoProvider} Poskytovatel informací o serveru
     * @return {@link IMulticastSender}
     */
    IMulticastSender getMulticastSender(ServerInfoProvider serverInfoProvider);

}
