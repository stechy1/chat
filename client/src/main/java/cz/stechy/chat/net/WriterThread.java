package cz.stechy.chat.net;

import cz.stechy.chat.net.message.IMessage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Třída reprezentující vlákno, která posílá data na server
 */
public class WriterThread extends Thread {

    private final Semaphore semaphore = new Semaphore(0);
    private final Queue<IMessage> messageQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean working = new AtomicBoolean(false);
    private final ObjectOutputStream writer;
    private final LostConnectionHandler lostConnectionHandler;
    private boolean interrupt = false;

    public WriterThread(final OutputStream outputStream, LostConnectionHandler lostConnectionHandler) throws IOException {
        super("WriterThread");
        this.lostConnectionHandler = lostConnectionHandler;
        System.out.println("Bylo vytvořeno nové zapisovací vlákno.");
        this.writer = new ObjectOutputStream(outputStream);
    }

    /**
     * Ukončí činnost zapisovacího vlákna
     */
    public void shutdown() {
        interrupt = true;
        messageQueue.clear();
        semaphore.release();

        try {
            join();
        } catch (InterruptedException ignored) {}
    }

    /**
     * Přidá zprávu do fronty k odeslání
     *
     * @param message {@link IMessage} Zpráva, která se má odeslat
     */
    public void addMessageToQueue(IMessage message) {
        messageQueue.add(message);
        if (!working.get()) {
            System.out.println("Probouzím vlákno spící na semaforu.");
            semaphore.release();
        }
    }

    @Override
    public void run() {
        do {
            while(messageQueue.isEmpty() && !interrupt) {
                try {
                    System.out.println("Jdu spát na semaforu.");
                    semaphore.acquire();
                } catch (InterruptedException ignored) {
                }
            }

            System.out.println("Vzbudil jsme se na semaforu, jdu pracovat.");
            working.set(true);
            while (!messageQueue.isEmpty()) {
                final IMessage msg = messageQueue.poll();
                assert msg != null;
                System.out.println(String.format("Odesílám zprávu: '%s'.", msg.toString()));
                try {
                    writer.writeObject(msg);
                    writer.flush();
                    System.out.println("Zpráva byla úspěšně odeslána.");
                } catch (IOException e) {
                    System.out.println("Zprávu se nepodařilo odeslat, ukončuji spojení.");
                    e.printStackTrace();
                    interrupt = true;
                    if (lostConnectionHandler != null) {
                        lostConnectionHandler.onLostConnection();
                    }
                }
            }
            working.set(false);
        } while(!interrupt);
    }
}