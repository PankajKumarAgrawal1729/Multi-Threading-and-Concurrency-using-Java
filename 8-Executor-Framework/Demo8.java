import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Demo8 {
    public static void main(String[] args) throws InterruptedException {
        demonstrate("AbortPolicy", new ThreadPoolExecutor.AbortPolicy());
        demonstrate("DiscardPolicy", new ThreadPoolExecutor.DiscardPolicy());
        demonstrate("DiscardOldestPolicy", new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    private static void demonstrate(String policyName, RejectedExecutionHandler policy)
            throws InterruptedException {
        System.out.println("--- " + policyName + " ---");
        CountDownLatch workerStarted = new CountDownLatch(1);
        CountDownLatch releaseWorker = new CountDownLatch(1);
        try (ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, 1, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1), policy)) {

            // Occupy the only worker so the next task fills the queue and the third is rejected.
            executor.execute(() -> {
                workerStarted.countDown();
                try {
                    releaseWorker.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Worker task finished");
            });
            workerStarted.await();

            executor.execute(() -> System.out.println("Oldest queued task ran"));
            try {
                executor.execute(() -> System.out.println("New rejected task ran"));
            } catch (RejectedExecutionException e) {
                // AbortPolicy reports rejection to the caller; discard policies do not throw.
                System.out.println("Rejected task reported to caller");
            }

            releaseWorker.countDown();
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
    }
}
