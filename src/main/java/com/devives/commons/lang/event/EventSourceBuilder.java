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
package com.devives.commons.lang.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Event source builder.
 */
public class EventSourceBuilder<E extends Event> {

    private boolean mutable_ = false;
    private boolean synchronized_ = false;
    private boolean independent_ = false;

    EventSourceBuilder() {
    }

    /**
     * Make event listener collection mutable, while occur firing event.
     *
     * @return this builder.
     */
    public EventSourceBuilder<E> setMutableWhileFire() {
        mutable_ = true;
        return this;
    }

    /**
     * Make event listener collection thread-safe.
     *
     * @return this builder.
     */
    public EventSourceBuilder<E> setSynchronized() {
        synchronized_ = true;
        return this;
    }

    /**
     * Make firing event independent one another.
     * <p>
     * Dependent fire - if one of listeners throw exception, next listeners in collection will not fire, exception will throw.<br>
     * Independent fire - all listeners will be firing independent one another. At the end will throw aggregate exception with all failures.
     *
     * @return this builder.
     */
    public EventSourceBuilder<E> setIndependentFire() {
        independent_ = true;
        return this;
    }

    /**
     * Build {@link EventSource} instance.
     *
     * @return new {@link EventSource} instance.
     */
    public EventSource<E> build() {
        Listeners<EventListener<E>> eventListenerList;
        EventSourceImpl.Gun<E> gun;
        if (synchronized_) {
            eventListenerList = new ListenersImpl<>(new ArrayList<>());
        } else {
            eventListenerList = new SynchronizedListenersImpl<>(new ListenersImpl<>(new CopyOnWriteArrayList<>()));
        }
        if (mutable_) {
            gun = new EventSourceImpl.MutableGun<>(independent_);
        } else {
            gun = new EventSourceImpl.ImmutableGun<>(independent_);
        }
        return new EventSourceImpl<>(eventListenerList, gun);
    }

    private final static class ListenersImpl<I> implements Listeners<I> {

        private final List<I> list_;
        private Set<I> set_;

        public ListenersImpl(List<I> list) {
            list_ = list;
            set_ = null;
        }

        @Override
        public void add(I item) {
            list_.add(item);
            set_ = null;
        }

        @Override
        public void addFirst(I item) {
            list_.add(0, item);
            set_ = null;
        }

        @Override
        public void addBefore(I prior, I item) {
            int priorIndex = list_.indexOf(prior);
            if (priorIndex < 0) {
                throw new IndexOutOfBoundsException(String.valueOf(priorIndex));
            }
            list_.add(priorIndex, item);
            set_ = null;
        }

        @Override
        public void addAfter(I next, I item) {
            int priorIndex = list_.indexOf(next);
            if (priorIndex < 0) {
                throw new IndexOutOfBoundsException(String.valueOf(priorIndex));
            }
            list_.add(priorIndex + 1, item);
            set_ = null;
        }

        @Override
        public boolean contains(I item) {
            if (set_ == null) {
                set_ = new HashSet<>(list_);
            }
            return set_.contains(item);
        }

        @Override
        public void remove(I item) {
            if (list_.remove(item)) {
                set_ = null;
            }
        }

        @Override
        public void clear() {
            list_.clear();
            set_ = null;
        }

        @Override
        public I[] toArray(I[] array) {
            return list_.toArray(array);
        }

    }

    private final static class SynchronizedListenersImpl<I> implements Listeners<I> {

        private final Listeners<I> list_;
        private final Object mutex_;

        public SynchronizedListenersImpl(Listeners<I> list) {
            list_ = list;
            mutex_ = list;
        }

        @Override
        public void add(I item) {
            synchronized (mutex_) {
                list_.add(item);
            }
        }

        @Override
        public void addFirst(I item) {
            synchronized (mutex_) {
                list_.addFirst(item);
            }
        }

        @Override
        public void addBefore(I prior, I item) {
            synchronized (mutex_) {
                list_.addAfter(prior, item);
            }
        }

        @Override
        public void addAfter(I next, I item) {
            synchronized (mutex_) {
                list_.addAfter(next, item);
            }
        }

        @Override
        public boolean contains(I item) {
            synchronized (mutex_) {
                return list_.contains(item);
            }
        }

        @Override
        public void remove(I item) {
            synchronized (mutex_) {
                list_.remove(item);
            }
        }

        @Override
        public void clear() {
            synchronized (mutex_) {
                list_.clear();
            }
        }

        @Override
        public I[] toArray(I[] array) {
            synchronized (mutex_) {
                return list_.toArray(array);
            }
        }

    }

}
