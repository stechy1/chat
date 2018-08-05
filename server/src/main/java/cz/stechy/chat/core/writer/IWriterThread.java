package cz.stechy.chat.core.writer;

import cz.stechy.chat.core.IThreadControl;
import java.io.ObjectOutputStream;

/**
 * Rozhraní definující metodu pro odeslání zprávy příjemci
 */
public interface IWriterThread extends IThreadControl {

    /**
     * Odešle zprávu
     *
     * @param writer {@link ObjectOutputStream} Writer, pomocí kterého se zpráva odešle
     * @param message Zpráva, která se má odeslat
     */
    void sendMessage(ObjectOutputStream writer, Object message);

}
