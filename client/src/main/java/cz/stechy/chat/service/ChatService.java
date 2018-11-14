package cz.stechy.chat.service;

import cz.stechy.chat.model.ChatContact;
import cz.stechy.chat.net.OnDataReceivedListener;
import cz.stechy.chat.net.message.ChatMessage;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageAdministrationData;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageAdministrationData.ChatAction;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClientState;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClientTyping;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageAdministrationData.IChatMessageAdministrationData;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageCommunicationData;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageCommunicationData.ChatMessageCommunicationDataContent;
import cz.stechy.chat.net.message.ChatMessage.IChatMessageData;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Třída poskytující chatovací služby
 */
public final class ChatService implements IChatService {

    // Kolekce připojených klientů
    private final ObservableMap<String, ChatContact> clients = FXCollections.observableHashMap();
    // Register posluchačů na příjem zprávy
    private final List<String> typingInformations = new ArrayList<>();
    // Komunikátor se serverem
    private final IClientCommunicationService communicator;
    private String thisUserId;

    /**
     * Vytvoří novou chatovací službu
     *
     * @param communicator {@link IClientCommunicationService} Služba poskytující komunikaci se serverem
     */
    public ChatService(IClientCommunicationService communicator) {
        this.communicator = communicator;
        this.communicator.connectionStateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case CONNECTED:
                    this.communicator.registerMessageObserver(ChatMessage.MESSAGE_TYPE, this.chatMessageListener);
                    break;
                case CONNECTING:
                    break;
                case DISCONNECTED:
                    this.communicator.unregisterMessageObserver(ChatMessage.MESSAGE_TYPE, this.chatMessageListener);
                    break;
            }

        });
    }

    /**
     * Vrátí kontakt na základě Id
     *
     * @param id Id kontaktu
     * @return {@link ChatContact}
     */
    private ChatContact getContactById(String id) {
        return clients.get(id);
    }

    @Override
    public void saveUserId(String id) {
        this.thisUserId = id;
    }

    @Override
    public void sendMessage(String id, String message) {
        final ChatContact chatContact = clients.get(id);
        if (chatContact == null) {
            System.out.println("Nebyl nalezen kontakt, kterému chci odeslat zprávu.");
            throw new RuntimeException("Klient nebyl nalezen.");
        }

        System.out.println("Odesílám zprávu uživateli: " + chatContact.getName());
        byte[] messageData = (message + " ").getBytes();
        communicator.sendMessage(new ChatMessage(new ChatMessageCommunicationData(id, messageData)));

        chatContact.addMessage(clients.get(thisUserId), message);
    }

    @Override
    public void notifyTyping(String id, boolean typing) {
        // Nebudu neustále posílat informaci, že klient píše
        if (typing && typingInformations.contains(id)) {
            return;
        }

        System.out.println("Informuji protější stranu, že jsem začal/přestal psát.");
        communicator.sendMessage(new ChatMessage(
            new ChatMessageAdministrationData(
                new ChatMessageAdministrationClientTyping(
                    typing ? ChatAction.CLIENT_TYPING : ChatAction.CLIENT_NOT_TYPING, id))));

        if (typing) {
            // Pokud klient začal psát, uložím si tuto informaci, abych příště již klienta neinformoval
            typingInformations.add(id);
        } else {
            // Pokud klient přestal psát, tak odeberu informaci, abych příště mohl klienta informovat
            typingInformations.remove(id);
        }
    }

    @Override
    public ObservableMap<String, ChatContact> getClients() {
        return FXCollections.unmodifiableObservableMap(clients);
    }

    private final OnDataReceivedListener chatMessageListener = message -> {
        final ChatMessage chatMessage = (ChatMessage) message;
        final IChatMessageData messageData = (IChatMessageData) chatMessage.getData();
        switch (messageData.getDataType()) {
            case DATA_ADMINISTRATION:
                final ChatMessageAdministrationData administrationData = (ChatMessageAdministrationData) messageData;
                final IChatMessageAdministrationData data = (IChatMessageAdministrationData) administrationData.getData();
                switch (data.getAction()) {
                    case CLIENT_CONNECTED:
                        final ChatMessageAdministrationClientState messageAdministrationClientConnected = (ChatMessageAdministrationClientState) data;
                        final String connectedClientID = messageAdministrationClientConnected.getId();
                        final String connectedClientName = messageAdministrationClientConnected.getName();
                        System.out.println("Připojil se nový klient " + connectedClientID);
                        Platform.runLater(() -> clients.putIfAbsent(connectedClientID, new ChatContact(connectedClientID, connectedClientName)));
                        break;
                    case CLIENT_DISCONNECTED:
                        final ChatMessageAdministrationClientState messageAdministrationClientDiconnected = (ChatMessageAdministrationClientState) data;
                        final String disconnectedClientID = messageAdministrationClientDiconnected.getId();
                        System.out.println("Odpojil se klient " + disconnectedClientID);
                        Platform.runLater(() -> clients.remove(disconnectedClientID));
                        break;
                    case CLIENT_TYPING:
                        final ChatMessageAdministrationClientTyping messageAdministrationClientTyping = (ChatMessageAdministrationClientTyping) data;
                        final String typingClientId = messageAdministrationClientTyping.getId();
                        final ChatContact typingClient = getContactById(typingClientId);
                        System.out.println("Klient: " + typingClientId + " začal psát.");
                        Platform.runLater(typingClient::setTyping);
                        break;
                    case CLIENT_NOT_TYPING:
                        final ChatMessageAdministrationClientTyping messageAdministrationClientNoTyping = (ChatMessageAdministrationClientTyping) data;
                        final String noTypingClientId = messageAdministrationClientNoTyping.getId();
                        final ChatContact noTypingClient = getContactById(noTypingClientId);
                        System.out.println("Klient: " + noTypingClientId + " přestal psát.");
                        Platform.runLater(noTypingClient::resetTyping);
                        break;
                    default:
                        throw new IllegalArgumentException("Neplatny argument.");
                }
                break;
            case DATA_COMMUNICATION:
                final ChatMessageCommunicationData communicationData = (ChatMessageCommunicationData) messageData;
                final ChatMessageCommunicationDataContent communicationDataContent = (ChatMessageCommunicationDataContent) communicationData.getData();
                final String destination = communicationDataContent.getDestination();
                final byte[] messageRaw = communicationDataContent.getData();
                final String messageContent = new String(messageRaw, StandardCharsets.UTF_8);
                Platform.runLater(() -> {
                    if (clients.containsKey(destination)) {
                        final ChatContact chatContact = clients.get(destination);
                        System.out.println("Byla přijata zpráva od klienta: " + chatContact.getName());
                        chatContact.addMessage(chatContact, messageContent);
                    }
                });
                break;
            default:
                throw new IllegalArgumentException("Neplatný parametr.");
        }
    };
}
