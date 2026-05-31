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

public class SynchronizedStateHolderImpl<STATE> extends StateHolderImpl<STATE> implements SynchronizedStateHolder<STATE> {
    private static final long serialVersionUID = 1L;

    public SynchronizedStateHolderImpl(STATE initialState) {
        super(initialState);
    }
    private final Object mutex = new Object();

    @Override
    public STATE get() {
        synchronized (mutex) {
            return super.get();
        }
    }

    @Override
    public void set(STATE value) {
        synchronized (mutex) {
            super.set(value);
        }
    }

    @Override
    public boolean trySet(STATE expected, STATE value) {
        synchronized (mutex) {
            return super.trySet(expected, value);
        }
    }

    @Override
    public boolean trySet(STATE[] expected, STATE value) {
        synchronized (mutex) {
            return super.trySet(expected, value);
        }
    }

    @Override
    public boolean isExpected(STATE... expected) {
        synchronized (mutex) {
            return super.isExpected(expected);
        }
    }

    @Override
    public void validate(STATE... expected) {
        synchronized (mutex) {
            super.validate(expected);
        }
    }

    @Override
    public <E extends InvalidStateException> void validate(STATE expected, Function<STATE, E> exceptionSupplier) throws E {
        synchronized (mutex) {
            super.validate(expected, exceptionSupplier);
        }
    }

    @Override
    public <E extends InvalidStateException> void validate(STATE[] expected, Function<STATE, E> exceptionSupplier) throws E {
        synchronized (mutex) {
            super.validate(expected, exceptionSupplier);
        }
    }

    @Override
    public final void performAtomicWork(FailableProcedure procedure) throws Exception {
        synchronized (mutex) {
            procedure.accept();
        }
    }

    @Override
    public final <R> R performAtomicWork(FailableFunction<R> function) throws Exception {
        synchronized (mutex) {
            return function.apply();
        }
    }

}
