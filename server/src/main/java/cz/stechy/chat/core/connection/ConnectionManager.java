package cz.stechy.chat.core.connection;

import com.google.inject.Inject;
import cz.stechy.chat.core.dispatcher.IClientDispatcher;
import cz.stechy.chat.core.event.IEventBus;
import cz.stechy.chat.core.writer.IWriterThread;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConnectionManager implements IConnectionManager {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

    // Kolekce klientů, se kterými server aktivně komunikuje
    private final List<IClient> clients = new ArrayList<>();

    private final IClientDispatcher clientDispatcher;
    // Zapisovací vlákno
    private final IWriterThread writerThread;
    // Event bus
    private final IEventBus eventBus;
    // Threadpool s vlákny pro jednotlivé klienty
    private final ExecutorService pool;
    // Maximální počet aktívně komunikujících klientů
    final int maxClients;

    @Inject
    public ConnectionManager(IClientDispatcher clientDispatcher, IWriterThread writerThread,
        IEventBus eventBus, ExecutorService pool, int maxClients) {
        this.clientDispatcher = clientDispatcher;
        this.writerThread = writerThread;
        this.eventBus = eventBus;
        this.pool = pool;
        this.maxClients = maxClients;
    }

    private synchronized void insertClientToListOrQueue(Client client) {
        if (clients.size() < maxClients) {
            clients.add(client);
            client.setConnectionClosedListener(() -> {
                clients.remove(client);
                eventBus.publishEvent(new ClientDisconnectedEvent(client));
                LOGGER.info("Počet připojených klientů: {}.", clients.size());
                if (clientDispatcher.hasClientInQueue()) {
                    LOGGER.info("V čekací listině se našel klient, který by rád komunikoval.");
                    this.insertClientToListOrQueue(clientDispatcher.getClientFromQueue());
                }
            });
            pool.submit(client);
            eventBus.publishEvent(new ClientConnectedEvent(client));
        } else {
            if (clientDispatcher.addClientToQueue(client)) {
                LOGGER.info("Přidávám klienta na čekací listinu.");
            } else {
                LOGGER.warn("Odpojuji klienta od serveru. Je připojeno příliš mnoho uživatelů.");
                client.close();
            }
        }
    }

    @Override
    public int getConnectedClientCount() {
        return clients.size();
    }

    @Override
    public int getMaxClients() {
        return maxClients;
    }

    @Override
    public void addClient(Socket socket) throws IOException {
        insertClientToListOrQueue(new Client(socket, writerThread, eventBus));
    }

    @Override
    public void onServerStart() {
        clientDispatcher.start();
        writerThread.start();
    }

    @Override
    public void onServerStop() {
        LOGGER.info("Odpojuji připojené klienty.");
        for (IClient client : clients) {
            client.close();
        }
        LOGGER.info("Ukončuji činnost thread poolu.");
        pool.shutdown();

        LOGGER.info("Ukončuji client dispatcher.");
        clientDispatcher.shutdown();

        LOGGER.info("Ukončuji writer thread.");
        writerThread.shutdown();
    }
}