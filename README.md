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
