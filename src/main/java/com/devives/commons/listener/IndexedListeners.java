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
package com.devives.commons.listener;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This class ensures the uniqueness of listeners added to the collection and their indexing,
 * to speed up the {@link #contains(Object)} check. It serves as an alternative to {@link DistinctListeners}
 * when dealing with a large number of listeners.
 *
 * @param <I> the type of listener.
 *
 * @since 0.2.0
 */
public final class IndexedListeners<I> extends ListenersWrapper<I> {

    private final Set<I> set_ = new HashSet<>();

    /**
     * Constructs a new IndexedListeners with the specified listeners.
     *
     * @param listeners the collection of listeners.
     */
    public IndexedListeners(Listeners<I> listeners) {
        super(listeners);
        Stream.of(listeners.toArray())
                .map(listener -> (I) listener)
                .forEach(listener -> {
                    if (!set_.add(listener)) {
                        throw new DuplicateRegistrationException("Duplicate event listener registration.");
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(I item) {
        Objects.requireNonNull(item, "item");
        if (set_.add(item)) {
            listeners_.add(item);
        } else {
            throw new DuplicateRegistrationException("Duplicate event listener registration.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFirst(I item) {
        Objects.requireNonNull(item, "item");
        if (set_.add(item)) {
            listeners_.addFirst(item);
        } else {
            throw new DuplicateRegistrationException("Duplicate event listener registration.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBefore(I item, I prior) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(prior, "prior");
        if (set_.add(item)) {
            listeners_.addBefore(item, prior);
        } else {
            throw new DuplicateRegistrationException("Duplicate event listener registration.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAfter(I item, I next) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(next, "next");
        if (set_.add(item)) {
            listeners_.addAfter(item, next);
        } else {
            throw new DuplicateRegistrationException("Duplicate event listener registration.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(I item) {
        Objects.requireNonNull(item, "item");
        return set_.contains(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(I item) {
        Objects.requireNonNull(item, "item");
        if (set_.remove(item)) {
            listeners_.remove(item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        set_.clear();
        listeners_.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return listeners_.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        return listeners_.toArray();
    }
}
