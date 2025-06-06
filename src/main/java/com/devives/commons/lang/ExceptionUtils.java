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
package com.devives.commons.lang;

import com.devives.commons.lang.exception.AggregateException;
import com.devives.commons.lang.exception.ArgumentNullException;
import com.devives.commons.lang.function.FailableFunction;
import com.devives.commons.lang.function.FailableProcedure;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * The class contains helper methods for working with exception objects.
 */
public class ExceptionUtils {

    /**
     * Returns the error message text along with the stack.
     *
     * @param throwable exception.
     * @return a string containing the message and stack.
     */
    public static String getMessageWithStackTrace(Throwable throwable) {
        return getStackTrace_(throwable, true);
    }

    /**
     * Returns a string representation of the exception stack.
     * @param throwable exception.
     * @return a string containing the stack.
     */
    public static String getStackTrace(Throwable throwable) {
        return getStackTrace_(throwable, false);
    }

    private static String getStackTrace_(Throwable throwable, boolean printMessage) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);
        if (throwable != null) {
            if (printMessage && StringUtils.nonEmpty(throwable.getMessage())) {
                printStream.println(throwable.getMessage());
            }
            throwable.printStackTrace(printStream);
        } else {
            new ArgumentNullException("throwable").printStackTrace(printStream);
        }
        printStream.flush();
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Returns the original exception.
     *
     * @param throwable exception
     * @return the original exception.
     */
    public static Throwable getInitialCause(final Throwable throwable) {
        Throwable cause = throwable;
        while (cause != null && cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

    /**
     * The method checks for the presence of specified classes in the exception chain
     *
     * @param throwable    The exception chain to be checked
     * @param innerClasses Classes of the exceptions to be searched for
     * @return {@code true} if the exception chain contains exceptions of the specified classes, otherwise {@code false}.
     */
    public static boolean hasCauseOfClass(Throwable throwable, Class... innerClasses) {
        Throwable t = throwable;
        while (t != null) {
            for (Class clazz : innerClasses) {
                if (t.getClass() == clazz) {
                    return true;
                }
            }
            t = t.getCause();
        }
        return false;
    }

    /**
     * The method suppresses exceptions of the specified types and throws the rest.
     *
     * @param func            anonymous method.
     * @param failResult      result in case of exception suppression.
     * @param suppressClasses types of suppressed exceptions.
     * @param <R>       result type of the anonymous method.
     * @param <E>       type of suppressed exceptions.
     * @return result of the anonymous method or <tt>failResult</tt> if an exception was suppressed.
     */
    @SafeVarargs
    static public <R, E extends Throwable> R suppressExceptionOfClass(FailableFunction<R> func, R failResult, Class<E>... suppressClasses) {
        try {
            return func.apply();
        } catch (Throwable th) {
            for (Class<?> exClass : suppressClasses) {
                if (exClass.isInstance(th)) {
                    return failResult;
                }
            }
            throw asUnchecked(th);
        }
    }

    /**
     * The method suppresses exceptions of the specified types and throws the rest.
     *
     * @param proc      anonymous method.
     * @param suppressClasses types of suppressed exceptions.
     * @param <E>       type of suppressed exceptions.
     */
    @SafeVarargs
    static public <E extends Throwable> void suppressExceptionOfClass(FailableProcedure proc, Class<E>... suppressClasses) {
        try {
            proc.accept();
        } catch (Throwable th) {
            for (Class<?> exClass : suppressClasses) {
                if (exClass.isInstance(th)) {
                    return;
                }
            }
            throw asUnchecked(th);
        }
    }

    static private final String DEFAULT_AGGREGATE_EXCEPTION_MESSAGE = "The group of Exceptions was thrown";

    /**
     * Throws an exception if the collection of exceptions is not empty.
     * If there is one exception in the collection, it will be thrown.
     * If there are several exceptions in the collection, an exception will be thrown that aggregates all the collected exceptions
     * in the Throwable.getSuppressed() collection.<br>
     * Example usage: collection of exceptions thrown while processing a collection of some objects.
     * <pre>{@code
     * List<Exception> collector = new ArrayList<>();
     * for (AutoCloseable item : iterable) {
     *      try {
     *          item.close();
     *      } catch (Exception e) {
     *          collector.add(e);
     *      }
     * }
     * ExceptionUtils.throwCollected(collector);
     * }</pre>
     *
     * @param collection Collection of exceptions
     * @param <E>        type of exceptions.
     */
    static public <E extends Throwable> void throwCollected(Collection<E> collection) /*throws E*/ {
        throwCollected(collection, DEFAULT_AGGREGATE_EXCEPTION_MESSAGE);
    }

    /**
     * Throws an exception if the collection of exceptions is not empty.
     * If there is one exception in the collection, it will be thrown.
     * If there are several exceptions in the collection, an exception will be thrown that aggregates all the collected exceptions
     * in the Throwable.getSuppressed() collection.
     * Example usage: collection of exceptions thrown while processing a collection of some objects.
     * <pre>{@code
     * List<Exception> collector = new ArrayList<>();
     * for (AutoCloseable item : iterable) {
     *      try {
     *          item.close();
     *      } catch (Exception e) {
     *          collector.add(e);
     *      }
     * }
     * ExceptionUtils.throwCollected(collector, "Exceptions was thrown while close collection of objects");
     * }</pre>
     *
     * @param collection Collection of exceptions
     * @param message    Message of the aggregated message
     * @param <E>        type of exceptions.
     */
    static public <E extends Throwable> void throwCollected(Collection<E> collection, String message) /*throws E*/ {
        switch (collection.size()) {
            case 0:
                break;
            case 1:
                throw ExceptionUtils.asUnchecked(collection.stream().findFirst().get());
            default:
                throw new AggregateException(
                        StringUtils.getFirstNonEmpty(message, DEFAULT_AGGREGATE_EXCEPTION_MESSAGE),
                        collection);
        }
    }

    /**
     * PRETENDS to return an exception wrapped or cast to an unchecked exception.
     * IN FACT throws {@code throwable} without checking the compiler for handling checked exceptions.
     * Used as a hack to reduce code clutter caused by incorrectly used checked exceptions.
     * <p>
     * Unlike throwAsUnchecked, allows using constructs like
     * {@code throws ExceptionUtils.asUnchecked(e)} to explain to the compiler that after this method
     * nothing will be executed anymore.
     *
     * @param throwable type castable throwable.
     * @param <E>       type of exceptions.
     * @return exception instance.
     */
    public static <E extends RuntimeException> E asUnchecked(final Throwable throwable) {
        // claim that the typeErasure invocation throws a RuntimeException
        return ExceptionUtils.<E, RuntimeException>eraseType(throwable);
    }

    /**
     * Claims a Throwable is another Throwable type using type erasure. This
     * hides a checked exception from the Java compiler, allowing a checked
     * exception to be thrown without having the exception in the method's throw
     * clause.
     */
    @SuppressWarnings("unchecked")
    private static <R, T extends Throwable> R eraseType(final Throwable throwable) throws T {
        throw (T) throwable;
    }

    /**
     * Converts checked exceptions thrown by the method into unchecked ones.
     * A service method to reduce code in lambda expressions when calling methods with checked exceptions.
     * <pre>{@code
     *   List<File> tempFiles = uploadItems.stream().map(item -> ExceptionUtils.asUnchecked(item::getFile)).collect(Collectors.toList());
     * }</pre>
     *
     * @param func anonymous method.
     * @param <R>  result type of the anonymous method.
     * @return result of the anonymous method.
     */
    static public <R> R passChecked(FailableFunction<R> func) {
        try {
            return func.apply();
        } catch (Exception e) {
            throw asUnchecked(e);
        }
    }

    /**
     * Converts checked exceptions thrown by the method into unchecked ones.
     * A service method to reduce code in lambda expressions when calling methods with checked exceptions.
     * <pre>{@code
     *   List<File> tempFiles = uploadItems.stream().map(item -> ExceptionUtils.asUnchecked(item::getFile)).collect(Collectors.toList());
     * }</pre>
     *
     * @param proc anonymous method.
     */
    static public void passChecked(FailableProcedure proc) {
        try {
            proc.accept();
        } catch (Exception e) {
            throw asUnchecked(e);
        }
    }

    /**
     * The method processes exceptions thrown from each anonymous method in the <tt>procs</tt> collection, forming
     * a collection of exceptions.
     * <pre>{@code
     *   ExceptionUtils.collect(obj1::someProc, obj2::someProc).ifPresent(ExceptionUtils::throwCollected);
     * }</pre>
     *
     * @param procs array of anonymous methods.
     * @return collection of occurred exceptions.
     */
    static public Optional<List<Exception>> collect(FailableProcedure... procs) {
        List<Exception> throwables = null;
        for (FailableProcedure proc : procs) {
            try {
                proc.accept();
            } catch (Exception thr) {
                if (throwables == null) {
                    throwables = new ArrayList<>();
                }
                throwables.add(thr);
            }
        }
        return Optional.ofNullable(throwables);
    }

    /**
     * The method processes exceptions thrown from each anonymous method in the <tt>procs</tt> collection, forming
     * a collection of exceptions. If one exception was thrown during the execution of anonymous methods, it will be thrown
     * from the current method. If several exceptions were thrown, an aggregated exception will be thrown from the method.
     *
     * @param procs array of anonymous methods.
     */
    static public void collectAndThrow(FailableProcedure... procs) {
        collect(procs).ifPresent(ExceptionUtils::throwCollected);
    }
}
