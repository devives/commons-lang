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

/**
 * A reference backed by a {@code volatile} field.
 * <p>
 * Instances are created by {@link #of(Object)}, {@link #ofNullable(Object)} and {@link #ofNullable()}.
 *
 * <p><strong>Thread safety.</strong></p>
 * {@link #get()} and {@link #set(Object)} are volatile read and write: a value set by one thread
 * is visible to other threads. Compound actions (e.g. {@code if (ref.isNull()) ref.set(x)})
 * are not atomic and require external synchronization.
 *
 * @param <T> the type of the value
 */
public class VolatileRef<T> extends Ref<T> {

    private volatile T value_;

    VolatileRef(T value) {
        value_ = validate(value);
    }

    /**
     * Creates a volatile reference that never holds {@code null}.
     *
     * @param value the initial value.
     * @param <T>   the type of the value.
     * @return a new reference.
     * @throws NullPointerException if {@code value} is {@code null}.
     */
    public static <T> VolatileRef<T> of(T value) {
        return new VolatileNonNullRef<>(value);
    }

    /**
     * Creates a volatile reference that may hold {@code null}.
     *
     * @param value the initial value, may be {@code null}.
     * @param <T>   the type of the value.
     * @return a new reference.
     */
    public static <T> VolatileRef<T> ofNullable(T value) {
        return new VolatileRef<>(value);
    }

    /**
     * Creates a volatile reference that may hold {@code null}.
     *
     * @param <T> the type of the value.
     * @return a new reference.
     */
    public static <T> VolatileRef<T> ofNullable() {
        return new VolatileRef<>(null);
    }

    T validate(T value) {
        return value;
    }

    @Override
    public final T get() {
        return value_;
    }

    @Override
    public final void set(T value) {
        value_ = validate(value);
    }

    @Override
    public boolean isNullable() {
        return true;
    }

}
