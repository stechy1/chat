package cz.stechy.chat.core.connection;

import com.google.inject.Inject;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

class ConnectionManager implements IConnectionManager {

    // Kolekce klientů, se kterými server aktivně komunikuje
    private final List<IClient> clients = new ArrayList<>();

    // Threadpool s vlákny pro jednotlivé klienty
    private final ExecutorService pool;
    // Maximální počet aktívně komunikujících klientů
    final int maxClients;

    @Inject
    public ConnectionManager(ExecutorService pool,int maxClients) {
        this.pool = pool;
        this.maxClients = maxClients;
    }

    private synchronized void insertClientToListOrQueue(Client client) {
        if (clients.size() < maxClients) {
            clients.add(client);
            client.setConnectionClosedListener(() -> {
                clients.remove(client);

                System.out.println("Počet připojených klientů: " + clients.size());

            });
            pool.submit(client);
        } else {
            // TODO vložit klienta do čekací fronty
        }
    }

    @Override
    public void addClient(Socket socket) throws IOException {
        insertClientToListOrQueue(new Client(socket));
    }

    @Override
    public void onServerStart() {

    }

    @Override
    public void onServerStop() {
        System.out.println("Odpojuji připojené klienty.");
        for (IClient client : clients) {
            client.close();
        }
        System.out.println("Ukončuji činnost thread poolu.");
        pool.shutdown();
    }
}