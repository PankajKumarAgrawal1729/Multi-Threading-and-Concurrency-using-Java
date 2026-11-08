import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Demo5 {
    public static void main(String[] args) throws InterruptedException {
        AtomicInteger threadNumber = new AtomicInteger();
        ThreadFactory factory = task -> {
            Thread worker = new Thread(task, "named-worker-" + threadNumber.incrementAndGet());
            worker.setUncaughtExceptionHandler((thread, error) ->
                    System.out.println(thread.getName() + " uncaught: " + error.getMessage()));
            return worker;
        };

        ExecutorService executor = Executors.newFixedThreadPool(2, factory);
        try {
            // execute() has no Future: a task failure reaches the worker's uncaught handler.
            executor.execute(() -> {
                throw new IllegalStateException("failure from execute");
            });

            // submit() captures failure in its Future; get() exposes it as ExecutionException.
            Future<Integer> result = executor.submit(() -> {
                throw new IllegalArgumentException("failure from submit");
            });
            try {
                result.get();
            } catch (ExecutionException e) {
                System.out.println("submit cause: " + e.getCause().getMessage());
            }
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
    }
}
