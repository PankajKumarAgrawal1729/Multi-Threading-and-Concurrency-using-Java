public class Demo5 {
    public static void main(String[] args) {
        Bank b1 = new Bank();
        Thread t1 = new Thread(() -> b1.deposit());
        Thread t2 = new Thread(() -> b1.withdraw());

        t1.start();
        t2.start();
    }
}

class Bank {
    // Custom locks allow us to lock on specific objects instead of the whole
    // instance.
    Object lock1 = new Object();
    Object lock2 = new Object();

    void deposit() {
        synchronized (lock1) {
            System.out.println("Deposit logic");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    synchronized void withdraw() {
        synchronized (lock2) {
            System.out.println("WithDraw logic");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Interview Question:
    void m1() {
        // This lock is created anew every time m1() is called, so all calls use
        // different locks.
        // That means synchronization does not work here because threads are not locking
        // on the same object.
        synchronized (new Object()) {
            System.out.println("M1 Entered");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("M1 Exit");
        }
    }
}

// Important: To synchronize correctly, all threads must lock on the same shared
// object.