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
package com.devives.commons.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SynchronizedStateHolderImplTest extends StateHolderContractTest {

    private static final long CRITICAL_SECTION_AWAIT_TIMEOUT_MS = 10_000L;

    private static final long READ_TIMEOUT_MS = 500L;

    @Override
    protected StateHolder<String> newHolder(String initialState) {
        return new SynchronizedStateHolderImpl<>(initialState);
    }

    /**
     * Закрепляет неблокирующий контракт чтения: поток внутри {@code performAtomicWork} удерживает мьютекс
     * holder'а, и ни одно чтение не должно его ждать.
     */
    @Test
    public void get_isExpected_validate_areNotBlockedByPerformAtomicWork() throws Exception {
        final SynchronizedStateHolderImpl<String> holder = new SynchronizedStateHolderImpl<>("A");
        final ExecutorService executor = newDaemonExecutor();
        final CountDownLatch entered = new CountDownLatch(1);
        final CountDownLatch release = new CountDownLatch(1);
        try {
            executor.submit(() -> {
                holder.performAtomicWork(() -> {
                    entered.countDown();
                    release.await();
                });
                return null;
            });
            Assertions.assertTrue(entered.await(CRITICAL_SECTION_AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS),
                    "performAtomicWork не вошёл в критическую секцию");

            Assertions.assertEquals("A", awaitRead("get()", executor, holder::get));
            Assertions.assertTrue(awaitRead("isExpected(String...)", executor, () -> holder.isExpected("A")));
            awaitRead("validate(String...)", executor, () -> {
                holder.validate("A");
                return null;
            });
        } finally {
            // Поток, заблокированный на мониторе, прерыванием не снимается: освобождать должен снятием гейта.
            release.countDown();
            executor.shutdownNow();
        }
    }

    /**
     * Запись изнутри {@code performAtomicWork} не должна приводить к взаимоблокировке: {@code set} и
     * {@code trySet} захватывают тот же мьютекс реентрано.
     */
    @Test
    public void performAtomicWork_writeMethodsAreReentrant() throws Exception {
        final SynchronizedStateHolderImpl<String> holder = new SynchronizedStateHolderImpl<>("A");
        final ExecutorService executor = newDaemonExecutor();
        try {
            final Future<Boolean> result = executor.submit(() -> holder.performAtomicWork(() -> {
                holder.set("B");
                return holder.trySet("B", "C");
            }));
            Assertions.assertEquals(Boolean.TRUE, result.get(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS));
            Assertions.assertEquals("C", holder.get());
        } finally {
            executor.shutdownNow();
        }
    }

    private static <T> T awaitRead(String method, ExecutorService executor, Callable<T> read) throws Exception {
        final Future<T> future = executor.submit(read);
        try {
            return future.get(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException exception) {
            throw new AssertionError("Метод " + method + " не вернулся за " + READ_TIMEOUT_MS
                    + " мс: вызов заблокирован мьютексом holder'а", exception);
        }
    }

    private static ExecutorService newDaemonExecutor() {
        return Executors.newCachedThreadPool(runnable -> {
            final Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
    }
}
