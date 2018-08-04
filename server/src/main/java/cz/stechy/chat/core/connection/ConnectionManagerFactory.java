package cz.stechy.chat.core.connection;

import com.google.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class ConnectionManagerFactory implements IConnectionManagerFactory {

    @Override
    public IConnectionManager getConnectionManager(int maxClients, int waitingQueueSize) {
        final ExecutorService pool = Executors.newFixedThreadPool(maxClients);
        return new ConnectionManager(pool, maxClients);
    }
}