import java.util.concurrent.CompletableFuture;

public class Demo1 {
    public static void main(String[] args) {
        // The following are alternative examples; uncomment only one at a time because
        // they each declare a local variable named f1.

        // supplyAsync starts a task and completes a future with its value (10).
        // thenApply transforms that value, so this future completes with 20.
        // CompletableFuture<Integer> f1 =
        // CompletableFuture.supplyAsync(() -> 10)
        // .thenApply(result -> result * 2);

        // get() blocks until completion and returns the result. It can throw
        // InterruptedException or ExecutionException, so handle them deliberately.
        // try {
        // System.out.println(f1.get());
        // } catch (Exception e) {
        // // TODO: handle exception
        // }

        // thenAccept consumes the previous stage's value and returns a
        // CompletableFuture<Void> because the consumer does not produce a value.
        // CompletableFuture<Void> f1 =
        // CompletableFuture.supplyAsync(() -> 10)
        // .thenAccept(result -> System.out.println(result));

        // thenRun executes an action after the previous stage completes, without
        // receiving its value; its resulting future also has type Void.
        // CompletableFuture<Void> f1 =
        // CompletableFuture.supplyAsync(() -> 10)
        // .thenRun(() -> System.out.println("Done"));

        // supplyAsync creates stages that run asynchronously; thenCombine joins their results.
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> 10);
        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> 20);
        CompletableFuture<Void> result = f1.thenCombine(f2, (a, b) -> a + b)
                .thenAccept(sum -> System.out.println(sum));

        // Wait for the final stage so main does not exit before the result is printed.
        result.join();
    }
}
