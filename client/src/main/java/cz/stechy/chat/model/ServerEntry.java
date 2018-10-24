package cz.stechy.chat.model;

import cz.stechy.chat.net.message.ServerStatusMessage.ServerStatusData;
import cz.stechy.chat.net.message.ServerStatusMessage.ServerStatusData.ServerStatus;
import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Třída reprezentující jeden záznam s informacemi o serveru
 */
public class ServerEntry {

    // region Variables

    private final UUID serverID;
    private final InetAddress serverAddress;
    private final StringProperty serverName = new SimpleStringProperty(this, "serverName", null);
    private final IntegerProperty connectedClients = new SimpleIntegerProperty(this, "connectedClients", 0);
    private final IntegerProperty maxClients = new SimpleIntegerProperty(this, "maxClients", Integer.MAX_VALUE);
    private final ObjectProperty<ServerStatus> serverStatus = new SimpleObjectProperty<>(this, "serverStatus", ServerStatus.EMPTY);
    private final BooleanProperty connected = new SimpleBooleanProperty(this, "connected", false);
    private final IntegerProperty port = new SimpleIntegerProperty(this, "port", 0);
    private final AtomicLong lastUpdate = new AtomicLong();

    // endregion

    // region Constructors

    public ServerEntry(ServerStatusData serverStatusData, InetAddress serverAddress) {
        this.serverID = serverStatusData.serverID;
        this.serverAddress = serverAddress;
        this.serverName.set(serverStatusData.serverName);
        this.connectedClients.set(serverStatusData.clientCount);
        this.maxClients.set(serverStatusData.maxClients);
        this.serverStatus.set(serverStatusData.serverStatus);
        this.port.set(serverStatusData.port);
        this.lastUpdate.set(System.currentTimeMillis());
    }

    // endregion

    // region Public methods

    /**
     * Aktualizuje data
     *
     * @param newServerStatusData {@link ServerStatusData} Nová data
     */
    public void update(ServerStatusData newServerStatusData) {
        this.serverName.set(newServerStatusData.serverName);
        this.connectedClients.set(newServerStatusData.clientCount);
        this.maxClients.set(newServerStatusData.maxClients);
        this.serverStatus.set(newServerStatusData.serverStatus);
        this.port.set(newServerStatusData.port);
        this.lastUpdate.set(System.currentTimeMillis());
    }

    /**
     * Zjistí, zda-li třída obsahuje stará (neaktualizovaná) data
     *
     * @return True, pokud se data dlouho neaktualizovala, jinak false
     */
    public boolean hasOldData() {
        final long time = System.currentTimeMillis();
        return time - lastUpdate.get() > 3000;
    }

    // endregion

    // region Getters & Setters

    public UUID getServerID() {
        return serverID;
    }

    public String getServerName() {
        return serverName.get();
    }

    public StringProperty serverNameProperty() {
        return serverName;
    }

    public int getConnectedClients() {
        return connectedClients.get();
    }

    public IntegerProperty connectedClientsProperty() {
        return connectedClients;
    }

    public int getMaxClients() {
        return maxClients.get();
    }

    public IntegerProperty maxClientsProperty() {
        return maxClients;
    }

    public ServerStatus getServerStatus() {
        return serverStatus.get();
    }

    public ObjectProperty<ServerStatus> serverStatusProperty() {
        return serverStatus;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public StringBinding clientsProperty() {
        return Bindings.createStringBinding(
            () -> String.format("%d/%d", connectedClients.get(), maxClients.get()),
            connectedClients, maxClients);
    }

    public int getPort() {
        return port.get();
    }

    public IntegerProperty portProperty() {
        return port;
    }

    public void setPort(int port) {
        this.port.set(port);
    }

    public boolean isConnected() {
        return connected.get();
    }

    public BooleanProperty connectedProperty() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected.set(connected);
    }

    // endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerEntry that = (ServerEntry) o;
        return Objects.equals(serverID, that.serverID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverID);
    }
    
}
