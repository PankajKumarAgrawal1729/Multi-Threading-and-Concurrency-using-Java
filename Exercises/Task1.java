// Exercise: Debugging exercise. Below is a Java class used by multiple
// request threads in a service:

// public class HitCounter {
//     private int count = 0;
//     private final Map<String, Integer> perUser = new HashMap<>();

//     public void record(String user) {
//         count++;
//         Integer c = perUser.get(user);
//         perUser.put(user, c == null ? 1 : c + 1);
//     }

//     public int getCount() {
//         return count;
//     }
// }
// Under heavy concurrent load, the totals come out wrong, and occasionally the
// service misbehaves in other ways. Identify every thread-safety problem you
// can see, explain precisely how each one happens at the
// instruction/interleaving level, and then show a corrected version. Say
// whether your fix uses locks, atomics or concurrent collections, and why you
// chose it.
//
// This file declares package Exercises. From the project root, run:
// javac Exercises/Task1.java
// java Exercises.Task1

/*
 * Exercise answer
 *
 * Problems in the original implementation:
 * 1. `count++` is a read-modify-write, not one atomic operation. For example,
 *    two threads can both read 10, both compute 11, and both write 11. Two
 *    requests were processed, but the count increased only once.
 * 2. `perUser.get(user)` followed by `perUser.put(...)` is also a compound
 *    read-modify-write. If two threads both read 4 for the same user, both can
 *    write 5, losing one hit. For a new user, both can read null and both write
 *    1 instead of the correct 2.
 * 3. HashMap is not safe for concurrent mutation. Concurrent get/put operations
 *    can lose updates and have no thread-safety or visibility guarantees. Making
 *    the map reference final would not make its contents thread-safe.
 * 4. The original plain `getCount()` can race with writers and may observe a
 *    stale value when called while requests are still running. In this demo,
 *    joining every worker makes its earlier writes visible to main, but join()
 *    does not make record() itself safe for concurrent calls.
 * 5. Updating the total and per-user map separately with different atomic
 *    objects would make each individual update safe, but would not make the pair
 *    one transaction. A reader during record() could see the total already
 *    incremented while the per-user update has not happened yet.
 *
 * Fix used below: `record()` and `getCount()` synchronize on the same
 * HitCounter instance. This serializes the whole count-plus-map update, protects
 * HashMap, and gives readers a happens-before relationship with completed
 * updates. This simple intrinsic lock is a good fit because the invariant spans
 * two pieces of state. All access to `perUser` must use this same monitor; a
 * future `getMap()` should also synchronize and return a defensive copy.

 * A ConcurrentHashMap with `merge()` could make each user's increment safe, and
 * an AtomicInteger could make the global count safe, but those two operations
 * still would not be atomic together. If callers need a coherent count-and-map
 * snapshot during active requests, use one shared lock for update and snapshot,
 * or store both values in one immutable state updated by a single CAS.
 */
package Exercises;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Task1 {
    public static void main(String[] args) throws InterruptedException {
        HitCounter counter = new HitCounter();

        // Four request threads concurrently update the same HitCounter instance.
        Thread t1 = new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                if (i % 2 == 0)
                    counter.record("Ram");
                else
                    counter.record("Shyam");
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                if (i % 2 == 0)
                    counter.record("Shyam");
                else
                    counter.record("Gopal");
            }
        });

        Thread t3 = new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                if (i % 2 == 0)
                    counter.record("Gopal");
                else
                    counter.record("Jay");
            }
        });

        Thread t4 = new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                if (i % 2 == 0)
                    counter.record("Jay");
                else
                    counter.record("Ram");
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        // Wait for every request thread before reporting the final total.
        t1.join();
        t2.join();
        t3.join();
        t4.join();

        System.out.println("Final Count: " + counter.getCount());
        System.out.println("Final User Data: " + counter.getMap());
    }
}

// class HitCounter {
//     // Both fields are shared state and are protected by this object's monitor.
//     private int count = 0;
//     private final Map<String, Integer> perUser = new HashMap<>();

//     // One lock protects the total and per-user update as a single operation.
//     // This prevents lost increments and unsafe concurrent access to HashMap.
//     public synchronized void record(String user) {
//         count++;
//         Integer c = perUser.get(user);
//         perUser.put(user, c == null ? 1 : c + 1);
//     }

//     // Use the same monitor so a read during active updates has visibility guarantees.
//     public synchronized int getCount() {
//         return count;
//     }

//     public synchronized Map<String, Integer> getMap() {
//         return perUser;
//     }
// }

class HitCounter {
    // Atomic fields make their individual updates and reads thread-safe.
    // They are separate atomics, so count and perUser are not one transactional snapshot
    // while record() calls are still in progress.
    private final AtomicInteger count = new AtomicInteger(0);
    private final AtomicReference<Map<String, Integer>> perUser = new AtomicReference<>(Collections.emptyMap());

    // Use this method for every hit so the global and per-user totals are both updated.
    public void record(String user) {
        count.incrementAndGet();
        put(user);
    }

    // Build a new map instead of mutating the currently published snapshot.
    // If another thread wins the CAS first, retry from that thread's newer snapshot.
    private void put(String key) {
        Map<String, Integer> prevPerUser;
        Map<String, Integer> nextPerUser;

        do {
            prevPerUser = perUser.get();
            // Read the value from the exact snapshot used as the CAS expectation.
            Integer value = prevPerUser.get(key);
            nextPerUser = new HashMap<>(prevPerUser);
            nextPerUser.put(key, value == null ? 1 : value + 1);
        } while (!perUser.compareAndSet(prevPerUser, nextPerUser));
    }

    // This is an atomic count read, but it may not correspond to a simultaneous getMap() read.
    public int getCount() {
        return count.get();
    }

    // Return a read-only copy; callers cannot mutate the map snapshot held by the counter.
    public Map<String, Integer> getMap() {
        return Collections.unmodifiableMap(new HashMap<>(perUser.get()));
    }
}