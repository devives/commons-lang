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

import com.devives.commons.lang.Validate;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class StateHolderImpl<STATE> implements StateHolder<STATE>, Serializable {
    private static final long serialVersionUID = 1L;
    private STATE state_;

    public StateHolderImpl(STATE initialState) {
        this.state_ = Objects.requireNonNull(initialState, "initialState");
    }

    public STATE get() {
        return state_;
    }

    @Override
    public boolean trySet(STATE expected, STATE value) {
        final boolean result = isExpected(expected);
        if (result) {
            this.state_ = value;
        }
        return result;
    }

    @Override
    public boolean trySet(STATE[] expected, STATE value) {
        final boolean result = isExpected(expected);
        if (result) {
            this.state_ = value;
        }
        return result;
    }

    public void set(STATE value) {
        this.state_ = Objects.requireNonNull(value, "value");
    }

    @Override
    public boolean isExpected(STATE... expected) {
        Validate.notEmpty(expected);
        for (STATE expectedState : expected) {
            Objects.requireNonNull(expectedState, "The 'null' value in the array of expected states.");
            if (state_.equals(expectedState)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void validate(STATE... expected) {
        Validate.notEmpty(expected);
        boolean success = isExpected(expected);
        if (!success) {
            String states = Stream.of(expected).map(Objects::toString).collect(Collectors.joining(" or "));
            throw new InvalidStateException("State '" + state_ + "' not equal expected: '" + states + "'");
        }
    }

    @Override
    public <E extends InvalidStateException> void validate(STATE expected, Function<STATE, E> exceptionSupplier) throws E {
        Objects.requireNonNull(expected, "expected");
        boolean success = isExpected(expected);
        if (!success) {
            throw exceptionSupplier.apply(this.state_);
        }
    }

    @Override
    public <E extends InvalidStateException> void validate(STATE[] expected, Function<STATE, E> exceptionSupplier) throws E {
        Validate.notEmpty(expected);
        boolean success = isExpected(expected);
        if (!success) {
            throw exceptionSupplier.apply(this.state_);
        }
    }
}
