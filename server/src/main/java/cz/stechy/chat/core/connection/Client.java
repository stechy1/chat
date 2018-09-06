package cz.stechy.chat.core.connection;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Třída reprezentuje připojeného klienta a zprostředkovává komunikaci s klientem
 */
public class Client implements IClient, Runnable {

    private final Socket socket;
    private final ObjectOutputStream writer;

    private ConnectionClosedListener connectionClosedListener;

    Client(Socket socket) throws IOException {
        this.socket = socket;
        writer = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Byl vytvořen nový klient.");
    }

    @Override
    public void close() {
        try {
            System.out.println("Uzavírám socket.");
            socket.close();
            System.out.println("Socket byl úspěšně uzavřen.");
        } catch (IOException e) {
            System.out.println("Socket se nepodařilo uzavřít!");
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessageAsync(Object message) {
        // TODO odeslat zprávu asynchronně
    }

    @Override
    public void sendMessage(Object message) throws IOException {
        writer.writeObject(message);
    }

    @Override
    public void run() {
        System.out.println("Spouštím nekonečnou smyčku pro komunikaci s klientem.");
        try (ObjectInputStream reader = new ObjectInputStream(socket.getInputStream())) {
            System.out.println("InputStream byl úspěšně vytvořen.");
            Object received;
            while ((received = reader.readObject()) != null) {
                System.out.println(String.format("Bylo přijato: '%s'", received));
            }
        } catch (EOFException |SocketException e) {
            System.out.println("Klient ukončil spojení.");
        } catch (IOException e) {
            System.out.println("Nastala neočekávaná vyjímka.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // Nikdy by nemělo nastat
            System.out.println("Nebyla nalezena třída.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Neznámá chyba.");
            e.printStackTrace();
        } finally {
            System.out.println("Volám connectionClosedListener.");
            if (connectionClosedListener != null) {
                connectionClosedListener.onConnectionClosed();
            }
            close();
        }
    }

    /**
     * Nastaví listener na ztrátu spojení s klientem
     *
     * @param connectionClosedListener {@link ConnectionClosedListener}
     */
    void setConnectionClosedListener(ConnectionClosedListener connectionClosedListener) {
        this.connectionClosedListener = connectionClosedListener;
    }

    /**
     * Rozhraní obsahující metodu, která se zavolá v případě, že se ukončí spojení s klientem
     */
    @FunctionalInterface
    public interface ConnectionClosedListener {

        /**
         * Metoda se zavolá v případě, že se ukončí spojení s klientem
         */
        void onConnectionClosed();
    }
}