public class Demo3 {
    public static void main(String[] args) {
        Demo3Box box = new Demo3Box();

        // Producer and consumer coordinate using the same Box monitor.
        Thread t1 = new Thread(() -> {
            for (int i = 1; i <= 20; i++) {
                try {
                    Thread.sleep(100);
                    box.producer(i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 1; i <= 20; i++) {
                try {
                    Thread.sleep(70);
                    box.consumer();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });

        t1.start();
        t2.start();
    }
}

// wait(), notify(), and notifyAll() must be called while owning the same object's monitor;
// otherwise Java throws IllegalMonitorStateException.

// wait() releases this object's monitor and waits. When awakened, the thread
// must reacquire the monitor before wait() returns.

// notify() wakes an arbitrary waiter; the awakened thread must reacquire the monitor.

// notifyAll() wakes all waiters, which then compete to reacquire the monitor.

class Demo3Box {
    // Both condition and item are guarded by this Box object's monitor.
    private Integer item;
    private boolean hasItem = false;

    synchronized void producer(Integer value) throws InterruptedException {
        // Use while because a wake-up does not guarantee the slot is empty.
        while (hasItem) {
            wait();
        }
        item = value;
        hasItem = true;
        System.out.println("Producer produce: " + item);
        // With one producer and one consumer, notify() wakes the other role.
        // For multiple producer/consumer waiters, notifyAll() is generally safer.
        notify();
    }

    synchronized void consumer() throws InterruptedException {
        // The loop handles spurious wake-ups and a condition changed by another thread.
        while (!hasItem) {
            wait();
        }
        System.out.println("Consumer Consume: " + item);
        item = null;
        hasItem = false;
        notify();
    }

}