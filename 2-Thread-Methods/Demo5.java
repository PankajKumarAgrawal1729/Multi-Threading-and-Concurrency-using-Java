public class Demo5 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            try {
                // Simulate work by sleeping for 2 seconds.
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Before starting, the thread is not alive yet.
        System.out.println(t1.isAlive()); // false

        t1.start();

        // After start(), it is alive and running.
        System.out.println(t1.isAlive()); // true

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // After completion, thread is no longer alive.
        System.out.println(t1.isAlive()); // false
    }
}

// isAlive() checks whether a thread has been started and has not yet terminated.
