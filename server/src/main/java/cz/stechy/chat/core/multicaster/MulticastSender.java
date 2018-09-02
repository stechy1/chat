package cz.stechy.chat.core.multicaster;

import cz.stechy.chat.cmd.CmdParser;
import cz.stechy.chat.cmd.IParameterFactory;
import cz.stechy.chat.cmd.IParameterProvider;
import cz.stechy.chat.core.ServerInfoProvider;
import cz.stechy.chat.net.message.IMessage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MulticastSender extends Thread implements IMulticastSender {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(MulticastSender.class);

    // Interval mezi jednotlivými multicastovými packety
    private static final long SLEEP_TIME = 2000L;
    // Výchozí multicastová adresa
    private static final String DEFAULT_MULTICAST_ADDRESS = "224.0.2.60";
    // Výchozí multicastový port
    private static final int DEFAULT_MULTICAST_PORT = 56489;

    // endregion

    // region Variables

    // Továrna na parametry serveru
    private final IParameterFactory parameterFactory;
    // Poskytovatel informací o serveru
    private final ServerInfoProvider serverInfoProvider;

    // Socket, na kterém se posílají zprávy
    private DatagramSocket socket;
    // Broadcast adresa
    private InetAddress broadcastAddress;
    // Broadcast port
    private int port;
    // Poskytovatel informací o serveru
    private boolean interrupt = false;

    // endregion

    // region Constructors

    MulticastSender(IParameterFactory parameterFactory, ServerInfoProvider serverInfoProvider) {
        super("MulticastSender");
        this.parameterFactory = parameterFactory;
        this.serverInfoProvider = serverInfoProvider;
    }

    // endregion

    // region Private methods

    /**
     * Inicializuje broadcast adresu, socket a port
     */
    private void init() {
        final IParameterProvider parameterProvider = parameterFactory.getParameters();
        try {
            this.broadcastAddress = InetAddress.getByName(parameterProvider
                .getString(CmdParser.MULTICAST_ADDRESS, DEFAULT_MULTICAST_ADDRESS));
            this.socket = new DatagramSocket();
            this.port = parameterProvider.getInteger(CmdParser.MULTICAST_PORT, DEFAULT_MULTICAST_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // endregion
    @Override
    public synchronized void start() {
        init();
        super.start();
    }

    @Override
    public void run() {
        if (socket == null || broadcastAddress == null) {
            interrupt = false;
        }

        while(!interrupt) {
            try {
                final IMessage serverStatusMessage = serverInfoProvider
                    .getServerStatusMessage();
                final byte[] data = serverStatusMessage.toByteArray();
                final DatagramPacket datagramPacket = new DatagramPacket(
                    data, data.length, broadcastAddress, port);
                LOGGER.debug("Odesílám datagram se zprávou: " + serverStatusMessage.toString());
                this.socket.send(datagramPacket);
            } catch (IOException e) {
                LOGGER.error("Nezdařilo se poslat multicast datagram.", e);
                break;
            }

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ignored) {}
        }
    }

    @Override
    public void shutdown() {
        interrupt = true;
        try {
            join();
        } catch (InterruptedException ignored) { }
    }
}