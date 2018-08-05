package cz.stechy.chat.core.connection;

import com.google.inject.Inject;
import cz.stechy.chat.core.dispatcher.IClientDispatcher;
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
    // Threadpool s vlákny pro jednotlivé klienty
    private final ExecutorService pool;
    // Maximální počet aktívně komunikujících klientů
    final int maxClients;

    @Inject
    public ConnectionManager(IClientDispatcher clientDispatcher, ExecutorService pool,
        int maxClients) {
        this.clientDispatcher = clientDispatcher;
        this.pool = pool;
        this.maxClients = maxClients;
    }

    private synchronized void insertClientToListOrQueue(Client client) {
        if (clients.size() < maxClients) {
            clients.add(client);
            client.setConnectionClosedListener(() -> {
                clients.remove(client);
                LOGGER.info("Počet připojených klientů: {}.", clients.size());
                if (clientDispatcher.hasClientInQueue()) {
                    LOGGER.info("V čekací listině se našel klient, který by rád komunikoval.");
                    this.insertClientToListOrQueue(clientDispatcher.getClientFromQueue());
                }
            });
            pool.submit(client);
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
    public void addClient(Socket socket) throws IOException {
        insertClientToListOrQueue(new Client(socket));
    }

    @Override
    public void onServerStart() {
        clientDispatcher.start();
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
    }
}