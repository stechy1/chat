package cz.stechy.chat;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import cz.stechy.chat.cmd.IParameterFactory;
import cz.stechy.chat.cmd.IParameterProvider;
import cz.stechy.chat.core.event.IEventBus;
import cz.stechy.chat.core.server.IServerThread;
import cz.stechy.chat.core.server.IServerThreadFactory;
import cz.stechy.chat.plugins.IPlugin;
import cz.stechy.chat.plugins.PriorityPluginComparator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Server {

    private final Scanner scanner = new Scanner(System.in);
    private final IParameterFactory parameterFactory;
    private final IServerThreadFactory serverThreadFactory;
    private final IEventBus eventBus;
    private final Map<String, IPlugin> plugins;

    @Inject
    public Server(IParameterFactory parameterFactory, IServerThreadFactory serverThreadFactory,
        IEventBus eventBus, Map<String, IPlugin> plugins) {
        this.parameterFactory = parameterFactory;
        this.serverThreadFactory = serverThreadFactory;
        this.eventBus = eventBus;
        this.plugins = plugins;
    }

    private void run(String[]args) throws IOException {
        final IParameterProvider parameters = parameterFactory.getParameters(args);
        final IServerThread serverThread = serverThreadFactory.getServerThread(parameters);

        System.out.println("Spouštím vlákno serveru.");

        initPlugins();
        serverThread.start();

        while(true) {
            final String input = scanner.nextLine();
            if ("exit".equals(input)) {
                break;
            }
        }

        System.out.println("Ukončuji server.");
        serverThread.shutdown();

        System.out.println("Server byl ukončen.");
    }

    /**
     * Vrátí seřazenou kolekci pluginů podle jejich priority
     *
     * @return Seřazenou kolekci pluginů
     */
    private List<IPlugin> getSortedPlugins() {
        final List<IPlugin> pluginList = new ArrayList<>(plugins.values());
        pluginList.sort(new PriorityPluginComparator());
        Collections.reverse(pluginList);

        return pluginList;
    }

    private void initPlugins() {
        System.out.println("Inicializuji pluginy.");
        final List<IPlugin> pluginList = getSortedPlugins();

        for (IPlugin plugin : pluginList) {
            plugin.init();
        }

        for (IPlugin plugin : pluginList) {
            plugin.registerMessageHandlers(eventBus);
        }

        for (IPlugin plugin : pluginList) {
            plugin.setupDependencies(plugins);
        }

        System.out.println("Inicializace pluginů dokončena.");
    }


    public static void main(String[] args) throws Exception {
        final Injector injector = Guice.createInjector(new ServerModule(), new PluginModule("./plugins"));
        Server server = injector.getInstance(Server.class);
        server.run(args);
    }

}
