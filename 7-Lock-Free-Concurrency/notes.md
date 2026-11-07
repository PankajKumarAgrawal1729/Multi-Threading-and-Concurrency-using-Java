# Lock-Free Concurrency in Java

## 1) Atomic Variables

The `java.util.concurrent.atomic` package provides atomic operations on individual values, such as `AtomicInteger`, `AtomicLong`, and `AtomicBoolean`.

Operations such as `incrementAndGet()` are indivisible to competing threads, unlike a plain `count++`, which is a read-modify-write sequence. Atomic classes also provide the memory-visibility guarantees specified by their APIs.

Atomic variables are useful for simple shared state. They do not automatically make a sequence of multiple operations on different variables atomic.

## 2) Atomic References

`AtomicReference<T>` stores a reference and provides atomic operations such as `get()`, `set()`, `compareAndSet()`, `getAndSet()`, and update methods.

For example, a seat can be booked by changing its state from `Empty` to a person's name with one compare-and-set operation. Only one racing caller can successfully replace the expected current reference.

CAS on `AtomicReference` compares references by identity, not by calling `.equals()`. Use the exact reference read from the atomic object as the expected value when implementing a CAS loop.

## 3) CAS and How It Helps

CAS means **Compare-And-Set** (also called compare-and-swap): atomically compare the current value with an expected value and, only if they match, replace it with a new value.

Conceptually:

```text
if (current == expected) {
    current = update;
    return success;
}
return failure;
```

Because the comparison and update form one atomic operation, competing updates cannot both succeed based on the same old value. A caller can retry after failure, which is how many atomic operations are built.

CAS prevents lost updates for the particular CAS-protected state transition. It does not make arbitrary surrounding code atomic, prevent every race, or guarantee that each individual thread will succeed promptly.

## 4) Lock vs CAS

| Lock | CAS / atomic operation |
|---|---|
| A thread acquires ownership; other threads may block | A thread attempts an atomic state transition and may retry if it loses |
| Convenient for protecting a multi-step critical section or several fields | Fits small state changes that can be represented as one atomic update |
| Can cause blocking, context switches, or deadlock if misused | Avoids application-level blocking for the CAS step, but retries can waste CPU under contention |
| Easier to express complex invariants | More difficult to reason about for complex state and vulnerable to ABA if version is not tracked |

CAS-based algorithms are not automatically faster. Choose the simplest mechanism that correctly protects the invariant; locks can be the better choice for multi-step operations.

## 5) ABA Problem

The ABA problem occurs when a thread reads value A, pauses, and later successfully CASes because the value is A again, even though another thread changed it A -> B -> A in between.

Ordinary CAS checks only whether the current value equals the expected value. It does not know that the state changed and returned to A. This can make a stale decision appear valid, especially in algorithms where intermediate changes carry meaning (for example, pointer-based stacks).

CAS is not itself a solution to ABA; basic CAS is the operation that may be fooled by it.

## 6) Versioning the State

Pair the value with a version or stamp and compare both together. Every successful update increments the version, so A/version-0 differs from A/version-2 even though the value is again A.

Java provides `AtomicStampedReference<T>` to atomically compare a reference and an integer stamp. `AtomicMarkableReference<T>` pairs a reference with a boolean mark when only a marked/unmarked distinction is needed. Version counters can eventually wrap, so a stamp is a practical mitigation, not an unlimited mathematical history.

`Demo4.java` shows a stale CAS failing after the state changes A -> B -> A because its stamp no longer matches.

## 7) Other Important Topics

- **CAS retry loops:** A failed CAS means another update won; reread the state, recompute, and retry. Keep retry-loop work safe to repeat and avoid side effects that could happen more than once.
- **Progress guarantees:** Lock-free means the system as a whole continues to make progress; it does not mean every thread is guaranteed to finish (that stronger per-thread property is wait-free).
- **Contention:** Many threads repeatedly failing CAS can burn CPU. Atomics are not automatically the best high-contention counter; `LongAdder` can improve throughput for statistics when a momentarily exact snapshot is not required.
- **Compound invariants:** If correctness depends on several values changing together, use a lock or encapsulate the state in an immutable object and CAS an `AtomicReference` to the whole state.
- **Retrying update functions:** Atomic update functions may be re-applied after contention. Their functions should be side-effect-free.
- **Memory ordering:** Atomic classes provide specified visibility/ordering guarantees. Do not assume a plain field becomes safely published just because another field is atomic; use the documented happens-before relationships.

## 8) Demo Guide

- `Demo1.java`: concurrent `AtomicInteger` increments.
- `Demo2.java`: seat booking with `AtomicReference.compareAndSet()`.
- `Demo3.java`: another atomic counter use case.
- `Demo4.java`: ABA mitigation using `AtomicStampedReference`.