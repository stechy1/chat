package cz.stechy.chat;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import cz.stechy.chat.cmd.IParameterFactory;
import cz.stechy.chat.cmd.IParameterProvider;
import cz.stechy.chat.core.event.IEventProcessor;
import cz.stechy.chat.core.server.IServerThread;
import cz.stechy.chat.core.server.IServerThreadFactory;
import cz.stechy.chat.plugins.IPlugin;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final Scanner scanner = new Scanner(System.in);
    private final IParameterFactory parameterFactory;
    private final IServerThreadFactory serverThreadFactory;
    private final IEventProcessor messageRegistrator;
    private final Map<String, IPlugin> plugins;

    @Inject
    public Server(IParameterFactory parameterFactory, IServerThreadFactory serverThreadFactory,
        IEventProcessor messageRegistrator, Map<String, IPlugin> plugins) {
        this.parameterFactory = parameterFactory;
        this.serverThreadFactory = serverThreadFactory;
        this.messageRegistrator = messageRegistrator;
        this.plugins = plugins;
    }

    private void run(String[] args) throws IOException {
        final IParameterProvider parameters = parameterFactory.getParameters(args);
        final IServerThread serverThread = serverThreadFactory.getServerThread(parameters);

        LOGGER.info("Spouštím server...");
        initPlugins();

        // Spuštění vlákna serveru
        serverThread.start();

        while(true) {
            final String input = scanner.nextLine();
            if ("exit".equals(input)) {
                break;
            }
        }

        LOGGER.info("Ukončuji server.");
        serverThread.shutdown();
        // Počkání na ukončení
        try {
            serverThread.join();
        } catch (InterruptedException ignored) {}

        LOGGER.info("Server byl ukončen.");
    }

    private void initPlugins() {
        LOGGER.info("Inicializuji pluginy.");

        for (IPlugin plugin : plugins.values()) {
            plugin.init();
        }

        for (IPlugin plugin : plugins.values()) {
            plugin.registerMessageHandlers(messageRegistrator);
        }

        for (IPlugin plugin : plugins.values()) {
            plugin.setupDependencies(plugins);
        }

        LOGGER.info("Inicializace pluginů dokončena.");
    }

    public static void main(String[] args) throws Exception {
        final Injector injector = Guice.createInjector(new ServerModule(), new PluginModule());
        final Server server = injector.getInstance(Server.class);
        server.run(args);
    }

}
