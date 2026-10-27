public class Demo4 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            // Keep looping until the thread is interrupted.
            while (!Thread.currentThread().interrupted()) {
                System.out.println("Running");
            }
        });

        t1.start();

        // Let the thread run for a while.
        Thread.sleep(2000);

        // interrupt() asks the thread to stop its current waiting or looping work.
        t1.interrupt();
    }
}
// interrupt() is used to signal a thread to stop or exit a waiting state.
// Thread.currentThread().interrupted() checks and clears the interrupt status.