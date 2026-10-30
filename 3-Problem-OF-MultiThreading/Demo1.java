public class Demo1 {
    public static void main(String[] args) throws InterruptedException {
        Counter c1 = new Counter();

        // Both threads update the same counter, so an unsynchronized increment can be lost.
        Thread t1 = new Thread(() -> {
            for (int i = 1; i <= 100000; i++) {
                c1.increment();
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 1; i <= 100000; i++) {
                c1.increment();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // join() makes this read happen after both worker threads have finished.
        // It does not prevent lost updates during the increments.
        System.out.println("Final Count: " + c1.count);
    }
}

class Counter {
    int count = 0;

    // This read-modify-write is not atomic; concurrent calls may overwrite each other's updates.
    void increment() {
        count++;
    }
}

// This demo illustrates a race condition caused by a non-atomic shared update.