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

import com.devives.commons.lang.ref.LazyRef;
import com.devives.commons.lang.ref.SynchronizedLazyRef;
import com.devives.commons.lang.ref.UnsynchronizedLazyRef;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class LazyRefResetTest {

    @Test
    public void unsynchronizedLazyRef_reset_reinitializesOnNextGet() {
        final AtomicInteger counter = new AtomicInteger();
        final LazyRef<Integer> ref = new UnsynchronizedLazyRef<>(counter::incrementAndGet);

        Assertions.assertEquals(1, ref.get());
        Assertions.assertEquals(1, ref.get());

        ref.reset();

        Assertions.assertFalse(ref.isPresent());
        Assertions.assertEquals(2, ref.get());
        Assertions.assertEquals(2, counter.get());
    }

    @Test
    public void synchronizedLazyRef_reset_reinitializesOnNextGet() {
        final AtomicInteger counter = new AtomicInteger();
        final LazyRef<Integer> ref = new SynchronizedLazyRef<>(counter::incrementAndGet);

        Assertions.assertEquals(1, ref.get());
        Assertions.assertEquals(1, ref.get());

        ref.reset();

        Assertions.assertFalse(ref.isPresent());
        Assertions.assertEquals(2, ref.get());
        Assertions.assertEquals(2, counter.get());
    }

    @Test
    public void unsynchronizedLazyRef_set_skipsInitiator() {
        assertSetSkipsInitiator(UnsynchronizedLazyRef::new);
    }

    @Test
    public void synchronizedLazyRef_set_skipsInitiator() {
        assertSetSkipsInitiator(SynchronizedLazyRef::new);
    }

    @Test
    public void of_withInitiator_returnsLazyUnsynchronizedRef() {
        final AtomicInteger counter = new AtomicInteger();
        final LazyRef<Integer> ref = LazyRef.of(counter::incrementAndGet);

        Assertions.assertInstanceOf(UnsynchronizedLazyRef.class, ref);
        Assertions.assertEquals(0, counter.get());
        Assertions.assertEquals(1, ref.get());
    }

    @Test
    public void of_withoutInitiator_returnsUnsynchronizedRef() {
        final LazyRef<Integer> ref = LazyRef.of();

        Assertions.assertInstanceOf(UnsynchronizedLazyRef.class, ref);
        Assertions.assertThrows(NullPointerException.class, ref::get);
        Assertions.assertEquals(42, ref.getOrCreate(() -> 42));
    }

    @Test
    public void ofSynchronized_withInitiator_returnsLazySynchronizedRef() {
        final AtomicInteger counter = new AtomicInteger();
        final LazyRef<Integer> ref = LazyRef.ofSynchronized(counter::incrementAndGet);

        Assertions.assertInstanceOf(SynchronizedLazyRef.class, ref);
        Assertions.assertEquals(0, counter.get());
        Assertions.assertEquals(1, ref.get());
    }

    @Test
    public void ofSynchronized_withoutInitiator_returnsSynchronizedRef() {
        final LazyRef<Integer> ref = LazyRef.ofSynchronized();

        Assertions.assertInstanceOf(SynchronizedLazyRef.class, ref);
        Assertions.assertThrows(NullPointerException.class, ref::get);
        Assertions.assertEquals(42, ref.getOrCreate(() -> 42));
    }

    private static void assertSetSkipsInitiator(Function<Supplier<Integer>, LazyRef<Integer>> factory) {
        final AtomicInteger counter = new AtomicInteger();
        final LazyRef<Integer> ref = factory.apply(counter::incrementAndGet);

        ref.set(42);

        Assertions.assertTrue(ref.isPresent());
        Assertions.assertEquals(42, ref.get());
        Assertions.assertEquals(0, counter.get());
    }

}
