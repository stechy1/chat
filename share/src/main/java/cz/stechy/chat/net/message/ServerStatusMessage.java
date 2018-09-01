package cz.stechy.chat.net.message;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Zpráva obsahující informace o stavu serveru
 */
public class ServerStatusMessage implements IMessage {

    private static final long serialVersionUID = -1429760060957272567L;

    public static final String MESSAGE_TYPE = "server-status";

    // Data s informacemi o serveru
    private final ServerStatusData statusData;

    /**
     * Vytvoří novou zprávu s informacemi o serveru
     *
     * @param statusData {@link ServerStatusData} Data s informacemi o serveru
     */
    public ServerStatusMessage(ServerStatusData statusData) {
        this.statusData = statusData;
    }

    @Override
    public String getType() {
        return MESSAGE_TYPE;
    }

    @Override
    public Object getData() {
        return statusData;
    }

    @Override
    public String toString() {
        return String.valueOf(getData());
    }

    public static final class ServerStatusData implements Serializable {

        private static final long serialVersionUID = -4288671744361722044L;

        public enum ServerStatus {
            EMPTY, HAVE_SPACE, FULL
        }

        public final UUID serverID;
        public final ServerStatus serverStatus;
        public final int clientCount;
        public final int maxClients;
        public final String serverName;
        public final int port;

        /**
         * Vytvoří novou instanci reprezentující informace o stavu serveru
         *
         * @param serverID ID serveru
         * @param serverStatus {@link ServerStatus} stav serveru
         * @param clientCount Počet aktuálně připojených klientů
         * @param maxClients Počet maximálně připojených klientů
         * @param serverName Název serveru
         * @param port Port, na kterém server naslouchá
         */
        public ServerStatusData(UUID serverID, ServerStatus serverStatus, int clientCount,
            int maxClients, String serverName, int port) {
            this.serverID = serverID;
            this.serverStatus = serverStatus;
            this.clientCount = clientCount;
            this.maxClients = maxClients;
            this.serverName = serverName;
            this.port = port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ServerStatusData that = (ServerStatusData) o;
            return clientCount == that.clientCount &&
                maxClients == that.maxClients &&
                port == that.port &&
                Objects.equals(serverID, that.serverID) &&
                serverStatus == that.serverStatus &&
                Objects.equals(serverName, that.serverName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serverID, serverStatus, clientCount, maxClients, serverName, port);
        }

        @Override
        public String toString() {
            return String.format("%s: %d/%d - %s; port=%d", serverName, clientCount, maxClients, serverStatus, port);
        }
    }
}