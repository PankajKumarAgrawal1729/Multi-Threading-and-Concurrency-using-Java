# Thread Methods in Java

## 1) Thread Methods Overview
Java threads provide many built-in methods to control execution, waiting, scheduling, and status.

Some important methods are:
- `sleep()`
- `join()`
- `yield()`
- `interrupt()`
- `isAlive()`
- `getName()` / `setName()`
- `getPriority()` / `setPriority()`
- `setDaemon(true)`

---

## 2) sleep()
`sleep()` pauses the current thread for a given time in milliseconds.

```java
Thread.sleep(2000);
```

### Purpose
- Used to delay execution.
- Helpful for simulating time-based work.
- Often used in examples and testing.

### Important point
It throws `InterruptedException`, so it must be handled with try-catch.

---

## 3) join()
`join()` waits for a thread to finish its execution before continuing the current thread.

```java
t1.join();
```

### Purpose
- Makes one thread wait for another thread.
- Useful when a thread's result is needed before proceeding.

### Example
If thread `t1` is doing important work and the main thread calls `t1.join()`, then the main thread waits until `t1` completes.

---

## 4) yield()
`yield()` is a thread method that tells the scheduler that the current thread is willing to give up its current execution time.

```java
Thread.yield();
```

### Important note
This is only a suggestion to the operating system / JVM scheduler. The final decision is taken by the OS or JVM, not by the Java code itself.

So:
- `yield()` does not guarantee the thread will stop running immediately.
- It only suggests a rescheduling opportunity.
- Actual behavior depends on the scheduler.

### Use case
It is useful in cooperative multitasking scenarios, but you should not rely on it for correctness.

---

## 5) interrupt()
`interrupt()` is used to interrupt a sleeping or waiting thread.

```java
thread.interrupt();
```

### Purpose
- Stops a thread that is blocked in sleep/wait state.
- Marks the thread as interrupted.

### Example
A thread running in a loop can check `Thread.currentThread().isInterrupted()` or `interrupted()` to stop itself.

---

## 6) isAlive()
`isAlive()` checks whether a thread is still running or has terminated.

```java
System.out.println(t1.isAlive());
```

### Purpose
- Used to know whether a thread has completed execution.
- Very helpful in monitoring thread status.

---

## 7) getName() and setName()
A thread can have a name, which helps in debugging and tracking.

```java
thread.setName("worker1");
System.out.println(thread.getName());
```

### Purpose
- Makes thread logs and debugging easier.
- Helps identify a specific thread in a multi-threaded application.

---

## 8) getPriority() and setPriority()
Each thread has a priority value between `1` and `10`.

```java
t1.setPriority(10);
System.out.println(t1.getPriority());
```

### Purpose
- The scheduler may consider priority while choosing which thread to execute.
- However, priority is only a hint and not guaranteed behavior.

### Default priority
Default priority is usually `5`.

---

## 9) Daemon Threads
A daemon thread runs in the background and does not block JVM shutdown.

```java
t1.setDaemon(true);
```

### Purpose
- Used for background tasks like monitoring, cleanup, or logging.
- JVM exits when all non-daemon threads finish.

### Important
Daemon threads are stopped automatically when the program ends.

---

## 10) Summary
Thread methods help us control thread execution and behavior in Java.

- `sleep()` → pauses execution temporarily
- `join()` → waits for another thread to complete
- `yield()` → suggests the scheduler to let another thread run, but OS decides finally
- `interrupt()` → interrupts a blocked thread
- `isAlive()` → checks if thread is still active
- `setName()` / `getName()` → manage thread identity
- `setPriority()` / `getPriority()` → assign or read thread priority
- `setDaemon(true)` → background thread

> `yield()` is only a request to the scheduler, not a guarantee.
> The OS/JVM decides whether to honor it or not.

---

## 11) Interview Key Point
"Thread methods like `yield()` are not commands; they are suggestions to the scheduler. The operating system has the final decision."
