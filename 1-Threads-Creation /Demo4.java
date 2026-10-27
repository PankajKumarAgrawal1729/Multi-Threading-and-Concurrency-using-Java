public class Demo4 {
    public static void main(String[] args) {
        // Store a reference to the current thread (main thread).
        Thread mainThread = Thread.currentThread();

        // Create a new thread object in the NEW state.
        Thread t1 = new Thread(() -> {
            System.out.println("My Thread Name: " + Thread.currentThread().getName());
            System.out.println("Main Thread Stage: " + mainThread.getState());
        });

        // A newly created thread is in the NEW state before start() is called.
        System.out.println("New Stage: " + t1.getState());

        // start() moves the thread to RUNNABLE state.
        t1.start();
        System.out.println("Runnable Stage: " + t1.getState());

        try {
            // Sleep for a while so the thread can execute and finish.
            Thread.sleep(3000);
        } catch (Exception e) {
            // Exception handling for sleep interruption.
            e.printStackTrace();
        }

        // Once the thread ends execution, it reaches TERMINATED state.
        System.out.println("Terminated Stage: " + t1.getState());
    }
}

// Thread lifecycle:
// NEW -> RUNNABLE -> TERMINATED
// Depending on scheduling and blocking, a thread may also go to BLOCKED / WAITING / TIMED_WAITING.
