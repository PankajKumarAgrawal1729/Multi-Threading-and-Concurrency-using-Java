public class Demo9 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (true) {
                System.out.println("Running");
            }
        });

        // setDaemon(true) makes this a background thread.
        // Daemon threads do not block JVM shutdown when program execution ends.
        t1.setDaemon(true);

        t1.start();

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Daemon Threads
// These threads are used for background tasks like monitoring or cleanup.