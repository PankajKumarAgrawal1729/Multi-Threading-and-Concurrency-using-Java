public class Demo7 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> System.out.println("Thread1 Running"));
        Thread t2 = new Thread(() -> System.out.println("Thread2 Running"));

        // setPriority() sets the scheduling preference of a thread.
        // The value ranges from 1 (MIN_PRIORITY) to 10 (MAX_PRIORITY).
        t1.setPriority(10);

        t2.start();
        t1.start();

        System.out.println("Priority Of Thread1: " + t1.getPriority());
        System.out.println("Priority Of Thread2: " + t2.getPriority());
    }
}

// Thread priorities are hints to the scheduler, not strict guarantees.
// The OS ultimately decides how threads are scheduled.
