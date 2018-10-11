package cz.stechy.chat;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;

/**
 * Pomocná třída obsahující pomocné executory
 */
public final class ThreadPool {

    // Obecný threadpool na standartní úkony
    public static final ExecutorService COMMON_EXECUTOR = ForkJoinPool.commonPool();

    // Javafx executor pro vykonání kódu v hlavním vlákně
    public static final Executor JAVAFX_EXECUTOR = Platform::runLater;

    // Executor pro pozdější spuštění úlohy
    public static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    /**
     * Ukončí běh všech executorů
     */
    static void shutDown() {
        COMMON_EXECUTOR.shutdown();
        SCHEDULER.shutdown();

        try {
            COMMON_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            SCHEDULER.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Privátní konstruktor k zabránění vytvoření instance
     */
    private ThreadPool() {
        throw new AssertionError();
    }
}
