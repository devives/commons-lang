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
package com.devives.commons.lang.call;

import com.devives.commons.lang.Ref;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

public class TryTest {

    @Test
    public void callable_ThrownThreeNestedExceptions() throws Exception {
        Exception exception = null;
        try {
            Long result = Try.<Long>callable(() -> {
                throw new NullPointerException("1");
            }).onCatch((th) -> {
                throw new IOException("2");
            }).doFinally(() -> {
                throw new IndexOutOfBoundsException("3");
            }).call();
        } catch (Exception e) {
            exception = e;
        }
        Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(IndexOutOfBoundsException.class, finalException),
                () -> Assertions.assertInstanceOf(IOException.class, finalException.getSuppressed()[0]),
                () -> Assertions.assertInstanceOf(NullPointerException.class, finalException.getSuppressed()[0].getSuppressed()[0])
        );
    }

    @Test
    public void callable_ThrownTwoNestedExceptions() throws Exception {
        Exception exception = null;
        try {
            Long result = Try.<Long>callable(() -> {
                throw new NullPointerException("1");
            }).doFinally(() -> {
                throw new IndexOutOfBoundsException("2");
            }).call();
        } catch (Exception e) {
            exception = e;
        }
        Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(IndexOutOfBoundsException.class, finalException),
                () -> Assertions.assertInstanceOf(NullPointerException.class, finalException.getSuppressed()[0])
        );
    }


    @Test
    public void callable_CallableCalled() throws Exception {
        Long result = Try.callable(() -> {
            return 1L;
        }).onCatch((th) -> {
            return 2L;
        }).call();
        Assertions.assertEquals(1L, result);
    }

    @Test
    public void callable_Success_CatchWasNotCalled() throws Exception {
        Ref<Boolean> finallyRef = new Ref<>(false);
        Long result = Try.callable(() -> {
            return 1L;
        }).onCatch((th) -> {
            finallyRef.set(true);
            return 2L;
        }).call();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, result),
                () -> Assertions.assertFalse(finallyRef.get())
        );
    }

    @Test
    public void callable_Failed_CatchWasCalled() throws Exception {
        Ref<Boolean> finallyRef = new Ref<>(false);
        Long result = Try.<Long>callable(() -> {
            throw new RuntimeException("1");
        }).onCatch((th) -> {
            finallyRef.set(true);
            return 2L;
        }).call();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2L, result),
                () -> Assertions.assertTrue(finallyRef.get())
        );
    }

    @Test
    public void callable_FinallyWasRun() throws Exception {
        Ref<Boolean> finallyRef = new Ref<>();
        Long result = Try.callable(() -> {
            return 1L;
        }).doFinally(() -> {
            finallyRef.set(true);
        }).call();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, result),
                () -> Assertions.assertTrue(finallyRef.get())
        );
    }

    @Test
    public void callable_CatchAndFinallyCalled() throws Exception {
        Ref<Boolean> finallyRef = new Ref<>();
        Long result = Try.<Long>callable(() -> {
            throw new UnsupportedOperationException("1");
        }).onCatch((th) -> {
            return 2L;
        }).doFinally(() -> {
            finallyRef.set(true);
        }).call();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2L, result),
                () -> Assertions.assertTrue(finallyRef.get())
        );
    }

    @Test
    public void callable_CallableAndFinallyCalled() throws Exception {
        Ref<Boolean> finallyRef = new Ref<>();
        Long result = Try.callable(() -> {
            return 1L;
        }).onCatch((th) -> {
            return 2L;
        }).doFinally(() -> {
            finallyRef.set(true);
        }).call();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, result),
                () -> Assertions.assertTrue(finallyRef.get())
        );
    }

    @Test
    public void callable_NoMemoryLeak() throws Exception {
        final Object testObject = new Object();
        TryCallable try_ = Try.callable(() -> {
            return testObject;
        });
        Assertions.assertEquals(testObject, try_.doFinally(() -> {
        }).call());
        Field field = try_.getClass().getDeclaredField("result");
        field.setAccessible(true);
        Assertions.assertNull(field.get(try_));
    }

    @Test
    public void callable_BiCall_DifferentValues() throws Exception {
        final AtomicLong sequence = new AtomicLong(0);
        TryCallable.Finally try_ = Try.callable(() -> {
            return sequence.incrementAndGet();
        }).doFinally(() -> {

        });
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, try_.call()),
                () -> Assertions.assertThrows(RuntimeException.class, () -> try_.call())
        );

    }


    @Test
    public void runnable_ThrownThreeNestedExceptions() throws Exception {
        Exception exception = null;
        try {
            Try.runnable(() -> {
                throw new UnsupportedOperationException("1");
            }).onCatch((th) -> {
                throw new IOException("2");
            }).doFinally(() -> {
                throw new IndexOutOfBoundsException("3");
            }).run();
        } catch (Exception e) {
            exception = e;
        }
        Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(IndexOutOfBoundsException.class, finalException),
                () -> Assertions.assertInstanceOf(IOException.class, finalException.getSuppressed()[0]),
                () -> Assertions.assertInstanceOf(UnsupportedOperationException.class, finalException.getSuppressed()[0].getSuppressed()[0])
        );
    }

    @Test
    public void runnable_ThrownTwoNestedExceptions() throws Exception {
        Exception exception = null;
        try {
            Try.runnable(() -> {
                throw new NullPointerException("1");
            }).doFinally(() -> {
                throw new IndexOutOfBoundsException("2");
            }).run();
        } catch (Exception e) {
            exception = e;
        }
        Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(IndexOutOfBoundsException.class, finalException),
                () -> Assertions.assertInstanceOf(NullPointerException.class, finalException.getSuppressed()[0])
        );
    }

    @Test
    public void runnable_RunnableWasRun() throws Exception {
        Ref<Long> resultRef = new Ref<>();
        Try.runnable(() -> {
            resultRef.set(1L);
        }).onCatch((th) -> {
            resultRef.set(2L);
        }).run();
        Assertions.assertEquals(1L, resultRef.get());
    }

    @Test
    public void runnable_Success_CatchWasNotCalled() throws Exception {
        Ref<Boolean> finallyRef = new Ref<>(false);
        Long result = Try.callable(() -> {
            return 1L;
        }).onCatch((th) -> {
            finallyRef.set(true);
            return 2L;
        }).call();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, result),
                () -> Assertions.assertFalse(finallyRef.get())
        );
    }

    @Test
    public void runnable_Failed_CatchWasCalled() throws Exception {
        Ref<Boolean> finallyRef = new Ref<>(false);
        Long result = Try.<Long>callable(() -> {
            throw new RuntimeException("1");
        }).onCatch((th) -> {
            finallyRef.set(true);
            return 2L;
        }).call();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2L, result),
                () -> Assertions.assertTrue(finallyRef.get())
        );
    }

    @Test
    public void runnable_CatchAndFinallyCalled() throws Exception {
        Ref<Boolean> finallyRef = new Ref<>();
        Ref<Long> resultRef = new Ref<>();
        Try.runnable(() -> {
            throw new UnsupportedOperationException("1");
        }).onCatch((th) -> {
            resultRef.set(2L);
        }).doFinally(() -> {
            finallyRef.set(true);
        }).run();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2L, resultRef.get()),
                () -> Assertions.assertTrue(finallyRef.get())
        );
    }

    @Test
    public void runnable_RunnableAndFinallyRun() throws Exception {
        Ref<Boolean> finallyRef = new Ref<>();
        Ref<Long> resultRef = new Ref<>();
        Try.runnable(() -> {
            resultRef.set(1L);
        }).onCatch((th) -> {
            resultRef.set(2L);
        }).doFinally(() -> {
            finallyRef.set(true);
        }).run();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, resultRef.get()),
                () -> Assertions.assertTrue(finallyRef.get())
        );
    }

    @Test
    public void runnable_RunnableAndFinallyWasRun() throws Exception {
        Ref<Boolean> finallyRef = new Ref<>();
        Ref<Long> resultRef = new Ref<>();
        Try.runnable(() -> {
            resultRef.set(1L);
        }).doFinally(() -> {
            finallyRef.set(true);
        }).run();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, resultRef.get()),
                () -> Assertions.assertTrue(finallyRef.get())
        );
    }

    @Test
    public void runnable_BiOnCatch_throwRuntimeException() throws Exception {
        TryRunnable tryRunnable = Try.runnable(() -> {
            // do nothing
        });
        tryRunnable.onCatch((th) -> {
            // do nothing
        });
        Assertions.assertThrows(IllegalStateException.class, () -> {
            tryRunnable.onCatch((th) -> {
                // do nothing
            });
        });
    }

    @Test
    public void runnable_BiDoFinally_throwRuntimeException() throws Exception {
        TryRunnable.Catch tryRunnableCatch = Try.runnable(() -> {
            // do nothing
        }).onCatch((th) -> {
            // do nothing
        });
        tryRunnableCatch.doFinally(() -> {
            // do nothing
        });
        Assertions.assertThrows(IllegalStateException.class, () -> {
            tryRunnableCatch.doFinally(() -> {
                // do nothing
            });
        });
    }

    @Test
    public void runnable_ReThrowTryException_DifferentValues() throws Exception {
        Ref<Exception> exceptionRef = new Ref<>();
        try {
            Try.runnable(() -> {
                exceptionRef.set(new IndexOutOfBoundsException("1"));
                throw exceptionRef.get();
            }).onCatch((th) -> {
                throw th;
            }).run();
        } catch (Exception e) {
            Assertions.assertEquals(exceptionRef.get(), e);
        }
        Assertions.assertNotNull(exceptionRef.get());
    }

}
