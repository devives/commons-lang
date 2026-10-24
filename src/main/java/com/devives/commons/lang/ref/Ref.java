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
package com.devives.commons.lang.ref;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A mutable reference to a value, used to pass a variable "by reference",
 * e.g. to change a local variable from a lambda.
 * <p>
 * Instances are created by the factory methods:
 * <ul>
 *     <li>{@link #of(Object)} &mdash; the reference never holds {@code null};
 *     <li>{@link #ofNullable(Object)} &mdash; the reference may hold {@code null}.
 * </ul>
 *
 * <p><strong>Equality.</strong></p>
 * Two references are equal if both are {@code Ref} and their values are equal
 * according to {@link java.util.Objects#equals(Object, Object)}. The implementation and
 * {@link #isNullable()} do not affect equality. The hash code is the hash code of the value,
 * or 0 if the value is {@code null}.
 *
 * <p><strong>Thread safety.</strong></p>
 * Implementations are not thread-safe. Concurrent access requires external synchronization.
 *
 * @param <T> the type of the value
 */
public interface Ref<T> {

    /**
     * Creates a reference that never holds {@code null}.
     *
     * @param value the initial value.
     * @param <T>   the type of the value.
     * @return a new reference.
     * @throws NullPointerException if {@code value} is {@code null}.
     */
    static <T> Ref<T> of(T value) {
        return new NonNullableRef<>(value);
    }

    /**
     * Creates a reference that may hold {@code null}.
     *
     * @param value the initial value, may be {@code null}.
     * @param <T>   the type of the value.
     * @return a new reference.
     */
    static <T> Ref<T> ofNullable(T value) {
        return new NullableRef<>(value);
    }

    /**
     * Creates a reference that may hold {@code null}.
     *
     * @param <T> the type of the value.
     * @return a new reference.
     */
    static <T> Ref<T> ofNullable() {
        return new NullableRef<>(null);
    }

    /**
     * Returns the value.
     *
     * @return the value; never {@code null} unless {@link #isNullable()}.
     */
    T get();

    /**
     * Sets the value.
     *
     * @param value the new value.
     * @throws NullPointerException if {@code value} is {@code null} and the reference is not {@link #isNullable()}.
     */
    void set(T value);

    /**
     * Returns whether the reference may hold {@code null}.
     *
     * @return {@code true} if created by {@link #ofNullable(Object)}, {@code false} if by {@link #of(Object)}.
     */
    boolean isNullable();

    /**
     * Return {@code true} if there is a value not present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value not present, otherwise {@code false}
     */
    boolean isNull();

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    boolean isPresent();

    /**
     * If a value is present, invoke the specified consumer with the value,
     * otherwise do nothing.
     *
     * @param consumer block to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     *                              null
     */
    void ifPresent(Consumer<? super T> consumer);

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other the value to be returned if there is no value present, may
     *              be null
     * @return the value, if present, otherwise {@code other}
     */
    T orElse(T other);

    /**
     * Return the value if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param other a {@code Supplier} whose result is returned if no value
     *              is present
     * @return the value if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if value is not present and {@code other} is
     *                              null
     */
    T orElseGet(Supplier<? extends T> other);

}
