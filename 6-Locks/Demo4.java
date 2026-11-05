import java.util.concurrent.Semaphore;

public class Demo4 {
    public static void main(String[] args) {
        // At most two workers may use this limited resource at once.
        Semaphore permits = new Semaphore(2, true);
        Thread[] workers = new Thread[5];

        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Thread(() -> {
                try {
                    // acquire() waits until a permit is available.
                    permits.acquire();
                    try {
                        System.out.println(Thread.currentThread().getName() + " using resource");
                        Thread.sleep(500);
                    } finally {
                        // Return the permit even if the work is interrupted or fails.
                        permits.release();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "worker-" + (i + 1));
            workers[i].start();
        }

        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}