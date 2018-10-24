package cz.stechy.chat.service;

import cz.stechy.chat.LanServerFinder;
import cz.stechy.chat.LanServerFinder.OnServerFoundListener;
import cz.stechy.chat.ThreadPool;
import cz.stechy.chat.model.ServerEntry;
import cz.stechy.chat.net.message.ServerStatusMessage.ServerStatusData;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Třída starající se o správu serverů v lokální síti
 */
public final class LocalServerService implements OnServerFoundListener {

    private static final String BROADCAST_ADDRESS = "224.0.2.60";
    private static final int BROADCAST_PORT = 56489;

    // Mapa všech nalezených serverů
    private final ObservableMap<UUID, ServerEntry> serverMap = FXCollections.observableMap(new HashMap<>());

    private LanServerFinder serverFinder;

    public LocalServerService() {
        try {
            this.serverFinder = new LanServerFinder(InetAddress.getByName(BROADCAST_ADDRESS), BROADCAST_PORT);
            this.serverFinder.setServerFoundListener(this);
            ThreadPool.COMMON_EXECUTOR.submit(this.serverFinder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServerFound(ServerStatusData data, InetAddress address) {
        ThreadPool.JAVAFX_EXECUTOR.execute(() -> {
            final UUID serverID = data.serverID;
            if (serverMap.containsKey(serverID)) {
                serverMap.get(serverID).update(data);
            } else {
                serverMap.put(serverID, new ServerEntry(data, address));
            }
        });
    }

    public ObservableMap<UUID, ServerEntry> getServerMap() {
        return FXCollections.unmodifiableObservableMap(serverMap);
    }

    public void stop() {
        serverFinder.shutdown();
    }
}
