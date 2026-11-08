import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Demo2 {
    public static void main(String[] args) {
        // submit() returns a Future for observing this task's completion and result.
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<Integer> f1 = executor.submit(() -> {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                // TODO: handle exception
            }

            return 10;
        });

        try {
            // get() blocks until the task completes, then returns its Callable result.
            System.out.println("Runnable Output: " + f1.get());
        } catch (Exception e) {
            // In production, handle interruption separately and inspect ExecutionException.
            // TODO: handle exception
        }

        System.out.println("Task Completed");
        // This demo has waited for its submitted task before shutting down the pool.
        executor.shutdown();
    }
}
