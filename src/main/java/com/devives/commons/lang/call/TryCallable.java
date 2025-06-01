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


import com.devives.commons.lang.function.FailableCallable;
import com.devives.commons.lang.function.FailableRunnable;
import com.devives.commons.lang.function.ThrowableFunction1;

/**
 * This class implements cascading exception handling in the <tt>try-catch-finally</tt> construct for methods that return a result.
 * <p>
 * Example usage:
 * <pre>{@code
 * Long result = Try.callable(() -> {
 *   // Main code block.
 *   return 1L;
 * }).onCatch((th) -> {
 *   // Error handling.
 *   return 2L;
 * }).doFinally(() -> {
 *   // Finalization.
 * }).call();
 * }</pre>
 * Replaces:
 * <pre>{@code
 * Long result = null;
 * try {
 *   // Main code block.
 *   result = 1L;
 * } catch (Throwable e){
 *   // Error handling.
 *   result = 2L;
 * } finally {
 *   // Finalization.
 * }
 * }</pre>
 * <p>
 * Methods of this class are not synchronized and do not assume calls from multiple threads.
 *
 * @param <R> the type of the return value of the construct.
 *
 * @see Try#callable(FailableCallable)
 *
 * @since 0.2.0
 */
public final class TryCallable<R> extends TryAbst<
        FailableCallable<R>,
        ThrowableFunction1<Throwable, R, Throwable>,
        TryCallable.Catch,
        TryCallable.Finally> {

    /**
     * The result of executing the {@code TryAbst#onTry} or {@code TryAbst#onCatch} block.
     */
    private R result = null;

    /**
     * Constructor for an instance of {@link TryCallable}.
     *
     * @param callable the <tt>try</tt> code block.
     */
    TryCallable(FailableCallable<R> callable) {
        super(callable);
    }

    /**
     * {@inheritDoc}
     */
    public final class Catch extends TryAbst.Catch {

        /**
         * {@inheritDoc}
         */
        @Override
        public Finally doFinally(FailableRunnable onFinally) {
            return TryCallable.this.doFinally(onFinally);
        }

        /**
         * Executes the <tt>try-catch</tt> construct.
         * <p>
         * The method can be called one time.
         *
         * @return the result of executing {@code TryAbst#onTry} or {@code TryAbst#onCatch}.
         */
        public R call() {
            return TryCallable.this.call();
        }
    }

    /**
     * {@inheritDoc}
     */
    public final class Finally extends TryAbst.Finally {

        /**
         * Executes the <tt>try-catch-finally</tt> construct.
         * <p>
         * The method can be called one time.
         *
         * @return the result of executing {@code TryAbst#onTry} or {@code TryAbst#onCatch}.
         */
        public R call() {
            return TryCallable.this.call();
        }
    }

    /**
     * Executes the <tt>try-catch-finally</tt> construct.
     *
     * @return the result of executing {@code TryAbst#onTry} or {@code TryAbst#onCatch}.
     * @throws Throwable the latest thrown exception.
     */
    private R call() {
        try {
            invokeTryCatchFinally();
            return result;
        } finally {
            // Prevent memory leak.
            result = null;
        }
    }

    /**
     * Initializes the {@code TryAbst#onCatch} code block, which will be executed only if an exception occurs in the {@code TryAbst#onTry} block.
     * <p>
     * The method can be called one time.
     *
     * @param onCatch the <tt>catch</tt> code block, which accepts the exception thrown from the {@code TryAbst#onTry} block as an argument.
     * @return a new instance of {@link TryRunnable.Catch}.
     */
    public Catch onCatch(ThrowableFunction1<Throwable, R, Throwable> onCatch) {
        return super.onCatch(onCatch, Catch::new);
    }

    /**
     * Initializes the {@code TryAbst#onFinally} code block.
     * <p>
     * The method can be called one time.
     *
     * @param onFinally the <tt>finally</tt> code block.
     * @return a new instance of {@link TryRunnable.Finally}.
     */
    public Finally doFinally(FailableRunnable onFinally) {
        return super.doFinally(onFinally, Finally::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void invokeTry(FailableCallable<R> onTry) throws Exception {
        result = onTry.call();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void invokeCatch(ThrowableFunction1<Throwable, R, Throwable> onCatch, Throwable th) throws Throwable {
        result = onCatch.apply(th);
    }
}