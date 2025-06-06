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
package com.devives.commons.lang.listener;

import com.devives.commons.lang.event.EventSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Builder for a collection of listeners.
 * <p>
 * Creates an instance of {@link Listeners} according to the specified parameters.
 *
 * @param <I> the type of listener.
 *
 * @since 0.2.0
 */
public final class ListenersBuilder<I> {
    private boolean cached_ = true;
    private boolean indexed_ = false;
    private boolean distinct_ = false;
    private boolean synchronized_ = false;
    private List<I> list_ = null;

    ListenersBuilder() {
    }

    /**
     * Set cached flag to true.
     *
     * @return this builder.
     * @see CachedListeners
     * @see #setCached(boolean)
     */
    public ListenersBuilder<I> setCached() {
        cached_ = true;
        return this;
    }

    /**
     * Set cached flag to the specified value.
     * <p>
     * If {@code true}, {@link CachedListeners} will be added to the chain of listener collection decorators.
     *
     * @param value the value to set.
     * @return this builder.
     * @see CachedListeners
     */
    public ListenersBuilder<I> setCached(boolean value) {
        cached_ = value;
        return this;
    }

    /**
     * Set distinct flag to true.
     *
     * @return this builder.
     * @see DistinctListeners
     * @see #setDistinct(boolean)
     */
    public ListenersBuilder<I> setDistinct() {
        distinct_ = true;
        return this;
    }

    /**
     * Set distinct flag to the specified value.
     * <p>
     * If {@code true} and the {@code setIndexed()} flag is not set, {@link DistinctListeners}
     * will be added to the chain of listener collection decorators.
     *
     * @param value the value to set.
     * @return this builder.
     * @see DistinctListeners
     */
    public ListenersBuilder<I> setDistinct(boolean value) {
        distinct_ = value;
        return this;
    }

    /**
     * Set indexed flag to true.
     *
     * @return this builder.
     * @see #setIndexed(boolean)
     * @see IndexedListeners
     */
    public ListenersBuilder<I> setIndexed() {
        indexed_ = true;
        return this;
    }

    /**
     * Set indexed flag to the specified value.
     * <p>
     * If {@code true}, {@link IndexedListeners} will be added to the chain of listener collection decorators.
     *
     * @param value the value to set.
     * @return this builder.
     * @see IndexedListeners
     */
    public ListenersBuilder<I> setIndexed(boolean value) {
        indexed_ = value;
        return this;
    }

    /**
     * Make event listener collection thread-safe.
     *
     * @return this builder.
     * @see SynchronizedListeners
     */
    public ListenersBuilder<I> setSynchronized() {
        synchronized_ = true;
        return this;
    }

    /**
     * Set synchronized flag to the specified value.
     * <p>
     * If {@code true}, {@link SynchronizedListeners} will be added to the chain of listener collection decorators.
     *
     * @param value the value to set.
     * @return this builder.
     * @see SynchronizedListeners
     */
    public ListenersBuilder<I> setSynchronized(boolean value) {
        synchronized_ = value;
        return this;
    }

    /**
     * Set the list of listeners.
     *
     * @param list the list of listeners.
     * @return this builder.
     */
    public ListenersBuilder<I> setList(List<I> list) {
        list_ = Objects.requireNonNull(list);
        return this;
    }

    /**
     * Build {@link EventSource} instance.
     *
     * @return new {@link EventSource} instance.
     */
    public Listeners<I> build() {
        List<I> list = Optional.ofNullable(list_).orElseGet(ArrayList::new);

        Listeners<I> listeners = new ListenersStore<>(list);

        if (indexed_) {
            listeners = new IndexedListeners<>(listeners);
        } else if (distinct_) {
            listeners = new DistinctListeners<>(listeners);
        }

        if (cached_) {
            listeners = new CachedListeners<>(listeners);
        }

        if (synchronized_) {
            listeners = new SynchronizedListeners<>(listeners);
        }

        return listeners;
    }
}