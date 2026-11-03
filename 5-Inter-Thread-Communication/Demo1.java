public class Demo1 {
    public static void main(String[] args) {
        Demo1Box box = new Demo1Box();

        // These threads access the same Box without synchronization.
        // Their sleeps only affect timing; they do not coordinate access safely.
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

class Demo1Box {
    // Shared mutable state; these fields are not protected by a lock in this demo.
    Integer item;
    Boolean flag = false;

    void producer(Integer value) {
        // This can overwrite an unconsumed item because there is no coordination.
        item = value;
        flag = true;
        System.out.println("Producer produce: " + item);
    }

    void consumer() {
        // This may read null or race with the producer.
        System.out.println("Consumer Consume: " + item);
        item = null;
        flag = false;
    }

}