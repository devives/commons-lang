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

import com.devives.commons.lang.ExceptionUtils;
import com.devives.commons.lang.function.FailableFunction;
import com.devives.commons.lang.function.FailableProcedure;

import java.util.Objects;
import java.util.function.Function;

/**
 * Потокобезопасная реализация хранителя состояний объекта.
 *
 * @param <STATE> Тип экземпляров состояний.
 */
public class SynchronizedStateHolderImpl<STATE> extends StateHolderBase<STATE> implements SynchronizedStateHolder<STATE> {
    private static final long serialVersionUID = 1L;
    private final Object mutex = new Object();
    private volatile STATE state_;

    public SynchronizedStateHolderImpl(STATE initialState) {
        state_ = Objects.requireNonNull(initialState, "initialState");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Reads the {@code volatile} field without acquiring the mutex, so this call is never blocked by a
     * concurrent write or by {@code performAtomicWork} in another thread.
     */
    @Override
    protected final STATE internalGet() {
        return state_;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Writes the {@code volatile} field. The mutex is not acquired here: the callers requiring mutual
     * exclusion ({@link #set(Object)}, {@link #trySet(Object, Object)}) wrap this call in
     * {@code performAtomicWork}.
     */
    @Override
    protected final void internalSet(STATE state) {
        state_ = Objects.requireNonNull(state, "state");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Acquires the mutex, therefore the call may be blocked by a concurrent write or by
     * {@code performAtomicWork} of another thread. The call is reentrant.
     */
    @Override
    public void set(STATE value) {
        performAtomicWork(() -> super.set(value));
    }

    /**
     * {@inheritDoc}
     * <p>
     * The comparison and the write are performed in one critical section, so no other thread can change
     * the state between them. The call may be blocked by a concurrent write or by
     * {@code performAtomicWork} of another thread, and is reentrant.
     */
    @Override
    public boolean trySet(STATE expected, STATE value) {
        return performAtomicWork(() -> super.trySet(expected, value));
    }

    /**
     * {@inheritDoc}
     * <p>
     * The comparison and the write are performed in one critical section, so no other thread can change
     * the state between them. The call may be blocked by a concurrent write or by
     * {@code performAtomicWork} of another thread, and is reentrant.
     */
    @Override
    public boolean trySet(STATE[] expected, STATE value) {
        return performAtomicWork(() -> super.trySet(expected, value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performAtomicWork(FailableProcedure procedure) {
        synchronized (mutex) {
            ExceptionUtils.passChecked(procedure);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R performAtomicWork(FailableFunction<R> function) {
        synchronized (mutex) {
            return ExceptionUtils.passChecked(function);
        }
    }

}
