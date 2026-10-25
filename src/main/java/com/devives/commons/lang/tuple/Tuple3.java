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
package com.devives.commons.lang.tuple;

import java.util.Objects;

/**
 * Неизменяемый кортеж из 3 элементов.
 *
 * @param <T1> тип элемента 1
 * @param <T2> тип элемента 2
 * @param <T3> тип элемента 3
 */
public final class Tuple3<T1, T2, T3> extends Tuple {
    /**
     * Элемент 1.
     */
    public final T1 _1;
    /**
     * Элемент 2.
     */
    public final T2 _2;
    /**
     * Элемент 3.
     */
    public final T3 _3;

    Tuple3(T1 _1, T2 _2, T3 _3) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }

    @Override
    public Object productElement(int n) {
        switch (n) {
            case 0:
                return this._1;
            case 1:
                return this._2;
            case 2:
                return this._3;
            default:
                throw new IndexOutOfBoundsException(Integer.toString(n));
        }
    }

    @Override
    public int productArity() {
        return 3;
    }

    public Object[] toArray() {
        return new Object[]{_1, _2, _3};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) o;
        return Objects.equals(_1, tuple3._1)
                && Objects.equals(_2, tuple3._2)
                && Objects.equals(_3, tuple3._3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3);
    }

    /**
     * Создаёт кортеж из трёх элементов.
     *
     * @param a1   элемент 1.
     * @param a2   элемент 2.
     * @param a3   элемент 3.
     * @param <T1> тип элемента 1.
     * @param <T2> тип элемента 2.
     * @param <T3> тип элемента 3.
     * @return новый кортеж.
     * @deprecated используйте {@link Tuple#of(Object, Object, Object)}.
     */
    @Deprecated
    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 a1, T2 a2, T3 a3) {
        return new Tuple3<>(a1, a2, a3);
    }

    @Override
    public Tuple3<T1, T2, T3> clone() {
        return new Tuple3<>(_1, _2, _3);
    }
}