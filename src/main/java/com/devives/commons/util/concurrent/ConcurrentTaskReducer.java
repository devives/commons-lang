/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.devives.commons.util.concurrent;

import com.devives.commons.lang.ExceptionUtils;
import com.devives.commons.lang.function.*;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Reduces the number of concurrent tasks (method invocations).
 * <p>
 * This class prevents simultaneous execution of the same task (method invocation with equal arguments)
 * by multiple threads. If several threads call a method with the same arguments at the same time, only one
 * thread performs the work, while the others wait until it finishes.
 * <p>
 * Effectively, code execution is synchronized across threads by the provided key.
 * <p><strong>Examples.</strong></p>
 * <ol>
 *     <li>Preparing/compiling report templates.
 *     <li>Loading query metadata.
 * </ol>
 *
 * @param <K> task key type.
 * @param <R> task result type.
 */
public class ConcurrentTaskReducer<K, R> {

    /**
     * Task map that synchronizes access to task execution synchronization objects.
     */
    private final ConcurrentMap<K, Task<R>> taskMap_ = new ConcurrentHashMap<>();

    /**
     * Task.
     * <p>
     * The object is created for a unique task key.
     *
     * @param <R> task execution result type.
     */
    private static final class Task<R> extends CompletableFuture<R> {
        private final AtomicBoolean started_ = new AtomicBoolean();
        private volatile long runningThreadId = -1;

        /**
         * Marks the task as started.
         *
         * @return {@code true} if the current thread is the first to start executing the task.
         */
        public boolean markStarted() {
            return started_.compareAndSet(false, true);
        }
    }

    /**
     * Creates a new task object and invokes the provided callable, or obtains a task object previously created
     * by another thread and waits for that thread to finish execution.
     *
     * @param key unique key.
     * @param callable callable method (task) to execute.
     * @return callable execution result.
     */
    public R call(final K key, final Callable<R> callable) {
        Objects.requireNonNull(key, "Argument `key` value can not be `null`.");
        Objects.requireNonNull(callable, "Argument `callable` value can not be `null`.");
        try {
            final Task<R> task = doCall(key, callable);
            ensureCanWait(task, key);
            return task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw ExceptionUtils.asUnchecked(e);
        } catch (ExecutionException e) {
            throw ExceptionUtils.asUnchecked(e.getCause());
        }
    }

    /**
     * Creates a new task object and invokes the provided callable, or obtains a task object previously created
     * by another thread and waits for that thread to finish execution.
     *
     * @param key unique key.
     * @param callable callable method (task) to execute.
     * @param timeout timeout for waiting for task completion. Applies only to threads waiting for completion,
     *                not to the thread executing the task.
     * @param unit timeout unit.
     * @return callable execution result.
     * @throws TimeoutException when the waiting timeout is exceeded.
     */
    public R call(final K key, final Callable<R> callable, int timeout, TimeUnit unit) throws TimeoutException {
        Objects.requireNonNull(key, "Argument `key` value can not be `null`.");
        Objects.requireNonNull(callable, "Argument `callable` value can not be `null`.");
        Objects.requireNonNull(unit, "Argument `unit` value can not be `null`.");
        try {
            final Task<R> task = doCall(key, callable);
            ensureCanWait(task, key);
            return task.get(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw ExceptionUtils.asUnchecked(e);
        } catch (ExecutionException e) {
            throw ExceptionUtils.asUnchecked(e.getCause());
        }
    }

    private Task<R> doCall(final K key, final Callable<R> callable) {
        final Task<R> task = taskMap_.computeIfAbsent(key, k -> new Task<>());
        if (task.markStarted()) {
            // The first thread that acquires the task executes it.
            task.runningThreadId = Thread.currentThread().getId();
            try {
                try {
                    task.complete(callable.call());
                } catch (Error e) {
                    task.completeExceptionally(e);
                    throw (Error) e;
                } catch (Throwable e) {
                    task.completeExceptionally(e);
                }
            } finally {
                taskMap_.remove(key, task);
                task.runningThreadId = -1;
            }
        }
        return task;
    }

    private void ensureCanWait(Task<R> task, K key) {
        if (!task.isDone() && task.runningThreadId == Thread.currentThread().getId()) {
            throw new IllegalStateException("Reentrant call with same key is not allowed: `" + key + "`.");
        }
    }

    /**
     * Creates a new method instance with a limit on the number of concurrent executions.
     * <p>
     * A decorator method is created for the provided callable and owns an instance of
     * {@link ConcurrentTaskReducer}.
     *
     * @param keyFormatter task key formatter.
     * @param callable callable method (task) to execute.
     * @param <R> callable result type.
     * @return new method instance with a limit on the number of concurrent executions.
     */
    public static <R> ReducedCallable<R> forCallable(
            Function<String> keyFormatter,
            ThrowableFunction<R, Exception> callable) {
        return new ReducedCallableImpl<>(keyFormatter, callable);
    }

    /**
     * Creates a new method instance with a limit on the number of concurrent executions.
     * <p>
     * A decorator method is created for the provided callable and owns an instance of
     * {@link ConcurrentTaskReducer}.
     *
     * @param keyFormatter task key formatter.
     * @param callable callable method (task) to execute.
     * @param <A1> callable argument type.
     * @param <R> callable result type.
     * @return new method instance with a limit on the number of concurrent executions.
     */
    public static <A1, R> ReducedCallable1<A1, R> forCallable(
            Function1<A1, String> keyFormatter,
            ThrowableFunction1<A1, R, Exception> callable) {
        return new ReducedCallable1Impl<>(keyFormatter, callable);
    }

    /**
     * Creates a new method instance with a limit on the number of concurrent executions.
     * <p>
     * A decorator method is created for the provided callable and owns an instance of
     * {@link ConcurrentTaskReducer}.
     *
     * @param keyFormatter task key formatter.
     * @param callable callable method (task) to execute.
     * @param <A1> first callable argument type.
     * @param <A2> second callable argument type.
     * @param <R> callable result type.
     * @return new method instance with a limit on the number of concurrent executions.
     */
    public static <A1, A2, R> ReducedCallable2<A1, A2, R> forCallable(
            Function2<A1, A2, String> keyFormatter,
            ThrowableFunction2<A1, A2, R, Exception> callable) {
        return new ReducedCallable2Impl<>(keyFormatter, callable);
    }

    private static abstract class AbstractReducedCallable<K, R, FORMATTER, CALLABLE> {
        protected final ConcurrentTaskReducer<K, R> taskReducer_ = new ConcurrentTaskReducer<>();
        protected final FORMATTER keyFormatter_;
        protected final CALLABLE callable_;

        public AbstractReducedCallable(FORMATTER keyFormatter, CALLABLE callable) {
            keyFormatter_ = Objects.requireNonNull(keyFormatter, "Argument `keyFormatter` value can not be `null`.");
            callable_ = Objects.requireNonNull(callable, "Argument `callable` value can not be `null`.");
        }
    }

    private static final class ReducedCallableImpl<R> extends AbstractReducedCallable<String, R, Function<String>, ThrowableFunction<R, Exception>> implements ReducedCallable<R> {

        public ReducedCallableImpl(Function<String> keyFormatter,
                                   ThrowableFunction<R, Exception> callable) {
            super(keyFormatter, callable);
        }

        @Override
        public R apply() {
            final String key = Objects.requireNonNull(keyFormatter_.apply(), "Key formatter must return non `null` value.");
            return taskReducer_.call(key, callable_::apply);
        }

        @Override
        public R apply(int timeout, TimeUnit unit) throws TimeoutException {
            final String key = Objects.requireNonNull(keyFormatter_.apply(), "Key formatter must return non `null` value.");
            return taskReducer_.call(key, callable_::apply, timeout, unit);
        }
    }

    private static final class ReducedCallable1Impl<A1, R>
            extends AbstractReducedCallable<String, R, Function1<A1, String>, ThrowableFunction1<A1, R, Exception>>
            implements ReducedCallable1<A1, R> {

        public ReducedCallable1Impl(Function1<A1, String> keyFormatter,
                                    ThrowableFunction1<A1, R, Exception> callable) {
            super(keyFormatter, callable);
        }

        @Override
        public R apply(A1 arg1) {
            final String key = Objects.requireNonNull(keyFormatter_.apply(arg1), "Key formatter must return non `null` value.");
            return taskReducer_.call(key, () -> callable_.apply(arg1));
        }

        @Override
        public R apply(A1 arg1, int timeout, TimeUnit unit) throws TimeoutException {
            final String key = Objects.requireNonNull(keyFormatter_.apply(arg1), "Key formatter must return non `null` value.");
            return taskReducer_.call(key, () -> callable_.apply(arg1), timeout, unit);
        }
    }

    private static final class ReducedCallable2Impl<A1, A2, R>
            extends AbstractReducedCallable<String, R, Function2<A1, A2, String>, ThrowableFunction2<A1, A2, R, Exception>>
            implements ReducedCallable2<A1, A2, R> {

        public ReducedCallable2Impl(Function2<A1, A2, String> keyFormatter, ThrowableFunction2<A1, A2, R, Exception> a1A2RExceptionFunction12) {
            super(keyFormatter, a1A2RExceptionFunction12);
        }

        @Override
        public R apply(A1 arg1, A2 arg2) {
            final String key = Objects.requireNonNull(keyFormatter_.apply(arg1, arg2), "Key formatter must return non `null` value.");
            return taskReducer_.call(key, () -> callable_.apply(arg1, arg2));
        }

        @Override
        public R apply(A1 arg1, A2 arg2, int timeout, TimeUnit unit) throws TimeoutException {
            final String key = Objects.requireNonNull(keyFormatter_.apply(arg1, arg2), "Key formatter must return non `null` value.");
            return taskReducer_.call(key, () -> callable_.apply(arg1, arg2), timeout, unit);
        }
    }

    /**
     * Method with a limit on the number of concurrent executions by multiple threads.
     *
     * @param <R> method result type.
     */
    public interface ReducedCallable<R> {

        /**
         * Executes the method.
         *
         * @return method execution result.
         */
        R apply();

        /**
         * Executes the method with a waiting timeout.
         *
         * @param timeout timeout for waiting for task completion. Applies only to threads waiting for completion,
         *                not to the thread executing the task.
         * @param unit timeout unit.
         * @return method execution result.
         * @throws TimeoutException when the waiting timeout is exceeded.
         */
        R apply(int timeout, TimeUnit unit) throws TimeoutException;
    }

    /**
     * Method with a limit on the number of concurrent executions by multiple threads.
     *
     * @param <R> method result type.
     */
    public interface ReducedCallable1<A1, R> {
        /**
         * Executes the method.
         *
         * @param arg1 argument 1.
         * @return method execution result.
         */
        R apply(A1 arg1);

        /**
         * Executes the method with a waiting timeout.
         *
         * @param arg1 argument 1.
         * @param timeout timeout for waiting for task completion. Applies only to threads waiting for completion,
         *                not to the thread executing the task.
         * @param unit timeout unit.
         * @return method execution result.
         * @throws TimeoutException when the waiting timeout is exceeded.
         */
        R apply(A1 arg1, int timeout, TimeUnit unit) throws TimeoutException;
    }

    /**
     * Method with a limit on the number of concurrent executions by multiple threads.
     *
     * @param <R> method result type.
     */
    public interface ReducedCallable2<A1, A2, R> {
        /**
         * Executes the method.
         *
         * @param arg1 argument 1.
         * @param arg2 argument 2.
         * @return method execution result.
         */
        R apply(A1 arg1, A2 arg2);

        /**
         * Executes the method with a waiting timeout.
         *
         * @param arg1 argument 1.
         * @param arg2 argument 2.
         * @param timeout timeout for waiting for task completion. Applies only to threads waiting for completion,
         *                not to the thread executing the task.
         * @param unit timeout unit.
         * @return method execution result.
         * @throws TimeoutException when the waiting timeout is exceeded.
         */
        R apply(A1 arg1, A2 arg2, int timeout, TimeUnit unit) throws TimeoutException;
    }

}
