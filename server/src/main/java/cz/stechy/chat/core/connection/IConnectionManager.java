package cz.stechy.chat.core.connection;

import java.io.IOException;
import java.net.Socket;

/**
 * Rozhraní obsahující metody pro správce spojení
 */
public interface IConnectionManager {

    /**
     * Přidá nového klienta do svého seznamu
     *
     * @param socket {@link Socket}
     * @throws IOException Pokud bylo spojení ztraceno
     */
    void addClient(Socket socket) throws IOException;

    /**
     * Metoda se zavolá, když se startuje server
     */
    void onServerStart();

    /**
     * Metoda se zavolá, když se ukončuje server
     */
    void onServerStop();

}