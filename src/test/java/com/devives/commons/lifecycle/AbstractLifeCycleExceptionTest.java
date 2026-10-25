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

import com.devives.commons.lang.ref.Ref;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Проверяет поведение {@link AbstractLifeCycle} по умолчанию при исключениях в хуках {@link LifeCycleBase}
 * и в обработчиках {@link LifeCycle.Listener}.
 */
public class AbstractLifeCycleExceptionTest {

    @Test
    public void onStarting_fails_startRethrowsAndMarksFailed() {
        final TestLifeCycle lifeCycle = new TestLifeCycle("onStarting", null);

        final RuntimeException actual = Assertions.assertThrows(RuntimeException.class, lifeCycle::start);

        Assertions.assertSame(lifeCycle.failure_, actual);
        Assertions.assertEquals(Arrays.asList("onStarting", "onFailure", "listener.onFailure"), lifeCycle.journal_);
        Assertions.assertTrue(lifeCycle.isFailed());
    }

    @Test
    public void onStart_fails_startRethrowsAndMarksFailed() {
        final TestLifeCycle lifeCycle = new TestLifeCycle("onStart", null);

        final RuntimeException actual = Assertions.assertThrows(RuntimeException.class, lifeCycle::start);

        Assertions.assertSame(lifeCycle.failure_, actual);
        Assertions.assertEquals(Arrays.asList(
                "onStarting", "listener.onStarting", "onStart", "onFailure", "listener.onFailure"), lifeCycle.journal_);
        Assertions.assertTrue(lifeCycle.isFailed());
    }

    @Test
    public void onStarted_fails_startRethrowsAndMarksFailed() {
        final TestLifeCycle lifeCycle = new TestLifeCycle("onStarted", null);

        final RuntimeException actual = Assertions.assertThrows(RuntimeException.class, lifeCycle::start);

        Assertions.assertSame(lifeCycle.failure_, actual);
        Assertions.assertEquals(Arrays.asList(
                "onStarting", "listener.onStarting", "onStart", "onStarted", "onFailure", "listener.onFailure"), lifeCycle.journal_);
        Assertions.assertTrue(lifeCycle.isFailed());
    }

    @Test
    public void onStopping_fails_stopRethrowsAndMarksFailed() throws Exception {
        final TestLifeCycle lifeCycle = new TestLifeCycle("onStopping", null);
        lifeCycle.start();
        lifeCycle.journal_.clear();

        final RuntimeException actual = Assertions.assertThrows(RuntimeException.class, lifeCycle::stop);

        Assertions.assertSame(lifeCycle.failure_, actual);
        Assertions.assertEquals(Arrays.asList("onStopping", "onFailure", "listener.onFailure"), lifeCycle.journal_);
        Assertions.assertTrue(lifeCycle.isFailed());
    }

    @Test
    public void onStop_fails_stopRethrowsAndMarksFailed() throws Exception {
        final TestLifeCycle lifeCycle = new TestLifeCycle("onStop", null);
        lifeCycle.start();
        lifeCycle.journal_.clear();

        final RuntimeException actual = Assertions.assertThrows(RuntimeException.class, lifeCycle::stop);

        Assertions.assertSame(lifeCycle.failure_, actual);
        Assertions.assertEquals(Arrays.asList(
                "onStopping", "listener.onStopping", "onStop", "onFailure", "listener.onFailure"), lifeCycle.journal_);
        Assertions.assertTrue(lifeCycle.isFailed());
    }

    @Test
    public void onStopped_fails_stopRethrowsAndMarksFailed() throws Exception {
        final TestLifeCycle lifeCycle = new TestLifeCycle("onStopped", null);
        lifeCycle.start();
        lifeCycle.journal_.clear();

        final RuntimeException actual = Assertions.assertThrows(RuntimeException.class, lifeCycle::stop);

        Assertions.assertSame(lifeCycle.failure_, actual);
        Assertions.assertEquals(Arrays.asList(
                "onStopping", "listener.onStopping", "onStop", "onStopped", "onFailure", "listener.onFailure"), lifeCycle.journal_);
        Assertions.assertTrue(lifeCycle.isFailed());
    }

    @Test
    public void listenerOnStarting_fails_startCompletes() throws Exception {
        final TestLifeCycle lifeCycle = new TestLifeCycle(null, "onStarting");

        lifeCycle.start();

        Assertions.assertEquals(Arrays.asList(
                "onStarting", "listener.onStarting", "onStart", "onStarted", "listener.onStarted"), lifeCycle.journal_);
        Assertions.assertTrue(lifeCycle.isStarted());
    }

    @Test
    public void listenerOnStarted_fails_startCompletes() throws Exception {
        final TestLifeCycle lifeCycle = new TestLifeCycle(null, "onStarted");

        lifeCycle.start();

        Assertions.assertEquals(Arrays.asList(
                "onStarting", "listener.onStarting", "onStart", "onStarted", "listener.onStarted"), lifeCycle.journal_);
        Assertions.assertTrue(lifeCycle.isStarted());
    }

    @Test
    public void listenerOnStopping_fails_stopCompletes() throws Exception {
        final TestLifeCycle lifeCycle = new TestLifeCycle(null, "onStopping");
        lifeCycle.start();
        lifeCycle.journal_.clear();

        lifeCycle.stop();

        Assertions.assertEquals(Arrays.asList(
                "onStopping", "listener.onStopping", "onStop", "onStopped", "listener.onStopped"), lifeCycle.journal_);
        Assertions.assertTrue(lifeCycle.isStopped());
    }

    @Test
    public void listenerOnStopped_fails_stopCompletes() throws Exception {
        final TestLifeCycle lifeCycle = new TestLifeCycle(null, "onStopped");
        lifeCycle.start();
        lifeCycle.journal_.clear();

        lifeCycle.stop();

        Assertions.assertEquals(Arrays.asList(
                "onStopping", "listener.onStopping", "onStop", "onStopped", "listener.onStopped"), lifeCycle.journal_);
        Assertions.assertTrue(lifeCycle.isStopped());
    }

    @Test
    public void listenerOnFailure_fails_startRethrowsOriginalFailureAndMarksFailed() {
        final TestLifeCycle lifeCycle = new TestLifeCycle("onStart", "onFailure");

        final RuntimeException actual = Assertions.assertThrows(RuntimeException.class, lifeCycle::start);

        Assertions.assertSame(lifeCycle.failure_, actual);
        Assertions.assertEquals(Arrays.asList(
                "onStarting", "listener.onStarting", "onStart", "onFailure", "listener.onFailure"), lifeCycle.journal_);
        Assertions.assertTrue(lifeCycle.isFailed());
    }

    @Test
    public void listenerOnFailure_rethrowsReceivedFailure_startRethrowsOriginalFailureAndMarksFailed() {
        final RuntimeException failure = new RuntimeException("onStart failed");
        final AbstractLifeCycle lifeCycle = new AbstractLifeCycle() {
            @Override
            protected void onStart() {
                throw failure;
            }

            @Override
            protected void onStop() {
            }
        };
        lifeCycle.addListener(new LifeCycle.Listener() {
            @Override
            public void onFailure(LifeCycle object, Throwable throwable) {
                throw (RuntimeException) throwable;
            }
        });

        final RuntimeException actual = Assertions.assertThrows(RuntimeException.class, lifeCycle::start);

        Assertions.assertSame(failure, actual);
        Assertions.assertTrue(lifeCycle.isFailed());
    }

    @Test
    public void listener_fails_anonymousLifeCycle_startCompletes() throws Exception {
        final AbstractLifeCycle lifeCycle = new AbstractLifeCycle() {
            @Override
            protected void onStart() {
            }

            @Override
            protected void onStop() {
            }
        };
        Assertions.assertNull(lifeCycle.getClass().getCanonicalName());
        lifeCycle.addListener(new LifeCycle.Listener() {
            @Override
            public void onStarted(LifeCycle object) {
                throw new RuntimeException("listener.onStarted failed");
            }
        });

        lifeCycle.start();

        Assertions.assertTrue(lifeCycle.isStarted());
    }

    /**
     * Объект жизненного цикла, который журналирует вызовы хуков и своего слушателя
     * и бросает исключение в заданном хуке и в заданном событии слушателя.
     */
    private static final class TestLifeCycle extends AbstractLifeCycle {
        private final List<String> journal_ = new ArrayList<>();
        private final String failingHook_;
        private final String failingEvent_;
        private Throwable failure_;

        /**
         * Создаёт объект жизненного цикла.
         *
         * @param failingHook  имя хука {@link LifeCycleBase}, который бросит {@link RuntimeException}
         *                     ({@code "onStarting"}, {@code "onStart"}, {@code "onStarted"}, {@code "onStopping"},
         *                     {@code "onStop"} или {@code "onStopped"}); {@code null} &mdash; хуки не бросают исключений.
         * @param failingEvent имя события {@link LifeCycle.Listener}, обработчик которого бросит {@link RuntimeException}
         *                     ({@code "onStarting"}, {@code "onStarted"}, {@code "onStopping"}, {@code "onStopped"}
         *                     или {@code "onFailure"}); {@code null} &mdash; слушатель не бросает исключений.
         */
        private TestLifeCycle(String failingHook, String failingEvent) {
            failingHook_ = failingHook;
            failingEvent_ = failingEvent;
            addListener(new LifeCycle.Listener() {
                @Override
                public void onStarting(LifeCycle object) {
                    record("onStarting");
                }

                @Override
                public void onStarted(LifeCycle object) {
                    record("onStarted");
                }

                @Override
                public void onStopping(LifeCycle object) {
                    record("onStopping");
                }

                @Override
                public void onStopped(LifeCycle object) {
                    record("onStopped");
                }

                @Override
                public void onFailure(LifeCycle object, Throwable throwable) {
                    record("onFailure");
                }

                private void record(String event) {
                    journal_.add("listener." + event);
                    if (event.equals(failingEvent_)) {
                        throw new RuntimeException("listener." + event + " failed");
                    }
                }
            });
        }

        private void call(String hook) {
            journal_.add(hook);
            if (hook.equals(failingHook_)) {
                throw new RuntimeException(hook + " failed");
            }
        }

        @Override
        protected void onStarting() {
            call("onStarting");
        }

        @Override
        protected void onStart() {
            call("onStart");
        }

        @Override
        protected void onStarted() {
            call("onStarted");
        }

        @Override
        protected void onStopping() {
            call("onStopping");
        }

        @Override
        protected void onStop() {
            call("onStop");
        }

        @Override
        protected void onStopped() {
            call("onStopped");
        }

        @Override
        protected FailureAction onFailure(Ref<Throwable> thRef) {
            journal_.add("onFailure");
            failure_ = thRef.get();
            return super.onFailure(thRef);
        }
    }
}
