import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Demo6 {
    public static void main(String[] args) throws InterruptedException {
        // Cached pools reuse idle workers and may create more threads for bursts.
        runTasks("cached", Executors.newCachedThreadPool());

        // Single-thread executors process submitted tasks one at a time, in order.
        runTasks("single", Executors.newSingleThreadExecutor());
    }

    private static void runTasks(String poolName, ExecutorService executor) throws InterruptedException {
        try {
            for (int taskId = 1; taskId <= 4; taskId++) {
                int currentTask = taskId;
                executor.execute(() -> System.out.println(
                        poolName + " task " + currentTask + " on " + Thread.currentThread().getName()));
            }
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
    }
}
