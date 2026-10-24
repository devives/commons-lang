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

import com.devives.commons.lang.LazyClosingDirectorBase.LazyCloseHandler;
import com.devives.commons.lang.SynchronizedLazyClosingDirector.SynchronizedLazyCloseHandler;
import com.devives.commons.lang.ref.Ref;
import com.devives.commons.util.usage.OrdinalUsage;
import com.devives.commons.util.usage.Usage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class LazyClosingDirectorTest {

    @Test
    public void close_afterCreate_callbackWasCalled() throws Exception {
        Ref<Boolean> calledRef = Ref.of(false);
        SynchronizedLazyClosingDirector director = new SynchronizedLazyClosingDirector(() -> calledRef.set(true));
        director.closeAsync();
        Assertions.assertTrue(calledRef.get());
    }

    @Test
    public void getUsageCount_afterIncUsageCount_equalsOne() throws Exception {
        SynchronizedLazyClosingDirector director = new SynchronizedLazyClosingDirector(() -> {
        });
        director.incUsageCount();
        Assertions.assertEquals(1, director.getUsageCount());
    }

    @Test
    public void close_afterAutoRelease_callbackWasCalled() throws Exception {
        Ref<Boolean> calledRef = Ref.of(false);
        SynchronizedLazyClosingDirector director = new SynchronizedLazyClosingDirector(() -> calledRef.set(true));
        try (Usage usage = OrdinalUsage.of(director)) {
            Assertions.assertFalse(calledRef.get());
            director.closeAsync();
            Assertions.assertFalse(calledRef.get());
        }
        Assertions.assertTrue(calledRef.get());
    }

    @Test
    public void close_afterRelease_callbackWasCalled() throws Exception {
        Ref<Boolean> calledRef = Ref.of(false);
        SynchronizedLazyClosingDirector director = new SynchronizedLazyClosingDirector(() -> calledRef.set(true));
        director.incUsageCount();
        Assertions.assertFalse(calledRef.get());
        director.closeAsync();
        Assertions.assertFalse(calledRef.get());
        director.decUsageCount();
        Assertions.assertTrue(calledRef.get());
    }

    private static void assertCompletedExceptionally(Future<Void> future, Class<? extends Throwable> causeClass) throws Exception {
        Assertions.assertTrue(future.isDone());
        ExecutionException e = Assertions.assertThrows(ExecutionException.class, () -> future.get(10, TimeUnit.SECONDS));
        Assertions.assertTrue(causeClass.isInstance(e.getCause()), String.valueOf(e.getCause()));
    }

    private static void assertReentrantUsageForbiddenByDefault(Function<LazyCloseHandler, LazyClosingDirectorBase> factory) throws Exception {
        AtomicReference<LazyClosingDirectorBase> directorRef = new AtomicReference<>();
        LazyClosingDirectorBase director = factory.apply(() -> directorRef.get().incUsageCount());
        directorRef.set(director);
        director.closeAsync();
        assertCompletedExceptionally(director.getLazyCloseFuture(), RuntimeException.class);
    }

    private static void assertReentrantUsageAllowed(Function<LazyCloseHandler, LazyClosingDirectorBase> factory) throws Exception {
        AtomicReference<LazyClosingDirectorBase> directorRef = new AtomicReference<>();
        AtomicInteger calls = new AtomicInteger();
        LazyClosingDirectorBase director = factory.apply(new LazyCloseHandler() {
            @Override
            public void lazyClose() throws Exception {
                calls.incrementAndGet();
                try (Usage<?> usage = OrdinalUsage.of(directorRef.get())) {
                    Assertions.assertEquals(1, directorRef.get().getUsageCount());
                }
            }

            @Override
            public boolean isReentrantUsageAllowed() {
                return true;
            }
        });
        directorRef.set(director);
        director.incUsageCount();
        director.closeAsync();
        director.decUsageCount();
        Assertions.assertNull(director.getLazyCloseFuture().get(10, TimeUnit.SECONDS));
        Assertions.assertEquals(1, calls.get());
        Assertions.assertEquals(0, director.getUsageCount());
        Assertions.assertThrows(RuntimeException.class, director::incUsageCount);
    }

    private static void assertNotReleasedReentrantUsageFails(Function<LazyCloseHandler, LazyClosingDirectorBase> factory) throws Exception {
        AtomicReference<LazyClosingDirectorBase> directorRef = new AtomicReference<>();
        LazyClosingDirectorBase director = factory.apply(new LazyCloseHandler() {
            @Override
            public void lazyClose() {
                directorRef.get().incUsageCount();
            }

            @Override
            public boolean isReentrantUsageAllowed() {
                return true;
            }
        });
        directorRef.set(director);
        director.closeAsync();
        assertCompletedExceptionally(director.getLazyCloseFuture(), IllegalStateException.class);
        Assertions.assertThrows(RuntimeException.class, director::incUsageCount);
    }

    private static SynchronizedLazyCloseHandler synchronizedHandler(LazyCloseHandler handler, Executor executor) {
        return new SynchronizedLazyCloseHandler() {
            @Override
            public void lazyClose() throws Exception {
                handler.lazyClose();
            }

            @Override
            public boolean isReentrantUsageAllowed() {
                return handler.isReentrantUsageAllowed();
            }

            @Override
            public Optional<Executor> getLazyCloseExecutor() {
                return Optional.ofNullable(executor);
            }
        };
    }

    @Test
    public void closeAsync_reentrantUsageByDefault_futureCompletedExceptionally() throws Exception {
        assertReentrantUsageForbiddenByDefault(LazyClosingDirector::new);
        assertReentrantUsageForbiddenByDefault(h -> new SynchronizedLazyClosingDirector(synchronizedHandler(h, null)));
    }

    @Test
    public void closeAsync_reentrantUsageAllowed_closedOnce() throws Exception {
        assertReentrantUsageAllowed(LazyClosingDirector::new);
        assertReentrantUsageAllowed(h -> new SynchronizedLazyClosingDirector(synchronizedHandler(h, null)));
    }

    @Test
    public void closeAsync_reentrantUsageNotReleased_futureCompletedExceptionally() throws Exception {
        assertNotReleasedReentrantUsageFails(LazyClosingDirector::new);
        assertNotReleasedReentrantUsageFails(h -> new SynchronizedLazyClosingDirector(synchronizedHandler(h, null)));
    }

    @Test
    public void incUsageCount_otherThreadWhileClosing_throws() throws Exception {
        CountDownLatch closingStarted = new CountDownLatch(1);
        CountDownLatch acquireAttempted = new CountDownLatch(1);
        SynchronizedLazyClosingDirector director = new SynchronizedLazyClosingDirector(synchronizedHandler(new LazyCloseHandler() {
            @Override
            public void lazyClose() throws Exception {
                closingStarted.countDown();
                Assertions.assertTrue(acquireAttempted.await(10, TimeUnit.SECONDS));
            }

            @Override
            public boolean isReentrantUsageAllowed() {
                return true;
            }
        }, null));
        CompletableFuture<Throwable> attempt = CompletableFuture.supplyAsync(() -> {
            try {
                Assertions.assertTrue(closingStarted.await(10, TimeUnit.SECONDS));
                director.incUsageCount();
                return null;
            } catch (Throwable e) {
                return e;
            } finally {
                acquireAttempted.countDown();
            }
        });
        director.closeAsync();
        Throwable error = attempt.get(10, TimeUnit.SECONDS);
        Assertions.assertTrue(error instanceof RuntimeException, String.valueOf(error));
        Assertions.assertNull(director.getLazyCloseFuture().get(10, TimeUnit.SECONDS));
    }

    @Test
    public void closeAsync_withExecutor_closedInExecutorThread() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Thread executorThread = executor.submit(Thread::currentThread).get(10, TimeUnit.SECONDS);
            AtomicReference<Thread> closingThread = new AtomicReference<>();
            AtomicReference<LazyClosingDirectorBase> directorRef = new AtomicReference<>();
            SynchronizedLazyClosingDirector director = new SynchronizedLazyClosingDirector(synchronizedHandler(new LazyCloseHandler() {
                @Override
                public void lazyClose() throws Exception {
                    closingThread.set(Thread.currentThread());
                    try (Usage<?> usage = OrdinalUsage.of(directorRef.get())) {
                        Assertions.assertEquals(1, directorRef.get().getUsageCount());
                    }
                }

                @Override
                public boolean isReentrantUsageAllowed() {
                    return true;
                }
            }, executor));
            directorRef.set(director);
            Assertions.assertNull(director.closeAsync().toCompletableFuture().get(10, TimeUnit.SECONDS));
            Assertions.assertSame(executorThread, closingThread.get());
            Assertions.assertThrows(RuntimeException.class, director::incUsageCount);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    public void closeAsync_executorRejects_futureCompletedExceptionally() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        SynchronizedLazyClosingDirector director = new SynchronizedLazyClosingDirector(synchronizedHandler(
                calls::incrementAndGet,
                r -> {
                    throw new RejectedExecutionException();
                }));
        director.closeAsync();
        assertCompletedExceptionally(director.getLazyCloseFuture(), RejectedExecutionException.class);
        Assertions.assertEquals(0, calls.get());
        Assertions.assertThrows(RuntimeException.class, director::incUsageCount);
    }
}
