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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Common implementation of {@link Ref}.
 * <p>
 * Subclasses restrict acceptable values by overriding {@link #validate(Object)}.
 *
 * @param <T> the type of the value
 */
abstract class AbstractRef<T> implements Ref<T> {

    private T value_;

    /**
     * Creates a reference.
     *
     * @param value the initial value, checked by {@link #validate(Object)}.
     */
    AbstractRef(T value) {
        value_ = validate(value);
    }

    /**
     * Checks a value before it is stored. Called by the constructor and by {@link #set(Object)},
     * so it must not depend on the state of the subclass.
     *
     * @param value the value to check.
     * @return the value to store.
     */
    protected T validate(T value) {
        return value;
    }

    @Override
    public T get() {
        return value_;
    }

    @Override
    public void set(T value) {
        value_ = validate(value);
    }

    @Override
    public boolean isNull() {
        return value_ == null;
    }

    @Override
    public boolean isPresent() {
        return value_ != null;
    }

    @Override
    public void ifPresent(Consumer<? super T> consumer) {
        if (value_ != null) {
            consumer.accept(value_);
        }
    }

    @Override
    public T orElse(T other) {
        return value_ != null ? value_ : other;
    }

    @Override
    public T orElseGet(Supplier<? extends T> other) {
        return value_ != null ? value_ : other.get();
    }

    /**
     * {@inheritDoc}
     * <p>
     * See {@link Ref} for the equality contract.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Ref)) {
            return false;
        }
        return Objects.equals(value_, ((Ref<?>) obj).get());
    }

    /**
     * {@inheritDoc}
     * <p>
     * See {@link Ref} for the hash code contract.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value_);
    }

    /**
     * Returns a string representation of this reference suitable for debugging.
     * The exact presentation format is unspecified and may vary between versions.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        return "Ref[" + value_ + "]";
    }

}
