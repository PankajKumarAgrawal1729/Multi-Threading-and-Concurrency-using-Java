public class Demo2 {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Main Thread Starts");

        Thread t1 = new Thread(() -> {
            try {
                // Sleep for 2 seconds to simulate some work.
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Thread-0 started");
        });

        // Start the child thread.
        t1.start();

        // join() waits until t1 finishes before continuing the main thread.
        // This ensures the main thread does not proceed before t1 is complete.
        t1.join();

        // Alternative: t1.join(1000); means wait at most 1 second.
        System.out.println("Main Thread Ends");
    }
}
