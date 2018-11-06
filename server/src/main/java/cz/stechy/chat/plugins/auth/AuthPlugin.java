package cz.stechy.chat.plugins.auth;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.stechy.chat.core.connection.Client;
import cz.stechy.chat.core.connection.ClientDisconnectedEvent;
import cz.stechy.chat.core.connection.IClient;
import cz.stechy.chat.core.connection.MessageReceivedEvent;
import cz.stechy.chat.core.event.IEvent;
import cz.stechy.chat.core.event.IEventBus;
import cz.stechy.chat.net.message.AuthMessage;
import cz.stechy.chat.net.message.AuthMessage.AuthMessageData;
import cz.stechy.chat.plugins.IPlugin;
import cz.stechy.chat.plugins.auth.event.LoginEvent;
import cz.stechy.chat.plugins.auth.event.LogoutEvent;
import cz.stechy.chat.plugins.auth.service.IAuthService;
import java.util.Optional;

@Singleton
public class AuthPlugin implements IPlugin {

    private static final String PLUGIN_NAME = "auth";

    private final IAuthService authService;
    private final IEventBus eventBus;

    @Inject
    public AuthPlugin(IAuthService authService, IEventBus eventBus) {
        this.authService = authService;
        this.eventBus = eventBus;
    }

    private void authMessageHandler(IEvent event) {
        assert event instanceof MessageReceivedEvent;
        final MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;
        final AuthMessage authMessage = (AuthMessage) messageReceivedEvent.getReceivedMessage();
        final AuthMessageData data = (AuthMessageData) authMessage.getData();

        switch (authMessage.getAction()) {
            case LOGIN:
                final IClient client = messageReceivedEvent.getClient();
                final Optional<User> optionalUser = authService.login(data.name, client);
                final boolean success = optionalUser.isPresent();

                client.sendMessageAsync(authMessage.getResponce(success, success ? optionalUser.get().id : null));
                if (success) {
                    eventBus.publishEvent(new LoginEvent(client, optionalUser.get()));
                }
                break;
            case LOGOUT:
                authService.logout(data.id).ifPresent(user -> eventBus.publishEvent(new LogoutEvent(user)));
                break;
            default:
                throw new RuntimeException("Neplatný parametr");
        }
    }

    private void clientDisconnectedHandler(IEvent event) {
        final ClientDisconnectedEvent disconnectedEvent = (ClientDisconnectedEvent) event;
        final Client disconnectedClient = disconnectedEvent.getClient();
        authService.logout(disconnectedClient).ifPresent(user -> eventBus.publishEvent(new LogoutEvent(user)));
    }

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public void init() {
        System.out.println("Inicializace pluginu: " + getName());
    }

    @Override
    public void registerMessageHandlers(IEventBus eventBus) {
        eventBus.registerEventHandler(AuthMessage.MESSAGE_TYPE, this::authMessageHandler);
        eventBus.registerEventHandler(ClientDisconnectedEvent.EVENT_TYPE, this::clientDisconnectedHandler);
    }
}
