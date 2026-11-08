import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Demo4 {
    public static void main(String[] args) {
        // core=2, max=5, keep-alive=10 seconds, and a bounded queue of five tasks.
        // The queue is offered tasks after core workers are busy; extra workers are
        // added toward max only when the queue fills.
        try (ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2, 5, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5))) {

            for (int i = 1; i <= 5; i++) {
                int taskId = i;

                executor.execute(() -> {
                    System.out.println("Task: " + taskId + " performed by " + Thread.currentThread().getName());

                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                });
            }
            // Orderly shutdown drains accepted work; new submissions are rejected.
            executor.shutdown();
        }
    }
}
