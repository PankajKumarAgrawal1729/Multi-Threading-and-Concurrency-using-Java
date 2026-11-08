import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Demo1 {
    public static void main(String[] args) {
        // The executor manages a reusable pool of two worker threads.
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // More tasks than workers are submitted; extra tasks wait in the pool queue.
        for (int i = 1; i <= 5; i++) {
            int taskId = i;
            executor.execute(() -> {
                System.out.println("Task: " + taskId + " is performed by: " + Thread.currentThread().getName());
            });
        }

        // Stop accepting new tasks; already submitted work is allowed to finish.
        executor.shutdown();
    }
}