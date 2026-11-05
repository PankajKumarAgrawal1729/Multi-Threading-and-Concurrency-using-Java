import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Demo2 {
    public static void main(String[] args) {
        ReadWriteSharedResource sr = new ReadWriteSharedResource();
        // Multiple readers may hold the read lock together; writers need exclusive
        // access.
        Thread r1 = new Thread(() -> sr.read());
        Thread r2 = new Thread(() -> sr.read());
        Thread r3 = new Thread(() -> sr.read());

        Thread w1 = new Thread(() -> sr.write(5));
        Thread w2 = new Thread(() -> sr.write(14));
        Thread w3 = new Thread(() -> sr.write(82));

        r1.start();
        r2.start();
        r3.start();

        w1.start();
        w2.start();
        w3.start();
    }
}

class ReadWriteSharedResource {
    private int value = 0;
    // The default constructor is non-fair; a fair lock can reduce starvation at a throughput cost.
    ReadWriteLock rwLock = new ReentrantReadWriteLock();
    Lock rl = rwLock.readLock(); // Shared read lock: multiple readers may enter together.
    Lock wl = rwLock.writeLock(); // Exclusive write lock: only one writer at a time.

    public int read() {
        // Readers share the read lock, allowing concurrent reads while no writer owns the lock.
        rl.lock();
        try {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                // TODO: handle exception
            }
            System.out.println(Thread.currentThread().getName() + " Read value: " + value);
            return value;
        } finally {
            rl.unlock();
        }
    }

    public void write(int newValue) {
        // A writer owns the write lock exclusively, blocking readers and other writers.
        wl.lock();
        try {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                // TODO: handle exception
            }
            value = newValue;
            System.out.println(Thread.currentThread().getName() + " Update new value: " + value);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            wl.unlock();
        }
    }
}