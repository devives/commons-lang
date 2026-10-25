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

import com.devives.commons.lang.CloseableBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Function;

/**
 * Красные тесты — доказательство finding из ревью {@code state} пакета: {@link Stateful}
 * объявлен {@link Serializable}, но тип поля {@code stateHolder_} — интерфейс
 * {@link StateHolder}, который {@code Serializable} не наследует и никаких требований
 * к сериализуемости реализаций не предъявляет.
 * <p>
 * Утверждаемое свойство: раз иерархия {@code Stateful} → {@code CloseableBase}
 * (closeable/lifecycle-объекты) обещает сериализуемость, она не должна зависеть от
 * того, какая реализация {@link StateHolder} подставлена, — контракт интерфейса явно
 * допускает сторонние реализации. Сегодня свойство нарушено: {@code writeObject}
 * бросает {@link NotSerializableException} на хранителе, и эти тесты падают.
 * <p>
 * Починка — включить {@code Serializable} в контракт {@link StateHolder} (либо сузить
 * тип поля {@code Stateful} до {@link StateHolderBase}): тесты станут зелёными сами.
 * Если же решено убрать {@code Serializable} из {@code Stateful} — тесты удаляются вместе
 * с обещанием сериализуемости.
 */
public class StatefulSerializationTest {

    /**
     * Красный: сериализация {@link Stateful} со сторонней (не {@code Serializable})
     * реализацией {@link StateHolder} обязана работать — сегодня падает на
     * {@link NotSerializableException}.
     */
    @Test
    public void stateful_mustSerializeWithAnyAdmissibleHolder() throws Exception {
        final PlainStateful restored = roundTrip(new PlainStateful(new PlainHolder<>("A")));
        Assertions.assertEquals("A", restored.state());
    }

    /**
     * Красный: то же для {@code CloseableBase}-наследника — ломается каждый
     * closeable/lifecycle-объект, а не только прямой потомок {@link Stateful}.
     */
    @Test
    public void closeableBase_mustSerializeWithAnyAdmissibleHolder() throws Exception {
        roundTrip(new PlainCloseable(new PlainHolder<>(StateFactory.named("OPENED"))));
    }

    /**
     * Контроль (зелёный): со штатным {@link StateHolderImpl} сериализация проходит —
     * значит падают не «сериализация вообще», а именно отсутствие требования
     * {@code Serializable} в контракте {@link StateHolder}.
     */
    @Test
    public void stateful_withStandardHolder_serializesAndRestoresState() throws Exception {
        final PlainStateful restored = roundTrip(new PlainStateful(new StateHolderImpl<>("A")));
        Assertions.assertEquals("A", restored.state());
    }

    @SuppressWarnings("unchecked")
    private static <T> T roundTrip(Object value) throws Exception {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(value);
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (T) in.readObject();
        }
    }

    /**
     * Сторонняя реализация {@link StateHolder}: ровно та, которую допускает контракт
     * интерфейса. {@code Serializable} не реализует — в этом весь тест.
     */
    private static final class PlainHolder<T> implements StateHolder<T> {
        private static final long serialVersionUID = 1L;
        private T state_;

        PlainHolder(T initialState) {
            state_ = initialState;
        }

        @Override
        public T get() {
            return state_;
        }

        @Override
        public void set(T value) {
            state_ = value;
        }

        @Override
        public boolean trySet(T expected, T value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean trySet(T[] expected, T value) {
            throw new UnsupportedOperationException();
        }

        @SafeVarargs
        @Override
        public final boolean isExpected(T... expected) {
            throw new UnsupportedOperationException();
        }

        @SafeVarargs
        @Override
        public final void validate(T... expected) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <E extends InvalidStateException> void validate(T expected, Function<T, E> exceptionSupplier) throws E {
            throw new UnsupportedOperationException();
        }

        @Override
        public <E extends InvalidStateException> void validate(T[] expected, Function<T, E> exceptionSupplier) throws E {
            throw new UnsupportedOperationException();
        }
    }

    private static final class PlainStateful extends Stateful<String> {
        PlainStateful(StateHolder<String> stateHolder) {
            super(stateHolder);
        }

        String state() {
            return getStateHolder().get();
        }
    }

    private static final class PlainCloseable extends CloseableBase {
        PlainCloseable(StateHolder<State> stateHolder) {
            super(stateHolder);
        }

        @Override
        protected void onClose() {
        }
    }
}
