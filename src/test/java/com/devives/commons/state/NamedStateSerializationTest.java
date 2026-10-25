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

import com.devives.commons.lang.AbstractCloseable;
import com.devives.commons.lang.CloseableStates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Поведение состояний и closeable-объектов при сериализации (сериализация → десериализация).
 * <p>
 * Состояние {@link NamedState} {@code Serializable}, несёт имя
 * ({@link NamedState#getName()}) и участвует в проверках эквивалентности через
 * {@code equals}/{@code hashCode}. Тесты проверяют, что после round-trip копия состояния
 * эквивалентна тому же именованному состоянию — то есть его эквивалентность определяется
 * именем, а не ссылкой на объект. От этого зависят свойства верхнего уровня: закрытый
 * объект остаётся закрытым, а {@code onClose()} не выполняется повторно.
 */
public class NamedStateSerializationTest {

    /**
     * Эквивалентность состояний после round-trip определяется именем: десериализованная
     * копия равна константе {@link CloseableStates#CLOSED} с тем же именем.
     */
    @Test
    public void roundTrippedNamedState_mustEqualSameNamedConstant() throws Exception {
        final NamedState restored = roundTrip(CloseableStates.CLOSED);
        Assertions.assertEquals(CloseableStates.CLOSED, restored);
    }

    /**
     * Closeable-объект, закрытый до сериализации, после десериализации остаётся закрытым:
     * {@code isClosed()} возвращает {@code true}.
     */
    @Test
    public void deserializedClosedObject_mustStillReportClosed() throws Exception {
        final TestCloseable restored = roundTrip(closedTestCloseable());
        Assertions.assertTrue(restored.isClosed(),
                "десериализованный закрытый объект обязан оставаться закрытым");
    }

    /**
     * Для объекта, уже прошедшего закрытие, повторный {@code close()} после десериализации
     * не выполняет {@code onClose()} снова. Счётчик закрытий переживает round-trip —
     * стартовое значение проверяется отдельно.
     */
    @Test
    public void deserializedClosedObject_mustNotRunOnCloseAgain() throws Exception {
        final TestCloseable restored = roundTrip(closedTestCloseable());
        Assertions.assertEquals(1, restored.closeCount(),
                "round-trip обязан сохранить след уже выполненного onClose()");
        restored.close();
        Assertions.assertEquals(1, restored.closeCount(),
                "закрытый объект не должен выполнять onClose() повторно");
    }

    /**
     * Закрытый объект без сериализации корректно сообщает своё состояние — логика
     * состояний работает независимо от сериализации.
     */
    @Test
    public void nonSerializedClosedObject_reportsClosed() throws Exception {
        Assertions.assertTrue(closedTestCloseable().isClosed());
    }

    /**
     * Round-trip сохраняет имя состояния.
     */
    @Test
    public void serialization_preservesStateName() throws Exception {
        final NamedState restored = roundTrip(CloseableStates.CLOSED);
        Assertions.assertEquals("CLOSED", restored.getName());
    }

    /**
     * Создает объект в штатном состоянии {@code OPENED} и закрывает его вызовом
     * {@code close()}: {@code onClose()} выполнен ровно один раз, счётчик закрытий равён 1.
     */
    private static TestCloseable closedTestCloseable() throws Exception {
        final TestCloseable closeable = new TestCloseable();
        closeable.close();
        return closeable;
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
     * Closeable-объект со счётчиком вызовов {@code onClose()}; счётчик сериализуется
     * вместе с состоянием.
     */
    private static final class TestCloseable extends AbstractCloseable {
        private int closeCount_;

        @Override
        protected void onClose() throws Exception {
            closeCount_++;
        }

        int closeCount() {
            return closeCount_;
        }
    }
}
