import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Demo4 {
    public static void main(String[] args) throws Exception {
        ThreadLocal<String> requestContext = ThreadLocal.withInitial(() -> "no-request");
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future<?> firstTask = executor.submit(() -> {
                requestContext.set("request-42");
                System.out.println("Set context on " + Thread.currentThread().getName());
            });

            Future<?> secondTask = executor.submit(() ->
                    System.out.println("Context on reused worker: " + requestContext.get()));

            firstTask.get();
            secondTask.get();

            // Pooled workers outlive tasks, so clear per-request values to avoid leaking context.
            Future<?> cleanupTask = executor.submit(() -> {
                requestContext.remove();
                System.out.println("Context after remove: " + requestContext.get());
            });
            cleanupTask.get();
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
    }
}