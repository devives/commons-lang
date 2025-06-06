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

/**
 * A class that extends ListenersWrapper and provides a way to synchronize access to listeners.
 *
 * @param <I> the type of listener.
 *
 * @since 0.2.0
 */
public final class SynchronizedListeners<I> extends ListenersWrapper<I> {

    private final Object mutex_ = new Object();

    /**
     * Constructs a new SynchronizedListeners with the specified listeners.
     *
     * @param listeners the collection of listeners.
     */
    public SynchronizedListeners(Listeners<I> listeners) {
        super(listeners);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(I item) {
        synchronized (mutex_) {
            listeners_.add(item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFirst(I item) {
        synchronized (mutex_) {
            listeners_.addFirst(item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBefore(I item, I prior) {
        synchronized (mutex_) {
            listeners_.addBefore(item, prior);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAfter(I item, I next) {
        synchronized (mutex_) {
            listeners_.addAfter(item, next);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(I item) {
        synchronized (mutex_) {
            return listeners_.contains(item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(I item) {
        synchronized (mutex_) {
            listeners_.remove(item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        synchronized (mutex_) {
            listeners_.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        synchronized (mutex_) {
            return listeners_.size();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        synchronized (mutex_) {
            return listeners_.toArray();
        }
    }
}
