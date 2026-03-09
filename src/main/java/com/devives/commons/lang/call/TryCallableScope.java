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
package com.devives.commons.lang.call;


import com.devives.commons.lang.ExceptionUtils;
import com.devives.commons.lang.function.FailableCallable;
import com.devives.commons.lang.function.FailableRunnable;
import com.devives.commons.lang.function.ThrowableFunction1;

import java.util.Objects;

/**
 * This class implements cascading exception handling in the <code>try-catch-finally</code> construct for methods that return a result.
 * <p>
 * Methods of this class are not synchronized and do not assume calls from multiple threads.
 *
 * @param <R> the type of the return value of the construct.
 * @see Try#callable(FailableCallable)
 * @since 0.2.0
 */
final class TryCallableScope<R> implements TryCallable<R>, TryCallable.Catch<R>, TryCallable.Finally<R> {
    /**
     * Main code block for which error handling is performed.
     */
    private FailableCallable<R> onTry;
    /**
     * Error handling block that will be executed only if an exception occurs in the {@link #onTry} block.
     */
    private ThrowableFunction1<Throwable, R, Throwable> onCatch;
    /**
     * Finalization block that will be executed after the execution of {@code onTry} and {@code onCatch}, if such a block is defined.
     * Accepts the exception that occurred in the {@link #onTry} block as an argument.
     */
    private FailableRunnable onFinally;

    /**
     * Constructor for an instance of {@link TryCallableScope}.
     *
     * @param callable the <code>try</code> code block.
     */
    TryCallableScope(FailableCallable<R> callable) {
        this.onTry = Objects.requireNonNull(callable, "callable");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Catch<R> onCatch(ThrowableFunction1<Throwable, R, Throwable> onCatch) {
        if (this.onCatch != null) {
            throw new IllegalStateException("The `onCatch` method was called twice.");
        }
        this.onCatch = Objects.requireNonNull(onCatch, "onCatch");
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Finally<R> doFinally(FailableRunnable onFinally) {
        if (this.onFinally != null) {
            throw new IllegalStateException("The `doFinally` method was called twice.");
        }
        this.onFinally = Objects.requireNonNull(onFinally, "onFinally");
        return this;
    }

    /**
     * Executes the {@code try-catch-finally} construct.
     *
     * @return the result of executing {@code TryAbst#onTry} or {@code TryAbst#onCatch}.
     * @throws Throwable the latest thrown exception.
     */
    public R call() {
        if (onTry == null) {
            throw new RuntimeException("The repeat Try invocation.");
        }
        try {
            return invokeTryCatchFinally();
        } finally {
            // Prevent memory leak.
            onTry = null;
            onCatch = null;
            onFinally = null;
        }
    }

    /**
     * Executes the {@code try-catch-finally} construct:
     * <ol>
     *   <li>Executes the {@link #onTry} block.
     *   <li>If an exception occurs, executes the {@link #onCatch} block.
     *   <li>Executes the finally block regardless of the results of executing {@link #onTry} and {@link #onCatch}.
     * </ol>
     * <p>
     * This method ensures handling of the exception chain arising in each block of the <code>try-catch-finally</code> construct by
     * adding suppressed exceptions to {@link Exception#addSuppressed(Throwable)}.
     *
     * @throws RuntimeException at the repeat Try invocation.
     */
    private R invokeTryCatchFinally() {
        R result = null;
        Throwable unhandledThrowable = null;
        try {
            result = onTry.call();
        } catch (Throwable tryEx) {
            if (onCatch != null) {
                try {
                    result = onCatch.apply(tryEx);
                } catch (Throwable catchEx) {
                    if (catchEx != tryEx) {
                        catchEx.addSuppressed(tryEx);
                    }
                    unhandledThrowable = catchEx;
                }
            } else {
                unhandledThrowable = tryEx;
            }
        } finally {
            if (onFinally != null) {
                try {
                    onFinally.run();
                } catch (Throwable finallyEx) {
                    if (unhandledThrowable != null) {
                        finallyEx.addSuppressed(unhandledThrowable);
                    }
                    unhandledThrowable = finallyEx;
                }
            }
        }
        if (unhandledThrowable != null) {
            throw ExceptionUtils.asUnchecked(unhandledThrowable);
        }
        return result;
    }
}