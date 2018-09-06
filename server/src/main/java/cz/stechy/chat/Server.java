package cz.stechy.chat;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import cz.stechy.chat.cmd.CmdParser;
import cz.stechy.chat.cmd.IParameterFactory;
import cz.stechy.chat.cmd.IParameterProvider;

public class Server {

    private final IParameterFactory parameterFactory;

    @Inject
    public Server(IParameterFactory parameterFactory) {
        this.parameterFactory = parameterFactory;
    }

    private void run(String[]args) {
        final IParameterProvider parameters = parameterFactory.getParameters(args);
        System.out.println("Maximalni pocet klientu: " + parameters.getInteger(CmdParser.CLIENTS));
    }

    public static void main(String[] args) throws Exception {
        final Injector injector = Guice.createInjector(new ServerModule());
        Server server = injector.getInstance(Server.class);
        server.run(args);
    }

}
