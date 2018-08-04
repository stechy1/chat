package cz.stechy.chat.core.writer;

import cz.stechy.chat.core.IThreadControl;
import cz.stechy.chat.net.message.IMessage;
import java.io.BufferedWriter;
import java.io.ObjectOutputStream;

/**
 * Rozhraní definující metody pro odeslání zprávi příjemci
 */
public interface IWriterThread extends IThreadControl {

    /**
     * Odešle zprávu
     *
     * @param writer {@link BufferedWriter} Writer, pomocí kterého se zpráva odešle
     * @param message Zpráva, která se má odeslat
     */
    void sendMessage(ObjectOutputStream writer, IMessage message);

}
