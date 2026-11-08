import java.util.concurrent.ExecutionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Demo7 {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        CountDownLatch fixedRateRuns = new CountDownLatch(3);
        CountDownLatch fixedDelayRuns = new CountDownLatch(3);

        try {
            // One-shot task, scheduled to run once after a short delay.
                ScheduledFuture<String> once = scheduler.schedule(
                    () -> "One-shot task result", 100, TimeUnit.MILLISECONDS);

            // A fixed-rate task targets a start-to-start period.
            ScheduledFuture<?> atRate = scheduler.scheduleAtFixedRate(() -> {
                System.out.println("Fixed rate");
                fixedRateRuns.countDown();
            }, 0, 150, TimeUnit.MILLISECONDS);

            // A fixed-delay task waits the delay after each execution finishes.
            ScheduledFuture<?> withDelay = scheduler.scheduleWithFixedDelay(() -> {
                System.out.println("Fixed delay");
                fixedDelayRuns.countDown();
            }, 0, 150, TimeUnit.MILLISECONDS);

            fixedRateRuns.await();
            fixedDelayRuns.await();
            atRate.cancel(false);
            withDelay.cancel(false);
            System.out.println(once.get());
        } finally {
            scheduler.shutdown();
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        }
    }
}
