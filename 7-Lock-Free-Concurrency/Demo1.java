import java.util.concurrent.atomic.AtomicInteger;

public class Demo1 {
    public static void main(String[] args) {
        Counter counter = new Counter();
        // Both workers update the same atomic counter concurrently.
        Thread t1 = new Thread(() -> {
            for (int i = 1; i <= 10000; i++) {
                counter.increament();
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 1; i <= 10000; i++) {
                counter.increament();
            }
        });

        t1.start();
        t2.start();

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            // TODO: handle exception
        }
        System.out.println("Final Count: " + counter.count);
    }
}

// AtomicInteger provides atomic operations without an application-level synchronized block.
class Counter {
    // int count = 0;
    AtomicInteger count = new AtomicInteger(0);

    public void increament() {
        // count++;
        count.incrementAndGet(); // This performs an atomic increment, implemented with atomic primitives (commonly CAS).
        // CAS -> Compare And Set Operation
    }
}