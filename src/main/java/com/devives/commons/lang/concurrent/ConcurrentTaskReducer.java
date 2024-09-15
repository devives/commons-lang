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
package com.devives.commons.lang.concurrent;

import com.devives.commons.lang.exception.ExceptionUtils;
import com.devives.commons.lang.function.*;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * Уменьшитель числа конкурентных задач (вызовов метода).
 * <p>
 * Класс исключает одновременное выполнение несколькими потоками задачи (вызова метода) с одинаковыми аргументами.
 * Если несколько потоков одновременно вызовут выполнение метода с одинаковыми аргументами, только один поток будет
 * выполнять расчёты/работу, остальные потоки будут ожидать завершения выполнения первым потоком.
 * <p>
 * Фактически, выполняется синхронизация выполнения кода несколькими потоками, относительно переданного ключа.
 * <h3>Примеры</h3>
 * <ol>
 *     <li>Подготовка/компиляция шаблонов отчётов.
 *     <li>Загрузка метаданных выборок.
 * </ol>
 *
 * @param <R> Тип результата задачи.
 */
public class ConcurrentTaskReducer<R> {

    /**
     * Карта задач, синхронизирующая доступ к объектам синхронизации выполнения задач.
     */
    private final ConcurrentMap<String, Task<R>> taskMap_ = new ConcurrentHashMap<>();

    /**
     * Задача.
     * <p>
     * Объект создаётся для уникального ключа задачи {@link Task#key}.
     *
     * @param <R> тип результата выполнения задачи.
     */
    private static final class Task<R> extends CompletableFuture<R> {
        /**
         * Уникальный ключ задачи, сформированный по совокупности аргументов задачи.
         */
        final String key;
        /**
         * Число использований/ожиданий выполнения задачи.
         */
        private int usages_ = 0;

        public Task(String key) {
            this.key = key;
        }

        /**
         * Увеличивает счётчик использований.
         *
         * @return новое значение.
         */
        public synchronized int incUsages() {
            return ++usages_;
        }

    }

    /**
     * Метод создаёт новый объект задачи и вызывает переданный анонимный метод или получает ранее созданный, другим
     * потоком, объект задачи и ожидает завершения выполнения работы другим потоком.
     *
     * @param key      уникальный ключ.
     * @param callable вызываемый анонимный метод (задача).
     * @return результат выполнения анонимного метода.
     */
    public R call(final String key, final Callable<R> callable) {
        try {
            final Task<R> task = doCall(key, callable);
            return task.get();
        } catch (InterruptedException e) {
            throw ExceptionUtils.asUnchecked(e);
        } catch (ExecutionException e) {
            throw ExceptionUtils.asUnchecked(e.getCause());
        }
    }

    /**
     * Метод создаёт новый объект задачи и вызывает переданный анонимный метод или получает ранее созданный, другим
     * потоком, объект задачи и ожидает завершения выполнения работы другим потоком.
     *
     * @param key      уникальный ключ.
     * @param callable вызываемый анонимный метод (задача).
     * @param timeout  таймаут ожидания выполнения задания. Применяется только к потокам ожидающим завершения
     *                 выполнения задачи, но не к потоку выполняющему задачу.
     * @param unit     единица измерения таймаута.
     * @return результат выполнения анонимного метода.
     * @throws TimeoutException При превышении длительности ожидания выполнения задания.
     */
    public R call(final String key, final Callable<R> callable, int timeout, TimeUnit unit) throws TimeoutException {
        try {
            final Task<R> task = doCall(key, callable);
            return task.get(timeout, unit);
        } catch (InterruptedException e) {
            throw ExceptionUtils.asUnchecked(e);
        } catch (ExecutionException e) {
            throw ExceptionUtils.asUnchecked(e.getCause());
        }
    }

    private Task<R> doCall(final String key, final Callable<R> callable) {
        Objects.requireNonNull(key, "Argument `key` value can not be `null`.");
        Objects.requireNonNull(callable, "Argument `callable` value can not be `null`.");
        final Task<R> task = taskMap_.computeIfAbsent(key, Task::new);
        // Метод `Task#incUsages()` синхронизированный.
        if (task.incUsages() == 1) {
            // Первый, захвативший задачу, поток выполняет её.
            try {
                try {
                    task.complete(callable.call());
                } catch (Throwable e) {
                    task.completeExceptionally(e);
                }
            } finally {
                taskMap_.remove(key);
            }
        }
        return task;
    }

    /**
     * Создаёт новый экземпляр новый экземпляр метода, с ограничением на число одновременных выполнений.
     * <p>
     * Для переданного анонимного метода будет создан метод-декоратор, владеющий экземпляром уменьшителя числа
     * конкурентных задач {@link ConcurrentTaskReducer}.
     *
     * @param keyFormatter форматировщик ключа задачи.
     * @param callable     вызываемый анонимный метод (задача).
     * @param <R>          тип результата анонимного метода.
     * @return новый экземпляр метода, с ограничением на число одновременных выполнений.
     */
    public static <R> ReducedCallable<R> forCallable(
            Function<String> keyFormatter,
            ExceptionFunction<R> callable) {
        return new ReducedCallableImpl<>(keyFormatter, callable);
    }

    /**
     * Создаёт новый экземпляр новый экземпляр метода, с ограничением на число одновременных выполнений.
     * <p>
     * Для переданного анонимного метода будет создан метод-декоратор, владеющий экземпляром уменьшителя числа
     * конкурентных задач {@link ConcurrentTaskReducer}.
     *
     * @param keyFormatter форматировщик ключа задачи.
     * @param callable     вызываемый анонимный метод (задача).
     * @param <A1>         тип аргумента анонимного метода.
     * @param <R>          тип результата анонимного метода.
     * @return новый экземпляр метода, с ограничением на число одновременных выполнений.
     */
    public static <A1, R> ReducedCallable1<A1, R> forCallable(
            Function1<A1, String> keyFormatter,
            ExceptionFunction1<A1, R> callable) {
        return new ReducedCallable1Impl<>(keyFormatter, callable);
    }

    /**
     * Создаёт новый экземпляр новый экземпляр метода, с ограничением на число одновременных выполнений.
     * <p>
     * Для переданного анонимного метода будет создан метод-декоратор, владеющий экземпляром уменьшителя числа
     * конкурентных задач {@link ConcurrentTaskReducer}.
     *
     * @param keyFormatter форматировщик ключа задачи.
     * @param callable     вызываемый анонимный метод (задача).
     * @param <A1>         тип первого аргумента анонимного метода.
     * @param <A2>         тип второго аргумента анонимного метода.
     * @param <R>          тип результата анонимного метода.
     * @return новый экземпляр метода, с ограничением на число одновременных выполнений.
     */
    public static <A1, A2, R> ReducedCallable2<A1, A2, R> forCallable(
            Function2<A1, A2, String> keyFormatter,
            ExceptionFunction2<A1, A2, R> callable) {
        return new ReducedCallable2Impl<>(keyFormatter, callable);
    }

    private static abstract class AbstractReducedCallable<R, FORMATTER, CALLABLE> {
        protected final ConcurrentTaskReducer<R> taskReducer_ = new ConcurrentTaskReducer<>();
        protected final FORMATTER keyFormatter_;
        protected final CALLABLE callable_;

        public AbstractReducedCallable(FORMATTER keyFormatter, CALLABLE callable) {
            keyFormatter_ = Objects.requireNonNull(keyFormatter, "Argument `keyFormatter` value can not be `null`.");
            callable_ = Objects.requireNonNull(callable, "Argument `callable` value can not be `null`.");
        }
    }

    private static final class ReducedCallableImpl<R> extends AbstractReducedCallable<R, Function<String>, ExceptionFunction<R>> implements ReducedCallable<R> {

        public ReducedCallableImpl(Function<String> keyFormatter,
                                   ExceptionFunction<R> callable) {
            super(keyFormatter, callable);
        }

        @Override
        public R apply() {
            final String key = Objects.requireNonNull(keyFormatter_.apply(), "Key formatter must return non `null` value.");
            return taskReducer_.call(key, callable_::apply);
        }

        @Override
        public R apply(int timeout, TimeUnit unit) throws TimeoutException {
            final String key = Objects.requireNonNull(keyFormatter_.apply(), "Key formatter must return non `null` value.");
            return taskReducer_.call(key, callable_::apply, timeout, unit);
        }
    }

    private static final class ReducedCallable1Impl<A1, R>
            extends AbstractReducedCallable<R, Function1<A1, String>, ExceptionFunction1<A1, R>>
            implements ReducedCallable1<A1, R> {

        public ReducedCallable1Impl(Function1<A1, String> keyFormatter,
                                    ExceptionFunction1<A1, R> callable) {
            super(keyFormatter, callable);
        }

        @Override
        public R apply(A1 arg1) {
            final String key = Objects.requireNonNull(keyFormatter_.apply(arg1), "Key formatter must return non `null` value.");
            return taskReducer_.call(key, () -> callable_.apply(arg1));
        }

        @Override
        public R apply(A1 arg1, int timeout, TimeUnit unit) throws TimeoutException {
            final String key = Objects.requireNonNull(keyFormatter_.apply(arg1), "Key formatter must return non `null` value.");
            return taskReducer_.call(key, () -> callable_.apply(arg1), timeout, unit);
        }
    }

    private static final class ReducedCallable2Impl<A1, A2, R>
            extends AbstractReducedCallable<R, Function2<A1, A2, String>, ExceptionFunction2<A1, A2, R>>
            implements ReducedCallable2<A1, A2, R> {

        public ReducedCallable2Impl(Function2<A1, A2, String> keyFormatter, ExceptionFunction2<A1, A2, R> a1A2RExceptionFunction12) {
            super(keyFormatter, a1A2RExceptionFunction12);
        }

        @Override
        public R apply(A1 arg1, A2 arg2) {
            final String key = Objects.requireNonNull(keyFormatter_.apply(arg1, arg2), "Key formatter must return non `null` value.");
            return taskReducer_.call(key, () -> callable_.apply(arg1, arg2));
        }

        @Override
        public R apply(A1 arg1, A2 arg2, int timeout, TimeUnit unit) throws TimeoutException {
            final String key = Objects.requireNonNull(keyFormatter_.apply(arg1, arg2), "Key formatter must return non `null` value.");
            return taskReducer_.call(key, () -> callable_.apply(arg1, arg2), timeout, unit);
        }
    }

    /**
     * Метод с ограничением на число одновременных выполнений несколькими потоками.
     *
     * @param <R> Тип результата метода.
     */
    public interface ReducedCallable<R> {

        /**
         * Выполняет метод.
         *
         * @return результат выполнения метода.
         */
        R apply();

        /**
         * Выполняет метод с указанием таймаута ожидания.
         *
         * @param timeout таймаут ожидания выполнения задания. Применяется только к потокам ожидающим завершения
         *                выполнения задачи, но не к потоку выполняющему задачу.
         * @param unit    единица измерения таймаута.
         * @return результат выполнения метода.
         * @throws TimeoutException При превышении длительности ожидания выполнения задания.
         */
        R apply(int timeout, TimeUnit unit) throws TimeoutException;
    }

    /**
     * Метод с ограничением на число одновременных выполнений несколькими потоками.
     *
     * @param <R> Тип результата метода.
     */
    public interface ReducedCallable1<A1, R> {
        /**
         * Выполняет метод.
         *
         * @param arg1 аргумент 1.
         * @return результат выполнения метода.
         */
        R apply(A1 arg1);

        /**
         * Выполняет метод с указанием таймаута ожидания.
         *
         * @param arg1    аргумент 1
         * @param timeout таймаут ожидания выполнения задания. Применяется только к потокам ожидающим завершения
         *                выполнения задачи, но не к потоку выполняющему задачу.
         * @param unit    единица измерения таймаута.
         * @return результат выполнения метода
         * @throws TimeoutException При превышении длительности ожидания выполнения задания.
         */
        R apply(A1 arg1, int timeout, TimeUnit unit) throws TimeoutException;
    }

    /**
     * Метод с ограничением на число одновременных выполнений несколькими потоками.
     *
     * @param <R> Тип результата метода.
     */
    public interface ReducedCallable2<A1, A2, R> {
        /**
         * @param arg1 аргумент 1.
         * @param arg2 аргумент 2.
         * @return результат выполнения метода.
         */
        R apply(A1 arg1, A2 arg2);

        /**
         * Выполняет метод с указанием таймаута ожидания.
         *
         * @param arg1    аргумент 1.
         * @param arg2    аргумент 2.
         * @param timeout таймаут ожидания выполнения задания. Применяется только к потокам ожидающим завершения
         *                выполнения задачи, но не к потоку выполняющему задачу.
         * @param unit    единица измерения таймаута.
         * @return результат выполнения метода.
         * @throws TimeoutException При превышении длительности ожидания выполнения задания.
         */
        R apply(A1 arg1, A2 arg2, int timeout, TimeUnit unit) throws TimeoutException;
    }

}