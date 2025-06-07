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
import java.util.Set;
import java.util.stream.Stream;

/**
 * This class ensures the uniqueness of listeners added to the collection.
 * <p>
 * It performs a search for the listener in the collection before adding. When dealing with a large number of listeners,
 * it may be inefficient. In such cases, {@link IndexedListeners} should be used.
 *
 * @param <I> the type of listener.
 *
 * @since 0.2.0
 */
final class DistinctListeners<I> extends ListenersWrapper<I> {

    /**
     * Constructs a new DistinctListeners with the specified listeners.
     *
     * @param listeners the collection of listeners.
     * @throws DuplicateRegistrationException if collection {@code listeners} contains duplicates.
     */
    public DistinctListeners(Listeners<I> listeners) {
        super(listeners);
        final Set<I> set = new HashSet<I>();
        Stream.of(listeners.toArray())
                .map(listener -> (I) listener)
                .forEach(listener -> {
                    if (!set.add(listener)) {
                        throw new DuplicateRegistrationException("Duplicate event listener registration.");
                    }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(I item) {
        if (!contains(item)) {
            listeners_.add(item);
        } else {
            throw new DuplicateRegistrationException("Duplicate event listener registration.");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws DuplicateRegistrationException if {@code item} was already added.
     */
    @Override
    public void addFirst(I item) {
        if (!contains(item)) {
            listeners_.addFirst(item);
        } else {
            throw new DuplicateRegistrationException("Duplicate event listener registration.");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws DuplicateRegistrationException if {@code item} was already added.
     */
    @Override
    public void addBefore(I item, I prior) {
        if (!contains(item)) {
            listeners_.addBefore(item, prior);
        } else {
            throw new DuplicateRegistrationException("Duplicate event listener registration.");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws DuplicateRegistrationException if {@code item} was already added.
     */
    @Override
    public void addAfter(I item, I next) {
        if (!contains(item)) {
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
        return listeners_.contains(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(I item) {
        listeners_.remove(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
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
