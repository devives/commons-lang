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

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Base abstract class containing common functionality for implementing
 * the <tt>try-catch-finally</tt> construct with support for cascading exception handling.
 *
 * Methods of this class are not synchronized and do not assume calls from multiple threads.
 * @param <ONTRY>   type of the main code block (try)
 * @param <ONCATCH> type of the exception handling block (catch)
 * @param <CATCH>   type of the closure for the Catch block
 * @param <FINALLY> type of the closure for the Finally block
 *
 * @since 0.2.0
 */
abstract class TryAbst<ONTRY, ONCATCH, CATCH extends TryAbst.Catch, FINALLY extends TryAbst.Finally> {
    /**
     * Main code block for which error handling is performed.
     */
    private ONTRY onTry;
    /**
     * Error handling block that will be executed only if an exception occurs in the {@link #onTry} block.
     */
    private ONCATCH onCatch;
    /**
     * Finalization block that will be executed after the execution of {@code onTry} and {@code onCatch}, if such a block is defined.
     * Accepts the exception that occurred in the {@link #onTry} block as an argument.
     */
    private FailableRunnable onFinally;

    /**
     * Constructs an instance of the class.
     *
     * @param onTry the <tt>try</tt> code block.
     */
    public TryAbst(ONTRY onTry) {
        this.onTry = Objects.requireNonNull(onTry, "onTry");
    }

    /**
     * Initializes the {@link #onCatch} code block.
     *
     * @param onCatch the <tt>catch</tt> code block.
     * @throws IllegalStateException if the method is called more than once.
     */
    protected final void initCatch(ONCATCH onCatch) {
        if (this.onCatch != null) {
            throw new IllegalStateException("The `onCatch` method was called twice.");
        }
        this.onCatch = Objects.requireNonNull(onCatch, "onCatch");
    }

    /**
     * Initializes the {@link #onFinally} code block.
     *
     * @param onFinally the finally code block.
     * @throws IllegalStateException if the method is called more than once.
     */
    protected final void initFinally(FailableRunnable onFinally) {
        if (this.onFinally != null) {
            throw new IllegalStateException("The `doFinally` method was called twice.");
        }
        this.onFinally = Objects.requireNonNull(onFinally, "onFinally");
    }

    /**
     * Class defines a set of methods available after defining the {@link TryAbst#onCatch} code block.
     */
    protected abstract class Catch {
        /**
         * Initializes the {@link #onFinally} code block.
         *
         * @param onFinally the finally code block.
         * @return builder instance.
         */
        protected abstract FINALLY doFinally(FailableRunnable onFinally);

    }

    /**
     * Class defines a set of methods available after defining the {@link TryAbst#onFinally} code block.
     */
    protected abstract static class Finally {

    }

    /**
     * Initializes the {@code TryAbst#onCatch} code block, which will be executed only if an exception occurs in the {@code TryAbst#onTry} block.
     *
     * @param onCatch the <tt>catch</tt> code block, which accepts the exception thrown from the {@code TryAbst#onTry} block as an argument.
     * @return new instance of {@link TryRunnable.Catch}.
     */
    protected final CATCH onCatch(ONCATCH onCatch, Supplier<CATCH> catchFactory) {
        initCatch(onCatch);
        return catchFactory.get();
    }

    /**
     * Initializes the {@code #onFinally} code block.
     *
     * @param onFinally the finally code block.
     * @return new instance of {@link TryRunnable.Finally}.
     */
    protected final FINALLY doFinally(FailableRunnable onFinally, Supplier<FINALLY> finallyFactory) {
        initFinally(onFinally);
        return finallyFactory.get();
    }

    /**
     * Executes the {@link #onTry} code block.
     *
     * @throws Exception the exception thrown from the {@link #onTry} block.
     */
    protected abstract void invokeTry(ONTRY onTry) throws Exception;

    /**
     * Executes the {@link #onCatch} code block.
     *
     * @param th the exception thrown from the {@link #onTry} block.
     * @throws Throwable the exception thrown from the {@link #onCatch} block.
     */
    protected abstract void invokeCatch(ONCATCH onCatch, Throwable th) throws Throwable;

    /**
     * Executes the {@link #onFinally} code block.
     *
     * @throws Exception the exception thrown from the {@link #onFinally} block.
     */
    protected final void invokeFinally(FailableRunnable onFinally) throws Exception {
       onFinally.run();
    }

    /**
     * Executes the <tt>try-catch-finally</tt> construct:
     * <ol>
     *   <li>Executes the {@link #onTry} block.
     *   <li>If an exception occurs, executes the {@link #onCatch} block.
     *   <li>Executes the finally block regardless of the results of executing {@link #onTry} and {@link #onCatch}.
     * </ol>
     * <p>
     * This method ensures handling of the exception chain arising in each block of the <tt>try-catch-finally</tt> construct by
     * adding suppressed exceptions to {@link Exception#addSuppressed(Throwable)}.
     *
     * @throws RuntimeException at the repeat Try invocation.
     */
    protected final void invokeTryCatchFinally() {
        if (onTry == null) {
            throw new RuntimeException("The repeat Try invocation.");
        }
        Throwable unhandledThrowable = null;
        try {
            try {
                invokeTry(onTry);
            } catch (Throwable tryEx) {
                if (onCatch != null) {
                    try {
                        invokeCatch(onCatch, tryEx);
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
                        invokeFinally(onFinally);
                    } catch (Throwable finallyEx) {
                        if (unhandledThrowable != null) {
                            finallyEx.addSuppressed(unhandledThrowable);
                        }
                        unhandledThrowable = finallyEx;
                    }
                }
            }
        } finally {
            onTry = null;
            onCatch = null;
            onFinally = null;
        }
        if (unhandledThrowable != null) {
            throw ExceptionUtils.asUnchecked(unhandledThrowable);
        }
    }
}