import java.util.concurrent.locks.StampedLock;

public class Demo3 {
    public static void main(String[] args) {
        StampedSharedResource sr = new StampedSharedResource();
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
// tryOptimisticRead:
// 1. get Stamp
// 2. Read data without lock
// 3. checking if data is modified

class StampedSharedResource {
    private int value = 0;
    // StampedLock supports optimistic reads but is not reentrant.
    StampedLock lock = new StampedLock();

    public int read() {
        // An optimistic read does not acquire a read lock; validate before trusting the snapshot.
        long stamp = lock.tryOptimisticRead();
        int currentValue = value;

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (!lock.validate(stamp)) {
            // A writer overlapped the read, so retry under a real read lock.
            System.out.println("FallOver");
            stamp = lock.readLock();
            try {
                currentValue = value;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        System.out.println(Thread.currentThread().getName() + " Read value: " + currentValue);
        return currentValue;
    }

    public void write(int newValue) {
        long stamp = lock.writeLock();
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
            lock.unlockWrite(stamp);
        }
    }
}