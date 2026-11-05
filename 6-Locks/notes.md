# Java Locks and Concurrency Utilities

## 1) Limitations of `synchronized`

Intrinsic monitors are useful for basic mutual exclusion, but offer limited control:
- No non-blocking `tryLock()` operation.
- No timed attempt to acquire a monitor.
- A thread waiting to enter a synchronized block cannot be made to abandon the lock attempt through interruption.
- No configurable fairness policy; which waiting thread acquires the monitor next is not guaranteed.
- Each object has one monitor wait set, so unrelated conditions are harder to coordinate separately.

`synchronized` is still a good choice for simple critical sections. Use the `Lock` APIs when timed, interruptible, or more flexible coordination is needed.

## 2) The `Lock` Interface

`java.util.concurrent.locks.Lock` provides explicit lock operations. `ReentrantLock` is a common implementation.

```java
lock.lock();
try {
    // critical section
} finally {
    lock.unlock();
}
```

Always unlock in `finally` after a successful acquisition. If `unlock()` is skipped, other threads may wait forever.

## 3) Pessimistic and Optimistic Locking

- **Pessimistic locking:** acquire a lock before accessing or changing shared data. Other conflicting operations wait. Examples: `ReentrantLock`, read/write locks, and a `StampedLock` read/write lock.
- **Optimistic reading:** read without acquiring a read lock, then validate that no write overlapped the read. `StampedLock.tryOptimisticRead()` implements this pattern. If validation fails, retry under a read lock.

Optimistic reads work best when reads are frequent and writes are uncommon. Do not use a possibly inconsistent optimistic snapshot until its stamp has been validated.

## 4) `ReentrantLock`

`ReentrantLock` is an explicit, reentrant exclusive lock. Reentrant means the owning thread can acquire the same lock again; it must unlock once for every successful acquisition.

Important methods:
- `lock()` waits until acquisition succeeds.
- `unlock()` releases one hold; call it from `finally`.
- `tryLock()` returns immediately with success/failure.
- `tryLock(timeout, unit)` waits up to the given duration and is interruptible.
- `lockInterruptibly()` waits for the lock but allows interruption while waiting.
- `newCondition()` creates a condition queue associated with this lock.
- `isHeldByCurrentThread()` and `getHoldCount()` help inspect the current thread's ownership/recursion count.

Constructing `new ReentrantLock(true)` requests fair access for queued threads. Fairness may lower throughput and does not guarantee operating-system scheduling order.

## 5) `ReadWriteLock`, Starvation, and Downgrading

`ReadWriteLock` separates access into:
- a shared read lock, allowing multiple readers when no writer owns the lock
- an exclusive write lock, held by one writer and excluding readers

This can help read-heavy workloads, but it is not automatically faster; lock overhead can outweigh the benefit for short operations.

Under sustained contention, readers or writers may experience starvation depending on implementation and policy. `ReentrantReadWriteLock` can be constructed in fair mode to reduce barging, with possible throughput costs.

### Lock downgrading
To downgrade safely, a thread acquires the read lock while it still owns the write lock, then releases the write lock. It can continue reading under the read lock. Do not release the write lock first, or another writer may change the state before the read lock is acquired.

Trying to upgrade from a read lock to a write lock can deadlock: multiple readers may each wait for the other readers to release their read locks. Release the read lock and use a carefully designed retry protocol instead.

## 6) `StampedLock`

`StampedLock` supports read locks, write locks, and optimistic reads. Lock operations return stamps that must be passed to the corresponding unlock/validate operation.

Typical optimistic-read sequence:
1. Call `tryOptimisticRead()` and keep its stamp.
2. Read the fields needed for a snapshot.
3. Call `validate(stamp)` before trusting the snapshot.
4. If validation fails, retry while holding `readLock()`.

`StampedLock` is **not reentrant** and does not track lock ownership like `ReentrantLock`. In particular, do not try to acquire its write lock again while already holding that write lock; the nested acquisition can block indefinitely. A thread holding the write lock may acquire a read lock before releasing the write lock to downgrade safely. Pair each acquired stamp with the correct unlock method.

## 7) `Semaphore`

A semaphore maintains a count of permits. `acquire()` takes a permit or waits until one is available; `release()` returns a permit. A semaphore can allow a fixed number of threads to use a limited resource concurrently.

Common use cases:
- limit concurrent database connections or API calls
- cap access to a pool of scarce resources
- model available slots in a bounded buffer

Use `new Semaphore(permits, true)` to request fair permit acquisition among queued threads. Always release a permit in `finally` after successful acquisition.

## 8) Semaphore vs Lock

| Semaphore | Lock |
|---|---|
| Controls a count of permits; can allow multiple holders | Usually provides exclusive ownership; read/write locks have explicit shared and exclusive modes |
| `acquire()` / `release()` | `lock()` / `unlock()` |
| Does not associate a permit with its acquiring thread; another thread can release a permit | Ownership-based locks generally must be unlocked by their owning thread |
| Useful for limiting concurrency/resource capacity | Useful for protecting a critical section and shared state |

A semaphore is not a direct replacement for a lock protecting a data structure. It limits access count, but does not by itself make compound state updates mutually exclusive.

## 9) `Condition`: `await()`, `signal()`, `signalAll()`

A `Condition` created by `Lock.newCondition()` provides wait queues associated with an explicit lock. Call its methods while holding the associated lock:
- `await()` releases the lock and suspends the current thread; before it returns, the thread reacquires the lock.
- `signal()` wakes one waiting thread.
- `signalAll()` wakes all threads waiting on that condition; they must reacquire the lock and compete to continue.

Always check a condition in a `while` loop because wake-ups do not prove the condition is true:

```java
lock.lock();
try {
    while (!conditionIsTrue) {
        condition.await();
    }
    // Use the state while still holding the lock.
} finally {
    lock.unlock();
}
```

## 10) Demo Guide

- `Demo1.java`: basic `ReentrantLock` mutual exclusion.
- `Demo2.java`: concurrent reads and exclusive writes with `ReadWriteLock`.
- `Demo3.java`: optimistic reads with `StampedLock` and fallback to a read lock.
- `Demo4.java`: semaphore limiting concurrent access to two resource slots.
- `Demo5.java`: bounded producer-consumer buffer using semaphores for item/slot counts and a lock for queue access.