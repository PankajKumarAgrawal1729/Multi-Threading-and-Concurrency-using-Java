import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Demo3 {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // If this Runnable were passed to execute(), its exception would escape on
        // the worker thread and would not be returned to the submitting caller.
        // try {
        //     executor.execute(() -> {
        //     int x = 10 / 0;
        // });
        // } catch (Exception e) {
        //     // TODO: handle exception
        //     System.out.println("Catched Exception");
        // }

        Future<Integer> f1 = executor.submit(() -> {
            // submit() stores this failure in the returned Future.
            return (10/ 0);
        });

        try {
            // Future.get() reports task failure using ExecutionException.
            System.out.println("F1 : " + f1.get());
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Catched Submit Exception");
        }

        executor.shutdown();
    }
}
