import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demo1 {
    public static void main(String[] args) {
        Resource r1 = new Resource();
        // All three threads share the same ReentrantLock through this resource.
        Thread t1 = new Thread(() -> r1.fn());
        Thread t2 = new Thread(() -> r1.fn());
        Thread t3 = new Thread(() -> r1.fn());

        t1.start();
        t2.start();
        t3.start();
    }
}

class Resource {
    // ReentrantLock is an explicit Lock implementation with features beyond
    // synchronized.
    Lock lock = new ReentrantLock();

    void fn() {
        // Always release an acquired lock in finally, including when work fails.
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " entered");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        } finally {
            lock.unlock();
        }

        System.out.println(Thread.currentThread().getName() + " exit");
    }
}