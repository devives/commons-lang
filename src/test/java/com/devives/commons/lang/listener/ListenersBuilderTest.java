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
package com.devives.commons.lang.listener;

import com.devives.commons.lang.Wrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ListenersBuilderTest {

    @Test
    public void build_expectedWrappers() throws Exception {
        Listeners listeners = new ListenersBuilder().build();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(listeners),
                () -> Assertions.assertTrue(Wrapper.class.isInstance(listeners)),
                () -> Assertions.assertTrue(((Wrapper) listeners).isWrapperFor(ListenersStore.class)),
                () -> Assertions.assertFalse(((Wrapper) listeners).isWrapperFor(DistinctListeners.class)),
                () -> Assertions.assertFalse(((Wrapper) listeners).isWrapperFor(IndexedListeners.class)),
                () -> Assertions.assertTrue(((Wrapper) listeners).isWrapperFor(CachedListeners.class)),
                () -> Assertions.assertFalse(((Wrapper) listeners).isWrapperFor(SynchronizedListeners.class))
        );
    }

    @Test
    public void setCached_false_noWrappers() throws Exception {
        Listeners listeners = new ListenersBuilder().setCached(false).build();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(listeners),
                () -> Assertions.assertFalse(Wrapper.class.isInstance(listeners))
        );
    }

    @Test
    public void setDistinct_expectedWrappers() throws Exception {
        Listeners listeners = new ListenersBuilder().setDistinct().build();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(listeners),
                () -> Assertions.assertTrue(Wrapper.class.isInstance(listeners)),
                () -> Assertions.assertTrue(((Wrapper) listeners).isWrapperFor(ListenersStore.class)),
                () -> Assertions.assertTrue(((Wrapper) listeners).isWrapperFor(DistinctListeners.class)),
                () -> Assertions.assertFalse(((Wrapper) listeners).isWrapperFor(IndexedListeners.class)),
                () -> Assertions.assertTrue(((Wrapper) listeners).isWrapperFor(CachedListeners.class)),
                () -> Assertions.assertFalse(((Wrapper) listeners).isWrapperFor(SynchronizedListeners.class))
        );
    }

    @Test
    public void setIndexed_expectedWrappers() throws Exception {
        Listeners listeners = new ListenersBuilder().setIndexed().build();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(listeners),
                () -> Assertions.assertTrue(Wrapper.class.isInstance(listeners)),
                () -> Assertions.assertTrue(((Wrapper) listeners).isWrapperFor(ListenersStore.class)),
                () -> Assertions.assertFalse(((Wrapper) listeners).isWrapperFor(DistinctListeners.class)),
                () -> Assertions.assertTrue(((Wrapper) listeners).isWrapperFor(IndexedListeners.class)),
                () -> Assertions.assertTrue(((Wrapper) listeners).isWrapperFor(CachedListeners.class)),
                () -> Assertions.assertFalse(((Wrapper) listeners).isWrapperFor(SynchronizedListeners.class))
        );
    }

    @Test
    public void setIndexed_setDistinct_expectedWrappers() throws Exception {
        Listeners listeners = new ListenersBuilder().setDistinct().setIndexed().build();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(listeners),
                () -> Assertions.assertTrue(Wrapper.class.isInstance(listeners)),
                () -> Assertions.assertTrue(((Wrapper) listeners).isWrapperFor(ListenersStore.class)),
                () -> Assertions.assertFalse(((Wrapper) listeners).isWrapperFor(DistinctListeners.class)),
                () -> Assertions.assertTrue(((Wrapper) listeners).isWrapperFor(IndexedListeners.class)),
                () -> Assertions.assertTrue(((Wrapper) listeners).isWrapperFor(CachedListeners.class)),
                () -> Assertions.assertFalse(((Wrapper) listeners).isWrapperFor(SynchronizedListeners.class))
        );
    }

    @Test
    public void setSynchronized_expectedWrappers() throws Exception {
        Listeners listeners = new ListenersBuilder().setSynchronized().build();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(listeners),
                () -> Assertions.assertTrue(Wrapper.class.isInstance(listeners)),
                () -> Assertions.assertTrue(((Wrapper) listeners).isWrapperFor(ListenersStore.class)),
                () -> Assertions.assertFalse(((Wrapper) listeners).isWrapperFor(DistinctListeners.class)),
                () -> Assertions.assertFalse(((Wrapper) listeners).isWrapperFor(IndexedListeners.class)),
                () -> Assertions.assertTrue(((Wrapper) listeners).isWrapperFor(CachedListeners.class)),
                () -> Assertions.assertTrue(((Wrapper) listeners).isWrapperFor(SynchronizedListeners.class))
        );
    }

}
