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

import com.devives.commons.lang.exception.AggregateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class ExceptionUtilsTest {

    @Test
    public void collect_methodsDeclaringDifferentCheckedExceptions_collectsAllInCallOrder() {
        final Procs procs = new Procs();

        final Optional<List<Throwable>> collected = ExceptionUtils.collect(
                procs::throwIOException,
                procs::throwTimeoutException,
                procs::throwCustomCheckedException);

        Assertions.assertTrue(collected.isPresent());
        final List<Throwable> throwables = collected.get();
        Assertions.assertEquals(3, throwables.size());
        Assertions.assertSame(procs.ioException, throwables.get(0));
        Assertions.assertSame(procs.timeoutException, throwables.get(1));
        Assertions.assertSame(procs.customCheckedException, throwables.get(2));
    }

    @Test
    public void collect_methodsDeclaringCheckedUncheckedAndError_collectsAllAndCallsEveryMethod() {
        final Procs procs = new Procs();

        final Optional<List<Throwable>> collected = ExceptionUtils.collect(
                procs::throwIllegalStateException,
                procs::declareIOExceptionAndTimeoutExceptionThrowTimeout,
                procs::declareIOExceptionAndReturn,
                procs::throwAssertionError);

        Assertions.assertTrue(collected.isPresent());
        final List<Throwable> throwables = collected.get();
        Assertions.assertEquals(3, throwables.size());
        Assertions.assertSame(procs.illegalStateException, throwables.get(0));
        Assertions.assertSame(procs.timeoutException, throwables.get(1));
        Assertions.assertSame(procs.assertionError, throwables.get(2));
        Assertions.assertEquals(4, procs.calls.size());
    }

    @Test
    public void collect_methodsDeclaringDifferentExceptionsReturnNormally_returnsEmpty() {
        final Procs procs = new Procs();

        final Optional<List<Throwable>> collected = ExceptionUtils.collect(
                procs::declareIOExceptionAndReturn,
                procs::declareCustomCheckedExceptionAndReturn);

        Assertions.assertFalse(collected.isPresent());
        Assertions.assertEquals(2, procs.calls.size());
    }

    @Test
    public void collectAndThrow_oneOfMethodsDeclaringDifferentExceptionsThrows_rethrowsItAsIs() {
        final Procs procs = new Procs();

        final TimeoutException actual = Assertions.assertThrows(TimeoutException.class,
                () -> ExceptionUtils.collectAndThrow(
                        procs::declareIOExceptionAndReturn,
                        procs::throwTimeoutException,
                        procs::declareCustomCheckedExceptionAndReturn));

        Assertions.assertSame(procs.timeoutException, actual);
        Assertions.assertEquals(3, procs.calls.size());
    }

    @Test
    public void collectAndThrow_methodsDeclaringDifferentExceptionsThrow_throwsAggregateWithSuppressedInCallOrder() {
        final Procs procs = new Procs();

        final AggregateException actual = Assertions.assertThrows(AggregateException.class,
                () -> ExceptionUtils.collectAndThrow(
                        procs::throwIOException,
                        procs::throwIllegalStateException,
                        procs::throwCustomCheckedException));

        final Throwable[] suppressed = actual.getSuppressed();
        Assertions.assertEquals(3, suppressed.length);
        Assertions.assertSame(procs.ioException, suppressed[0]);
        Assertions.assertSame(procs.illegalStateException, suppressed[1]);
        Assertions.assertSame(procs.customCheckedException, suppressed[2]);
    }

    @Test
    public void getMessageTree_null_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> ExceptionUtils.getMessageTree(null));
    }

    @Test
    public void getMessageTree_singleException_returnsToString() {
        final IOException exception = new IOException("io");

        Assertions.assertEquals("java.io.IOException: io", ExceptionUtils.getMessageTree(exception));
    }

    @Test
    public void getMessageTree_suppressedAndCause_indentsNestedSuppressedFirst() {
        final IllegalStateException a = new IllegalStateException("a");
        final IOException s = new IOException("s");
        s.initCause(new TimeoutException("t"));
        a.addSuppressed(s);
        a.initCause(new IllegalArgumentException("b"));

        Assertions.assertEquals(lines(
                        "java.lang.IllegalStateException: a",
                        "    Suppressed: java.io.IOException: s",
                        "        Caused by: java.util.concurrent.TimeoutException: t",
                        "    Caused by: java.lang.IllegalArgumentException: b"),
                ExceptionUtils.getMessageTree(a));
    }

    @Test
    public void getMessageTree_circularCause_marksCircularReferenceWithCaption() {
        final IllegalStateException a = new IllegalStateException("a");
        final IllegalArgumentException b = new IllegalArgumentException("b");
        a.initCause(b);
        b.initCause(a);

        Assertions.assertEquals(lines(
                        "java.lang.IllegalStateException: a",
                        "    Caused by: java.lang.IllegalArgumentException: b",
                        "        Caused by: [CIRCULAR REFERENCE: java.lang.IllegalStateException: a]"),
                ExceptionUtils.getMessageTree(a));
    }

    @Test
    public void getMessageTree_circularSuppressed_marksCircularReferenceWithCaption() {
        final IllegalStateException a = new IllegalStateException("a");
        final IllegalArgumentException b = new IllegalArgumentException("b");
        a.addSuppressed(b);
        b.addSuppressed(a);

        Assertions.assertEquals(lines(
                        "java.lang.IllegalStateException: a",
                        "    Suppressed: java.lang.IllegalArgumentException: b",
                        "        Suppressed: [CIRCULAR REFERENCE: java.lang.IllegalStateException: a]"),
                ExceptionUtils.getMessageTree(a));
    }

    private static String lines(String... lines) {
        return String.join(System.lineSeparator(), lines);
    }

    private static final class CustomCheckedException extends Exception {
        private static final long serialVersionUID = 1L;

        private CustomCheckedException(String message) {
            super(message);
        }
    }

    /**
     * Methods with different declared exception types, used as {@code ThrowableProcedure} method references.
     */
    private static final class Procs {
        private final IOException ioException = new IOException("io");
        private final TimeoutException timeoutException = new TimeoutException("timeout");
        private final CustomCheckedException customCheckedException = new CustomCheckedException("custom");
        private final IllegalStateException illegalStateException = new IllegalStateException("unchecked");
        private final AssertionError assertionError = new AssertionError("error");
        private final List<String> calls = new ArrayList<>();

        private void throwIOException() throws IOException {
            calls.add("throwIOException");
            throw ioException;
        }

        private void throwTimeoutException() throws TimeoutException {
            calls.add("throwTimeoutException");
            throw timeoutException;
        }

        private void throwCustomCheckedException() throws CustomCheckedException {
            calls.add("throwCustomCheckedException");
            throw customCheckedException;
        }

        private void throwIllegalStateException() {
            calls.add("throwIllegalStateException");
            throw illegalStateException;
        }

        private void throwAssertionError() {
            calls.add("throwAssertionError");
            throw assertionError;
        }

        private void declareIOExceptionAndTimeoutExceptionThrowTimeout() throws IOException, TimeoutException {
            calls.add("declareIOExceptionAndTimeoutExceptionThrowTimeout");
            throw timeoutException;
        }

        private void declareIOExceptionAndReturn() throws IOException {
            calls.add("declareIOExceptionAndReturn");
        }

        private void declareCustomCheckedExceptionAndReturn() throws CustomCheckedException {
            calls.add("declareCustomCheckedExceptionAndReturn");
        }
    }

}
