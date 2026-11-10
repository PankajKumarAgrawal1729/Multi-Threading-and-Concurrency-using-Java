import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Demo2 {
    public static void main(String[] args) {
        int[] arr = new int[]{1, 2, 3, 5, 6, 7, 8, 9, 4};

        // ForkJoinPool recursively splits work and uses work-stealing among workers.
        try (ForkJoinPool pool = new ForkJoinPool()) {
            SumTask task = new SumTask(arr, 0, arr.length);

            // invoke() submits the root task and waits for its result.
            int result = pool.invoke(task);

            System.out.println("Sum: " + result);

            pool.shutdown();
        }
    }
}

class SumTask extends RecursiveTask<Integer> {
    private final int[] arr;
    private final int start;
    private final int end;

    // The range is half-open: start is included and end is excluded.
    public SumTask(int[] arr, int start, int end) {
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        // Small ranges are processed directly instead of creating more subtasks.
        if (end - start <= 2) {
            int sum = 0;
            for (int i = start; i < end; i++) {
                sum += arr[i];
            }
            return sum;
        }

        int mid = start + (end - start) / 2;

        SumTask leftTask = new SumTask(arr, start, mid);
        // Keep the ranges contiguous and non-overlapping: [start, mid), [mid, end).
        SumTask rightTask = new SumTask(arr, mid, end);

        leftTask.fork();
        // Compute one branch locally while another worker can steal the forked branch.
        int sum2 = rightTask.compute();
        int sum1 = leftTask.join();

        return sum1 + sum2;
    }
}