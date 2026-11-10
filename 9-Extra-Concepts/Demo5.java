import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Demo5 {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            // Callable overload: Future contains the returned value.
            Future<Integer> callableFuture = executor.submit(() -> 21);

            // Runnable overload: Future.get() returns null when the task completes.
            Future<?> runnableFuture = executor.submit(
                    () -> System.out.println("Runnable task completed"));

            // Runnable-plus-result overload: Future completes with the supplied result.
            Future<String> suppliedResultFuture = executor.submit(
                    () -> System.out.println("Runnable with supplied result"), "done");

            // Future.get() blocks; ordinary Future has no built-in chaining operation.
            int value = callableFuture.get();
            runnableFuture.get();
            System.out.println("Callable result: " + value);
            System.out.println("Supplied result: " + suppliedResultFuture.get());

            // CompletableFuture supports non-blocking stage composition and recovery.
            CompletableFuture<Integer> pipeline = CompletableFuture
                    .supplyAsync(() -> 21, executor)
                    .thenApply(number -> number * 2)
                    .thenCompose(doubled -> CompletableFuture.supplyAsync(() -> doubled + 1, executor))
                    .exceptionally(error -> -1);

            System.out.println("Chained result: " + pipeline.join());
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
    }
}