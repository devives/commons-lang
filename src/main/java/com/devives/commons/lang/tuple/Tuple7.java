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

public final class Tuple7<T1, T2, T3, T4, T5, T6, T7> extends Tuple {
    public final T1 _1;
    public final T2 _2;
    public final T3 _3;
    public final T4 _4;
    public final T5 _5;
    public final T6 _6;
    public final T7 _7;

    Tuple7(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6, T7 _7) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
        this._4 = _4;
        this._5 = _5;
        this._6 = _6;
        this._7 = _7;
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
            default:
                throw new IndexOutOfBoundsException(Integer.toString(n));
        }
    }

    @Override
    public int productArity() {
        return 7;
    }

    public Object[] toArray() {
        return new Object[]{_1, _2, _3, _4, _5, _6, _7};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple7<?, ?, ?, ?, ?, ?, ?> tuple7 = (Tuple7<?, ?, ?, ?, ?, ?, ?>) o;
        return Objects.equals(_1, tuple7._1)
                && Objects.equals(_2, tuple7._2)
                && Objects.equals(_3, tuple7._3)
                && Objects.equals(_4, tuple7._4)
                && Objects.equals(_5, tuple7._5)
                && Objects.equals(_6, tuple7._6)
                && Objects.equals(_7, tuple7._7);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3, _4, _5, _6, _7);
    }

    @Override
    public Tuple7<T1, T2, T3, T4, T5, T6, T7> clone() {
        return new Tuple7<>(_1, _2, _3, _4, _5, _6, _7);
    }

}
