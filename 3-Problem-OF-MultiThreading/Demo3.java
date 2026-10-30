// Without a happens-before relationship, another thread is not guaranteed to observe
// ordinary shared writes in source-code order.

public class Demo3 {
    static int x = 0;
    static boolean flag = false;

    public static void main(String[] args) {
        // This happend rarely that due to re-ordering flag marked true first then due
        // to context switch t2 print x as 0
        Thread t1 = new Thread(() -> {
            // These ordinary writes are not safely published to t2.
            x = 10;
            flag = true;
        });

        Thread t2 = new Thread(() -> {
            // This read is not guaranteed to observe x == 10, even if flag is true.
            if (flag) {
                System.out.println(x);
            }
        });

        t1.start();
        t2.start();
    }
}
// This example has a data race: the output is not guaranteed to be 10.
// A volatile flag can publish the preceding x write when the reader observes true.
// Alternatively, use synchronization or another documented happens-before mechanism.