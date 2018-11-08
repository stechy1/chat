package cz.stechy.chat.plugins.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.stechy.chat.core.connection.IClient;
import cz.stechy.chat.core.connection.MessageReceivedEvent;
import cz.stechy.chat.core.event.IEvent;
import cz.stechy.chat.core.event.IEventBus;
import cz.stechy.chat.net.message.ChatMessage;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClientRequestConnect;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClientState;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClientTyping;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageAdministrationData.IChatMessageAdministrationData;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageCommunicationData.ChatMessageCommunicationDataContent;
import cz.stechy.chat.net.message.ChatMessage.IChatMessageData;
import cz.stechy.chat.plugins.IPlugin;
import cz.stechy.chat.plugins.auth.event.LoginEvent;
import cz.stechy.chat.plugins.auth.event.LogoutEvent;
import cz.stechy.chat.plugins.chat.service.IChatService;

@Singleton
public class ChatPlugin implements IPlugin {

    public static final String PLUGIN_NAME = "chat";

    private final IChatService chatService;

    @Inject
    public ChatPlugin(IChatService chatService) {
        this.chatService = chatService;
    }

    private void loginEventHandler(IEvent event) {
        final LoginEvent loginEvent = (LoginEvent) event;
        chatService.addClient(loginEvent.client, loginEvent.user.id, loginEvent.user.name);
    }

    private void logoutEventHandler(IEvent event) {
        final LogoutEvent logoutEvent = (LogoutEvent) event;
        chatService.removeClient(logoutEvent.user.id);
    }

    private void chatMessageHandler(IEvent event) {
        final MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;
        final IClient client = messageReceivedEvent.getClient();
        final ChatMessage chatMessage = (ChatMessage) messageReceivedEvent.getReceivedMessage();
        final IChatMessageData chatMessageData = (IChatMessageData) chatMessage.getData();

        switch (chatMessageData.getDataType()) {
            case DATA_ADMINISTRATION:
                IChatMessageAdministrationData administrationData = (IChatMessageAdministrationData) chatMessageData.getData();
                switch (administrationData.getAction()) {
                    case CLIENT_REQUEST_CONNECT:
                        final ChatMessageAdministrationClientRequestConnect clientRequestConnect = (ChatMessageAdministrationClientRequestConnect) administrationData;
                        final String clientId = clientRequestConnect.getId();
                        final String clientName = clientRequestConnect.getName();
                        chatService.addClient(client, clientId, clientName);
                        break;
                    case CLIENT_DISCONNECTED:
                        final ChatMessageAdministrationClientState clientDisconnected = (ChatMessageAdministrationClientState) administrationData;
                        final String disconnectedClientId = clientDisconnected.getId();
                        chatService.removeClient(disconnectedClientId);
                        break;
                    case CLIENT_TYPING:
                        final ChatMessageAdministrationClientTyping clientIsTyping = (ChatMessageAdministrationClientTyping) administrationData;
                        final String typingClientId = clientIsTyping.getId();
                        chatService.informClientIsTyping(typingClientId, chatService.findIdByClient(client).orElse(""), true);
                        break;
                    case CLIENT_NOT_TYPING:
                        final ChatMessageAdministrationClientTyping clientIsNotTyping = (ChatMessageAdministrationClientTyping) administrationData;
                        final String notTypingClientId = clientIsNotTyping.getId();
                        chatService.informClientIsTyping(notTypingClientId, chatService.findIdByClient(client).orElse(""), false);
                        break;
                    default:
                        throw new IllegalArgumentException("Neplatný argument. " + administrationData.getAction());
                }
                break;
            case DATA_COMMUNICATION:
                final ChatMessageCommunicationDataContent communicationDataContent = (ChatMessageCommunicationDataContent) chatMessageData.getData();
                final String destinationClientId = communicationDataContent.getDestination();
                final String sourceClientId = chatService.findIdByClient(client).orElse("");
                final byte[] rawMessage = communicationDataContent.getData();
                chatService.sendMessage(destinationClientId, sourceClientId, rawMessage);
                break;
            default:
                throw new IllegalArgumentException("Neplatný argument." + chatMessageData.getDataType());
        }
    }

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public void init() {
        System.out.println("Inicializuji chat plugin.");
    }

    @Override
    public void registerMessageHandlers(IEventBus eventBus) {
        eventBus.registerEventHandler(LoginEvent.EVENT_TYPE, this::loginEventHandler);
        eventBus.registerEventHandler(LogoutEvent.EVENT_TYPE, this::logoutEventHandler);
        eventBus.registerEventHandler(ChatMessage.MESSAGE_TYPE, this::chatMessageHandler);
    }
}
