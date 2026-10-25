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

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Абстрактный продукт.
 * <p>
 * Является точкой входа для создания кортежей: {@code Tuple.of(...)}.
 */
public abstract class Tuple implements Product, Iterable<Object> {

    Tuple() {
    }

    /**
     * Создаёт кортеж без элементов.
     *
     * @return новый кортеж.
     */
    public static Tuple0 of() {
        return new Tuple0();
    }

    /**
     * Создаёт кортеж из одного элемента.
     *
     * @param _1   элемент 1.
     * @param <T1> тип элемента 1.
     * @return новый кортеж.
     */
    public static <T1> Tuple1<T1> of(T1 _1) {
        return new Tuple1<>(_1);
    }

    /**
     * Создаёт кортеж из 2 элементов.
     *
     * @param _1   элемент 1.
     * @param _2   элемент 2.
     * @param <T1> тип элемента 1.
     * @param <T2> тип элемента 2.
     * @return новый кортеж.
     */
    public static <T1, T2> Tuple2<T1, T2> of(T1 _1, T2 _2) {
        return new Tuple2<>(_1, _2);
    }

    /**
     * Создаёт кортеж из 3 элементов.
     *
     * @param _1   элемент 1.
     * @param _2   элемент 2.
     * @param _3   элемент 3.
     * @param <T1> тип элемента 1.
     * @param <T2> тип элемента 2.
     * @param <T3> тип элемента 3.
     * @return новый кортеж.
     */
    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 _1, T2 _2, T3 _3) {
        return new Tuple3<>(_1, _2, _3);
    }

    /**
     * Создаёт кортеж из 4 элементов.
     *
     * @param _1   элемент 1.
     * @param _2   элемент 2.
     * @param _3   элемент 3.
     * @param _4   элемент 4.
     * @param <T1> тип элемента 1.
     * @param <T2> тип элемента 2.
     * @param <T3> тип элемента 3.
     * @param <T4> тип элемента 4.
     * @return новый кортеж.
     */
    public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> of(T1 _1, T2 _2, T3 _3, T4 _4) {
        return new Tuple4<>(_1, _2, _3, _4);
    }

    /**
     * Создаёт кортеж из 5 элементов.
     *
     * @param _1   элемент 1.
     * @param _2   элемент 2.
     * @param _3   элемент 3.
     * @param _4   элемент 4.
     * @param _5   элемент 5.
     * @param <T1> тип элемента 1.
     * @param <T2> тип элемента 2.
     * @param <T3> тип элемента 3.
     * @param <T4> тип элемента 4.
     * @param <T5> тип элемента 5.
     * @return новый кортеж.
     */
    public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5) {
        return new Tuple5<>(_1, _2, _3, _4, _5);
    }

    /**
     * Создаёт кортеж из 6 элементов.
     *
     * @param _1   элемент 1.
     * @param _2   элемент 2.
     * @param _3   элемент 3.
     * @param _4   элемент 4.
     * @param _5   элемент 5.
     * @param _6   элемент 6.
     * @param <T1> тип элемента 1.
     * @param <T2> тип элемента 2.
     * @param <T3> тип элемента 3.
     * @param <T4> тип элемента 4.
     * @param <T5> тип элемента 5.
     * @param <T6> тип элемента 6.
     * @return новый кортеж.
     */
    public static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> of(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6) {
        return new Tuple6<>(_1, _2, _3, _4, _5, _6);
    }

    /**
     * Создаёт кортеж из 7 элементов.
     *
     * @param _1   элемент 1.
     * @param _2   элемент 2.
     * @param _3   элемент 3.
     * @param _4   элемент 4.
     * @param _5   элемент 5.
     * @param _6   элемент 6.
     * @param _7   элемент 7.
     * @param <T1> тип элемента 1.
     * @param <T2> тип элемента 2.
     * @param <T3> тип элемента 3.
     * @param <T4> тип элемента 4.
     * @param <T5> тип элемента 5.
     * @param <T6> тип элемента 6.
     * @param <T7> тип элемента 7.
     * @return новый кортеж.
     */
    public static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> of(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6, T7 _7) {
        return new Tuple7<>(_1, _2, _3, _4, _5, _6, _7);
    }

    /**
     * Создаёт кортеж из 8 элементов.
     *
     * @param _1   элемент 1.
     * @param _2   элемент 2.
     * @param _3   элемент 3.
     * @param _4   элемент 4.
     * @param _5   элемент 5.
     * @param _6   элемент 6.
     * @param _7   элемент 7.
     * @param _8   элемент 8.
     * @param <T1> тип элемента 1.
     * @param <T2> тип элемента 2.
     * @param <T3> тип элемента 3.
     * @param <T4> тип элемента 4.
     * @param <T5> тип элемента 5.
     * @param <T6> тип элемента 6.
     * @param <T7> тип элемента 7.
     * @param <T8> тип элемента 8.
     * @return новый кортеж.
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> of(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6, T7 _7, T8 _8) {
        return new Tuple8<>(_1, _2, _3, _4, _5, _6, _7, _8);
    }

    /**
     * Создаёт кортеж из 9 элементов.
     *
     * @param _1   элемент 1.
     * @param _2   элемент 2.
     * @param _3   элемент 3.
     * @param _4   элемент 4.
     * @param _5   элемент 5.
     * @param _6   элемент 6.
     * @param _7   элемент 7.
     * @param _8   элемент 8.
     * @param _9   элемент 9.
     * @param <T1> тип элемента 1.
     * @param <T2> тип элемента 2.
     * @param <T3> тип элемента 3.
     * @param <T4> тип элемента 4.
     * @param <T5> тип элемента 5.
     * @param <T6> тип элемента 6.
     * @param <T7> тип элемента 7.
     * @param <T8> тип элемента 8.
     * @param <T9> тип элемента 9.
     * @return новый кортеж.
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> of(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6, T7 _7, T8 _8, T9 _9) {
        return new Tuple9<>(_1, _2, _3, _4, _5, _6, _7, _8, _9);
    }

    /**
     * Создаёт кортеж из 10 элементов.
     *
     * @param _1   элемент 1.
     * @param _2   элемент 2.
     * @param _3   элемент 3.
     * @param _4   элемент 4.
     * @param _5   элемент 5.
     * @param _6   элемент 6.
     * @param _7   элемент 7.
     * @param _8   элемент 8.
     * @param _9   элемент 9.
     * @param _10   элемент 10.
     * @param <T1> тип элемента 1.
     * @param <T2> тип элемента 2.
     * @param <T3> тип элемента 3.
     * @param <T4> тип элемента 4.
     * @param <T5> тип элемента 5.
     * @param <T6> тип элемента 6.
     * @param <T7> тип элемента 7.
     * @param <T8> тип элемента 8.
     * @param <T9> тип элемента 9.
     * @param <T10> тип элемента 10.
     * @return новый кортеж.
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> of(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6, T7 _7, T8 _8, T9 _9, T10 _10) {
        return new Tuple10<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10);
    }

    /**
     * Возвращает элементы кортежа в виде массива.
     *
     * @return новый массив элементов в порядке их следования.
     */
    public abstract Object[] toArray();

    @Override
    public final Iterator<Object> iterator() {
        return Arrays.stream(toArray()).iterator();
    }

    @Override
    public final String toString() {
        return "Tuple" + productArity() + "{" + String.join(", ", Stream.of(toArray()).map(elem -> String.valueOf(elem)).toArray(String[]::new)) + "}";
    }
}
