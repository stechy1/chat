package cz.stechy.chat.core.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Vlákno serveru
 */
class ServerThread extends Thread implements IServerThread {

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
            System.out.println(String.format("Server naslouchá na portu: %d.", serverSocket.getLocalPort()));
            // Nové vlákno serveru
            while (running) {
                try {
                    final Socket socket = serverSocket.accept();
                    System.out.println("Server přijal nové spojení.");

                    connectionManager.addClient(socket);
                } catch (SocketTimeoutException ignored) {
                }
            }

        } catch (IOException e) {
            System.out.println("Chyba v server socketu.");
            e.printStackTrace();
        }

        System.out.println("Ukončuji server.");
        connectionManager.onServerStop();
    }
}