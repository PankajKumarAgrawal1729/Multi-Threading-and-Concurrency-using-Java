public class Demo7 {
    public static void main(String[] args) {
        TestDemo7 test = new TestDemo7();

        // These two methods lock on different objects:
        // - Test.class (class-level lock)
        // - this (instance-level lock for the object `test`)
        // So they can run concurrently because they are not blocking on the same
        // monitor.
        Thread t1 = new Thread(() -> TestDemo7.m1());
        Thread t2 = new Thread(() -> test.m2());

        t1.start();
        t2.start();
    }
}

class TestDemo7 {
    static void m1() {
        synchronized (TestDemo7.class) {
            System.out.println("M1 Entered");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("M1 Exit");
        }
    }

    synchronized void m2() {
        synchronized (this) {
            System.out.println("M2 Entered");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("M2 Exit");
        }
    }
}

// Key point: class lock and instance lock are different monitors.
// They do not block each other.