package cz.stechy.chat.net.message;

import java.io.Serializable;

public class ChatMessage implements IMessage {

    private static final long serialVersionUID = -7817515518938131863L;

    public static final String MESSAGE_TYPE = "chat";

    private final IChatMessageData data;

    public ChatMessage(IChatMessageData data) {
        this.data = data;
    }

    @Override
    public String getType() {
        return MESSAGE_TYPE;
    }

    @Override
    public Object getData() {
        return data;
    }

    public interface IChatMessageData extends Serializable {

        ChatMessageDataType getDataType();

        Object getData();

    }

    public enum ChatMessageDataType {
        DATA_ADMINISTRATION, DATA_COMMUNICATION
    }

    public static final class ChatMessageAdministrationData implements IChatMessageData {

        private static final long serialVersionUID = 8237826895694688852L;

        private final IChatMessageAdministrationData data;

        public ChatMessageAdministrationData(IChatMessageAdministrationData data) {
            this.data = data;
        }

        @Override
        public ChatMessageDataType getDataType() {
            return ChatMessageDataType.DATA_ADMINISTRATION;
        }

        @Override
        public Object getData() {
            return data;
        }

        public enum ChatAction {
            CLIENT_REQUEST_CONNECT, // Požadavek na připojení k chatovací službě
            CLIENT_CONNECTED, CLIENT_DISCONNECTED, // Akce klientů
            CLIENT_TYPING, CLIENT_NOT_TYPING, // Informace o tom, zda-li někdo píše
        }

        public interface IChatMessageAdministrationData extends Serializable {

            ChatAction getAction();

        }

        public static final class ChatMessageAdministrationClientRequestConnect implements IChatMessageAdministrationData {

            private static final long serialVersionUID = 642524654412490721L;

            private final String id;
            private final String name;

            public ChatMessageAdministrationClientRequestConnect(String id, String name) {
                this.id = id;
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            @Override
            public ChatAction getAction() {
                return ChatAction.CLIENT_REQUEST_CONNECT;
            }
        }

        public static final class ChatMessageAdministrationClientState implements IChatMessageAdministrationData {

            private static final long serialVersionUID = -6101992378764622660L;

            private final ChatAction action;
            private final String id;
            private final String name;

            public ChatMessageAdministrationClientState(ChatAction action, String id) {
                this(action, id, "");
            }

            public ChatMessageAdministrationClientState(ChatAction action, String id, String name) {
                this.id = id;
                this.name = name;
                assert action == ChatAction.CLIENT_CONNECTED || action == ChatAction.CLIENT_DISCONNECTED;
                this.action = action;
            }

            public String getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            @Override
            public ChatAction getAction() {
                return action;
            }
        }

        public static final class ChatMessageAdministrationClientTyping implements IChatMessageAdministrationData {

            private static final long serialVersionUID = 630432882631419944L;

            private final ChatAction action;
            private final String id;

            public ChatMessageAdministrationClientTyping(ChatAction action, String id) {
                assert action == ChatAction.CLIENT_TYPING || action == ChatAction.CLIENT_NOT_TYPING;
                this.action = action;
                this.id = id;
            }

            public String getId() {
                return id;
            }

            @Override
            public ChatAction getAction() {
                return action;
            }
        }
    }

    public static final class ChatMessageCommunicationData implements IChatMessageData {

        private static final long serialVersionUID = -2426630119019364058L;

        private final ChatMessageCommunicationDataContent data;

        public ChatMessageCommunicationData(String id, byte[] data) {
            this.data = new ChatMessageCommunicationDataContent(id, data);
        }

        @Override
        public ChatMessageDataType getDataType() {
            return ChatMessageDataType.DATA_COMMUNICATION;
        }

        @Override
        public Object getData() {
            return data;
        }

        public static final class ChatMessageCommunicationDataContent implements Serializable {

            private static final long serialVersionUID = -905319575968060192L;

            private final String destination;
            private final byte[] data;

            ChatMessageCommunicationDataContent(String destination, byte[] data) {
                this.destination = destination;
                this.data = data;
            }

            public String getDestination() {
                return destination;
            }

            public byte[] getData() {
                return data;
            }
        }
    }
}
