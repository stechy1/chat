package cz.stechy.chat.net.message;

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
}
