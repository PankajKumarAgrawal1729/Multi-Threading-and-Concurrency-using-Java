# Monitor and Locks in Java

## 1) What is a Monitor Lock?
A monitor is a synchronization mechanism built around every Java object. When a thread enters a synchronized method or block, it acquires the object's monitor. Other threads cannot enter the same monitor until the first thread releases it.

This is the basic locking model used by Java to protect shared data and critical sections.

---

## 2) Why do we need synchronization?
Without synchronization, multiple threads can access shared data at the same time and produce inconsistent results.

Synchronization helps us to ensure:
- atomicity of updates
- visibility of values across threads
- proper ordering of instructions
- safe access to shared resources

---

## 3) synchronized method
```java
synchronized void increment() {
    count++;
}
```

A synchronized instance method locks on the current object (`this`). A synchronized static method locks on the class object (`ClassName.class`).

---

## 4) synchronized block
```java
synchronized (this) {
    count++;
}
```

A synchronized block locks on a specific object reference. This gives more control than locking the whole method.

Example:
- `synchronized (this)` lock on the current instance
- `synchronized (Counter.class)` lock on the class
- `synchronized (lock1)` lock on a custom object

---

## 5) How locks work internally
Internally, Java uses a monitor associated with each object.

When a thread enters a synchronized block/method:
1. It tries to acquire the monitor lock of the object.
2. If available, the thread becomes the owner.
3. The thread executes the critical section.
4. When the thread exits the synchronized block, it releases the monitor.
5. Other waiting threads compete for the same monitor.

Java maintains internal structures for each object monitor:
- owner thread
- waiting pool
- entry set / monitor queue

### Important concept
A thread that cannot acquire the lock is blocked and waits until the lock becomes available.

This is why synchronized code prevents simultaneous execution of a critical section by multiple threads.

---

## 6) Synchronized on object vs class

### Object lock
```java
synchronized void m1() { ... }
```
This lock is associated with the instance object.

### Class lock
```java
static synchronized void m1() { ... }
```
This lock is associated with the class object, so all instances of that class share the same class-level lock.

### Important
A static synchronized method and an instance synchronized method do not use the same lock.

---

## 7) What happens if lock is not on same object?
If two threads use different lock objects, then they do not block each other. This is an important interview point.

```java
synchronized (new Object()) { ... }
```
This is not a real shared lock because each call creates a different object. So the synchronization does not synchronize across threads.

---

## 8) Monitor lock and waiting threads
When a thread runs inside a synchronized block, other threads trying to enter the same lock have to wait. Java does not allow two threads to own the same monitor at the same time.

This ensures mutual exclusion for the critical section.

---

## 9) Thread scheduling hint: yield()
`yield()` is a method that suggests to the operating system that the current thread can pause and allow other threads to run.

However:
- it is only a suggestion
- the OS decides what to do
- the final scheduling decision is always taken by the operating system or JVM scheduler

So, just like other scheduling hints, `yield()` does not guarantee immediate thread switching.

---

## 10) Real-world use of locks
Locks are used to protect:
- shared counters
- bank account balances
- inventory stock
- shared lists or caches
- any resource that multiple threads can update

---

## 11) Summary
- `synchronized` uses a monitor lock associated with an object or class.
- Only one thread can own a monitor at a time.
- Other threads wait until the lock is released.
- Locks ensure mutual exclusion, visibility, and ordering for critical sections.
- `yield()` is just a scheduling hint; the OS still decides the final behavior.
- Using the wrong lock object can break synchronization completely.

---

## 12) Key Interview Point
"Java locks are built on object monitors. The monitor is acquired by one thread at a time, and other threads wait in the lock queue until it is released. The scheduler may also accept or ignore hints like `yield()`, but it always makes the final decision."
