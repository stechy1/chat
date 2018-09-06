package cz.stechy.chat.core.writer;

import com.google.inject.Singleton;
import cz.stechy.chat.net.message.IMessage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

@Singleton
public class WriterThread extends Thread implements IWriterThread {

    private final Semaphore semaphore = new Semaphore(0);
    private final Queue<QueueTuple> messageQueue = new ConcurrentLinkedQueue<>();
    private boolean working = false;
    private boolean interrupt = false;

    public WriterThread() {
        super("WriterThread");
    }

    @Override
    public void sendMessage(ObjectOutputStream writer, IMessage message) {
        messageQueue.add(new QueueTuple(writer, message));
        if (!working) {
            working = true;
            System.out.println("Probouzím vlákno na semaforu.");
            semaphore.release();
        }
    }

    @Override
    public void run() {
        System.out.println("Spouštím zapisovací vlákno.");
        while(!interrupt) {
            System.out.println("Jdu spát.");
            while(messageQueue.isEmpty() && !interrupt) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException ignored) {}
            }

            working = true;
            System.out.println("Vzbudil jsem se na semaforu, jdu pracovat.");
            while(!messageQueue.isEmpty()) {
                final QueueTuple entry = messageQueue.poll();
                assert entry != null;
                System.out.println(String.format("Odesílám zprávu: '%s'", entry.message));
                try {
                    entry.writer.writeObject(entry.message);
                    entry.writer.flush();
                    System.out.println("Zpráva byla úspěšně odeslána.");
                } catch (IOException e) {
                    System.out.println("Zprávu se nepodařio doručit.");
                    e.printStackTrace();
                }
            }
            working = false;
        }

        System.out.println("Ukončuji writer thread.");
    }

    @Override
    public void shutdown() {
        interrupt = true;
        semaphore.release();
        try {
            join();
        } catch (InterruptedException ignored) {}
    }

    private static final class QueueTuple {
        final IMessage message;
        final ObjectOutputStream writer;

        private QueueTuple(ObjectOutputStream writer, IMessage message) {
            this.message = message;
            this.writer = writer;
        }
    }
}