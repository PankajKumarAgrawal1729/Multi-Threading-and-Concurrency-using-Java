public class Demo3 {
    public static void main(String[] args) {
        TestDemo3 test = new TestDemo3();

        // Both methods are synchronized on the same object instance.
        // So only one thread can enter either synchronized method at a time.
        Thread t1 = new Thread(() -> test.m1());
        Thread t2 = new Thread(() -> test.m2());

        t1.start();
        t2.start();
    }
}

class TestDemo3 {
    synchronized void m1() {
        System.out.println("M1 Entered");
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("M1 Exit");
    }

    synchronized void m2() {
        System.out.println("M2 Entered");
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("M2 Exit");
    }
}

// Output can be:
// M1 Entered
// M1 Exit
// M2 Entered
// M2 Exit
//
// OR:
// M2 Entered
// M2 Exit
// M1 Entered
// M1 Exit
//
// This happens because the monitor belongs to the whole object.
// Only one thread can hold the object's lock at a time, so the second thread
// waits.