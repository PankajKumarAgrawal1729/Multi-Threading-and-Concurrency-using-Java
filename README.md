# Multi-Threading and Concurrency using Java

This repository contains Java programs and notes for learning multithreading and concurrency basics.

## Project Goal
The purpose of this project is to understand:
- how threads are created in Java
- how thread methods work
- how thread lifecycle works
- differences between process and thread
- basic concurrency concepts and thread behavior

## Folder Structure

### 1-Threads-Creation
Contains examples demonstrating basic thread creation using:
- extending the `Thread` class
- implementing the `Runnable` interface
- thread names and IDs
- thread lifecycle states

Files:
- `Demo.java`
- `Demo2.java`
- `Demo3.java`
- `Demo4.java`
- `notes.md`

### 2-Thread-Methods
Contains examples for common thread methods such as:
- `sleep()`
- `join()`
- `yield()`
- `interrupt()`
- `isAlive()`
- `setName()` / `getName()`
- `setPriority()` / `getPriority()`
- daemon threads

Files:
- `Demo.java`
- `Demo2.java`
- `Demo3.java`
- `Demo4.java`
- `Demo5.java`
- `Demo6.java`
- `Demo7.java`
- `Demo9.java`
- `notes.md`

### 3-Problem-Of-Multithreading
Contains examples of common problems caused by unsynchronized access to shared data:
- race conditions and non-atomic updates
- visibility of shared values
- ordering and happens-before relationships
- `yield()` and priority as scheduler hints, not guarantees

Files:
- `Demo1.java`
- `Demo2.java`
- `Demo3.java`
- `notes.md`

### 4-Monitor-Locks
Contains examples of Java monitor-based synchronization and locking behavior:
- `synchronized` methods and blocks
- object locks vs class locks
- critical sections and shared resource protection
- why using the wrong lock object breaks synchronization
- how locks work internally through object monitors

Files:
- `Demo1.java`
- `Deemo2.java`
- `Demo3.java`
- `Demo4.java`
- `Demo5.java`
- `Demo6.java`
- `Demo7.java`
- `notes.md`

### 5-Inter-Thread-Communication
Contains producer-consumer examples and thread coordination patterns:
- producer-consumer problem
- busy waiting and why `synchronized` alone is not a communication protocol
- `wait()`, `notify()`, and guarded blocks
- `sleep()` vs `wait()`
- spurious wake-ups and why conditions must be checked in a loop
- how using `notify()` with multiple waiters can prevent progress

Files:
- `Demo1.java`
- `Demo2.java`
- `Demo3.java`
- `notes.md`

### 6-Locks
Contains examples of explicit Java locks and concurrency utilities:
- `ReentrantLock` and the `Lock` interface
- `ReadWriteLock` for concurrent reads and exclusive writes
- `StampedLock` optimistic reads and validation
- semaphores for limiting concurrent access and bounded buffers
- lock conditions with `await()`, `signal()`, and `signalAll()` (covered in notes)

Files:
- `Demo1.java` through `Demo5.java`
- `notes.md`

## Important Concepts Covered
- Program vs Process vs Thread
- Process vs Thread comparison
- Need for threads
- Shared memory vs separate memory
- Java thread creation types
- `start()` vs `run()`
- One thread cannot be started more than once
- Thread life cycle
- Scheduling suggestions like `yield()` and OS control
- Monitor locks and synchronization
- How Java locks work internally using object monitors
- synchronized object lock vs class lock
- when synchronization fails because the lock object is different
- producer-consumer coordination
- busy waiting vs monitor-based waiting
- `wait()` / `notify()` and spurious wake-ups
- explicit `Lock` APIs and their advantages over intrinsic monitors
- pessimistic locking, optimistic reads, and lock downgrading
- semaphore permits and differences between semaphores and locks
- read/write lock starvation considerations and conditions

## How to Run
Compile the Java files using:

```bash
javac Demo.java
```

Or compile all files in a folder:

```bash
javac Demo.java Demo2.java Demo3.java Demo4.java
```

Then run:

```bash
java Demo
```

## Notes
The project also includes study notes in each folder to help understand the theory behind the code examples.

## Status
This project is intended for learning and practice in Java multithreading and concurrency basics.
