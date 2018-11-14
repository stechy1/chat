package cz.stechy.chat.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class ChatContact {

    private final ObservableList<ChatMessageEntry> messages = FXCollections.observableArrayList();
    private final StringProperty name = new SimpleStringProperty(this, "name", null);
    private final ObjectProperty<Color> contactColor = new SimpleObjectProperty<>(this, "contactColor", null);
    private final IntegerProperty unreadedMessages = new SimpleIntegerProperty(this, "unreadedMessages", 0);
    private final BooleanProperty typing = new SimpleBooleanProperty(this, "typing", false);
    private final String id;

    public ChatContact(String id, String name) {
        this.id = id;
        this.name.set(name);
        contactColor.set(Color.color(Math.random(), Math.random(), Math.random()));
    }

    /**
     * Přidá zprávu do kolekce všech zpráv
     *
     * @param chatContact {@link ChatContact} Kontakt, od koho zpráva je
     * @param message Obsah zprávy
     */
    public void addMessage(ChatContact chatContact, String message) {
        messages.add(new ChatMessageEntry(chatContact, message));
        unreadedMessages.set(unreadedMessages.get() + 1);
    }

    /**
     * Vyresetuje číselník nepřečtených zpráv
     */
    public void resetUnreadedMessages() {
        unreadedMessages.set(0);
    }

    /**
     * Nastaví kontaktu příznak, že něco píše
     */
    public void setTyping() {
        typing.set(true);
    }

    /**
     * Vyresetuje kontaktu příznak, že něco píše
     */
    public void resetTyping() {
        typing.set(false);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public Color getColor() {
        return contactColor.get();
    }

    public ReadOnlyObjectProperty<Color> contactColorProperty() {
        return contactColor;
    }

    public ReadOnlyIntegerProperty unreadedMessagesProperty() {
        return unreadedMessages;
    }

    public boolean isTyping() {
        return typing.get();
    }

    public BooleanProperty typingProperty() {
        return typing;
    }

    public ObservableList<ChatMessageEntry> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return getName();
    }
}
