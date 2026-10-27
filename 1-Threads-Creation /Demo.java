public class Demo {
    public static void main(String[] args) {
        // Create a thread by creating an object of a class that extends Thread.
        MyThread thread1 = new MyThread();

        // start() creates a new thread and calls run() in that new thread.
        // If we call run() directly, it will execute in the current thread.
        thread1.start();
    }
}

// Using the "Thread class" to create a thread.
// A class can extend Thread and override run() to define the thread task.
class MyThread extends Thread {
    @Override
    public void run() {
        // This code runs when the thread starts.
        System.out.println("Thread is Running");
    }
}