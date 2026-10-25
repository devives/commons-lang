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
 * Неизменяемый кортеж из 10 элементов.
 *
 * @param <T1> тип элемента 1
 * @param <T2> тип элемента 2
 * @param <T3> тип элемента 3
 * @param <T4> тип элемента 4
 * @param <T5> тип элемента 5
 * @param <T6> тип элемента 6
 * @param <T7> тип элемента 7
 * @param <T8> тип элемента 8
 * @param <T9> тип элемента 9
 * @param <T10> тип элемента 10
 */
public final class Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> extends Tuple {
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
    /**
     * Элемент 4.
     */
    public final T4 _4;
    /**
     * Элемент 5.
     */
    public final T5 _5;
    /**
     * Элемент 6.
     */
    public final T6 _6;
    /**
     * Элемент 7.
     */
    public final T7 _7;
    /**
     * Элемент 8.
     */
    public final T8 _8;
    /**
     * Элемент 9.
     */
    public final T9 _9;
    /**
     * Элемент 10.
     */
    public final T10 _10;

    Tuple10(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6, T7 _7, T8 _8, T9 _9, T10 _10) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
        this._4 = _4;
        this._5 = _5;
        this._6 = _6;
        this._7 = _7;
        this._8 = _8;
        this._9 = _9;
        this._10 = _10;
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
            case 3:
                return this._4;
            case 4:
                return this._5;
            case 5:
                return this._6;
            case 6:
                return this._7;
            case 7:
                return this._8;
            case 8:
                return this._9;
            case 9:
                return this._10;
            default:
                throw new IndexOutOfBoundsException(Integer.toString(n));
        }
    }

    @Override
    public int productArity() {
        return 10;
    }

    public Object[] toArray() {
        return new Object[]{_1, _2, _3, _4, _5, _6, _7, _8, _9, _10};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple10<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> tuple10 = (Tuple10<?, ?, ?, ?, ?, ?, ?, ?, ?, ?>) o;
        return Objects.equals(_1, tuple10._1)
                && Objects.equals(_2, tuple10._2)
                && Objects.equals(_3, tuple10._3)
                && Objects.equals(_4, tuple10._4)
                && Objects.equals(_5, tuple10._5)
                && Objects.equals(_6, tuple10._6)
                && Objects.equals(_7, tuple10._7)
                && Objects.equals(_8, tuple10._8)
                && Objects.equals(_9, tuple10._9)
                && Objects.equals(_10, tuple10._10);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10);
    }

    @Override
    public Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> clone() {
        return new Tuple10<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10);
    }
}
