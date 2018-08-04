package cz.stechy.chat;

import com.google.inject.AbstractModule;
import cz.stechy.chat.cmd.IParameterFactory;
import cz.stechy.chat.cmd.ParameterFactory;
import cz.stechy.chat.core.connection.ConnectionManagerFactory;
import cz.stechy.chat.core.connection.IConnectionManagerFactory;
import cz.stechy.chat.core.dispatcher.ClientDispatcherFactory;
import cz.stechy.chat.core.dispatcher.IClientDispatcherFactory;
import cz.stechy.chat.core.event.EventProcessor;
import cz.stechy.chat.core.event.IEventProcessor;
import cz.stechy.chat.core.server.IServerThreadFactory;
import cz.stechy.chat.core.server.ServerThreadFactory;
import cz.stechy.chat.core.writer.IWriterThread;
import cz.stechy.chat.core.writer.WriterThread;

public class ServerModule extends AbstractModule {

    @Override
    public void configure() {
        bind(IParameterFactory.class).to(ParameterFactory.class);
        bind(IServerThreadFactory.class).to(ServerThreadFactory.class);
        bind(IWriterThread.class).to(WriterThread.class);
        bind(IClientDispatcherFactory.class).to(ClientDispatcherFactory.class);
        bind(IEventProcessor.class).to(EventProcessor.class);
        bind(IConnectionManagerFactory.class).to(ConnectionManagerFactory.class);
    }
}
