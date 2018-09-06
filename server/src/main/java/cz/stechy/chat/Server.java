package cz.stechy.chat;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import cz.stechy.chat.cmd.IParameterFactory;
import cz.stechy.chat.cmd.IParameterProvider;
import cz.stechy.chat.core.server.IServerThread;
import cz.stechy.chat.core.server.IServerThreadFactory;
import java.io.IOException;
import java.util.Scanner;

public class Server {

    private final Scanner scanner = new Scanner(System.in);
    private final IParameterFactory parameterFactory;
    private final IServerThreadFactory serverThreadFactory;

    @Inject
    public Server(IParameterFactory parameterFactory, IServerThreadFactory serverThreadFactory) {
        this.parameterFactory = parameterFactory;
        this.serverThreadFactory = serverThreadFactory;
    }

    private void run(String[]args) throws IOException {
        final IParameterProvider parameters = parameterFactory.getParameters(args);
        final IServerThread serverThread = serverThreadFactory.getServerThread(parameters);

        System.out.println("Spouštím vlákno serveru.");
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


    public static void main(String[] args) throws Exception {
        final Injector injector = Guice.createInjector(new ServerModule());
        Server server = injector.getInstance(Server.class);
        server.run(args);
    }

}
