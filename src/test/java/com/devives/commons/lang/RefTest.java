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
package com.devives.commons.lang;

import com.devives.commons.lang.ref.Ref;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class RefTest {

    @Test
    public void of_nonNullValue_holdsValue() {
        Ref<Integer> ref = Ref.of(1);

        Assertions.assertEquals(1, ref.get());
        Assertions.assertFalse(ref.isNullable());
        Assertions.assertTrue(ref.isPresent());
        Assertions.assertFalse(ref.isNull());
    }

    @Test
    public void of_nullValue_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> Ref.of(null));
    }

    @Test
    public void of_setNull_throwsNullPointerExceptionAndKeepsValue() {
        Ref<Integer> ref = Ref.of(1);

        Assertions.assertThrows(NullPointerException.class, () -> ref.set(null));
        Assertions.assertEquals(1, ref.get());
    }

    @Test
    public void of_setNonNull_replacesValue() {
        Ref<Integer> ref = Ref.of(1);
        ref.set(2);

        Assertions.assertEquals(2, ref.get());
    }

    @Test
    public void ofNullable_nullValue_holdsNull() {
        Ref<Integer> ref = Ref.ofNullable(null);

        Assertions.assertNull(ref.get());
        Assertions.assertTrue(ref.isNullable());
        Assertions.assertTrue(ref.isNull());
        Assertions.assertFalse(ref.isPresent());
    }

    @Test
    public void ofNullable_setNull_clearsValue() {
        Ref<Integer> ref = Ref.ofNullable(1);
        ref.set(null);

        Assertions.assertNull(ref.get());
    }

    @Test
    public void orElse_nullValue_returnsOther() {
        Assertions.assertEquals(2, Ref.<Integer>ofNullable(null).orElse(2));
        Assertions.assertEquals(2, Ref.<Integer>ofNullable(null).orElseGet(() -> 2));
    }

    @Test
    public void orElse_presentValue_returnsValue() {
        Assertions.assertEquals(1, Ref.of(1).orElse(2));
        Assertions.assertEquals(1, Ref.of(1).orElseGet(() -> 2));
    }

    @Test
    public void ifPresent_invokesConsumerOnlyForPresentValue() {
        List<Integer> accepted = new ArrayList<>();

        Ref.of(1).ifPresent(accepted::add);
        Ref.<Integer>ofNullable(null).ifPresent(accepted::add);

        Assertions.assertEquals(1, accepted.size());
        Assertions.assertEquals(1, accepted.get(0));
    }

    @Test
    public void equals_sameValueDifferentNullability_areEqual() {
        Ref<Integer> nonNullable = Ref.of(1);
        Ref<Integer> nullable = Ref.ofNullable(1);

        Assertions.assertEquals(nonNullable, nullable);
        Assertions.assertEquals(nullable, nonNullable);
        Assertions.assertEquals(nonNullable.hashCode(), nullable.hashCode());
        Assertions.assertNotEquals(Ref.of(1), Ref.of(2));
        Assertions.assertNotEquals(Ref.of(1), 1);
    }

    @Test
    public void equals_bothNull_areEqual() {
        Assertions.assertEquals(Ref.ofNullable(null), Ref.ofNullable(null));
        Assertions.assertEquals(0, Ref.ofNullable(null).hashCode());
    }

    @Test
    public void toString_containsValue() {
        Assertions.assertEquals("Ref[1]", Ref.of(1).toString());
        Assertions.assertEquals("Ref[null]", Ref.ofNullable(null).toString());
    }

}
