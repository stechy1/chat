package cz.stechy.chat.net.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Základní rozhraní pro všechny zprávy, které se budou posílat mezi serverem a klientam
 */
public interface IMessage extends Serializable {

    /**
     * Vrátí typ zprávy
     *
     * @return Typ zprávy
     */
    String getType();

    /**
     * Vrátí data, která obsahuje zpráva
     *
     * @return Data
     */
    Object getData();

    /**
     * Zjistí, zda-li byla vykonaná akce úspěšná, či nikoliv
     *
     * @return True, pokud byla akce úspěšní, jinak False
     */
    default boolean isSuccess() {
        return true;
    }

    /**
     * Převede objekt na balík dat
     *
     * @return Pole bytů
     * @throws IOException Pokud se nepodaří objekt serializovat
     */
    default byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.writeByte(0);
        final byte[] bytes = baos.toByteArray();
        assert bytes.length < 1024;

        return bytes;
    }
}
