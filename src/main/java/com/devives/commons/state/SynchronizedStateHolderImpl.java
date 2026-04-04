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

    public SynchronizedStateHolderImpl(STATE initialState) {
        super(initialState);
    }

    @Override
    public synchronized STATE get() {
        return super.get();
    }

    @Override
    public synchronized void set(STATE value) {
        super.set(value);
    }

    @Override
    public synchronized boolean trySet(STATE expected, STATE value) {
        return super.trySet(expected, value);
    }

    @Override
    public synchronized boolean trySet(STATE[] expected, STATE value) {
        return super.trySet(expected, value);
    }

    @Override
    public synchronized boolean isExpected(STATE... expected) {
        return super.isExpected(expected);
    }

    @Override
    public synchronized void validate(STATE... expected) {
        super.validate(expected);
    }

    @Override
    public synchronized <E extends InvalidStateException> void validate(STATE expected, Function<STATE, E> exceptionSupplier) throws E {
        super.validate(expected, exceptionSupplier);
    }

    @Override
    public synchronized <E extends InvalidStateException> void validate(STATE[] expected, Function<STATE, E> exceptionSupplier) throws E {
        super.validate(expected, exceptionSupplier);
    }

    @Override
    public synchronized final void performAtomicWork(FailableProcedure procedure) throws Exception {
        procedure.accept();
    }

    @Override
    public synchronized final <R> R performAtomicWork(FailableFunction<R> function) throws Exception {
        return function.apply();
    }

}
