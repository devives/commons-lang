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

import java.util.List;
import java.util.Objects;

/**
 * A class that implements the Listeners interface and provides a store for listeners.
 *
 * @param <I> the type of listener.
 *
 * @since 0.2.0
 */
public final class ListenersStore<I> implements Listeners<I> {

    private final List<I> list_;

    /**
     * Constructs a new ListenersStore with the specified list.
     *
     * @param list the list of listeners.
     */
    public ListenersStore(List<I> list) {
        list_ = Objects.requireNonNull(list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(I item) {
        Objects.requireNonNull(item, "item");
        list_.add(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFirst(I item) {
        Objects.requireNonNull(item, "item");
        list_.add(0, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBefore(I item, I prior) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(prior, "prior");
        int priorIndex = list_.indexOf(prior);
        if (priorIndex < 0) {
            throw new IndexOutOfBoundsException(String.valueOf(priorIndex));
        }
        list_.add(priorIndex, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAfter(I item, I next) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(next, "next");
        int priorIndex = list_.indexOf(next);
        if (priorIndex < 0) {
            throw new IndexOutOfBoundsException(String.valueOf(priorIndex));
        }
        list_.add(priorIndex + 1, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(I item) {
        Objects.requireNonNull(item, "item");
        return list_.contains(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(I item) {
        Objects.requireNonNull(item, "item");
        list_.remove(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        list_.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return list_.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        return list_.toArray();
    }

}
