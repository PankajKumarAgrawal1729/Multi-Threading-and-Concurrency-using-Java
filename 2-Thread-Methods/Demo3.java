public class Demo3 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                System.out.println("T1: " + i);

                // yield() is only a suggestion to the scheduler.
                // It asks the OS/JVM to allow another thread to run,
                // but the final decision is made by the operating system.
                Thread.yield();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                System.out.println("T2: " + i);
            }
        });

        t1.start();
        t2.start();
    }
}

// Thread.yield();
// Important: yield() does not guarantee immediate thread switching.
// It is just a hint to the scheduler, and the OS decides the final behavior.