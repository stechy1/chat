package cz.stechy.chat.core.dispatcher;

import cz.stechy.chat.core.connection.Client;
import cz.stechy.chat.net.message.TextMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

class ClientDispatcher extends Thread implements IClientDispatcher {

    private static final int SLEEP_TIME = 5000;

    private final Semaphore semaphore = new Semaphore(0);
    private final Queue<Client> waitingQueue = new ConcurrentLinkedQueue<>();
    private final Collection<Client> clientsToRemove = new ArrayList<>();
    private final int waitingQueueSize;
    private boolean interupt = false;

    /**
     * Vytvoří novou instanci třídy {@link ClientDispatcher}
     *
     * @param waitingQueueSize Velikost čekací fronty
     */
    ClientDispatcher(int waitingQueueSize) {
        super("ClientDispatcher");
        this.waitingQueueSize = waitingQueueSize;
    }

    @Override
    public void run() {
        System.out.println("Spouštím client dispatchera.");
        while(!interupt) {
            while(waitingQueue.isEmpty() && !interupt) {
                try {
                    System.out.println("Jdu spát na semaforu.");
                    semaphore.acquire();
                } catch (InterruptedException ignored) {}
            }

            if (interupt) {
                System.out.println("Přidávám všechny klienty na seznam pro ukončení spojení.");
                clientsToRemove.addAll(waitingQueue);
            } else {
                System.out.println("Posílám zprávu všem klientům.");
                final int count = waitingQueue.size();
                waitingQueue.forEach(client -> {
                    try {
                        client.sendMessage(new TextMessage("count: " + count));
                    } catch (IOException e) {
                        System.out.println("Klient neudržel spojení, musím se ho zbavit.");
                        clientsToRemove.add(client);
                    }
                });
            }

            System.out.println("Zbavuji se všech klientů, kteří neudrželi spojení, nebo bylo potřeba spojení s nimi ukončit.");
            waitingQueue.removeAll(clientsToRemove);
            for (Client client : clientsToRemove) {
                client.close();
            }
            clientsToRemove.clear();

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ignored) {}
        }

        System.out.println("Client dispatcher končí.");
    }

    @Override
    public boolean hasClientInQueue() {
        return !waitingQueue.isEmpty();
    }

    @Override
    public Client getClientFromQueue() {
        return waitingQueue.poll();
    }

    @Override
    public boolean addClientToQueue(Client client) {
        if (waitingQueue.size() < waitingQueueSize) {
            waitingQueue.add(client);
            semaphore.release();
            return true;
        }

        return false;
    }

    @Override
    public void shutdown() {
        interupt = true;
        semaphore.release();
        try {
            join();
        } catch (InterruptedException ignored) { }
    }
}