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

/**
 * Entry point for creating <code>try-catch-finally</code> constructs with cascading exception handling.
 * <p>
 * Provides factory methods for:
 * <ul>
 *     <li>{@link TryRunnable}: operations without a return value.
 *     <li>{@link TryCallable}: operations with a return value.
 * </ul>
 * Prevents loss of original exceptions in the `try-catch-finally` block if induced exceptions occur
 * in `catch` and/or `finally`.
 * <p>
 * The choice of the thrown exception follows the Java language rules for {@code catch} and the
 * try-with-resources rules for {@code finally}:
 * <ul>
 *     <li>an exception thrown from {@code try} propagates unless a {@code catch} block is defined;
 *     <li>an exception thrown from {@code catch} replaces the original one. If it is not already linked with the
 *     original exception through the cause or suppressed chains, the original exception is added to its
 *     {@link Throwable#getSuppressed() suppressed} exceptions, so it is not lost when the handler fails unexpectedly;
 *     <li>an exception thrown from {@code finally} never hides the primary exception: it is added to the
 *     {@link Throwable#getSuppressed() suppressed} exceptions of the primary one, and propagates itself
 *     only when there is no primary exception. This matches the behavior of an exception thrown from
 *     {@link AutoCloseable#close()} in the {@code try (AutoCloseable resource = ...) {}} statement (JLS 14.20.3.1).
 * </ul>
 * The exception is thrown from the methods {@link TryRunnable.Catch#run()} and {@link TryCallable.Catch#call()}.
 * <p>
 * Methods of this class are not synchronized and do not assume calls from multiple threads.
 *
 * @see TryCallable
 * @see TryRunnable
 *
 * @since 0.2.0
 */
public final class Try {
    private Try() {
    }

    /**
     * Factory method to initialize a {@link TryRunnable} construct for operations that do not return a value.
     * <p>
     * This method creates a new instance of {@link TryRunnable}, which allows defining a <code>try-catch-finally</code>
     * structure for a given runnable operation. It provides a fluent API to handle exceptions and finalize
     * operations in a controlled manner.
     * <p>
     * Example usage:
     * <pre>{@code
     * Try.runnable(() -> {
     *   // Main code block that may throw an exception.
     * }).onCatch((th) -> {
     *   // Handle errors and optionally rethrow exception.
     * }).doFinally(() -> {
     *   // Perform cleanup or finalization tasks.
     * }).run();
     * }</pre>
     *
     * @param runnable  the runnable operation that may throw an exception. Must not be null.
     * @return new {@link TryRunnable} instance.
     * @throws NullPointerException if the provided {@code runnable} is null.
     */
    public static TryRunnable runnable(FailableRunnable runnable) {
        return new TryRunnableScope(runnable);
    }

    /**
     * Factory method to initialize a {@link TryCallable} construct for operations that return a value.
     * <p>
     * This method creates a new instance of {@link TryCallable}, which allows defining a <code>try-catch-finally</code>
     * structure for a given callable operation. It provides a fluent API to handle exceptions and finalize
     * operations in a controlled manner.
     * <p>
     * Example usage:
     * <pre>{@code
     * Long result = Try.callable(() -> {
     *   // Main code block that may throw an exception.
     *   return 1L;
     * }).onCatch((th) -> {
     *   // Handle errors and optionally rethrow exception or return a fallback value.
     *   return 2L;
     * }).doFinally(() -> {
     *   // Perform cleanup or finalization tasks.
     * }).call();
     * }</pre>
     *
     * @param <R>       the type of the return value of the callable operation.
     * @param callable  the callable operation that may throw an exception. Must not be null.
     * @return new {@link TryCallable} instance.
     * @throws NullPointerException if the provided {@code callable} is null.
     */
    public static <R> TryCallable<R> callable(FailableCallable<R> callable) {
        return new TryCallableScope<R>(callable);
    }
}