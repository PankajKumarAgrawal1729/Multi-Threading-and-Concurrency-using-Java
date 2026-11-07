import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Demo3 {
    public static void main(String[] args) {
        LikeCounter counter = new LikeCounter();

        Thread t1 = new Thread(() -> counter.like());
        Thread t2 = new Thread(() -> counter.like());

        t1.start();
        t2.start();

        try {
            Thread.sleep(300);
        } catch (Exception e) {
            // TODO: handle exception
        }

        System.out.println("Total Likes: " + counter.totalCount);
    }
}

class LikeCounter {
    // AtomicReference<Integer> totalCount = new AtomicReference<>(0);
    // Integer currentCount;
    // Integer finalCount;

    AtomicInteger totalCount = new AtomicInteger(0);

    public void like() {
        totalCount.incrementAndGet();
        // while (true) {
        //     currentCount = totalCount.get();
        //     finalCount = currentCount + 1;

        //     if (totalCount.compareAndSet(currentCount, finalCount)) {
        //         return;
        //     }

        //     System.out.println("Conflict Detected Re-try");
        // }
    }
}