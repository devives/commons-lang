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

import com.devives.commons.lang.function.FailableRunnable;
import com.devives.commons.lang.function.ThrowableProcedure1;

/**
 * This class implements cascading exception handling in the <tt>try-catch-finally</tt> construct for methods that do not
 * return a result.
 * <p>
 * Example usage:
 * <pre>{@code
 * Try.runnable(() -> {
 *   // Main code block.
 * }).onCatch((th) -> {
 *   // Error handling.
 * }).doFinally(() -> {
 *   // Finalization.
 * }).run();
 * }</pre>
 * Replaces:
 * <pre>{@code
 * try {
 *   // Main code block.
 * } catch (Throwable e){
 *   // Error handling.
 * } finally {
 *   // Finalization.
 * }
 * }</pre>
 * <p>
 * Methods of this class are not synchronized and do not assume calls from multiple threads.
 *
 * @see Try#runnable(FailableRunnable)
 *
 * @since 0.2.0
 */
public final class TryRunnable extends TryAbst<
        FailableRunnable,
        ThrowableProcedure1<Throwable, Throwable>,
        TryRunnable.Catch,
        TryRunnable.Finally> {

    /**
     * Constructor for an instance of {@link TryRunnable}.
     *
     * @param runnable the <tt>try</tt> code block.
     */
    TryRunnable(FailableRunnable runnable) {
        super(runnable);
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
            return TryRunnable.this.doFinally(onFinally);
        }

        /**
         * Executes the <tt>try-catch</tt> construct.
         * <p>
         * The method can be called one time.
         */
        public void run() {
            TryRunnable.this.invokeTryCatchFinally();
        }
    }


    public final class Finally extends TryAbst.Finally {
        /**
         * Executes the <tt>try-catch-finally</tt> construct.
         * <p>
         * The method can be called one time.
         */
        public void run() {
            TryRunnable.this.invokeTryCatchFinally();
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
    public Catch onCatch(ThrowableProcedure1<Throwable, Throwable> onCatch) {
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
    protected void invokeTry(FailableRunnable onTry) throws Exception {
        onTry.run();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void invokeCatch(ThrowableProcedure1<Throwable, Throwable> onCatch, Throwable th) throws Throwable {
        onCatch.accept(th);
    }
}