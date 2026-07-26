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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AutoCloseableUtilsTest {

    @Test
    public void closeIfAutoCloseable_autoCloseable_closesObject() {
        TestCloseable closeable = new TestCloseable();

        AutoCloseableUtils.closeIfAutoCloseable(closeable);

        Assertions.assertEquals(1, closeable.getCloseCount());
    }

    @Test
    public void closeIfAutoCloseable_nonAutoCloseable_doesNothing() {
        Assertions.assertDoesNotThrow(() -> AutoCloseableUtils.closeIfAutoCloseable(new Object()));
    }

    @Test
    public void closeIfAutoCloseable_varargs_closesAutoCloseableObjectsAndSkipsOthers() {
        TestCloseable first = new TestCloseable();
        TestCloseable second = new TestCloseable();

        AutoCloseableUtils.closeIfAutoCloseable(new Object(), null, first, second);

        Assertions.assertEquals(1, first.getCloseCount());
        Assertions.assertEquals(1, second.getCloseCount());
    }

    @Test
    public void closeIfAutoCloseable_closeThrows_rethrowsOriginalException() {
        Exception exception = new Exception("close failed");
        TestCloseable closeable = new TestCloseable(exception);

        Exception thrown = Assertions.assertThrows(
                Exception.class,
                () -> AutoCloseableUtils.closeIfAutoCloseable((Object[]) new Object[]{closeable})
        );

        Assertions.assertSame(exception, thrown);
        Assertions.assertEquals(1, closeable.getCloseCount());
    }

    private static class TestCloseable implements AutoCloseable {
        private final Exception exception_;
        private int closeCount_;

        private TestCloseable() {
            this(null);
        }

        private TestCloseable(Exception exception) {
            exception_ = exception;
        }

        @Override
        public void close() throws Exception {
            closeCount_++;
            if (exception_ != null) {
                throw exception_;
            }
        }

        private int getCloseCount() {
            return closeCount_;
        }
    }
}
