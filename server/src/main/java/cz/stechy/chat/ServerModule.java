package cz.stechy.chat;

import com.google.inject.AbstractModule;
import cz.stechy.chat.cmd.IParameterFactory;
import cz.stechy.chat.cmd.ParameterFactory;
import cz.stechy.chat.core.connection.ConnectionManagerFactory;
import cz.stechy.chat.core.connection.IConnectionManagerFactory;
import cz.stechy.chat.core.server.IServerThreadFactory;
import cz.stechy.chat.core.server.ServerThreadFactory;

public class ServerModule extends AbstractModule {

    @Override
    public void configure() {
        bind(IParameterFactory.class).to(ParameterFactory.class);
        bind(IServerThreadFactory.class).to(ServerThreadFactory.class);
        bind(IConnectionManagerFactory.class).to(ConnectionManagerFactory.class);
    }
}
