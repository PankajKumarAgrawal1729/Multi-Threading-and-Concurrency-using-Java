import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Demo3 {
    public static void main(String[] args) {
        // A virtual thread can also be started directly when executor management is not needed.
        // Thread t1 = Thread.startVirtualThread(() -> {
        //     System.out.println(Thread.currentThread());
        // });

        // try {
        //     t1.join();
        // } catch (Exception e) {
        //     // TODO: handle exception
        // }

        // This executor starts a virtual thread per task; close waits for submitted tasks.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 5; i++) {
                int taskId = i;
                executor.submit(() -> {
                    System.out.println("Task-" + taskId + " executed by: " + Thread.currentThread());
                });
            }

            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
