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

import com.devives.commons.lang.ref.Ref;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

public class TryTest {

    /**
     * Контрольный тест: в try-with-resources наружу уходит исключение тела,
     * а исключение из {@code close()} добавляется к нему как suppressed (JLS 14.20.3.1).
     */
    @Test
    public void tryWithResources_BodyExceptionIsPrimary_CloseExceptionIsSuppressed() {
        final Exception finalException;
        try (AutoCloseable resource = () -> {
            throw new IllegalStateException("close");
        }) {
            throw new IOException("body");
        } catch (Exception e) {
            finalException = e;
        }
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(IOException.class, finalException),
                () -> Assertions.assertEquals("body", finalException.getMessage()),
                () -> Assertions.assertEquals(1, finalException.getSuppressed().length),
                () -> Assertions.assertInstanceOf(IllegalStateException.class, finalException.getSuppressed()[0]),
                () -> Assertions.assertEquals("close", finalException.getSuppressed()[0].getMessage())
        );
    }

    /**
     * Контрольный тест: если тело try-with-resources завершилось нормально, наружу уходит исключение из {@code close()}.
     */
    @Test
    public void tryWithResources_BodyCompletedNormally_CloseExceptionIsThrown() {
        Exception exception = null;
        try (AutoCloseable resource = () -> {
            throw new IllegalStateException("close");
        }) {
            // do nothing
        } catch (Exception e) {
            exception = e;
        }
        final Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(IllegalStateException.class, finalException),
                () -> Assertions.assertEquals(0, finalException.getSuppressed().length)
        );
    }

    /**
     * Контрольный тест: исключение из блока {@code finally} полностью заменяет исключение блока {@code try},
     * исходное исключение теряется (JLS 14.20.2).
     */
    @Test
    @SuppressWarnings("finally")
    public void tryFinally_FinallyExceptionReplacesTryException() {
        final Exception finalException;
        try {
            try {
                throw new IOException("try");
            } finally {
                throw new IllegalStateException("finally");
            }
        } catch (Exception e) {
            finalException = e;
        }
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(IllegalStateException.class, finalException),
                () -> Assertions.assertEquals(0, finalException.getSuppressed().length),
                () -> Assertions.assertNull(finalException.getCause())
        );
    }

    /**
     * Контрольный тест: исключение из блока {@code catch} заменяет исключение блока {@code try},
     * исходное исключение не добавляется ни в suppressed, ни в cause (JLS 14.20.1).
     */
    @Test
    public void tryCatch_CatchExceptionReplacesTryException() {
        final Exception finalException;
        try {
            try {
                throw new IOException("try");
            } catch (IOException e) {
                throw new IllegalStateException("catch");
            }
        } catch (Exception e) {
            finalException = e;
        }
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(IllegalStateException.class, finalException),
                () -> Assertions.assertEquals(0, finalException.getSuppressed().length),
                () -> Assertions.assertNull(finalException.getCause())
        );
    }

    /**
     * Контрольный тест: исключения из блоков {@code try}, {@code catch} и {@code finally} — наружу уходит исключение
     * из {@code finally}, остальные теряются.
     */
    @Test
    @SuppressWarnings("finally")
    public void tryCatchFinally_FinallyExceptionReplacesCatchException() {
        final Exception finalException;
        try {
            try {
                throw new IOException("try");
            } catch (IOException e) {
                throw new UnsupportedOperationException("catch");
            } finally {
                throw new IllegalStateException("finally");
            }
        } catch (Exception e) {
            finalException = e;
        }
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(IllegalStateException.class, finalException),
                () -> Assertions.assertEquals(0, finalException.getSuppressed().length),
                () -> Assertions.assertNull(finalException.getCause())
        );
    }

    @Test
    public void callable_ThrownThreeNestedExceptions() throws Exception {
        Exception exception = null;
        try {
            Long result = Try.<Long>callable(() -> {
                throw new NullPointerException("try");
            }).onCatch((th) -> {
                throw new IOException("catch");
            }).doFinally(() -> {
                throw new IndexOutOfBoundsException("finally");
            }).call();
        } catch (Exception e) {
            exception = e;
        }
        Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(IOException.class, finalException),
                () -> Assertions.assertInstanceOf(NullPointerException.class, finalException.getSuppressed()[0]),
                () -> Assertions.assertInstanceOf(IndexOutOfBoundsException.class, finalException.getSuppressed()[1])
        );
    }

    @Test
    public void callable_Catch_ThrownThreeNestedExceptions() throws Exception {
        Exception exception = null;
        try {
            Long result = Try.<Long>callable(() -> {
                throw new NullPointerException("try");
            }).onCatch((th) -> {
                throw new IOException("catch", th);
            }).doFinally(() -> {
                throw new IndexOutOfBoundsException("finally");
            }).call();
        } catch (Exception e) {
            exception = e;
        }
        Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(IOException.class, finalException),
                () -> Assertions.assertInstanceOf(NullPointerException.class, finalException.getCause()),
                () -> Assertions.assertInstanceOf(IndexOutOfBoundsException.class, finalException.getSuppressed()[0])
        );
    }

    @Test
    public void callable_Catch_ExceptionRethrown() throws Exception {
        Exception exception = null;
        try {
            Long result = Try.<Long>callable(() -> {
                throw new NullPointerException("try");
            }).onCatch((th) -> {
                throw th;
            }).call();
        } catch (Exception e) {
            exception = e;
        }
        Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(NullPointerException.class, finalException),
                () -> Assertions.assertNull(finalException.getCause())
        );
    }

    /**
     * Распаковка в {@code catch} не должна создавать цикл: исходное исключение уже ссылается
     * на распакованное через cause, поэтому в suppressed распакованного оно не добавляется.
     */
    @Test
    public void callable_Catch_UnwrapCause_NoCircularReference() throws Exception {
        IOException cause = new IOException("cause");
        Exception exception = null;
        try {
            Try.<Long>callable(() -> {
                throw new RuntimeException("try", cause);
            }).onCatch((th) -> {
                throw th.getCause();
            }).doFinally(() -> {
                throw new IndexOutOfBoundsException("finally");
            }).call();
        } catch (Exception e) {
            exception = e;
        }
        Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertSame(cause, finalException),
                () -> Assertions.assertEquals(1, finalException.getSuppressed().length),
                () -> Assertions.assertInstanceOf(IndexOutOfBoundsException.class, finalException.getSuppressed()[0])
        );
    }

    @Test
    public void callable_ThrownTwoNestedExceptions() throws Exception {
        Exception exception = null;
        try {
            Long result = Try.<Long>callable(() -> {
                throw new NullPointerException("try");
            }).doFinally(() -> {
                throw new IndexOutOfBoundsException("finally");
            }).call();
        } catch (Exception e) {
            exception = e;
        }
        Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(NullPointerException.class, finalException),
                () -> Assertions.assertInstanceOf(IndexOutOfBoundsException.class, finalException.getSuppressed()[0])
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
        Ref<Boolean> finallyRef = Ref.of(false);
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
        Ref<Boolean> finallyRef = Ref.of(false);
        Long result = Try.<Long>callable(() -> {
            throw new RuntimeException("try");
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
        Ref<Boolean> finallyRef = Ref.ofNullable(null);
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
        Ref<Boolean> finallyRef = Ref.ofNullable(null);
        Long result = Try.<Long>callable(() -> {
            throw new UnsupportedOperationException("try");
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
        Ref<Boolean> finallyRef = Ref.ofNullable(null);
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
        Field field = try_.getClass().getDeclaredField("onTry");
        field.setAccessible(true);
        Assertions.assertNull(field.get(try_));
        field = try_.getClass().getDeclaredField("onCatch");
        field.setAccessible(true);
        Assertions.assertNull(field.get(try_));
        field = try_.getClass().getDeclaredField("onFinally");
        field.setAccessible(true);
        Assertions.assertNull(field.get(try_));
    }

    @Test
    public void runnable_NoMemoryLeak() throws Exception {
        final Object testObject = new Object();
        TryRunnable try_ = Try.runnable(() -> {

        });
        try_.doFinally(() -> {
        }).run();
        Field field = try_.getClass().getDeclaredField("onTry");
        field.setAccessible(true);
        Assertions.assertNull(field.get(try_));
        field = try_.getClass().getDeclaredField("onCatch");
        field.setAccessible(true);
        Assertions.assertNull(field.get(try_));
        field = try_.getClass().getDeclaredField("onFinally");
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
                throw new UnsupportedOperationException("try");
            }).onCatch((th) -> {
                throw new IOException("catch");
            }).doFinally(() -> {
                throw new IndexOutOfBoundsException("finally");
            }).run();
        } catch (Exception e) {
            exception = e;
        }
        Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(IOException.class, finalException),
                () -> Assertions.assertInstanceOf(UnsupportedOperationException.class, finalException.getSuppressed()[0]),
                () -> Assertions.assertInstanceOf(IndexOutOfBoundsException.class, finalException.getSuppressed()[1])
        );
    }

    @Test
    public void runnable_CatchSuppressException() throws Exception {
        Try.runnable(() -> {
            throw new UnsupportedOperationException("try");
        }).onCatch((th) -> {
            /* Do nothing */
        }).run();
    }

    @Test
    public void runnable_Catch_ThrownThreeNestedExceptions() throws Exception {
        Exception exception = null;
        try {
            Try.runnable(() -> {
                throw new UnsupportedOperationException("try");
            }).onCatch((th) -> {
                // Исходное исключение обработано.
                throw new IOException("catch", th);
            }).doFinally(() -> {
                throw new IndexOutOfBoundsException("finally");
            }).run();
        } catch (Exception e) {
            exception = e;
        }
        Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(IOException.class, finalException),
                () -> Assertions.assertInstanceOf(UnsupportedOperationException.class, finalException.getCause()),
                () -> Assertions.assertInstanceOf(IndexOutOfBoundsException.class, finalException.getSuppressed()[0])
        );
    }

    @Test
    public void runnable_Catch_ExceptionRethrown() throws Exception {
        Exception exception = null;
        try {
            Try.runnable(() -> {
                throw new IOException("try");
            }).onCatch((th) -> {
                throw th;
            }).run();
        } catch (Exception e) {
            exception = e;
        }
        Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(IOException.class, finalException),
                () -> Assertions.assertNull(finalException.getCause())
        );
    }

    /**
     * Распаковка в {@code catch} не должна создавать цикл: исходное исключение уже ссылается
     * на распакованное через cause, поэтому в suppressed распакованного оно не добавляется.
     */
    @Test
    public void runnable_Catch_UnwrapCause_NoCircularReference() throws Exception {
        IOException cause = new IOException("cause");
        Exception exception = null;
        try {
            Try.runnable(() -> {
                throw new RuntimeException("try", cause);
            }).onCatch((th) -> {
                throw th.getCause();
            }).doFinally(() -> {
                throw new IndexOutOfBoundsException("finally");
            }).run();
        } catch (Exception e) {
            exception = e;
        }
        Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertSame(cause, finalException),
                () -> Assertions.assertEquals(1, finalException.getSuppressed().length),
                () -> Assertions.assertInstanceOf(IndexOutOfBoundsException.class, finalException.getSuppressed()[0])
        );
    }

    @Test
    public void runnable_ThrownTwoNestedExceptions() throws Exception {
        Exception exception = null;
        try {
            Try.runnable(() -> {
                throw new NullPointerException("try");
            }).doFinally(() -> {
                throw new IndexOutOfBoundsException("finally");
            }).run();
        } catch (Exception e) {
            exception = e;
        }
        Exception finalException = exception;
        Assertions.assertAll(
                () -> Assertions.assertInstanceOf(NullPointerException.class, finalException),
                () -> Assertions.assertInstanceOf(IndexOutOfBoundsException.class, finalException.getSuppressed()[0])
        );
    }

    @Test
    public void runnable_RunnableWasRun() throws Exception {
        Ref<Long> resultRef = Ref.ofNullable(null);
        Try.runnable(() -> {
            resultRef.set(1L);
        }).onCatch((th) -> {
            resultRef.set(2L);
        }).run();
        Assertions.assertEquals(1L, resultRef.get());
    }

    @Test
    public void runnable_Success_CatchWasNotCalled() throws Exception {
        Ref<Boolean> finallyRef = Ref.of(false);
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
        Ref<Boolean> finallyRef = Ref.of(false);
        Long result = Try.<Long>callable(() -> {
            throw new RuntimeException("try");
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
        Ref<Boolean> finallyRef = Ref.ofNullable(null);
        Ref<Long> resultRef = Ref.ofNullable(null);
        Try.runnable(() -> {
            throw new UnsupportedOperationException("try");
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
        Ref<Boolean> finallyRef = Ref.ofNullable(null);
        Ref<Long> resultRef = Ref.ofNullable(null);
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
        Ref<Boolean> finallyRef = Ref.ofNullable(null);
        Ref<Long> resultRef = Ref.ofNullable(null);
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
        Ref<Exception> exceptionRef = Ref.ofNullable(null);
        try {
            Try.runnable(() -> {
                exceptionRef.set(new IndexOutOfBoundsException("try"));
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
