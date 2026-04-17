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
package com.devives.commons.util.concurrent;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class ConcurrentTaskReducerTest {

    @Test
    void forCallableShouldExecuteZeroArgumentCallableOnceForConcurrentCallsWithSameKey() throws Exception {
        AtomicInteger executions = new AtomicInteger();
        ConcurrentTaskReducer.ReducedCallable<String> callable = ConcurrentTaskReducer.forCallable(
                () -> "same-key",
                () -> {
                    executions.incrementAndGet();
                    Thread.sleep(150);
                    return "ok";
                });
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<String> first = executorService.submit(() -> {
            start.await();
            return callable.apply();
        });

        Future<String> second = executorService.submit(() -> {
            start.await();
            return callable.apply();
        });

        try {
            start.countDown();
            Assertions.assertThat(first.get(2, TimeUnit.SECONDS)).isEqualTo("ok");
            Assertions.assertThat(second.get(2, TimeUnit.SECONDS)).isEqualTo("ok");
            Assertions.assertThat(executions).hasValue(1);
        } finally {
            executorService.shutdownNow();
        }
    }

    @Test
    void forCallableShouldSupportTimeoutApplyForWaitingZeroArgumentCallable() throws Exception {
        AtomicInteger executions = new AtomicInteger();
        CountDownLatch taskStarted = new CountDownLatch(1);
        CountDownLatch unblockTask = new CountDownLatch(1);
        ConcurrentTaskReducer.ReducedCallable<String> callable = ConcurrentTaskReducer.forCallable(
                () -> "same-key",
                () -> {
                    executions.incrementAndGet();
                    taskStarted.countDown();
                    unblockTask.await();
                    return "ok";
                });
        AtomicReference<String> waitingResult = new AtomicReference<>();
        AtomicReference<Throwable> waitingFailure = new AtomicReference<>();

        Thread executorThread = new Thread(() -> callable.apply());
        Thread waitingThread = new Thread(() -> {
            try {
                waitingResult.set(callable.apply(1, TimeUnit.SECONDS));
            } catch (Throwable throwable) {
                waitingFailure.set(throwable);
            }
        });

        executorThread.start();
        Assertions.assertThat(taskStarted.await(1, TimeUnit.SECONDS)).isTrue();

        waitingThread.start();
        Thread.sleep(100);
        unblockTask.countDown();

        executorThread.join(2000);
        waitingThread.join(2000);

        Assertions.assertThat(executorThread.isAlive()).isFalse();
        Assertions.assertThat(waitingThread.isAlive()).isFalse();
        Assertions.assertThat(waitingFailure.get()).isNull();
        Assertions.assertThat(waitingResult.get()).isEqualTo("ok");
        Assertions.assertThat(executions).hasValue(1);
    }

    @Test
    void forCallableShouldExecuteSingleArgumentCallableOnceForConcurrentCallsWithSameFormattedKey() throws Exception {
        AtomicInteger executions = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ConcurrentTaskReducer.ReducedCallable1<Integer, String> callable = ConcurrentTaskReducer.forCallable(
                value -> "key:" + value,
                value -> {
                    executions.incrementAndGet();
                    Thread.sleep(150);
                    return "value:" + value;
                });

        Future<String> first = executorService.submit(() -> {
            start.await();
            return callable.apply(1);
        });

        Future<String> second = executorService.submit(() -> {
            start.await();
            return callable.apply(1);
        });

        try {
            start.countDown();
            Assertions.assertThat(first.get(2, TimeUnit.SECONDS)).isEqualTo("value:1");
            Assertions.assertThat(second.get(2, TimeUnit.SECONDS)).isEqualTo("value:1");
            Assertions.assertThat(executions).hasValue(1);
        } finally {
            executorService.shutdownNow();
        }
    }

    @Test
    void forCallableShouldSupportTimeoutApplyForWaitingSingleArgumentCallable() throws Exception {
        AtomicInteger executions = new AtomicInteger();
        CountDownLatch taskStarted = new CountDownLatch(1);
        CountDownLatch unblockTask = new CountDownLatch(1);
        ConcurrentTaskReducer.ReducedCallable1<Integer, String> callable = ConcurrentTaskReducer.forCallable(
                value -> "key:" + value,
                value -> {
                    executions.incrementAndGet();
                    taskStarted.countDown();
                    unblockTask.await();
                    return "value:" + value;
                });
        AtomicReference<String> waitingResult = new AtomicReference<>();
        AtomicReference<Throwable> waitingFailure = new AtomicReference<>();

        Thread executorThread = new Thread(() -> callable.apply(1));
        Thread waitingThread = new Thread(() -> {
            try {
                waitingResult.set(callable.apply(1, 1, TimeUnit.SECONDS));
            } catch (Throwable throwable) {
                waitingFailure.set(throwable);
            }
        });

        executorThread.start();
        Assertions.assertThat(taskStarted.await(1, TimeUnit.SECONDS)).isTrue();

        waitingThread.start();
        Thread.sleep(100);
        unblockTask.countDown();

        executorThread.join(2000);
        waitingThread.join(2000);

        Assertions.assertThat(executorThread.isAlive()).isFalse();
        Assertions.assertThat(waitingThread.isAlive()).isFalse();
        Assertions.assertThat(waitingFailure.get()).isNull();
        Assertions.assertThat(waitingResult.get()).isEqualTo("value:1");
        Assertions.assertThat(executions).hasValue(1);
    }

    @Test
    void forCallableShouldExecuteSingleArgumentCallableSeparatelyForDifferentFormattedKeys() throws Exception {
        AtomicInteger executions = new AtomicInteger();
        CountDownLatch started = new CountDownLatch(2);
        CountDownLatch unblockTasks = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ConcurrentTaskReducer.ReducedCallable1<Integer, String> callable = ConcurrentTaskReducer.forCallable(
                value -> "key:" + value,
                value -> {
                    executions.incrementAndGet();
                    started.countDown();
                    unblockTasks.await();
                    return "value:" + value;
                });

        Future<String> first = executorService.submit(() -> callable.apply(1));
        Future<String> second = executorService.submit(() -> callable.apply(2));

        try {
            Assertions.assertThat(started.await(1, TimeUnit.SECONDS)).isTrue();
            unblockTasks.countDown();
            Assertions.assertThat(first.get(2, TimeUnit.SECONDS)).isEqualTo("value:1");
            Assertions.assertThat(second.get(2, TimeUnit.SECONDS)).isEqualTo("value:2");
            Assertions.assertThat(executions).hasValue(2);
        } finally {
            executorService.shutdownNow();
        }
    }

    @Test
    void forCallableShouldExecuteTwoArgumentCallableOnceForConcurrentCallsWithSameFormattedKey() throws Exception {
        AtomicInteger executions = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ConcurrentTaskReducer.ReducedCallable2<Integer, Integer, String> callable = ConcurrentTaskReducer.forCallable(
                (left, right) -> left + ":" + right,
                (left, right) -> {
                    executions.incrementAndGet();
                    Thread.sleep(150);
                    return left + "+" + right;
                });

        Future<String> first = executorService.submit(() -> {
            start.await();
            return callable.apply(1, 2);
        });

        Future<String> second = executorService.submit(() -> {
            start.await();
            return callable.apply(1, 2);
        });

        try {
            start.countDown();
            Assertions.assertThat(first.get(2, TimeUnit.SECONDS)).isEqualTo("1+2");
            Assertions.assertThat(second.get(2, TimeUnit.SECONDS)).isEqualTo("1+2");
            Assertions.assertThat(executions).hasValue(1);
        } finally {
            executorService.shutdownNow();
        }
    }

    @Test
    void forCallableShouldExecuteTwoArgumentCallableSeparatelyForDifferentFormattedKeys() throws Exception {
        AtomicInteger executions = new AtomicInteger();
        CountDownLatch started = new CountDownLatch(2);
        CountDownLatch unblockTasks = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ConcurrentTaskReducer.ReducedCallable2<Integer, Integer, String> callable = ConcurrentTaskReducer.forCallable(
                (left, right) -> left + ":" + right,
                (left, right) -> {
                    executions.incrementAndGet();
                    started.countDown();
                    unblockTasks.await();
                    return left + "+" + right;
                });

        Future<String> first = executorService.submit(() -> callable.apply(1, 2));
        Future<String> second = executorService.submit(() -> callable.apply(2, 3));

        try {
            Assertions.assertThat(started.await(1, TimeUnit.SECONDS)).isTrue();
            unblockTasks.countDown();
            Assertions.assertThat(first.get(2, TimeUnit.SECONDS)).isEqualTo("1+2");
            Assertions.assertThat(second.get(2, TimeUnit.SECONDS)).isEqualTo("2+3");
            Assertions.assertThat(executions).hasValue(2);
        } finally {
            executorService.shutdownNow();
        }
    }

    @Test
    void callShouldExecuteTaskOnceForConcurrentCallsWithSameKey() throws Exception {
        com.devives.commons.util.concurrent.ConcurrentTaskReducer<String, String> reducer = new com.devives.commons.util.concurrent.ConcurrentTaskReducer<>();
        AtomicInteger executions = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<String> first = executorService.submit(() -> {
            start.await();
            return reducer.call("same-key", () -> {
                executions.incrementAndGet();
                Thread.sleep(150);
                return "ok";
            });
        });

        Future<String> second = executorService.submit(() -> {
            start.await();
            return reducer.call("same-key", () -> {
                executions.incrementAndGet();
                Thread.sleep(150);
                return "ok";
            });
        });

        try {
            start.countDown();
            Assertions.assertThat(first.get(2, TimeUnit.SECONDS)).isEqualTo("ok");
            Assertions.assertThat(second.get(2, TimeUnit.SECONDS)).isEqualTo("ok");
            Assertions.assertThat(executions).hasValue(1);
        } finally {
            executorService.shutdownNow();
        }
    }

    @Test
    void callShouldFailFastForReentrantCallWithSameKey() {
        com.devives.commons.util.concurrent.ConcurrentTaskReducer<String, String> reducer = new com.devives.commons.util.concurrent.ConcurrentTaskReducer<>();

        Assertions.assertThatThrownBy(() -> reducer.call("key", () -> reducer.call("key", () -> "value")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Reentrant call with same key is not allowed");
    }

    @Test
    void callShouldRestoreInterruptFlagWhenWaitingThreadInterrupted() throws Exception {
        com.devives.commons.util.concurrent.ConcurrentTaskReducer<String, Integer> reducer = new com.devives.commons.util.concurrent.ConcurrentTaskReducer<>();
        CountDownLatch taskStarted = new CountDownLatch(1);
        CountDownLatch unblockTask = new CountDownLatch(1);
        CountDownLatch waiterStarted = new CountDownLatch(1);
        AtomicReference<Throwable> waiterFailure = new AtomicReference<>();
        AtomicBoolean interruptFlagAfterCall = new AtomicBoolean();

        Thread executorThread = new Thread(() -> reducer.call("key", () -> {
            taskStarted.countDown();
            unblockTask.await();
            return 1;
        }));

        Thread waitingThread = new Thread(() -> {
            waiterStarted.countDown();
            try {
                reducer.call("key", () -> 2);
            } catch (Throwable throwable) {
                waiterFailure.set(throwable);
            } finally {
                interruptFlagAfterCall.set(Thread.currentThread().isInterrupted());
            }
        });

        executorThread.start();
        Assertions.assertThat(taskStarted.await(1, TimeUnit.SECONDS)).isTrue();

        waitingThread.start();
        Assertions.assertThat(waiterStarted.await(1, TimeUnit.SECONDS)).isTrue();
        Thread.sleep(100);
        waitingThread.interrupt();

        waitingThread.join(2000);
        unblockTask.countDown();
        executorThread.join(2000);

        Assertions.assertThat(waitingThread.isAlive()).isFalse();
        Assertions.assertThat(executorThread.isAlive()).isFalse();
        Assertions.assertThat(waiterFailure.get()).isNotNull();
        Assertions.assertThat(interruptFlagAfterCall).isTrue();
    }
}
