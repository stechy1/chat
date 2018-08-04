package cz.stechy.chat.core.connection;

import java.io.IOException;

/**
 * Rozhraní definující metody připojeného klienta
 */
public interface IClient {

    /**
     * Odešle asynchronně klientovi zprávu
     *
     * @param message Zpráva, která se má odeslat
     */
    void sendMessageAsync(Object message);

    /**
     * Odešle synchronně klientovi zprávu
     *
     * @param message Zpráva, která se má odelsat
     * @throws IOException Pokud se nepodaří zprávu odeslat
     */
    void sendMessage(Object message) throws IOException;

    /**
     * Uzavře spojení s klientem
     */
    void close();

}
