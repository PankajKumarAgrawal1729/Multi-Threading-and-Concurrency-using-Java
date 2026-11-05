import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Demo5 {
    public static void main(String[] args) throws InterruptedException {
        BoundedBuffer buffer = new BoundedBuffer(3);

        Thread producer = new Thread(() -> buffer.produceItems(5), "producer");
        Thread consumer = new Thread(() -> buffer.consumeItems(5), "consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }
}

class BoundedBuffer {
    private final Queue<Integer> items = new ArrayDeque<>();
    private final Semaphore emptySlots;
    private final Semaphore availableItems = new Semaphore(0);
    private final ReentrantLock queueLock = new ReentrantLock();

    BoundedBuffer(int capacity) {
        emptySlots = new Semaphore(capacity);
    }

    void produceItems(int count) {
        for (int item = 1; item <= count; item++) {
            try {
                // Reserve an empty slot before adding to the shared queue.
                emptySlots.acquire();
                queueLock.lock();
                try {
                    items.add(item);
                    System.out.println("Produced: " + item);
                } finally {
                    queueLock.unlock();
                }
                // Signal that one more item is ready for a consumer.
                availableItems.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    void consumeItems(int count) {
        for (int i = 0; i < count; i++) {
            try {
                // Wait until a producer has published an item.
                availableItems.acquire();
                queueLock.lock();
                try {
                    System.out.println("Consumed: " + items.remove());
                } finally {
                    queueLock.unlock();
                }
                // Signal that a buffer slot is free again.
                emptySlots.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}