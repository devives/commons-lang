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

public final class Tuple1<T1> extends Tuple {
    public final T1 _1;

    Tuple1(T1 _1) {
        this._1 = _1;
    }

    @Override
    public Object productElement(int n) {
        switch (n) {
            case 0:
                return this._1;
            default:
                throw new IndexOutOfBoundsException(Integer.toString(n));
        }
    }

    @Override
    public int productArity() {
        return 1;
    }

    public Object[] toArray() {
        return new Object[]{_1};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;
        return Objects.equals(_1, tuple2._1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1);
    }

    @Override
    public Tuple1<T1> clone() {
        return new Tuple1<>(_1);
    }
}
