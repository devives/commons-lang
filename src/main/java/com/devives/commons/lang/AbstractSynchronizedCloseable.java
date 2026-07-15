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

import com.devives.commons.lang.tuple.Tuple2;
import com.devives.commons.state.State;
import com.devives.commons.state.SynchronizedStateHolder;
import com.devives.commons.state.SynchronizedStateHolderImpl;

import java.util.concurrent.CompletableFuture;

/**
 * An abstract, thread-safe, implementation of a closable resource.
 */
public abstract class AbstractSynchronizedCloseable extends CloseableBase {
    private static final long serialVersionUID = 1L;

    /**
     * Sharing future across threads.
     * <p>
     * Read/Write is synchronized.
     */
    private transient CompletableFuture<Void> closeFuture_;

    /**
     * The thread currently performing the close.
     * <p>
     * Used to detect a recursive {@code #close()} call within the closing thread and avoid
     * self-deadlock on {@link #closeFuture_}. It is cleared outside the mutex, hence {@code volatile}.
     */
    private transient volatile Thread closingThread_;

    public AbstractSynchronizedCloseable() {
        this(OPENED);
    }

    public AbstractSynchronizedCloseable(State initialState) {
        super(new SynchronizedStateHolderImpl<>(initialState));
    }

    @Override
    protected SynchronizedStateHolder<State> getStateHolder() {
        return (SynchronizedStateHolder<State>) super.getStateHolder();
    }

    /**
     * Release object's resources.
     * <p><strong>Notes.</strong></p>
     * Closing of object can be cancelled by results of calling {@link #canBeClosed()} method.
     * <p>
     * If two or more threads call {@code #close()}, all of them will wait end of object closing in the first thread.
     *
     * @throws Exception when resource closing failed.
     */
    public final void close() throws Exception {
        final SynchronizedStateHolder<State> stateHolder = getStateHolder();
        final Tuple2<Boolean, CompletableFuture<Void>> tuple2 = stateHolder.performAtomicWork(() -> {
            if (!stateHolder.isExpected(CLOSING, CLOSED) && canBeClosed()) {
                stateHolder.set(CLOSING);
                CompletableFuture<Void> closeFuture = new CompletableFuture<>();
                closingThread_ = Thread.currentThread();
                closeFuture_ = closeFuture;
                return Tuple2.of(true, closeFuture);
            } else if (closingThread_ == Thread.currentThread()) {
                // Recursive close() within the closing thread: the outer frame will finish the close.
                return Tuple2.of(false, CompletableFuture.completedFuture(null));
            } else {
                CompletableFuture<Void> closeFuture = closeFuture_;
                return Tuple2.of(false, closeFuture != null ? closeFuture : CompletableFuture.completedFuture(null));
            }
        });
        final boolean performClose = tuple2._1;
        final CompletableFuture<Void> closeFuture = tuple2._2;
        if (performClose) {
            try {
                try {
                    doClose();
                } finally {
                    stateHolder.set(CLOSED);
                    closingThread_ = null;
                }
            } catch (Throwable e) {
                closeFuture.completeExceptionally(e);
            }
            closeFuture.complete(null);
        }
        // Future#get вызывается для выброса исключения, если #doClose() завершилось с ошибкой, в каждом потоке вызвавшем #close().
        closeFuture.get();
    }

    @Override
    protected final synchronized void doClose() throws Exception {
        super.doClose();
    }

}
