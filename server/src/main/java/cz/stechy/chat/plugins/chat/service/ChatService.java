package cz.stechy.chat.plugins.chat.service;

import com.google.inject.Singleton;
import cz.stechy.chat.core.connection.IClient;
import cz.stechy.chat.net.message.ChatMessage;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageAdministrationData;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageAdministrationData.ChatAction;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClientState;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClientTyping;
import cz.stechy.chat.net.message.ChatMessage.ChatMessageCommunicationData;
import cz.stechy.chat.net.message.IMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

@Singleton
class ChatService implements IChatService {

    private final Map<String, ChatClient> clients = new HashMap<>();

    /**
     * Odešle zprávu všem klientům, kteří jsou přihlášení k chatu
     *
     * @param message {@link IMessage} Zpráva, která se má odeslat všem
     */
    private void broadcastMessage(IMessage message) {
        clients.values().forEach(chatClient -> chatClient.client.sendMessageAsync(message));
    }

    @Override
    public synchronized void addClient(IClient client, String id, String name) {
        System.out.println("Přidávám nového klienta {} na seznam v chatu.");
        final ChatClient chatClient = new ChatClient(client, name);
        // Odešlu klientovi aktuální seznam všech klientů
        clients.forEach((clientId, entry) ->
            client.sendMessageAsync(new ChatMessage(
                new ChatMessageAdministrationData(
                    new ChatMessageAdministrationClientState(
                        ChatAction.CLIENT_CONNECTED, clientId, entry.name)))));
        // Přidám záznam o klientovi na seznam
        clients.put(id, chatClient);
        // Rozešlu broadcast všem připojeným klientům
        // Ano, odešlu ho i klientovi, který se právě připojil
        // Tím získám jistou výhodu a můžu snadno přidat sám sebe na seznam kontaktů
        broadcastMessage(new ChatMessage(
            new ChatMessageAdministrationData(
                new ChatMessageAdministrationClientState(
                    ChatAction.CLIENT_CONNECTED, id, name))));
    }

    @Override
    public synchronized void removeClient(String id) {
        System.out.println("Odebírám klienta ze seznamu chatu.");
        // Odeberu záznam ze seznamu klientů
        clients.remove(id);
        // A rozešlu broadcast všem připojeným klientům, že se klient odpojuje
        broadcastMessage(new ChatMessage(
            new ChatMessageAdministrationData(
                new ChatMessageAdministrationClientState(
                    ChatAction.CLIENT_DISCONNECTED, id))));
    }

    @Override
    public void sendMessage(String destinationClientId, String sourceClientId, byte[] rawMessage) {
        clients.get(destinationClientId).client.sendMessageAsync(new ChatMessage(new ChatMessageCommunicationData(sourceClientId, rawMessage)));
    }

    @Override
    public Optional<String> findIdByClient(IClient client) {
        final Optional<Entry<String, ChatClient>> entryOptional = clients.entrySet()
            .stream()
            .filter(entry -> entry.getValue().client == client)
            .findFirst();

        return entryOptional.map(Entry::getKey);
    }

    @Override
    public void informClientIsTyping(String destinationClientId, String sourceClientId, boolean typing) {
        clients.get(destinationClientId).client.sendMessageAsync(
            new ChatMessage(
                new ChatMessageAdministrationData(
                    new ChatMessageAdministrationClientTyping(
                        typing ? ChatAction.CLIENT_TYPING : ChatAction.CLIENT_NOT_TYPING, sourceClientId
                    ))));
    }

    private static final class ChatClient {
        final IClient client;
        final String name;

        private ChatClient(IClient client, String name) {
            this.client = client;
            this.name = name;
        }
    }
}
