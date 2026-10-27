public class Demo2 {
    public static void main(String[] args) {
        // Runnable is a functional interface used to define the task to run.
        MyRunnable runnable = new MyRunnable();

        // Pass the runnable instance to a Thread object.
        Thread t1 = new Thread(runnable);

        // Lambda expression can also be used directly as Runnable.
        Thread t2 = new Thread(() -> System.out.println("Thread is Running using Lambda Expression"));

        // start() starts a new thread; each thread executes its own run() method.
        t1.start();
        t2.start();
    }
}

// Using the "Runnable interface" is the preferred way to create threads.
// It separates the task from the thread lifecycle and supports better design.
class MyRunnable implements Runnable {
    @Override
    public void run() {
        // This block defines the job that the thread will perform.
        System.out.println("Thread is Running using Runnable");
    }
}