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
