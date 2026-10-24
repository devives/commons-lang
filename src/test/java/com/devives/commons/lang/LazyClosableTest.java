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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class LazyClosableTest {

    @Test
    public void closeAsync_reentrantUsageInOnCloseByDefault_futureCompletedExceptionally() throws Exception {
        ReentrantLazyCloseableImpl lazyCloseable = new ReentrantLazyCloseableImpl(false);
        lazyCloseable.closeAsync();
        Future<Void> future = lazyCloseable.lazyClosingDirector_.getLazyCloseFuture();
        Assertions.assertThrows(ExecutionException.class, () -> future.get(10, TimeUnit.SECONDS));
        Assertions.assertEquals(1, lazyCloseable.onCloseCalls);
    }

    @Test
    public void closeAsync_reentrantUsageInOnClose_closed() throws Exception {
        ReentrantLazyCloseableImpl lazyCloseable = new ReentrantLazyCloseableImpl(true);
        try (Usage<?> usage = OrdinalUsage.of(lazyCloseable)) {
            lazyCloseable.closeAsync();
            Assertions.assertFalse(lazyCloseable.isClosed());
        }
        Assertions.assertNull(lazyCloseable.closeAsync().toCompletableFuture().get(10, TimeUnit.SECONDS));
        Assertions.assertTrue(lazyCloseable.isClosed());
        Assertions.assertEquals(1, lazyCloseable.onCloseCalls);
    }

    private static class ReentrantLazyCloseableImpl extends AbstractLazyCloseable {

        private static final long serialVersionUID = 1L;
        private final boolean reentrantUsageAllowed;
        private int onCloseCalls = 0;

        private ReentrantLazyCloseableImpl(boolean reentrantUsageAllowed) {
            this.reentrantUsageAllowed = reentrantUsageAllowed;
        }

        @Override
        protected boolean isReentrantUsageAllowed() {
            return reentrantUsageAllowed;
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
