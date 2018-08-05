package cz.stechy.chat.core.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.stechy.chat.core.dispatcher.IClientDispatcherFactory;
import cz.stechy.chat.core.writer.IWriterThread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class ConnectionManagerFactory implements IConnectionManagerFactory {

    private final IClientDispatcherFactory clientDispatcherFactory;
    private final IWriterThread writerThread;

    @Inject
    public ConnectionManagerFactory(IClientDispatcherFactory clientDispatcherFactory,
        IWriterThread writerThread) {
        this.clientDispatcherFactory = clientDispatcherFactory;
        this.writerThread = writerThread;
    }

    @Override
    public IConnectionManager getConnectionManager(int maxClients, int waitingQueueSize) {
        final ExecutorService pool = Executors.newFixedThreadPool(maxClients);
        return new ConnectionManager(clientDispatcherFactory.getClientDispatcher(waitingQueueSize),
            writerThread, pool, maxClients);
    }
}