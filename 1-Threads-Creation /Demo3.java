public class Demo3 {
    public static void main(String[] args) {
        // currentThread() returns the thread object in which the main method is running.
        System.out.println("Main Thread Name: " + Thread.currentThread().getName());
        System.out.println("Main Thread Id: " + Thread.currentThread().getId());

        // Create a new thread using a lambda expression.
        Thread t1 = new Thread(() -> {
            // Inside a child thread, currentThread() refers to that new thread.
            System.out.println("My Thread Name is: " + Thread.currentThread().getName());
            System.out.println("My Thread Id is: " + Thread.currentThread().getId());
        });

        // Start the new thread.
        t1.start();
    }
}
// Thread Id and Name
// Every thread in Java has a unique ID and a name, which help us identify it during execution.