package cz.stechy.chat.model;

public final class ChatMessageEntry {

    private final ChatContact chatContact;
    private final String message;

    ChatMessageEntry(ChatContact chatContact, String message) {
        this.chatContact = chatContact;
        this.message = message;
    }

    public ChatContact getChatContact() {
        return chatContact;
    }

    public String getMessage() {
        return message;
    }

}
