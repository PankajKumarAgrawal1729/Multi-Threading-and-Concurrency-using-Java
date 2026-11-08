# Java Executor Framework

## 1) Executor Framework and Hierarchy

The Executor Framework separates submitting work from creating and managing threads. A client submits a `Runnable` or `Callable`; an executor decides when and on which worker thread to run it.

A simplified type hierarchy:

```text
Executor (interface)
└── ExecutorService (interface, also AutoCloseable)
    ├── ScheduledExecutorService (interface)
    └── AbstractExecutorService (abstract class)
        ├── ThreadPoolExecutor
        │   └── ScheduledThreadPoolExecutor implements ScheduledExecutorService
        └── ForkJoinPool
```

The `Executor` interface declares `execute(Runnable)`. `ExecutorService` adds task submission, bulk operations, lifecycle management, and (since Java 19) `close()`. `Executors` provides factory methods, while `ThreadPoolExecutor` is the configurable general-purpose implementation.

## 2) `Runnable` and `Callable`

- `Runnable.run()` takes no arguments and returns no result. It cannot declare checked exceptions.
- `Callable<V>.call()` returns a value of type `V` and may throw checked exceptions.
- `execute(Runnable)` submits a fire-and-forget task and returns no `Future`.
- `submit(...)` accepts a `Runnable` or `Callable` and returns a `Future`.

An overload `submit(Runnable task, T result)` completes its future with the supplied result after the runnable finishes.

## 3) `Future<V>`

A `Future<V>` represents the result or completion of a submitted task:
- `get()` waits for completion and returns the result; it may throw `ExecutionException` if the task failed.
- `get(timeout, unit)` waits up to the timeout and may throw `TimeoutException`.
- `cancel(mayInterruptIfRunning)` attempts cancellation.
- `isCancelled()` and `isDone()` report state.

Calling `get()` blocks the caller until completion. Cancellation is cooperative; interruption does not forcibly stop code that ignores interruption.

## 4) `ExecutorService` Methods

The `ExecutorService` interface provides these methods (plus `execute` inherited from `Executor`):

### Task submission and bulk execution
- `<T> Future<T> submit(Callable<T> task)`
- `Future<?> submit(Runnable task)`
- `<T> Future<T> submit(Runnable task, T result)`
- `<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)`
- `<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)`
- `<T> T invokeAny(Collection<? extends Callable<T>> tasks)`
- `<T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)`

`invokeAll` waits for all tasks (or the timeout); inspect each returned future to observe task exceptions. `invokeAny` returns one successfully completed result and cancels unfinished tasks; if all fail it throws `ExecutionException`.

### Lifecycle
- `void shutdown()` stops accepting new work but allows already submitted tasks to finish.
- `List<Runnable> shutdownNow()` requests interruption of running tasks and returns tasks that never started; it does not guarantee that running tasks stop.
- `boolean isShutdown()` reports whether shutdown was requested.
- `boolean isTerminated()` reports whether shutdown is complete and all tasks have finished.
- `boolean awaitTermination(long timeout, TimeUnit unit)` waits for termination up to the timeout.
- `void close()` (Java 19+) shuts down and waits for termination; try-with-resources can manage this lifecycle.

## 5) `execute`, `submit`, Exceptions, and `ThreadFactory`

`execute()` does not return a `Future`. If an executed task throws an unchecked exception or error, it escapes the task on the worker thread and is handled by that thread's uncaught-exception handler; the submitting caller does not receive it directly. The pool may replace a worker that exits abruptly.

`submit()` wraps the task in a future. A failure is stored and is observed by calling `Future.get()`, which throws `ExecutionException` with the original failure as its cause. If code never checks the future, the failure may appear to have been lost.

A `ThreadFactory` creates the executor's worker threads. A custom factory can set useful names, daemon status, priority, and an `UncaughtExceptionHandler`. It should return a usable thread; returning `null` prevents the executor from creating that worker.

## 6) `ThreadPoolExecutor`

The main constructor accepts:

```text
corePoolSize, maximumPoolSize, keepAliveTime, timeUnit,
workQueue, threadFactory, rejectedExecutionHandler
```

Task admission order is important:
1. If fewer than `corePoolSize` workers exist, create a worker.
2. Otherwise, try to enqueue the task.
3. If the queue is full, grow the pool up to `maximumPoolSize`.
4. If the pool is at maximum and the queue is full, invoke the rejection handler.

Common methods:
- `execute()` and inherited `submit()` / bulk operations
- `shutdown()`, `shutdownNow()`, `isShutdown()`, `isTerminated()`, `awaitTermination()`
- `setCorePoolSize()`, `setMaximumPoolSize()`, `setKeepAliveTime()`
- `allowCoreThreadTimeOut()`, `prestartCoreThread()`, `prestartAllCoreThreads()`
- `setThreadFactory()`, `setRejectedExecutionHandler()`
- monitoring: `getPoolSize()`, `getActiveCount()`, `getLargestPoolSize()`, `getTaskCount()`, `getCompletedTaskCount()`, `isTerminating()`
- queue management: `getQueue()`, `remove(task)`, `purge()`

Use queue access mainly for monitoring/management, not to coordinate application work. Always shut down executors you create.

## 7) Blocking Queue Types

Common `BlockingQueue` choices for a thread pool:
- `ArrayBlockingQueue`: bounded FIFO queue backed by an array; capacity is required.
- `LinkedBlockingQueue`: optionally bounded linked FIFO queue; without a capacity it is effectively unbounded.
- `SynchronousQueue`: no storage; each handoff requires a waiting consumer/worker.
- `PriorityBlockingQueue`: unbounded priority-ordered queue; tasks need comparable ordering or a comparator.
- `DelayQueue`: unbounded queue where an element is available only after its delay expires.

Queue choice changes pool behavior. For example, an unbounded queue usually means a `ThreadPoolExecutor` queues after core workers are busy and may never grow toward its maximum size.

## 8) Cached Thread Pool

`Executors.newCachedThreadPool()` creates a pool that reuses idle workers and creates additional threads as needed. It behaves approximately like a `ThreadPoolExecutor` with zero core threads, a very large maximum, a `SynchronousQueue`, and a short keep-alive for idle threads.

It is useful for many short-lived asynchronous tasks when bursts are expected and tasks do not block for long. Because it can create a very large number of threads, it can exhaust memory or other system resources under sustained load. Prefer an explicitly bounded `ThreadPoolExecutor` when concurrency must be limited.

The returned type is `ExecutorService`; use its `execute`, `submit`, bulk methods, and shutdown lifecycle methods.

## 9) Single-Thread Executor

`Executors.newSingleThreadExecutor()` uses one worker and a queue to execute tasks sequentially. It is useful when tasks must not overlap and should be processed in submission order. If its worker dies after an uncaught task failure, the executor creates a replacement so queued work can continue.

The returned type is `ExecutorService`. It exposes the standard submission and lifecycle methods, but not the internal `ThreadPoolExecutor` configuration. `newFixedThreadPool(1)` also has one worker, but returns a configurable implementation that can be cast/changed, so the single-thread factory is preferable when the single-worker guarantee should remain encapsulated.

## 10) Scheduled Thread Pool

`Executors.newScheduledThreadPool(n)` creates a `ScheduledExecutorService` backed by a `ScheduledThreadPoolExecutor`.

Important scheduling methods:
- `schedule(Runnable, delay, unit)` runs once after a delay.
- `<V> schedule(Callable<V>, delay, unit)` runs once and returns a result through `ScheduledFuture<V>`.
- `scheduleAtFixedRate(task, initialDelay, period, unit)` targets a fixed start-to-start period. If a run takes longer than the period, later runs start late; one periodic execution of the same task does not overlap itself.
- `scheduleWithFixedDelay(task, initialDelay, delay, unit)` waits the given delay after one run completes before starting the next.

Both periodic methods return a `ScheduledFuture`; cancel it to stop future runs. Handle task exceptions: an uncaught exception suppresses later executions of that periodic task.

## 11) Rejection Policies

A rejection policy runs when a `ThreadPoolExecutor` cannot accept a task, commonly because it is shut down or both maximum workers and the work queue are full.

- **AbortPolicy** (default): throws `RejectedExecutionException` to the submitter.
- **DiscardPolicy**: silently drops the newly rejected task. Use only when dropping work is explicitly acceptable.
- **DiscardOldestPolicy**: removes the oldest queued task and retries submitting the new task. This loses the queued task and should be used only when that tradeoff is intentional.
- **CallerRunsPolicy** (also built in): runs the task in the submitting thread while the executor is active, providing backpressure; after shutdown it discards the task.

Rejection handling is part of the application's reliability policy. Do not silently discard work that must be completed.

## 12) Demo Guide

- `Demo1.java` through `Demo4.java`: fixed pool, futures, task exceptions, and custom `ThreadPoolExecutor`.
- `Demo5.java`: custom `ThreadFactory` and exception behavior of `execute()` vs `submit()`.
- `Demo6.java`: cached and single-thread executor behavior.
- `Demo7.java`: one-shot and periodic scheduled tasks.
- `Demo8.java`: abort, discard, and discard-oldest rejection policies.
