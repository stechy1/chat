package cz.stechy.chat.core.server;


import cz.stechy.chat.core.IThreadControl;
import cz.stechy.chat.core.ServerInfoProvider;

/**
 * Značkovací rozhraní pro hlavní vlákno serveru
 */
public interface IServerThread extends IThreadControl, ServerInfoProvider {

}