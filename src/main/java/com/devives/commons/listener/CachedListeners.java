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

/**
 * The class caches the listener array when calling {@link CachedListeners#toArray()} to avoid frequent
 * conversion of the listener collection to an array. The cache is reset when calling the {@link #add},
 * {@link #remove} or {@link #clear} methods.
 *
 * @param <I> the type of listener.
 *
 * @since 0.2.0
 */
public final class CachedListeners<I> extends ListenersWrapper<I> {

    /**
     * Синхронизация обеспечивается декоратором {@link SynchronizedListeners}.
     */
    private Object[] array_;

    public CachedListeners(Listeners<I> listeners) {
        super(listeners);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(I item) {
        array_ = null;
        listeners_.add(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFirst(I item) {
        array_ = null;
        listeners_.addFirst(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBefore(I item, I prior) {
        array_ = null;
        listeners_.addBefore(item, prior);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAfter(I item, I next) {
        array_ = null;
        listeners_.addAfter(item, next);
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
        array_ = null;
        listeners_.remove(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        array_ = null;
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
     *
     * @return new or cached array instance.
     */
    @Override
    public Object[] toArray() {
        if (array_ == null) {
            array_ = listeners_.toArray();
        }
        return array_;
    }

}
