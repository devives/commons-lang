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
package com.devives.commons.state;

import com.devives.commons.lang.Validate;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Базовая реализация хранителя состояния: в ней сосредоточены сравнение и валидация состояния,
 * синхронизации базовый класс не выполняет.
 * <p>
 * Операции чтения ({@link #get()}, {@code isExpected}, все перегрузки {@code validate}) читают состояние
 * ровно один раз через {@link #internalGet()} и не захватывают блокировок. Операции записи
 * ({@link #set(Object)} и обе перегрузки {@code trySet}) вызывают {@link #internalSet(Object)}. Взаимную
 * исключительность переходов и видимость записей обеспечивает подкласс: в
 * {@link SynchronizedStateHolderImpl} запись выполняется под мьютексом, а чтение — без него, поэтому
 * читатель видит промежуточное состояние перехода, а не ждёт его завершения.
 * <p>
 * Значение {@code null} состоянием не является: обе реализации отклоняют его с
 * {@link NullPointerException}.
 *
 * @param <STATE> тип экземпляров состояний.
 */
public abstract class StateHolderBase<STATE> implements StateHolder<STATE>, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * INTERNAL: Возвращает текущее значение состояния.
     *
     * @return текущее состояние.
     */
    protected abstract STATE internalGet();

    /**
     * INTERNAL: Устанавливает новое значение свойства {@link #internalGet()}.
     *
     * @param state новое значение
     * @throws NullPointerException если {@code value} равен {@code null}.
     */
    protected abstract void internalSet(STATE state);

    /**
     * Возвращает текущее состояние.
     * <p>
     * Блокировок не захватывает: состояние читается один раз через {@link #internalGet()}.
     *
     * @return текущее состояние.
     */
    public STATE get() {
        return internalGet();
    }

    /**
     * Устанавливает {@code value}, если текущее состояние эквивалентно {@code expected}.
     * <p>
     * В базовой реализации проверка и запись не атомарны; в {@link SynchronizedStateHolderImpl} оба шага
     * выполняются в одной критической секции.
     *
     * @param expected ожидаемое состояние.
     * @param value    новое состояние.
     * @return {@code true}, если состояние изменено, иначе {@code false}.
     * @throws NullPointerException если {@code value} равен {@code null}.
     */
    @Override
    public boolean trySet(STATE expected, STATE value) {
        final boolean result = isExpected(expected);
        if (result) {
            this.internalSet(value);
        }
        return result;
    }

    /**
     * Устанавливает {@code value}, если текущее состояние эквивалентно одному из {@code expected}.
     * <p>
     * В базовой реализации проверка и запись не атомарны; в {@link SynchronizedStateHolderImpl} оба шага
     * выполняются в одной критической секции.
     *
     * @param expected ожидаемые состояния.
     * @param value    новое состояние.
     * @return {@code true}, если состояние изменено, иначе {@code false}.
     * @throws NullPointerException     если {@code value} равен {@code null}.
     * @throws IllegalArgumentException если {@code expected} — пустой массив.
     */
    @Override
    public boolean trySet(STATE[] expected, STATE value) {
        final boolean result = isExpected(expected);
        if (result) {
            this.internalSet(value);
        }
        return result;
    }

    /**
     * Устанавливает {@code value} без проверки текущего состояния.
     *
     * @param value новое состояние.
     * @throws NullPointerException если {@code value} равен {@code null}.
     */
    public void set(STATE value) {
        this.internalSet(value);
    }

    /**
     * Проверяет, эквивалентно ли текущее состояние одному из {@code expected}.
     * <p>
     * Блокировок не захватывает, поэтому результат описывает момент времени: параллельный переход может
     * сменить состояние сразу после чтения.
     *
     * @param expected ожидаемые состояния.
     * @return {@code true}, если текущее состояние эквивалентно одному из {@code expected}, иначе {@code false}.
     * @throws NullPointerException     если {@code expected} равен {@code null} либо содержит {@code null}.
     * @throws IllegalArgumentException если {@code expected} — пустой массив.
     */
    @SafeVarargs
    @Override
    public final boolean isExpected(STATE... expected) {
        return isActualEqualToExpected(this.internalGet(), expected);
    }

    /**
     * Метод проверяет эквивалентность текущего состояния одному из ожидаемых состояний.
     *
     * @param actual   текущее состояние
     * @param expected проверяемые/ожидаемые состояния
     * @param <T>      тип экземпляров состояний
     * @return true, если текущее эквивалентно одному из ожидаемых состояний, иначе false.
     * @throws NullPointerException     если {@code value} равен {@code null}.
     * @throws IllegalArgumentException если {@code expected} — пустой массив.
     */
    @SafeVarargs
    static protected <T> boolean isActualEqualToExpected(T actual, T... expected) {
        Objects.requireNonNull(actual, "The actual state is null.");
        Validate.notEmpty(expected);
        for (T expectedState : expected) {
            Objects.requireNonNull(expectedState, "The 'null' value in the array of expected states.");
            if (actual.equals(expectedState)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверяет, эквивалентно ли текущее состояние одному из {@code expected}.
     * <p>
     * Блокировок не захватывает, поэтому исключение может быть вызвано промежуточным состоянием перехода,
     * выполняемого в другом потоке.
     *
     * @param expected ожидаемые состояния.
     * @throws InvalidStateException    если текущее состояние не эквивалентно ни одному из {@code expected}.
     * @throws IllegalArgumentException если {@code expected} — пустой массив.
     */
    @SafeVarargs
    @Override
    public final void validate(STATE... expected) {
        STATE actual = internalGet();
        boolean success = isActualEqualToExpected(actual, expected);
        if (!success) {
            String states = Stream.of(expected).map(Objects::toString).collect(Collectors.joining(" or "));
            throw new InvalidStateException("The actual state '" + actual + "' not equal expected: '" + states + "'");
        }
    }

    /**
     * Проверяет, эквивалентно ли текущее состояние {@code expected}.
     * <p>
     * Блокировок не захватывает, поэтому исключение может быть вызвано промежуточным состоянием перехода,
     * выполняемого в другом потоке.
     *
     * @param expected          ожидаемое состояние.
     * @param exceptionSupplier поставщик исключения, вызывается с текущим состоянием.
     * @param <E>               тип исключения.
     * @throws E                    если текущее состояние не эквивалентно {@code expected}.
     * @throws NullPointerException если {@code expected} равен {@code null}.
     */
    @Override
    public <E extends InvalidStateException> void validate(STATE expected, Function<STATE, E> exceptionSupplier) throws E {
        STATE actual = internalGet();
        boolean success = StateHolderBase.isActualEqualToExpected(actual, expected);
        if (!success) {
            throw exceptionSupplier.apply(actual);
        }
    }

    /**
     * Проверяет, эквивалентно ли текущее состояние одному из {@code expected}.
     * <p>
     * Блокировок не захватывает, поэтому исключение может быть вызвано промежуточным состоянием перехода,
     * выполняемого в другом потоке.
     *
     * @param expected          ожидаемые состояния.
     * @param exceptionSupplier поставщик исключения, вызывается с текущим состоянием.
     * @param <E>               тип исключения.
     * @throws E                        если текущее состояние не эквивалентно ни одному из {@code expected}.
     * @throws IllegalArgumentException если {@code expected} — пустой массив.
     */
    @Override
    public <E extends InvalidStateException> void validate(STATE[] expected, Function<STATE, E> exceptionSupplier) throws E {
        STATE actual = internalGet();
        boolean success = StateHolderBase.isActualEqualToExpected(actual, expected);
        if (!success) {
            throw exceptionSupplier.apply(actual);
        }
    }
}
