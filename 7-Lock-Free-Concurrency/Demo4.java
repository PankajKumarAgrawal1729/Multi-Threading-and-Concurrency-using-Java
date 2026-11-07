import java.util.concurrent.atomic.AtomicStampedReference;

public class Demo4 {
    public static void main(String[] args) {
        AtomicStampedReference<String> state = new AtomicStampedReference<>("A", 0);

        // A thread reads both the reference and its version (stamp).
        int[] observedStamp = new int[1];
        String observedReference = state.get(observedStamp);

        // Simulate another thread changing A -> B -> A. The reference looks unchanged,
        // but the stamp records that the state changed twice.
        state.compareAndSet("A", "B", 0, 1);
        state.compareAndSet("B", "A", 1, 2);

        // CAS with the stale version fails even though the current reference is A.
        boolean updated = state.compareAndSet(
                observedReference,
                "C",
                observedStamp[0],
                observedStamp[0] + 1);

        int[] currentStamp = new int[1];
        String currentReference = state.get(currentStamp);
        System.out.println("Stale update succeeded: " + updated);
        System.out.println("Current state: " + currentReference + ", version: " + currentStamp[0]);
    }
}