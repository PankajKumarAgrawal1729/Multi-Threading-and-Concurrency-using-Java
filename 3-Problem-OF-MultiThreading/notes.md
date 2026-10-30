# Problems of Multithreading

When threads access shared data concurrently, the result can depend on timing and scheduling. This folder demonstrates three common problems: race conditions, visibility, and ordering.

## 1) Race Condition and Atomicity

A race condition occurs when multiple threads access and update shared data without suitable coordination, so the result depends on their timing.

In `Demo1.java`, both threads call `count++`. This operation is not atomic: it consists conceptually of reading `count`, adding one, and writing the result. The threads can interleave these steps, causing an increment to be lost. Therefore, the final count can be less than `200000`.

Calling `join()` ensures the main thread waits for both workers to finish. It does not make the increments themselves thread-safe.

Ways to protect a shared counter include:
- Synchronizing the update (for example, with a synchronized method or block).
- Using `AtomicInteger` and its `incrementAndGet()` method.
- Using a lock around the shared update.

## 2) Visibility

Visibility is whether one thread's write to shared data becomes observable by another thread. Without a synchronization mechanism, a thread may continue to observe an old value.

In `Demo2.java`, `flag` is `volatile`. A write to a volatile variable happens-before a later read of that variable, making the update visible to the reader. `volatile` does not make compound operations such as `count++` atomic.

Other ways to establish visibility include synchronized blocks/methods, locks, and suitable atomic classes. Avoid using `System.out.println()` as a visibility mechanism; its incidental synchronization is not a substitute for an explicit, correct memory-ordering design.

## 3) Ordering

Compilers, the JVM, and processors may reorder operations when the reordering preserves the behavior required by the Java Memory Model. Without a happens-before relationship, another thread is not guaranteed to observe ordinary shared reads and writes in the order the source code appears to express.

In `Demo3.java`, `x` and `flag` are ordinary shared variables. The program has a data race, and if the reader observes `flag` as `true`, it is not guaranteed to observe the corresponding update to `x`. The output is not guaranteed to be `10`.

Declaring `flag` volatile would establish the needed publication ordering for a write to `x` before the volatile write and a reader that observes that volatile write. Synchronization or another documented happens-before mechanism can also be used.

## Scheduler Hints: `yield()` and Priority

Methods such as `Thread.yield()` and thread-priority settings can suggest scheduling preferences to the JVM/OS scheduler. They do not guarantee that another thread will run, force a context switch, or establish visibility between threads. The scheduler makes the final scheduling decision, so program correctness must not depend on these hints.

## Summary

- **Atomicity:** an operation completes as one indivisible action; `count++` is not atomic.
- **Visibility:** a thread sees another thread's updates through mechanisms such as `volatile` or synchronization.
- **Ordering:** a happens-before relationship constrains what writes another thread can observe and in what order.
- `join()` waits for thread completion, but does not protect concurrent updates while they run.
- Prefer explicit synchronization or concurrency utilities over relying on timing, `println()`, `yield()`, or priority.