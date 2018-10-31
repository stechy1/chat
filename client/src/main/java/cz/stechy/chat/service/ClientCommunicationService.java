package cz.stechy.chat.service;

import cz.stechy.chat.ThreadPool;
import cz.stechy.chat.net.ConnectionState;
import cz.stechy.chat.net.OnDataReceivedListener;
import cz.stechy.chat.net.ReaderThread;
import cz.stechy.chat.net.WriterThread;
import cz.stechy.chat.net.message.IMessage;
import cz.stechy.chat.net.message.ServerStatusMessage;
import cz.stechy.chat.net.message.ServerStatusMessage.ServerStatusData;
import cz.stechy.chat.net.message.ServerStatusMessage.ServerStatusData.ServerStatus;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class ClientCommunicationService implements IClientCommunicationService {

    private final ObjectProperty<Socket> socket = new SimpleObjectProperty<>(this, "socket", null);
    private final ReadOnlyObjectWrapper<ConnectionState> connectionState = new ReadOnlyObjectWrapper<>(this, "connectionState", ConnectionState.DISCONNECTED);
    private final HashMap<String, List<OnDataReceivedListener>> listeners = new HashMap<>();
    private final StringProperty host = new SimpleStringProperty(this, "host", null);
    private final IntegerProperty port = new SimpleIntegerProperty(this, "port", -1);
    private final StringProperty connectedServerName = new SimpleStringProperty(this, "connectedServerName", null);
    private final ObjectProperty<ServerStatus> serverStatus = new SimpleObjectProperty<>(this, "serverStatus", ServerStatus.EMPTY);
    private final Queue<Request> requests = new LinkedBlockingQueue<>();

    private ReaderThread readerThread;
    private WriterThread writerThread;

    public ClientCommunicationService() {
        socket.addListener(this::socketListener);
        connectedServerName.bind(Bindings.createStringBinding(() -> String.format("%s:%d", host.get(), port.get()), host, port, connectionState));
    }

    private void socketListener(ObservableValue<? extends Socket> observableValue, Socket oldSocket, Socket newSocket) {
        if (newSocket == null) {
            readerThread = null;
            writerThread = null;
            unregisterMessageObserver(ServerStatusMessage.MESSAGE_TYPE, this.serverStatusListener);
            return;
        }

        try {
            readerThread = new ReaderThread(newSocket.getInputStream(), listener, this::disconnect);
            writerThread = new WriterThread(newSocket.getOutputStream(), this::disconnect);

            readerThread.start();
            writerThread.start();
            registerMessageObserver(ServerStatusMessage.MESSAGE_TYPE, this.serverStatusListener);
        } catch (IOException e) {
            System.out.println("Vyskytl se problém při vytváření komunikace se serverem.");
        }
    }

    private final OnDataReceivedListener listener = message -> {
        if (message.isResponce()) {
            final Request poll = requests.poll();
            if (poll != null) {
                poll.onResponce(message);
            }
            return;
        }

        final List<OnDataReceivedListener> listenerList = listeners.get(message.getType());
        if (listenerList == null) {
            return;
        }

        for (OnDataReceivedListener listener : listenerList) {
            listener.onDataReceived(message);
        }
    };

    private void changeState(ConnectionState state) {
        connectionState.set(state);
    }

    private final OnDataReceivedListener serverStatusListener = message -> {
        final ServerStatusMessage statusMessage = (ServerStatusMessage) message;
        final ServerStatusData status = (ServerStatusData) statusMessage.getData();
        serverStatus.set(status.serverStatus);
    };

    private boolean isConnected() {
        return connectionState.get() == ConnectionState.CONNECTED;
    }

    @Override
    public CompletableFuture<Boolean> connect(String host, int port) {
        if (isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        changeState(ConnectionState.CONNECTING);

        return CompletableFuture.supplyAsync(() -> {
            final Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(host, port), 3000);
                return socket;
            } catch (IOException e) {
                return null;
            }
        }, ThreadPool.COMMON_EXECUTOR)
            .thenApplyAsync(socket -> {
                this.socket.set(socket);
                if (socket != null) {
                    this.host.set(host);
                    this.port.set(port);
                } else {
                    changeState(ConnectionState.DISCONNECTED);
                    this.host.set(null);
                    this.port.set(-1);
                }
                return socket != null;
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    @Override
    public CompletableFuture<Boolean> disconnect() {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Ukončuji spojení se serverem.");
            try {
                socket.get().close();

                System.out.println("Ukončuji čtecí vlákno.");
                readerThread.shutdown();

                System.out.println("Čtecí vlákno bylo úspěšně ukončeno.");

                System.out.println("Ukončuji zapisovací vlákno.");
                writerThread.shutdown();

                System.out.println("Zapisovací vlákno bylo úspěšně ukončeno.");

                System.out.println("Spojení se podařilo ukončit");
            } catch (IOException e) {
                System.out.println("Nastala neočekávaná chyba při uzavírání socketu.");
                e.printStackTrace();
                return false;
            }

            return true;
        }, ThreadPool.COMMON_EXECUTOR)
            .thenApplyAsync(success -> {
                if (success) {
                    this.socket.set(null);
                    changeState(ConnectionState.DISCONNECTED);
                }

                return success;
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    @Override
    public synchronized void sendMessage(IMessage message) {
        if (writerThread != null) {
            writerThread.addMessageToQueue(message);
        }
    }

    @Override
    public synchronized CompletableFuture<IMessage> sendMessageFuture(IMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            sendMessage(message);
            return null;
        })
            .thenCompose(ignored -> {
                Request request = new Request();
                requests.add(request);
                return request.getFuture();
            });
    }

    @Override
    public synchronized void registerMessageObserver(String messageType, OnDataReceivedListener listener) {
        List<OnDataReceivedListener> listenerList = listeners.computeIfAbsent(messageType, k -> new ArrayList<>());

        listenerList.add(listener);
    }

    @Override
    public synchronized void unregisterMessageObserver(String messageType, OnDataReceivedListener listener) {
        List<OnDataReceivedListener> listenerList = listeners.get(messageType);
        if (listenerList == null) {
            return;
        }

        listenerList.remove(listener);
    }

    @Override
    public ConnectionState getConnectionState() {
        return connectionState.get();
    }

    @Override
    public ReadOnlyObjectProperty<ConnectionState> connectionStateProperty() {
        return connectionState.getReadOnlyProperty();
    }

    @Override
    public String getConnectedServerName() {
        return connectedServerName.get();
    }
}
