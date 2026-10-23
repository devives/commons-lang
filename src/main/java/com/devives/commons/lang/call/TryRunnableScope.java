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
import com.devives.commons.lang.function.FailableRunnable;
import com.devives.commons.lang.function.ThrowableProcedure1;

import java.util.Objects;

/**
 * This class implements cascading exception handling in the {@code try-catch-finally} construct for methods that do not return a result.
 * <p>
 * Methods of this class are not synchronized and do not assume calls from multiple threads.
 * @see Try#runnable(FailableRunnable)
 */
final class TryRunnableScope extends TryScope implements TryRunnable, TryRunnable.Catch, TryRunnable.Finally {
    /**
     * Main code block for which error handling is performed.
     */
    private FailableRunnable onTry;
    /**
     * Error handling block that will be executed only if an exception occurs in the {@link #onTry} block.
     */
    private ThrowableProcedure1<Throwable, Throwable> onCatch;
    /**
     * Finalization block that will be executed after the execution of {@code onTry} and {@code onCatch}, if such a block is defined.
     * Accepts the exception that occurred in the {@link #onTry} block as an argument.
     */
    private FailableRunnable onFinally;

    /**
     * Constructor for an instance of {@link TryRunnableScope}.
     *
     * @param runnable the {@code try} code block.
     */
    TryRunnableScope(FailableRunnable runnable) {
        this.onTry = Objects.requireNonNull(runnable, "callable");
    }

    /**
     * {@inheritDoc}
     */
    public Catch onCatch(ThrowableProcedure1<Throwable, Throwable> onCatch) {
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
    public Finally doFinally(FailableRunnable onFinally) {
        if (this.onFinally != null) {
            throw new IllegalStateException("The `doFinally` method was called twice.");
        }
        this.onFinally = Objects.requireNonNull(onFinally, "onFinally");
        return this;
    }

    /**
     * Executes the {@code try-catch-finally} construct.
     *
     * @throws Throwable the primary exception: thrown from {@code onCatch}, or from {@code onTry} if no
     *                   {@code onCatch} is defined, or from {@code onFinally} if there is no other exception.
     */
    public void run() {
        if (onTry == null) {
            throw new RuntimeException("The repeat Try invocation.");
        }
        try {
            invokeTryCatchFinally();
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
     * Exceptions are chained with {@link Throwable#addSuppressed(Throwable)} as follows:
     * <ul>
     *   <li>an exception from {@link #onCatch} becomes primary. The exception from {@link #onTry} is added to its
     *   suppressed exceptions unless the two are already linked through the cause or suppressed chains
     *   (rethrow, wrapping as a cause, unwrapping);
     *   <li>an exception from {@link #onFinally} is added to the suppressed exceptions of the primary one,
     *   as in try-with-resources, and becomes primary only if there is no other exception.
     * </ul>
     *
     * @throws RuntimeException at the repeat Try invocation.
     */
    private void invokeTryCatchFinally() {
        Throwable unhandledThrowable = null;
        try {
            onTry.run();
        } catch (Throwable tryEx) {
            if (onCatch != null) {
                try {
                    onCatch.accept(tryEx);
                } catch (Throwable catchEx) {
                    unhandledThrowable = catchEx;
                    if (!isLinked(catchEx, tryEx)) {
                        catchEx.addSuppressed(tryEx);
                    }
                }
            } else {
                unhandledThrowable = tryEx;
            }
        } finally {
            if (onFinally != null) {
                try {
                    onFinally.run();
                } catch (Throwable finallyEx) {
                    if (unhandledThrowable != null && unhandledThrowable != finallyEx) {
                        unhandledThrowable.addSuppressed(finallyEx);
                    } else {
                        unhandledThrowable = finallyEx;
                    }
                }
            }
        }
        if (unhandledThrowable != null) {
            throw ExceptionUtils.asUnchecked(unhandledThrowable);
        }
    }
}