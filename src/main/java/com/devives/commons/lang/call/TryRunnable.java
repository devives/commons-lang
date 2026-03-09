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
 * This interface declare cascading exception handling in the <code>try-catch-finally</code> construct for methods that do not
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
 * Methods of the implementations are not synchronized and do not assume calls from multiple threads.
 *
 * @see Try#runnable(FailableRunnable)
 *
 * @since 0.2.0
 */
public interface TryRunnable {
    /**
     * Interface declare methods witch are available after defining {@code try-catch} code blocks.
     */
    interface Catch<R> {

        /**
         * Initializes the {@code finally} code block.
         *
         * @param onFinally the finally code block.
         * @return builder instance.
         */
        Finally doFinally(FailableRunnable onFinally);

        /**
         * Executes the {@code try-catch} construct.
         * <p>
         * The method can be called one time.
         */
        void run();
    }

    /**
     * Interface declare methods witch are available after defining {@code finally} code block.
     */
    interface Finally {

        /**
         * Executes the <code>try-catch-finally</code> construct.
         * <p>
         * The method can be called one time.
         */
        void run();
    }

    /**
     * Initializes the {@code TryAbst#onCatch} code block, which will be executed only if an exception occurs in the {@code TryAbst#onTry} block.
     * <p>
     * The method can be called one time.
     *
     * @param onCatch the <code>catch</code> code block, which accepts the exception thrown from the {@code TryAbst#onTry} block as an argument.
     * @return a new instance of {@link TryRunnable.Catch}.
     */
    Catch onCatch(ThrowableProcedure1<Throwable, Throwable> onCatch);

    /**
     * Initializes the {@code TryAbst#onFinally} code block.
     * <p>
     * The method can be called one time.
     *
     * @param onFinally the <code>finally</code> code block.
     * @return a new instance of {@link TryRunnable.Finally}.
     */
    Finally doFinally(FailableRunnable onFinally);

}