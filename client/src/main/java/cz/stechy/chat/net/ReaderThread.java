package cz.stechy.chat.net;

import cz.stechy.chat.net.message.IMessage;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Třída reprezentující vlákno, které přijímá data ze serveru
 */
public class ReaderThread extends Thread {

    private final InputStream inputStream;
    private final OnDataReceivedListener dataReceivedListener;
    private final LostConnectionHandler lostConnectionHandler;
    private boolean interrupt = false;

    public ReaderThread(final InputStream inputStream, OnDataReceivedListener dataReceivedListener,
        LostConnectionHandler lostConnectionHandler) {
        super("ReaderThread");
        this.lostConnectionHandler = lostConnectionHandler;
        assert dataReceivedListener != null;
        this.dataReceivedListener = dataReceivedListener;
        System.out.println("Bylo vytvořeno nové čtecí vlákno.");
        this.inputStream = inputStream;
    }

    /**
     * Ukončí činnost čtecího vlákna
     */
    public void shutdown() {
        interrupt = true;

        try {
            join();
        } catch (InterruptedException ignored) {}
    }

    @Override
    public void run() {
        System.out.println("Spouštím nekonečnou smyčku pro komunikaci se serverem.");
        try (final ObjectInputStream reader = new ObjectInputStream(inputStream)) {
            IMessage received;
            while ((received = (IMessage) reader.readObject()) != null && !interrupt) {
                System.out.println(String.format("Byla přijata nějaká data: '%s'", received.toString()));
                dataReceivedListener.onDataReceived(received);
            }
        } catch (EOFException e) {
            System.out.println("Spojení bylo nečekaně ukončeno.");
        } catch (IOException e) {
            System.out.println("Spojení bylo ukončeno.");
        } catch (ClassNotFoundException e) {
            // Nikdy by nemělo nastat
            System.out.println("Nebyla nalezena třída.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Neznámá chyba.");
            e.printStackTrace();
        } finally {
            if (lostConnectionHandler != null) {
                lostConnectionHandler.onLostConnection();
            }
        }

        System.out.println("Čtecí vlákno bylo ukončeno.");
    }
}