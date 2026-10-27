public class Demo6 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            // getName() returns the current thread's name.
            System.out.println(Thread.currentThread().getName());
        });

        // setName() gives this thread a custom name for easier debugging.
        t1.setName("worker1");
        t1.start();
    }
}

// Naming threads helps track task execution in a multi-threaded program.
