package cz.stechy.chat.core.writer;

import cz.stechy.chat.core.IThreadControl;
import cz.stechy.chat.net.message.IMessage;
import java.io.ObjectOutputStream;

/**
 * Rozhraní definující metodu pro odeslání zprávy příjemci
 */
public interface IWriterThread extends IThreadControl {

    /**
     * Odešle zprávu
     *
     * @param writer {@link ObjectOutputStream} Writer, pomocí kterého se zpráva odešle
     * @param message {@link IMessage} Zpráva, která se má odeslat
     */
    void sendMessage(ObjectOutputStream writer, IMessage message);

}
