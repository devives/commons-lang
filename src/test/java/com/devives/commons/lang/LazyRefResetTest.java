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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class LazyRefResetTest {

    @Test
    public void lazyRef_reset_reinitializesOnNextGet() {
        final AtomicInteger counter = new AtomicInteger();
        final LazyRef<Integer> ref = new LazyRef<>(counter::incrementAndGet);

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
        final SynchronizedLazyRef<Integer> ref = new SynchronizedLazyRef<>(counter::incrementAndGet);

        Assertions.assertEquals(1, ref.get());
        Assertions.assertEquals(1, ref.get());

        ref.reset();

        Assertions.assertFalse(ref.isPresent());
        Assertions.assertEquals(2, ref.get());
        Assertions.assertEquals(2, counter.get());
    }

}
