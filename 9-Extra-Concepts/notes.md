# Extra Java Concurrency Concepts

## 1) `submit()` Overloads

`ExecutorService` has three `submit` overloads:

```java
<T> Future<T> submit(Callable<T> task)
Future<?> submit(Runnable task)
<T> Future<T> submit(Runnable task, T result)
```

- A `Callable<T>` produces a result, which is returned by `Future.get()`.
- A plain `Runnable` has no result, so its future returns `null` after successful completion.
- `Runnable` plus a supplied result returns that supplied result after the runnable completes.

All overloads capture task failures in the returned future. Call `get()` to observe completion or failure.

## 2) `Future<V>` and Its Methods

A `Future<V>` represents a pending or completed result. Its core methods are:
- `cancel(boolean mayInterruptIfRunning)`: request cancellation; interruption is cooperative.
- `isCancelled()`: whether cancellation succeeded.
- `isDone()`: whether the task completed, failed, or was cancelled.
- `get()`: wait for completion and return the value; throws `ExecutionException` for task failure and `CancellationException` if cancelled.
- `get(long timeout, TimeUnit unit)`: timed wait; may throw `TimeoutException`.

Since Java 19, `Future` also has:
- `state()`: reports `RUNNING`, `SUCCESS`, `FAILED`, or `CANCELLED`.
- `resultNow()`: retrieve a successful result without waiting; throws if not in the successful state.
- `exceptionNow()`: retrieve a failure without waiting; throws if not in the failed state.

`InterruptedException` from `get()` means the waiting thread was interrupted; it does not mean the task itself failed. Restore the interrupt flag or propagate the exception when appropriate.

## 3) Limitations of `Future`

A basic `Future` is useful for one result, but:
- `get()` blocks the caller.
- It has no built-in way to chain a next computation or combine multiple results.
- It has no completion callback.
- Cancellation is only a request and may not stop a task that ignores interruption.
- Errors are not observed unless the future is inspected, usually through `get()` or `exceptionNow()`.

Use `CompletableFuture` when asynchronous composition, callbacks, combining tasks, or declarative error recovery is needed.

## 4) `CompletableFuture`

`CompletableFuture<T>` implements both `Future<T>` and `CompletionStage<T>`. It can be manually completed or used to build asynchronous stages.

Creation and completion:
- `completedFuture(value)`, `supplyAsync(supplier)`, `runAsync(runnable)`
- `complete(value)`, `completeExceptionally(error)`, `cancel(...)`
- `get()` blocks and wraps task failures in `ExecutionException`; `join()` blocks and wraps them in `CompletionException`.

By default, async methods without an explicit executor use `ForkJoinPool.commonPool()`. Prefer passing an executor when you need explicit resource or isolation control.

## 5) Chaining and Combining

Common stage operations:
- `thenApply(fn)`: transform a result synchronously in a completion thread.
- `thenApplyAsync(fn[, executor])`: transform asynchronously.
- `thenAccept(consumer)`: consume a result without returning a value.
- `thenRun(action)`: run an action after completion without receiving its value.
- `thenCompose(fn)`: flatten a stage that itself returns a `CompletionStage`; useful for dependent asynchronous work.
- `thenCombine(other, fn)`: combine results from independent stages.
- `allOf(...)` / `anyOf(...)`: wait for all / any of several futures.
- `exceptionally(fn)`, `handle(fn)`, `whenComplete(action)`: recover, transform success/failure, or observe completion.

A chain starts when its source stage is completed. Avoid calling `join()` or `get()` inside a stage when the next operation can be expressed by chaining; blocking can waste workers or cause thread starvation.

## 6) `ForkJoinPool`

`ForkJoinPool` is designed for divide-and-conquer tasks. A task splits into smaller subtasks, computes or forks branches, then combines their results.

- Extend `RecursiveTask<V>` when computation returns a value.
- Extend `RecursiveAction` when it does not return a value.
- `fork()` schedules a subtask, `join()` waits for its result, and `compute()` performs work.
- Work-stealing lets an idle worker take tasks from another worker's deque.
- Choose a sufficiently large base case; splitting tiny tasks adds overhead.

The common pool is shared process-wide. Use a dedicated pool when isolation or different parallelism is important. Fork/join is best for CPU-bound work; blocking I/O can reduce pool effectiveness unless managed appropriately.

## 7) `ThreadLocal<T>`

A `ThreadLocal<T>` gives each thread its own independently stored value. It is not a way to share or synchronize one value among threads.

```java
ThreadLocal<String> context = ThreadLocal.withInitial(() -> "unset");
context.set("request-123");
String value = context.get();
context.remove();
```

A pooled worker can serve many unrelated tasks, and its thread-local values remain between tasks. Always call `remove()` in a `finally` block when the value is task/request scoped, or data may leak into later work. `ThreadLocal` also does not automatically follow a logical operation across asynchronous stages that may run on different threads.

## 8) Virtual Threads

Virtual threads are lightweight Java threads managed by the JVM. They became a permanent feature in Java 21.

- `Thread.startVirtualThread(runnable)` starts a virtual thread.
- `Thread.ofVirtual().name(...).start(runnable)` builds and starts one with options.
- `Executors.newVirtualThreadPerTaskExecutor()` creates a virtual thread for each submitted task; close/shutdown the executor to wait for submitted work and release it.

Virtual threads are especially useful for large numbers of tasks that spend time blocked on supported I/O. They do not make CPU-bound work faster than available processors, and should not be pooled like platform threads. Use semaphores or other limits to protect scarce downstream resources. Thread-local values work, but per-thread state can have memory costs at very large virtual-thread counts; keep task context scoped and clean up where needed.

## 9) Demo Guide

- `Demo1.java`: combine independent `CompletableFuture` results.
- `Demo2.java`: recursive array sum with `ForkJoinPool`.
- `Demo3.java`: virtual-thread-per-task executor.
- `Demo4.java`: `ThreadLocal` isolation, worker reuse, and cleanup.
- `Demo5.java`: `submit()` overloads, blocking `Future` calls, and a `CompletableFuture` chain.
