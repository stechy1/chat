package cz.stechy.chat.core.server;

import com.google.inject.Inject;
import cz.stechy.chat.core.connection.IConnectionManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vlákno serveru
 */
class ServerThread extends Thread implements IServerThread {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);

    private static final int SOCKET_TIMEOUT = 5000;

    private final IConnectionManager connectionManager;
    // Číslo portu
    private final int port;

    // Indikátor, zda-li vlákno běží, nebo ne
    private boolean running = false;

    /**
     * Vytvoří novou instanci vlákna serveru
     *
     * @param connectionManager {@link IConnectionManager}
     * @param port Číslo portu
     */
    @Inject
    ServerThread(IConnectionManager connectionManager, int port) {
        super("ServerThread");
        this.connectionManager = connectionManager;
        this.port = port;
    }

    @Override
    public void shutdown() {
        running = false;
        try {
            join();
        } catch (InterruptedException ignored) {}
    }

    @Override
    public synchronized void start() {
        running = true;
        super.start();
    }

    @Override
    public void run() {
        connectionManager.onServerStart();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Každých 5 vteřin dojde k vyjímce SocketTimeoutException
            // To proto, že metoda serverSocket.accept() je blokující
            // a my bychom neměli šanci činnost vlákna ukončit
            serverSocket.setSoTimeout(SOCKET_TIMEOUT);
            LOGGER
                .info(String.format("Server naslouchá na portu: %d.", serverSocket.getLocalPort()));
            // Nové vlákno serveru
            while (running) {
                try {
                    final Socket socket = serverSocket.accept();
                    LOGGER.info("Server přijal nové spojení.");

                    connectionManager.addClient(socket);
                } catch (SocketTimeoutException ignored) {
                }
            }

        } catch (IOException e) {
            LOGGER.error("Chyba v server socketu.", e);
        }

        LOGGER.info("Ukončuji server.");
        connectionManager.onServerStop();
    }
}