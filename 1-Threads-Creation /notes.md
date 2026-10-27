# Java Multithreading and Concurrency Notes

## 1) What is a Program, Process, and Thread?

### Program
A program is a set of instructions written to perform a task. It is stored on disk and is not executing by itself.

Example: A Java application or a .class file is a program.

### Process
A process is a running instance of a program. Each process has its own memory space and system resources.

Example: Opening two different applications such as Chrome and VS Code creates two separate processes.

### Thread
A thread is the smallest unit of execution within a process. Threads share memory and resources of the process but execute independently.

Example: A web browser may run multiple tabs and tasks inside the same process using threads.

---

## 2) Difference between Process and Thread

| Feature | Process | Thread |
|---|---|---|
| Memory | Separate memory space | Shares same memory space with parent process |
| Creation cost | Heavy | Lightweight |
| Communication | More expensive (IPC required) | Easier and faster |
| Isolation | Strong isolation | Less isolation; one thread can affect others |
| Execution | Independent program instance | Sub-unit of a process |

### Key point
A process is like an independent application. A thread is a lightweight execution path inside that application.

---

## 3) Need of Threads
Threads are needed because:

- To improve performance by doing multiple tasks at the same time.
- To handle independent tasks concurrently.
- To keep the UI responsive while background work runs.
- To reduce CPU idle time and use resources efficiently.
- To perform parallel processing in modern applications.

Example:
- A web app may download data, render UI, and process user input simultaneously using threads.

---

## 4) Memory Space: Common Between Threads vs Separate Space

Threads of the same process share:
- Code segment
- Heap memory
- Static variables
- Shared resources

Each thread still has:
- Its own stack memory
- Its own program counter
- Its own local variables
- Its own thread-specific execution context

### Important
Because threads share heap memory, they can communicate quickly, but they also need synchronization to avoid data races and inconsistent results.

---

## 5) Java Thread Creation and Their Types

In Java, threads are created in two main ways:

### 1. By extending the Thread class
```java
class MyThread extends Thread {
    public void run() {
        System.out.println("Thread is running");
    }
}
```

### 2. By implementing the Runnable interface
```java
class MyRunnable implements Runnable {
    public void run() {
        System.out.println("Thread is running");
    }
}
```

### Which is preferred?
Implementing Runnable is generally preferred because:
- It allows class inheritance from another class.
- It separates task logic from thread management.
- It is more flexible and object-oriented.

---

## 6) start() vs run()

### start()
The `start()` method creates a new thread and schedules it for execution. Java internally calls the `run()` method on the newly created thread.

```java
thread.start();
```

### run()
The `run()` method contains the task to be executed. Calling it directly does not create a new thread. It just executes the method in the current thread.

```java
thread.run();
```

### Why we do not directly call run()?
Because direct `run()` call does not start a new thread; it runs in the same thread. That means it does not provide actual multithreading behavior.

### Example
```java
thread.start(); // correct: creates a new thread
thread.run();   // wrong for multithreading: runs in current thread
```

---

## 7) Can We Start One Thread More Than 1 Time?

No, a thread cannot be started more than once.

If you try to call `start()` on the same thread instance again, Java throws:

```java
java.lang.IllegalThreadStateException
```

This is because the thread lifecycle is one-time. Once a thread is started and completed, it cannot be restarted.

---

## 8) Life Cycle of a Thread

A Java thread has the following states:

1. NEW
   - Thread object is created but not started yet.

2. RUNNABLE
   - Thread is ready to run and is eligible for CPU execution.

3. BLOCKED
   - Thread is waiting to acquire a lock or monitor.

4. WAITING
   - Thread is waiting indefinitely for another thread to perform an action.

5. TIMED_WAITING
   - Thread is waiting for a specified time.

6. TERMINATED
   - Thread has finished execution.

### Thread state example
```java
Thread t = new Thread(() -> System.out.println("Hello"));
System.out.println(t.getState()); // NEW

t.start();
```

The state changes based on execution and scheduling by the JVM.

---

## 9) Summary
- A program is a static set of instructions.
- A process is a running program with its own memory.
- A thread is a lightweight execution path inside a process.
- Threads share memory, making communication easier but requiring synchronization.
- Java supports thread creation using `Thread` and `Runnable`.
- Use `start()` to create a new thread; `run()` alone is just a normal method call.
- One thread instance cannot be started more than once.
- Threads move through various lifecycle states from NEW to TERMINATED.

---

## 10) Important Interview Points
- Threads are lightweight compared to processes.
- Threads in the same process share memory.
- `start()` creates a new call stack and executes `run()` in a new thread.
- `run()` is not asynchronous when called directly.
- Avoid shared data races by using synchronization when multiple threads access same data.
