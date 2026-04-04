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

import java.util.function.Function;

/**
 * State holder interface. Implementations can be thread-safe or thread-unsafe.
 */
public interface StateHolder<STATE> {

    /**
     * Return object state.
     *
     * @return state.
     */
    STATE get();

    /**
     * Try set the objet state.
     * <p>
     * If current state is equal {@code expected}, {@code value} will be set.
     *
     * @param expected expected state.
     * @param value    new state.
     * @return {@code true}, if value was set, else {@code false}.
     */
    boolean trySet(STATE expected, STATE value);

    /**
     * Try set the objet state.
     * <p>
     * If current state is equal {@code expected}, {@code value} will be set.
     *
     * @param expected expected states.
     * @param value    new state.
     * @return {@code true}, if value was set, else {@code false}.
     */
    boolean trySet(STATE[] expected, STATE value);

    /**
     * Set the objet state.
     *
     * @param value state.
     */
    void set(STATE value);

    /**
     *
     * @param expected expected states.
     * @return true, if one of states is set, else false.
     */
    boolean isExpected(STATE... expected);

    /**
     * Checks whether the current state is equivalent to the expected state.
     *
     * @param expected Expected state.
     * @throws InvalidStateException if the current state is equivalent to the one of expected states.
     * @throws IllegalArgumentException expected array length is empty.
     */
    void validate(STATE... expected);

    /**
     * Checks whether the current state is equivalent to the expected state.
     *
     * @param expected          Expected state.
     * @param exceptionSupplier Exception instance supplier.
     * @param <E>               Type of exception.
     * @throws InvalidStateException if the current state is equivalent to the expected state.
     */
    <E extends InvalidStateException> void validate(STATE expected, Function<STATE, E> exceptionSupplier) throws E;

    /**
     * Checks whether the current state is equivalent to the expected state.
     *
     * @param expected          Expected state.
     * @param exceptionSupplier Exception instance supplier.
     * @param <E>               Type of exception.
     * @throws InvalidStateException if the current state is equivalent to the expected state.
     */
    <E extends InvalidStateException> void validate(STATE[] expected, Function<STATE, E> exceptionSupplier) throws E;

}