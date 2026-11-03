public class Demo1 {
    public static void main(String[] args) throws InterruptedException {
        CounterDemo1 c1 = new CounterDemo1();

        // Two threads update the same counter object.
        // This is a shared resource, so synchronized ensures that access is safe.
        Thread t1 = new Thread(() -> {
            for (int i = 1; i <= 100000; i++) {
                c1.increament();
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 1; i <= 100000; i++) {
                c1.increament();
            }
        });

        t1.start();
        t2.start();

        // Wait for both threads to finish before printing the final count.
        t1.join();
        t2.join();
        System.out.println(c1.count);
    }
}

class CounterDemo1 {
    public int count = 0;

    // synchronized method locks on the current object (`this`).
    // Only one thread can enter this method at a time for the same Counter
    // instance.
    synchronized void increament() {
        count++;
    }
}

// This demo shows how synchronized prevents race conditions on shared data.
