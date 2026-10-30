// Visibility: without a happens-before relationship, one thread may not observe another thread's update.

public class Demo2 {
    // volatile guarantees visibility and ordering for accesses to this flag.
    // It does not make compound operations on other variables atomic.
    static volatile boolean flag = false;

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            // The volatile write is visible to a reader that observes this value.
            flag = true;
        });
        Thread t2 = new Thread(() -> {
            while (!flag) {
                // The volatile read repeatedly checks for the update from t1.
            }
            System.out.println("Thread2 Complete");
        });

        t1.start();
        t2.start();
    }
}
