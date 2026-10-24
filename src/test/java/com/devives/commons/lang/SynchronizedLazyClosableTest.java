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

import com.devives.commons.util.usage.OrdinalUsage;
import com.devives.commons.util.usage.Usage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SynchronizedLazyClosableTest {

    @Test
    public void isClosed_afterNew_False() throws Exception {
        SynchronizedLazyCloseableImpl lazyCloseable = new SynchronizedLazyCloseableImpl();
        Assertions.assertFalse(lazyCloseable.isClosed());
    }

    @Test
    public void isClosed_afterClose_True() throws Exception {
        SynchronizedLazyCloseableImpl lazyCloseable = new SynchronizedLazyCloseableImpl();
        lazyCloseable.closeAsync();
        Assertions.assertTrue(lazyCloseable.isClosed());
    }

    @Test
    public void isClosed_afterIncUsageCount_False() throws Exception {
        SynchronizedLazyCloseableImpl lazyCloseable = new SynchronizedLazyCloseableImpl();
        try (Usage<?> usage = OrdinalUsage.of(lazyCloseable)) {
            lazyCloseable.closeAsync();
            Assertions.assertFalse(lazyCloseable.isClosed());
        }
    }

    @Test
    public void isClosed_afterDecUsageCount_True() throws Exception {
        SynchronizedLazyCloseableImpl lazyCloseable = new SynchronizedLazyCloseableImpl();
        try (Usage<?> usage = OrdinalUsage.of(lazyCloseable)) {
            lazyCloseable.closeAsync();
        }
        Assertions.assertTrue(lazyCloseable.isClosed());
    }

    private static class SynchronizedLazyCloseableImpl extends AbstractSynchronizedLazyCloseable {

        private static final long serialVersionUID = 1L;
        private int someValue = 1;

        public int someFunction() {
            return getStateHolder().performAtomicWork(() -> someValue);

        }


        @Override
        protected void onClose() throws Exception {

        }

        @Override
        public int getUsageCount() {
            return 0;
        }
    }

    @Test
    public void closeAsync_reentrantUsageInOnClose_closed() throws Exception {
        ReentrantLazyCloseableImpl lazyCloseable = new ReentrantLazyCloseableImpl(null);
        Assertions.assertNull(lazyCloseable.closeAsync().toCompletableFuture().get(10, TimeUnit.SECONDS));
        Assertions.assertTrue(lazyCloseable.isClosed());
        Assertions.assertEquals(1, lazyCloseable.onCloseCalls);
    }

    @Test
    public void closeAsync_withExecutor_closed() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            ReentrantLazyCloseableImpl lazyCloseable = new ReentrantLazyCloseableImpl(executor);
            Assertions.assertNull(lazyCloseable.closeAsync().toCompletableFuture().get(10, TimeUnit.SECONDS));
            Assertions.assertTrue(lazyCloseable.isClosed());
            Assertions.assertEquals(1, lazyCloseable.onCloseCalls);
        } finally {
            executor.shutdownNow();
        }
    }

    private static class ReentrantLazyCloseableImpl extends AbstractSynchronizedLazyCloseable {

        private static final long serialVersionUID = 1L;
        private final Executor executor;
        private int onCloseCalls = 0;

        private ReentrantLazyCloseableImpl(Executor executor) {
            this.executor = executor;
        }

        @Override
        protected boolean isReentrantUsageAllowed() {
            return true;
        }

        @Override
        protected Optional<Executor> getLazyCloseExecutor() {
            return Optional.ofNullable(executor);
        }

        @Override
        protected void onClose() throws Exception {
            onCloseCalls++;
            try (Usage<?> usage = OrdinalUsage.of(this)) {
                Assertions.assertEquals(1, lazyClosingDirector_.getUsageCount());
            }
        }

        @Override
        public int getUsageCount() {
            return lazyClosingDirector_.getUsageCount();
        }
    }
}
