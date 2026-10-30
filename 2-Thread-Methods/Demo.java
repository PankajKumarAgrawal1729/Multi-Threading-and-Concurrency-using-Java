public class Demo {
    public static void main(String[] args) {
        // This is the main thread of the program.
        System.out.println("Main Thread Starts");

        try {
            // sleep() pauses the current thread for a given number of milliseconds.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // If the thread is interrupted while sleeping, this exception is thrown.
            e.printStackTrace();
        }

        System.out.println("Main Thread Ends");
    }
}

/*
 * Important methods of Thread:
 * 1. Thread.sleep(milliseconds)
 * 2. join()
 * 3. yield()
 * 4. interrupt()
 */