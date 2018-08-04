package cz.stechy.chat.plugins.hello;

import com.google.inject.Inject;
import cz.stechy.chat.core.Client;
import cz.stechy.chat.core.Client.MessageReceivedEvent;
import cz.stechy.chat.core.event.Event;
import cz.stechy.chat.core.event.IEventRegistrator;
import cz.stechy.chat.core.connection.ClientConnectedEvent;
import cz.stechy.chat.core.connection.ClientDisconnectedEvent;
import cz.stechy.chat.net.message.HelloMessage;
import cz.stechy.chat.plugins.IPlugin;
import cz.stechy.chat.plugins.hello.service.IHelloService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloPlugin implements IPlugin {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloPlugin.class);

    private final IHelloService helloService;

    @Inject
    public HelloPlugin(IHelloService helloService) {
        this.helloService = helloService;
    }

    private void helloMessageHandler(Event event) {
        MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;
        Client client = messageReceivedEvent.getClient();
        client.sendMessageAsync(helloService.sayHello());
    }

    private void clientConnectedHandler(Event event) {
        ClientConnectedEvent clientConnectedEvent = (ClientConnectedEvent) event;
        final Client client = clientConnectedEvent.getClient();
        helloService.onClientConnected(client);
    }

    private void clientDisconnectedHandler(Event event) {
        ClientDisconnectedEvent clientDisconnectedEvent = (ClientDisconnectedEvent) event;
        final Client client = clientDisconnectedEvent.getClient();
        helloService.onClientDisconnected(client);
    }

    @Override
    public String getName() {
        return "HelloPlugin";
    }

    @Override
    public void init() {
        LOGGER.info("Inicializace pluginu: {}", getName());
    }

    @Override
    public void registerMessageHandlers(IEventRegistrator registrator) {
        registrator.registerEventHandler(HelloMessage.MESSAGE_TYPE, this::helloMessageHandler);
        registrator.registerEventHandler(ClientConnectedEvent.EVENT_TYPE, this::clientConnectedHandler);
        registrator.registerEventHandler(ClientDisconnectedEvent.EVENT_TYPE, this::clientDisconnectedHandler);
    }


    @Override
    public void setupDependencies(Map<String, IPlugin> otherPlugins) {

    }
}
