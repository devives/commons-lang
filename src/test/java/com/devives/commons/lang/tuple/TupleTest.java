/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.devives.commons.lang.tuple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class TupleTest {

    public static Stream<Arguments> getTestTuples() {
        return Stream.of(
                Arguments.arguments(Tuple.of()),
                Arguments.arguments(Tuple.of(1)),
                Arguments.arguments(Tuple.of(1, 2)),
                Arguments.arguments(Tuple.of(1, 2, 3)),
                Arguments.arguments(Tuple.of(1, 2, 3, 4)),
                Arguments.arguments(Tuple.of(1, 2, 3, 4, 5)),
                Arguments.arguments(Tuple.of(1, 2, 3, 4, 5, 6)),
                Arguments.arguments(Tuple.of(1, 2, 3, 4, 5, 6, 7)),
                Arguments.arguments(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8)),
                Arguments.arguments(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9)),
                Arguments.arguments(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
        );
    }

    @ParameterizedTest
    @MethodSource("getTestTuples")
    public void productArity_andArrayLength_areEquals(Tuple tuple) {
        Assertions.assertEquals(tuple.productArity(), tuple.toArray().length);
    }

    @ParameterizedTest
    @MethodSource("getTestTuples")
    public void productElement_andArrayElement_areEquals(Tuple tuple) {
        int i = 0;
        for (Object element : tuple.toArray()) {
            Assertions.assertEquals(element, tuple.productElement(i));
            i++;
        }
    }

    @ParameterizedTest
    @MethodSource("getTestTuples")
    public void productElement_andIteratorElement_areEquals(Tuple tuple) {
        int i = 0;
        for (Object element : tuple) {
            Assertions.assertEquals(element, tuple.productElement(i));
            i++;
        }
    }

    @Test
    public void equals_differentTypeParameters_false() {
        Tuple tuple_1 = Tuple.of(1, 2);
        Tuple tuple_2 = Tuple.of(1L, 2);
        Assertions.assertNotEquals(tuple_1, tuple_2);
    }

    @Test
    public void equals_sameTypeParameters_true() {
        Tuple tuple_1 = Tuple.of(1L, 2);
        Tuple tuple_2 = Tuple.of(1L, 2);
        Assertions.assertEquals(tuple_1, tuple_2);
    }

    @Test
    public void toString_tuple_equalsExpected() {
        Assertions.assertEquals("Tuple10{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}", Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).toString());
    }

    @Test
    public void toString_tupleWithNulls_equalsExpected() {
        Assertions.assertEquals("Tuple2{null, null}", Tuple.of(null, null).toString());
    }

}
