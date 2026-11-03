# Inter-Thread Communication in Java

## 1) Producer-Consumer Problem

The producer-consumer problem has one or more producer threads creating items and one or more consumer threads processing them through shared storage (a buffer or queue).

For a one-slot buffer:
- A producer must wait while the slot is full.
- A consumer must wait while the slot is empty.
- Access to the slot and its state must be coordinated as one protocol.

`Demo1.java` shows the shared item without coordination. A consumer can read before any item is produced, or producer and consumer writes can race. The `flag` field is not used to protect the operations.

## 2) Why `synchronized` Alone Does Not Solve It

`synchronized` provides mutual exclusion and visibility for code using the same monitor. It does not by itself make a thread wait efficiently for a condition or notify it when that condition changes.

If the `while` loops in `Demo2.java` were simply placed inside synchronized methods, a thread could hold the monitor while spinning. The other thread would then be unable to enter the synchronized method to change the condition, causing the threads to stall. The coordination needs a way to release the monitor while waiting.

For production code, a bounded `BlockingQueue` is usually a simpler and safer producer-consumer solution.

## 3) Busy Waiting

Busy waiting (also called spinning) repeatedly checks a condition in a loop:

```java
while (!condition) {
    // keep checking
}
```

This can consume CPU while doing no useful work. `volatile` can make a field's reads and writes visible, but it does not make a multi-step producer-consumer protocol atomic or provide a way to sleep until the condition changes.

## 4) Inter-Thread Communication with `wait()` and `notify()`

Java object monitors support coordination:
- Call `wait()` while owning that object's monitor. The current thread releases that monitor and enters a waiting state.
- Call `notify()` while owning the same monitor to wake one arbitrary waiter.
- Call `notifyAll()` while owning the monitor to wake all waiters; they then compete to reacquire the monitor.
- A woken thread must reacquire the monitor before returning from `wait()`.

Calling `wait()`, `notify()`, or `notifyAll()` without owning that object's monitor throws `IllegalMonitorStateException`.

The condition check, state update, and wait/notification should all use the same lock. Wait in a `while` loop:

```java
synchronized (lock) {
    while (!condition) {
        lock.wait();
    }
    // condition holds while this thread owns the lock
}
```

## 5) Why `notify()` May Lead to Deadlock

`notify()` chooses an arbitrary waiter; it does not select a thread based on which condition it needs. With multiple producers and consumers sharing one monitor, it may wake a producer when only a consumer can make progress, or vice versa. If the awakened thread checks its condition, finds it false, and waits again, all threads can end up waiting with no future notification. The program can then make no progress (a coordination deadlock).

`notifyAll()` avoids choosing only one potentially wrong waiter: all waiters wake, reacquire the monitor one at a time, and re-check their conditions in `while` loops. This can cause extra wake-ups, but is often the safer choice when several condition types share a monitor. A `notifyAll()` call does not fix incorrect state transitions or inconsistent lock usage.

## 6) `sleep()` vs `wait()`

| `Thread.sleep(...)` | `Object.wait(...)` |
|---|---|
| Pauses the current thread for a duration | Waits for notification or, when timed, for a timeout |
| Does not release locks the thread holds | Releases the monitor on the object whose `wait()` is called |
| Static method on `Thread` | Instance method on `Object` |
| Does not require owning a monitor | Must be called while owning that object's monitor |
| Does not coordinate a shared condition | Used with a condition check and notification protocol |

Both can be interrupted and throw `InterruptedException`.

## 7) Spurious Wake-Ups and Guarded Blocks

A waiting thread can wake without a matching notification, or wake after notification when another thread has already changed the condition. Therefore, a return from `wait()` does not mean the condition is true.

Always re-check the condition in a loop (`while`, not `if`) while holding the same monitor. This is called a guarded block. The loop handles spurious wake-ups and competing threads correctly.

## Summary

- Shared state must be protected by one consistent synchronization protocol.
- `synchronized` protects a critical section; `wait()` releases its monitor while a thread waits for a condition.
- `volatile` provides visibility for individual variable accesses, not a complete coordination protocol.
- Busy waiting wastes CPU; prefer guarded `wait()`/`notifyAll()` or a concurrency utility such as `BlockingQueue`.
- Always check conditions in a `while` loop after waking.
- `notify()` wakes an arbitrary waiter; with multiple condition types it can leave the system without a thread able to make progress.