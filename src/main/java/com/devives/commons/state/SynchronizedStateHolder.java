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

import com.devives.commons.lang.function.FailableFunction;
import com.devives.commons.lang.function.FailableProcedure;

import java.util.function.Function;

/**
 * Synchronized state holder.
 * <p>
 * <strong>Locking semantics.</strong> State transitions ({@code set}, {@code trySet}) and
 * {@link #performAtomicWork(FailableProcedure)} are executed under one and the same mutex and can be
 * blocked until it is released. Read methods ({@code get}, {@code isExpected} and every
 * {@code validate} overload) do not acquire that mutex, so they are never blocked by a thread holding
 * it: they return the state observed at the moment of the call.
 * <p>
 * As a consequence, while another thread is inside a critical section, a reader observes the
 * <em>intermediate</em> state of the transition (such as {@code STARTING}, {@code STOPPING} or
 * {@code CLOSING}) instead of waiting for the transition to complete. Only the final state is
 * guaranteed to be stable; the intermediate one may be replaced at any moment.
 *
 * @param <STATE> type of state instances.
 */
public interface SynchronizedStateHolder<STATE> extends StateHolder<STATE> {

    /**
     * {@inheritDoc}
     * <p>
     * Returns the state without acquiring the mutex: the call is never blocked by a concurrent
     * {@code set}, {@code trySet} or {@code performAtomicWork}.
     */
    @Override
    STATE get();

    /**
     * {@inheritDoc}
     * <p>
     * Reads the state once without acquiring the mutex, so an {@link InvalidStateException} may be
     * thrown because of an intermediate state of a transition running in another thread.
     *
     * @param expected expected state.
     * @param exceptionSupplier exception instance supplier.
     * @param <E> exception type.
     */
    @Override
    <E extends InvalidStateException> void validate(STATE expected, Function<STATE, E> exceptionSupplier) throws E;

    /**
     * {@inheritDoc}
     * <p>
     * Reads the state once without acquiring the mutex.
     */
    @Override
    <E extends InvalidStateException> void validate(STATE[] expected, Function<STATE, E> exceptionSupplier) throws E;

    /**
     * Execute an anonymous method in {@code synchronized} code block.
     * <p>
     * Acquires the same mutex as {@code set} and {@code trySet}, and is reentrant: the holder's own
     * write methods may be called from {@code procedure}. Other threads writing state wait for the
     * block to complete; readers do not.
     *
     * @param procedure anonymous method
     */
    void performAtomicWork(FailableProcedure procedure);

    /**
     * Execute an anonymous method in {@code synchronized} code block.
     * <p>
     * Acquires the same mutex as {@code set} and {@code trySet}, and is reentrant: the holder's own
     * write methods may be called from {@code function}. Other threads writing state wait for the
     * block to complete; readers do not.
     *
     * @param function anonymous method.
     * @param <R>      type of anonymous method result.
     * @return result of anonymous method.
     */
    <R> R performAtomicWork(FailableFunction<R> function);
}
