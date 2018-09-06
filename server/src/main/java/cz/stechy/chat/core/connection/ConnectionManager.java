package cz.stechy.chat.core.connection;

import com.google.inject.Inject;
import cz.stechy.chat.core.dispatcher.IClientDispatcher;
import cz.stechy.chat.core.writer.IWriterThread;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

class ConnectionManager implements IConnectionManager {

    // Kolekce klientů, se kterými server aktivně komunikuje
    private final List<IClient> clients = new ArrayList<>();

    private final IClientDispatcher clientDispatcher;
    // Zapisovací vlákno
    private final IWriterThread writerThread;
    // Threadpool s vlákny pro jednotlivé klienty
    private final ExecutorService pool;
    // Maximální počet aktívně komunikujících klientů
    final int maxClients;

    @Inject
    public ConnectionManager(IClientDispatcher clientDispatcher, IWriterThread writerThread,
        ExecutorService pool, int maxClients) {
        this.clientDispatcher = clientDispatcher;
        this.writerThread = writerThread;
        this.pool = pool;
        this.maxClients = maxClients;
    }

    private synchronized void insertClientToListOrQueue(Client client) {
        if (clients.size() < maxClients) {
            clients.add(client);
            client.setConnectionClosedListener(() -> {
                clients.remove(client);

                System.out.println("Počet připojených klientů: " + clients.size());

                if (clientDispatcher.hasClientInQueue()) {
                    System.out.println("V čekací listině se našel klient, který by rád komunikoval.");
                    this.insertClientToListOrQueue(clientDispatcher.getClientFromQueue());
                }
            });
            pool.submit(client);
        } else {
            if (clientDispatcher.addClientToQueue(client)) {
                System.out.println("Přidávám klienta na čekací listinu.");
            } else {
                System.out.println("Odpojuji klienta od serveru. Je připojeno příliš mnoho uživatelů.");
                client.close();
            }
        }
    }

    @Override
    public void addClient(Socket socket) throws IOException {
        insertClientToListOrQueue(new Client(socket, writerThread));
    }

    @Override
    public void onServerStart() {
        clientDispatcher.start();
        writerThread.start();
    }

    @Override
    public void onServerStop() {
        System.out.println("Odpojuji připojené klienty.");
        for (IClient client : clients) {
            client.close();
        }
        System.out.println("Ukončuji činnost thread poolu.");
        pool.shutdown();

        System.out.println("Ukončuji client dispatcher.");
        clientDispatcher.shutdown();

        LOGGER.info("Ukončuji writer thread.");
        writerThread.shutdown();
    }
}