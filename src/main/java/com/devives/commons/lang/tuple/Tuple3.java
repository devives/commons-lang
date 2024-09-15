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
import java.util.Objects;

public final class Tuple3<T1, T2, T3> implements Product {
    public final T1 _1;
    public final T2 _2;
    public final T3 _3;

    public Tuple3(T1 _1, T2 _2, T3 _3) {
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

    @Override
    public Iterator<Object> iterator() {
        return Arrays.stream(new Object[]{_1, _2, _3}).iterator();
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

    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 a1, T2 a2, T3 a3) {
        return new Tuple3<>(a1, a2, a3);
    }
}