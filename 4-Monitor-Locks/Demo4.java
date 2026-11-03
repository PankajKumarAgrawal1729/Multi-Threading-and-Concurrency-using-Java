public class Demo4 {
    public static void main(String[] args) throws InterruptedException {
        CounterDemo4 c1 = new CounterDemo4();

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

        t1.join();
        t2.join();
        System.out.println(c1.count);
    }
}

class CounterDemo4 {
    public int count = 0;

    // Locking on `this` is equivalent to synchronizing the instance method.
    // Only one thread can enter this block for the same object instance.
    void increament() {
        synchronized (this) {
            count++;
        }
    }
}

// This shows that a synchronized block can protect a critical section without
// making the whole method synchronized.
