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
import com.devives.commons.lang.ref.VolatileRef;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class VolatileRefTest {

    @Test
    public void of_nonNullValue_holdsValue() {
        VolatileRef<Integer> ref = VolatileRef.of(1);

        Assertions.assertEquals(1, ref.get());
        Assertions.assertFalse(ref.isNullable());
        Assertions.assertTrue(ref.isPresent());
        Assertions.assertFalse(ref.isNull());
    }

    @Test
    public void of_nullValue_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> VolatileRef.of(null));
    }

    @Test
    public void of_setNull_throwsNullPointerExceptionAndKeepsValue() {
        VolatileRef<Integer> ref = VolatileRef.of(1);

        Assertions.assertThrows(NullPointerException.class, () -> ref.set(null));
        Assertions.assertEquals(1, ref.get());
    }

    @Test
    public void of_setNonNull_replacesValue() {
        VolatileRef<Integer> ref = VolatileRef.of(1);
        ref.set(2);

        Assertions.assertEquals(2, ref.get());
    }

    @Test
    public void ofNullable_nullValue_holdsNull() {
        VolatileRef<Integer> ref = VolatileRef.ofNullable(null);

        Assertions.assertNull(ref.get());
        Assertions.assertTrue(ref.isNullable());
        Assertions.assertTrue(ref.isNull());
        Assertions.assertFalse(ref.isPresent());
    }

    @Test
    public void ofNullable_noArgs_holdsNull() {
        VolatileRef<Integer> ref = VolatileRef.ofNullable();

        Assertions.assertNull(ref.get());
        Assertions.assertTrue(ref.isNullable());
    }

    @Test
    public void ofNullable_setNull_clearsValue() {
        VolatileRef<Integer> ref = VolatileRef.ofNullable(1);
        ref.set(null);

        Assertions.assertNull(ref.get());
    }

    @Test
    public void orElse_nullValue_returnsOther() {
        Assertions.assertEquals(2, VolatileRef.<Integer>ofNullable(null).orElse(2));
        Assertions.assertEquals(2, VolatileRef.<Integer>ofNullable(null).orElseGet(() -> 2));
    }

    @Test
    public void orElse_presentValue_returnsValue() {
        Assertions.assertEquals(1, VolatileRef.of(1).orElse(2));
        Assertions.assertEquals(1, VolatileRef.of(1).orElseGet(() -> 2));
    }

    @Test
    public void ifPresent_invokesConsumerOnlyForPresentValue() {
        List<Integer> accepted = new ArrayList<>();

        VolatileRef.of(1).ifPresent(accepted::add);
        VolatileRef.<Integer>ofNullable(null).ifPresent(accepted::add);

        Assertions.assertEquals(1, accepted.size());
        Assertions.assertEquals(1, accepted.get(0));
    }

    @Test
    public void equals_sameValueDifferentImplementation_areEqual() {
        Ref<Integer> simple = Ref.of(1);
        Ref<Integer> volatileRef = VolatileRef.of(1);

        Assertions.assertEquals(simple, volatileRef);
        Assertions.assertEquals(volatileRef, simple);
        Assertions.assertEquals(simple.hashCode(), volatileRef.hashCode());
        Assertions.assertEquals(VolatileRef.ofNullable(null), Ref.ofNullable(null));
        Assertions.assertNotEquals(VolatileRef.of(1), VolatileRef.of(2));
    }

    @Test
    public void set_visibleFromOtherThread() throws InterruptedException {
        VolatileRef<Integer> ref = VolatileRef.of(1);
        Thread writer = new Thread(() -> ref.set(2));
        writer.start();
        writer.join();

        Assertions.assertEquals(2, ref.get());
    }

    @Test
    public void toString_containsValue() {
        Assertions.assertEquals("Ref[1]", VolatileRef.of(1).toString());
        Assertions.assertEquals("Ref[null]", VolatileRef.ofNullable(null).toString());
    }

}
