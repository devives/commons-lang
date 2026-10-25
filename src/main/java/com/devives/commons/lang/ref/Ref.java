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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A mutable reference to a value, used to pass a variable "by reference",
 * e.g. to change a local variable from a lambda.
 * <p>
 * The hierarchy is closed: it cannot be extended outside this package.
 * <ul>
 *     <li>{@link SimpleRef} &mdash; a plain field; created by {@link #of(Object)} and {@link #ofNullable(Object)};
 *     <li>{@link VolatileRef} &mdash; a {@code volatile} field; created by {@link VolatileRef#of(Object)}
 *     and {@link VolatileRef#ofNullable(Object)}.
 * </ul>
 * {@link SimpleRef} and {@link VolatileRef} are abstract; each has two {@code final} implementations,
 * one that may hold {@code null} and one that never does.
 * The {@code of} factories create references that never hold {@code null};
 * the {@code ofNullable} factories create references that may hold {@code null}.
 *
 * <p><strong>Equality.</strong></p>
 * Two references are equal if both are {@code Ref} and their values are equal
 * according to {@link java.util.Objects#equals(Object, Object)}. The implementation,
 * {@link #isNullable()} and volatility do not affect equality. The hash code is the hash code of the value,
 * or 0 if the value is {@code null}.
 *
 * <p><strong>Thread safety.</strong></p>
 * {@link SimpleRef} is not thread-safe; concurrent access requires external synchronization.
 * {@link VolatileRef} guarantees visibility of {@link #get()} and {@link #set(Object)} between threads,
 * but compound actions (check-then-act) are not atomic.
 *
 * <p><strong>Performance.</strong></p>
 * {@link #get()} and {@link #set(Object)} have two implementations ({@link SimpleRef} and {@link VolatileRef}).
 * A hot call site through the {@code Ref} type that sees all kinds of references may become megamorphic;
 * declare variables as {@link SimpleRef} or {@link VolatileRef} where this matters.
 *
 * @param <T> the type of the value
 */
public abstract class Ref<T> {

    Ref() {
    }

    /**
     * Creates a reference that never holds {@code null}.
     *
     * @param value the initial value.
     * @param <T>   the type of the value.
     * @return a new reference.
     * @throws NullPointerException if {@code value} is {@code null}.
     */
    public static <T> Ref<T> of(T value) {
        return new NonNullRef<>(value);
    }

    /**
     * Creates a reference that may hold {@code null}.
     *
     * @param value the initial value, may be {@code null}.
     * @param <T>   the type of the value.
     * @return a new reference.
     */
    public static <T> Ref<T> ofNullable(T value) {
        return new NullableRef<>(value);
    }

    /**
     * Creates a reference that may hold {@code null}.
     *
     * @param <T> the type of the value.
     * @return a new reference.
     */
    public static <T> Ref<T> ofNullable() {
        return new NullableRef<>(null);
    }

    /**
     * Returns the value.
     *
     * @return the value; never {@code null} unless {@link #isNullable()}.
     */
    public abstract T get();

    /**
     * Sets the value.
     *
     * @param value the new value.
     * @throws NullPointerException if {@code value} is {@code null} and the reference is not {@link #isNullable()}.
     */
    public abstract void set(T value);

    /**
     * Returns whether the reference may hold {@code null}.
     *
     * @return {@code true} if created by an {@code ofNullable} factory, {@code false} if by an {@code of} factory.
     */
    public abstract boolean isNullable();

    /**
     * Return {@code true} if there is a value not present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value not present, otherwise {@code false}
     */
    public final boolean isNull() {
        return get() == null;
    }

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public final boolean isPresent() {
        return get() != null;
    }

    /**
     * If a value is present, invoke the specified consumer with the value,
     * otherwise do nothing.
     *
     * @param consumer block to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     *                              null
     */
    public final void ifPresent(Consumer<? super T> consumer) {
        final T value = get();
        if (value != null) {
            consumer.accept(value);
        }
    }

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other the value to be returned if there is no value present, may
     *              be null
     * @return the value, if present, otherwise {@code other}
     */
    public final T orElse(T other) {
        final T value = get();
        return value != null ? value : other;
    }

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
    public final T orElseGet(Supplier<? extends T> other) {
        final T value = get();
        return value != null ? value : other.get();
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Ref)) {
            return false;
        }
        return Objects.equals(get(), ((Ref<?>) obj).get());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(get());
    }

    @Override
    public final String toString() {
        return "Ref[" + get() + "]";
    }

}
