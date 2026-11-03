// Static synchronized means the lock is on the class object, not on a specific instance.

public class Demo6 {
    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> CounterDemo6.increament());
        Thread t2 = new Thread(() -> CounterDemo6.increament());

        t1.start();
        t2.start();

        t1.join();
        t2.join();
        System.out.println(CounterDemo6.count);
    }
}

class CounterDemo6 {
    public static int count = 0;

    // A static synchronized method locks on `Counter.class`.
    // This is shared by all instances of the class.
    static void increament() {
        synchronized (CounterDemo6.class) {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            count++;
            System.out.println(count);
        }
    }
}

// This is different from an instance-level lock (`this`) because all objects
// share the same class lock.
