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
package com.devives.commons.lifecycle;

import com.devives.commons.lang.SynchronizedLazyCloseableAbst;
import com.devives.commons.util.usage.OrdinalUsage;
import com.devives.commons.util.usage.Usage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        try (Usage usage = OrdinalUsage.of(lazyCloseable)) {
            lazyCloseable.closeAsync();
            Assertions.assertFalse(lazyCloseable.isClosed());
        }
    }

    @Test
    public void isClosed_afterDecUsageCount_True() throws Exception {
        SynchronizedLazyCloseableImpl lazyCloseable = new SynchronizedLazyCloseableImpl();
        try (Usage usage = OrdinalUsage.of(lazyCloseable)) {
            lazyCloseable.closeAsync();
        }
        Assertions.assertTrue(lazyCloseable.isClosed());
    }

    private static class SynchronizedLazyCloseableImpl extends SynchronizedLazyCloseableAbst {

        @Override
        protected void onClose() throws Exception {

        }

        @Override
        public int getUsageCount() {
            return 0;
        }
    }
}


