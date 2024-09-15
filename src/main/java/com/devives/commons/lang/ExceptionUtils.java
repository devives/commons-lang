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
import com.devives.commons.lang.function.ExceptionFunction;
import com.devives.commons.lang.function.ExceptionProcedure;
import com.devives.commons.lang.function.FailableFunction;
import com.devives.commons.lang.function.FailableProcedure;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * Класс содержит вспомогательные методы для работы с объектов исключения.
 */
public class ExceptionUtils {

    /**
     * Возвращает текст сообщения об ошибке вместе со стеком.
     *
     * @param e Исключение
     * @return Текст исключения со стеком
     */
    public static String getMessageWithStackTrace(Throwable e) {
        return getMessageWithStackTrace_(e);
    }

    public static String getStackTrace() {
        return getStackTrace((String) null);
    }

    public static String getStackTrace(String message) {
        return getMessageWithStackTrace_(new Exception(StringUtils.getFirstNonEmpty(message, "Stack trace")));
    }

    /**
     * Возвращает текст исходного сообщения об ошибке вместе со стеком.
     *
     * @param e Исключение
     * @return Текст исключения со стеком
     */
    public static String getCauseMessageWithStackTrace(Throwable e) {
        return getMessageWithStackTrace_(getOriginalCauseThrowable(e));
    }

    private static String getMessageWithStackTrace_(Throwable throwable) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);
        if (throwable != null) {
            if (StringUtils.nonEmpty(throwable.getMessage())) {
                printStream.println(throwable.getMessage());
            }
            throwable.printStackTrace(printStream);
        } else {
            new ArgumentNullException("throwable").printStackTrace(printStream);
        }
        printStream.flush();
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    public static String getStackTrace(Throwable throwable) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);
        if (throwable != null) {
            throwable.printStackTrace(printStream);
        } else {
            new ArgumentNullException("throwable").printStackTrace(printStream);
        }
        printStream.flush();
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Возвращает объект исключения, являющийся исходным.
     *
     * @param e Исключение
     * @return Исходное исключение.
     */
    public static Throwable getOriginalCauseThrowable(final Throwable e) {
        Throwable cause = e;
        while (cause != null && cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

    public static RuntimeException toRuntimeException(Throwable e) {
        return (e instanceof RuntimeException)
                ? (RuntimeException) e
                : new RuntimeException(e);
    }

    public static Exception toException(Throwable e) {
        return (e instanceof Exception)
                ? (Exception) e
                : new Exception(e);
    }

    /**
     * Метод проверяет присутствие в цепочке исключений указанных классов
     *
     * @param throwable    Проверяемая цепочка исключений
     * @param innerClasses Классы искомых исключений
     * @return true, если в цепочке исключений есть исключения указанных классов
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
     * Метод подавляет исключения указанных типов и выбрасывает остальные.
     *
     * @param func      анонимный метод.
     * @param exClasses типы подавляемых исключений.
     * @param <R>       тип результата анонимного метода.
     * @param <E>       тип подавляемых исключений.
     * @return результат анонимного метода.
     * @throws Exception исключение выброшенное из анонимного метода.
     */
    @SafeVarargs
    static public <R, E extends Throwable> R suppressExceptionOfClass(FailableFunction<R, Exception> func, Class<E>... exClasses) throws Exception {
        try {
            return func.apply();
        } catch (Throwable th) {
            for (Class<?> exClass : exClasses) {
                if (exClass.isInstance(th)) {
                    return null;
                }
            }
            if (th instanceof RuntimeException) {
                throw (RuntimeException) th;
            } else {
                throw th;
            }
        }
    }

    /**
     * Метод подавляет исключения указанных типов и выбрасывает остальные.
     *
     * @param proc      анонимный метод.
     * @param exClasses типы подавляемых исключений.
     * @param <E>       тип подавляемых исключений.
     * @throws Exception исключение выброшенное из анонимного метода.
     */
    @SafeVarargs
    static public <E extends Throwable> void suppressExceptionOfClass(FailableProcedure<Exception> proc, Class<E>... exClasses) throws Exception {
        try {
            proc.accept();
        } catch (Throwable th) {
            for (Class<?> exClass : exClasses) {
                if (exClass.isInstance(th)) {
                    return;
                }
            }
            if (th instanceof RuntimeException) {
                throw (RuntimeException) th;
            } else {
                throw th;
            }
        }
    }

    static private final String DEFAULT_AGGREGATE_EXCEPTION_MESSAGE = "The group of Exceptions was thrown";

    /**
     * Выбрасывает исключение, если коллекция исключений не пуста.
     * Если в коллекции одно исключение, оно будет выброшено.
     * Если в коллекции несколько исключений, будет выброшено исключение, агрегирующее все собранные исключения
     * в коллекции Throwable.getSuppressed().<br>
     * Пример использования:  сбор исключений, выбрасываемых, при обработке коллекции неких объектов.
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
     * @param collection Коллекция исключений
     * @param <E>        type of exceptions.
     */
    static public <E extends Throwable> void throwCollected(Collection<E> collection) /*throws E*/ {
        throwCollected(collection, DEFAULT_AGGREGATE_EXCEPTION_MESSAGE);
    }

    /**
     * Выбрасывает исключение, если коллекция исключений не пуста.
     * Если в коллекции одно исключение, оно будет выброшено.
     * Если в коллекции несколько исключений, будет выброшено исключение, агрегирующее все собранные исключения
     * в коллекции Throwable.getSuppressed().
     * Пример использования:  сбор исключений, выбрасываемых, при обработке коллекции неких объектов.
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
     * @param collection Коллекция исключений
     * @param message    Сообщение агрегированного сообщения
     * @param <E>        type of exceptions.
     */
    static public <E extends Throwable> void throwCollected(Collection<E> collection, String message) /*throws E*/ {
        switch (collection.size()) {
            case 0:
                break;
            case 1:
                throw ExceptionUtils.asUnchecked(collection.stream().findFirst().get());
            default:
                AggregateException aggregateException = new AggregateException(StringUtils.getFirstNonEmpty(message, DEFAULT_AGGREGATE_EXCEPTION_MESSAGE));
                collection.forEach(aggregateException::addSuppressed);
                throw ExceptionUtils.asUnchecked(aggregateException);
        }
    }

    /**
     * ДЕЛАЕТ ВИД, что возвращает исключение обёрнутое либо приведённое к unchecked исключения.
     * НА САМОМ ДЕЛЕ бросает {@code throwable} без проверки компилятора на обработку checked исключений.
     * Используется на правах хака снижающего захламление кода неправильно используемыми checked исключениями.
     * <p>
     * В отличие от throwAsUnchecked позволяет использовать конструкции вида
     * {@code throws ExceptionUtils.asUnchecked(e)} чтобы объяснить компилятору что после этого метода
     * уже ничего выполняться не будет.
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
     * Преобразует отмеченные исключения, выбрасываемые методом, в неотмеченные.
     * Сервисный метод для сокращения кода в лямбда-выражениях, при вызовах методов с отмеченным исключениями.
     * <pre>{@code
     *   List<File> tempFiles = uploadItems.stream().map(item -> ExceptionUtils.asUnchecked(item::getFile)).collect(Collectors.toList());
     * }</pre>
     *
     * @param func анонимный метод.
     * @param <R>  тип результата анонимного метода.
     * @return результат анонимного метода.
     */
    static public <R> R passChecked(ExceptionFunction<R> func) {
        try {
            return func.apply();
        } catch (Exception e) {
            throw asUnchecked(e);
        }
    }

    /**
     * Преобразует отмеченные исключения, выбрасываемые методом, в неотмеченные.
     * Сервисный метод для сокращения кода в лямбда-выражениях, при вызовах методов с отмеченным исключениями.
     * <pre>{@code
     *   List<File> tempFiles = uploadItems.stream().map(item -> ExceptionUtils.asUnchecked(item::getFile)).collect(Collectors.toList());
     * }</pre>
     *
     * @param proc анонимный метод.
     */
    static public void passChecked(ExceptionProcedure proc) {
        try {
            proc.accept();
        } catch (Exception e) {
            throw asUnchecked(e);
        }
    }

}
