package cz.stechy.chat;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import cz.stechy.chat.cmd.CmdParser;
import cz.stechy.chat.cmd.IParameterFactory;
import cz.stechy.chat.cmd.IParameterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final IParameterFactory parameterFactory;

    @Inject
    public Server(IParameterFactory parameterFactory) {
        this.parameterFactory = parameterFactory;
    }

    private void run(String[]args) {
        final IParameterProvider parameters = parameterFactory.getParameters(args);
        LOGGER.info("Maximalni pocet klientu: {}", parameters.getInteger(CmdParser.CLIENTS));
    }

    public static void main(String[] args) throws Exception {
        final Injector injector = Guice.createInjector(new ServerModule());
        Server server = injector.getInstance(Server.class);
        server.run(args);
    }

}
