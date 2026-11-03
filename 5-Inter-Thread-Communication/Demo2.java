public class Demo2 {
    public static void main(String[] args) {
        Demo2Box box = new Demo2Box();

        // This version spins while waiting for the one-slot buffer condition.
        // Busy waiting can waste CPU and does not provide efficient communication.
        Thread t1 = new Thread(() -> {
            for (int i = 1; i <= 20; i++) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                box.producer(i);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 1; i <= 20; i++) {
                try {
                    Thread.sleep(70);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                box.consumer();
            }
        });

        t1.start();
        t2.start();
    }
}

// This demo illustrates busy waiting. Adding synchronized around the spin loops
// would be incorrect: a spinning thread could keep the monitor and prevent the
// other thread from entering to change the condition.
class Demo2Box {
    // volatile provides visibility for these individual fields, not atomicity for
    // the combined check/update protocol.
    volatile Integer item;
    volatile Boolean flag = false;

    void producer(Integer value) {
        // Spin while the slot is full; this continuously consumes CPU.
        while (flag) {
        }
        item = value;
        flag = true;
        System.out.println("Producer produce: " + item);
    }

    void consumer() {
        // Spin while the slot is empty; volatile alone does not make this efficient.
        while (!flag) {

        }
        System.out.println("Consumer Consume: " + item);
        item = null;
        flag = false;
    }

}