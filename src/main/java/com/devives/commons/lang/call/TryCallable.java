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
 * This interface declare cascading exception handling in the <code>try-catch-finally</code> construct for methods that return a result.
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
 * Methods of the implementations are not synchronized and do not assume calls from multiple threads.
 *
 * @param <R> the type of the return value of the construct.
 *
 * @see Try#callable(FailableCallable)
 *
 * @since 0.2.0
 */
public interface TryCallable<R> {
    /**
     * Interface declare methods witch are available after defining {@code try-catch} code blocks.
     */
    interface Catch<R> {

        /**
         * Initializes the {@code finally} code block.
         * <p>
         * The method can be called one time.
         *
         * @param onFinally the finally code block.
         * @return builder instance.
         */
        Finally<R> doFinally(FailableRunnable onFinally);

        /**
         * Executes the {@code try-catch} construct.
         * <p>
         * The method can be called one time.
         *
         * @return the result of executing {@code TryAbst#onTry} or {@code TryAbst#onCatch}.
         */
        R call();
    }

    /**
     * Interface declare methods witch are available after defining {@code finally} code block.
     */
    interface Finally<R> {

        /**
         * Executes the <code>try-catch-finally</code> construct.
         * <p>
         * The method can be called one time.
         *
         * @return the result of executing {@code TryAbst#onTry} or {@code TryAbst#onCatch}.
         */
        R call();
    }

    /**
     * Initializes the {@code catch} code block, which will be executed only if an exception occurs in the {@code TryAbst#onTry} block.
     * <p>
     * The method can be called one time.
     *
     * @param onCatch the <code>catch</code> code block, which accepts the exception thrown from the {@code TryAbst#onTry} block as an argument.
     * @return a new instance of {@link TryRunnable.Catch}.
     */
    Catch<R> onCatch(ThrowableFunction1<Throwable, R, Throwable> onCatch);

    /**
     * Initializes the {@code finally} code block.
     * <p>
     * The method can be called one time.
     *
     * @param onFinally the <code>finally</code> code block.
     * @return a new instance of {@link TryRunnable.Finally}.
     */
    Finally<R> doFinally(FailableRunnable onFinally);

}