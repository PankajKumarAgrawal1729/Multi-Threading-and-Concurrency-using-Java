public class Deemo2 {
    public static void main(String[] args) {
        Test test = new Test();

        // Both threads call the same synchronized method on the same object.
        // Only one thread can enter the critical section at a time.
        Thread t1 = new Thread(() -> test.show());
        Thread t2 = new Thread(() -> test.show());

        t1.start();
        t2.start();
    }
}

class Test {
    // A synchronized method acquires the monitor lock of the current object.
    // Until this method finishes, other threads trying to access the same object
    // lock will wait.
    synchronized void show() {
        System.out.println(Thread.currentThread().getName() + ": Inside Show");
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Show Finished");
    }
}

// Why do we need synchronized?
// 1. To protect shared data
// 2. To make operations atomic
// 3. To ensure visibility
// 4. To prevent re-ordering

// synchronized is also called a monitor lock or object lock.
// Every Java object has an internal lock/monitor used for synchronization.