package cz.stechy.chat;

import cz.stechy.chat.net.message.ServerStatusMessage;
import cz.stechy.chat.net.message.ServerStatusMessage.ServerStatusData;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

/**
 * Třída starající se o nalezení dostupných serverů v lokální síti
 */
public class LanServerFinder implements Runnable {

    // region Variables

    // Socket, na kterém se naslouchá
    private final MulticastSocket socket;

    // Listener pro informování o nalezení serveru
    private OnServerFoundListener serverFoundListener;

    private boolean interrupt = false;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou instanci třídy {@link LanServerFinder}
     *
     * @param broadcastAddress Broadcastová adresa, na které se posílají datagramy
     * @param port Port, na kterém se posílají datagramy
     * @throws IOException Pokud se nepodaří vytvořit socket
     */
    public LanServerFinder(InetAddress broadcastAddress, int port) throws IOException {
        // Broadcast adresa
        this.socket = new MulticastSocket(port);
        this.socket.setSoTimeout(5000);
        this.socket.joinGroup(broadcastAddress);
    }

    // endregion

    // region Public methods

    /**
     * Ukončí činnost vyhledávání serverů
     */
    public void shutdown() {
        interrupt = true;
    }

    // endregion

    // region Getters & Setters

    public OnServerFoundListener getServerFoundListener() {
        return serverFoundListener;
    }

    public void setServerFoundListener(OnServerFoundListener serverFoundListener) {
        this.serverFoundListener = serverFoundListener;
    }

    // endregion

    @Override
    public void run() {
        final byte[] data = new byte[1024];
        final DatagramPacket datagramPacket = new DatagramPacket(data, data.length);

        while(!interrupt) {
            try {
                socket.receive(datagramPacket);
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                break;
            }

            final ByteArrayInputStream bais = new ByteArrayInputStream(
                datagramPacket.getData(),
                datagramPacket.getOffset(),
                datagramPacket.getLength());
            try {
                final ObjectInputStream ois = new ObjectInputStream(bais);
                final ServerStatusMessage statusMessage = (ServerStatusMessage) ois.readObject();
                final ServerStatusData statusData = (ServerStatusData) statusMessage.getData();
                if (serverFoundListener != null) {
                    serverFoundListener.onServerFound(statusData);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Rozhraní pro obsluhu události nalezení serveru
     */
    @FunctionalInterface
    public interface OnServerFoundListener {

        /**
         * Metoda se zavolá vždy, když přijde packet ze serveru
         *
          * @param data {@link ServerStatusData} Data s informacemi o serveru
         */
        void onServerFound(ServerStatusData data);
    }
}