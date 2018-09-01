package cz.stechy.chat.core.server;

import com.google.inject.Inject;
import cz.stechy.chat.core.connection.IConnectionManager;
import cz.stechy.chat.core.multicaster.IMulticastSender;
import cz.stechy.chat.core.multicaster.IMulticastSenderFactory;
import cz.stechy.chat.net.message.IMessage;
import cz.stechy.chat.net.message.ServerStatusMessage;
import cz.stechy.chat.net.message.ServerStatusMessage.ServerStatusData;
import cz.stechy.chat.net.message.ServerStatusMessage.ServerStatusData.ServerStatus;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vlákno serveru
 */
class ServerThread extends Thread implements IServerThread {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);

    // Unikátní ID serveru
    private static final UUID ID = UUID.randomUUID();
    // Timeout socketu
    private static final int SOCKET_TIMEOUT = 5000;

    // endregion

    // region Variables

    // Správce jednotlivých spojení s klienty
    private final IConnectionManager connectionManager;
    // Odesílač informací o serveru
    private final IMulticastSender multicastSender;
    // Název serveru
    private final String serverName;
    // Číslo portu
    private final int port;

    // Indikátor, zda-li vlákno běží, nebo ne
    private boolean running = false;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou instanci vlákna serveru
     * @param connectionManager {@link IConnectionManager}
     * @param multicastSenderFactory {@link IMulticastSenderFactory}
     * @param serverName Název serveru
     * @param port Číslo portu
     */
    @Inject
    ServerThread(IConnectionManager connectionManager,
        IMulticastSenderFactory multicastSenderFactory, String serverName, int port) {
        super("ServerThread");
        this.connectionManager = connectionManager;
        this.multicastSender = multicastSenderFactory.getMulticastSender(this);
        this.serverName = serverName;
        this.port = port;
    }

    // endregion

    @Override
    public IMessage getServerStatusMessage() {
        final int connectedClients = connectionManager.getConnectedClientCount();
        final int maxClients = connectionManager.getMaxClients();
        final int delta = maxClients - connectedClients;
        ServerStatus status = ServerStatus.EMPTY;
        if (delta == 0) {
            status = ServerStatus.FULL;
        } else if (delta > 0 && delta < maxClients) {
            status = ServerStatus.HAVE_SPACE;
        }

        return new ServerStatusMessage(new ServerStatusData(
            ID, status, connectedClients, maxClients, serverName, port));
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
        multicastSender.start();
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
        multicastSender.shutdown();
        connectionManager.onServerStop();
    }
}