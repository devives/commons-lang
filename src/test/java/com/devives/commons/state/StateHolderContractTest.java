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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Контракт {@link StateHolder}, обязательный для любой реализации: его проверяют и
 * {@link StateHolderImpl}, и {@link SynchronizedStateHolderImpl}.
 */
public abstract class StateHolderContractTest {

    protected abstract StateHolder<String> newHolder(String initialState);

    @Test
    public void get_returnsInitialState() {
        Assertions.assertEquals("A", newHolder("A").get());
    }

    @Test
    public void constructor_rejectsNullInitialState() {
        Assertions.assertThrows(NullPointerException.class, () -> newHolder(null));
    }

    @Test
    public void set_changesState() {
        final StateHolder<String> holder = newHolder("A");
        holder.set("B");
        Assertions.assertEquals("B", holder.get());
    }

    @Test
    public void set_nullValue_throwsExceptionAndKeepsState() {
        final StateHolder<String> holder = newHolder("A");
        Assertions.assertThrows(NullPointerException.class, () -> holder.set(null));
        Assertions.assertEquals("A", holder.get());
    }

    @Test
    public void trySet_expectedMatches_setsValueAndReturnsTrue() {
        final StateHolder<String> holder = newHolder("A");
        Assertions.assertTrue(holder.trySet("A", "B"));
        Assertions.assertEquals("B", holder.get());
    }

    @Test
    public void trySet_expectedNotMatches_returnsFalseAndKeepsState() {
        final StateHolder<String> holder = newHolder("A");
        Assertions.assertFalse(holder.trySet("B", "C"));
        Assertions.assertEquals("A", holder.get());
    }

    /**
     * Закрепляет контракт «значение не может быть {@code null}»: до выделения {@code StateHolderBase}
     * {@code trySet} записывал {@code null} и возвращал {@code true}, после чего любое
     * {@code isExpected}/{@code validate} на этом экземпляре бросало {@code NullPointerException}.
     */
    @Test
    public void trySet_nullValue_throwsExceptionAndKeepsState() {
        final StateHolder<String> holder = newHolder("A");
        Assertions.assertThrows(NullPointerException.class, () -> holder.trySet("A", null));
        Assertions.assertEquals("A", holder.get());
    }

    @Test
    public void trySetArray_oneOfExpectedMatches_setsValueAndReturnsTrue() {
        final StateHolder<String> holder = newHolder("A");
        Assertions.assertTrue(holder.trySet(new String[]{"X", "A"}, "B"));
        Assertions.assertEquals("B", holder.get());
    }

    @Test
    public void trySetArray_noneOfExpectedMatches_returnsFalseAndKeepsState() {
        final StateHolder<String> holder = newHolder("A");
        Assertions.assertFalse(holder.trySet(new String[]{"X", "Y"}, "B"));
        Assertions.assertEquals("A", holder.get());
    }

    @Test
    public void trySetArray_emptyExpected_throwsIllegalArgumentExceptionAndKeepsState() {
        final StateHolder<String> holder = newHolder("A");
        Assertions.assertThrows(IllegalArgumentException.class, () -> holder.trySet(new String[]{}, "B"));
        Assertions.assertEquals("A", holder.get());
    }

    @Test
    public void isExpected_actualEqualsOneOfExpected_returnsTrue() {
        Assertions.assertTrue(newHolder("A").isExpected("X", "A"));
    }

    @Test
    public void isExpected_actualEqualsNoneOfExpected_returnsFalse() {
        Assertions.assertFalse(newHolder("A").isExpected("X", "Y"));
    }

    @Test
    public void isExpected_noExpected_throwsIllegalArgumentException() {
        final StateHolder<String> holder = newHolder("A");
        Assertions.assertThrows(IllegalArgumentException.class, holder::isExpected);
    }

    @Test
    public void isExpected_expectedContainsNull_throwsNullPointerException() {
        final StateHolder<String> holder = newHolder("A");
        Assertions.assertThrows(NullPointerException.class, () -> holder.isExpected("X", null));
    }

    /**
     * Проверка элементов массива выполняется лениво, по ходу сравнения: когда состояние совпало с одним из
     * первых ожидаемых, стоящий дальше {@code null} не отбрасывается. Поведение не менялось при выделении
     * {@code StateHolderBase} — закреплено как есть, чтобы его изменение было видно.
     */
    @Test
    public void isExpected_actualMatchesBeforeNullElement_returnsTrue() {
        final StateHolder<String> holder = newHolder("A");
        Assertions.assertTrue(holder.isExpected("A", null));
    }

    @Test
    public void validate_actualMatches_doesNotThrow() {
        newHolder("A").validate("X", "A");
    }

    @Test
    public void validate_actualNotMatches_throwsInvalidStateException() {
        final StateHolder<String> holder = newHolder("A");
        final InvalidStateException exception =
                Assertions.assertThrows(InvalidStateException.class, () -> holder.validate("X", "Y"));
        Assertions.assertTrue(exception.getMessage().contains("A"), exception.getMessage());
    }

    @Test
    public void validate_noExpected_throwsIllegalArgumentException() {
        final StateHolder<String> holder = newHolder("A");
        Assertions.assertThrows(IllegalArgumentException.class, holder::validate);
    }

    @Test
    public void validateSingle_expectedMismatch_throwsSuppliedException() {
        final StateHolder<String> holder = newHolder("A");
        final StateMarkException exception = Assertions.assertThrows(StateMarkException.class,
                () -> holder.validate("B", actual -> new StateMarkException("actual=" + actual)));
        Assertions.assertEquals("actual=A", exception.getMessage());
    }

    @Test
    public void validateSingle_expectedMatch_doesNotThrow() {
        newHolder("A").validate("A", actual -> new StateMarkException(actual));
    }

    @Test
    public void validateArray_expectedMismatch_throwsSuppliedException() {
        final StateHolder<String> holder = newHolder("A");
        final StateMarkException exception = Assertions.assertThrows(StateMarkException.class,
                () -> holder.validate(new String[]{"X", "Y"}, actual -> new StateMarkException("actual=" + actual)));
        Assertions.assertEquals("actual=A", exception.getMessage());
    }

    @Test
    public void validateArray_expectedMatch_doesNotThrow() {
        newHolder("A").validate(new String[]{"A", "X"}, actual -> new StateMarkException(actual));
    }

    @Test
    public void validateArray_emptyExpected_throwsIllegalArgumentException() {
        final StateHolder<String> holder = newHolder("A");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> holder.validate(new String[]{}, actual -> new StateMarkException(actual)));
    }

    protected static class StateMarkException extends InvalidStateException {
        private static final long serialVersionUID = 1L;

        protected StateMarkException(String message) {
            super(message);
        }
    }
}
