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

import com.devives.commons.lang.Wrapper;

import java.util.Objects;

/**
 * An abstract class that wraps a collection of listeners and provides a way to access them.
 *
 * @param <I> the type of listener.
 *
 * @since 0.2.0
 */
abstract class ListenersWrapper<I> implements Listeners<I>, Wrapper {

    final Listeners<I> listeners_;

    /**
     * Constructs a new ListenersWrapper with the specified listeners.
     *
     * @param listeners the collection of listeners.
     */
    public ListenersWrapper(Listeners<I> listeners) {
        listeners_ = Objects.requireNonNull(listeners);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) {
        if (iface.isInstance(this)) return true;
        if (iface.isInstance(listeners_)) return true;
        if (listeners_ instanceof Wrapper) {
            return ((Wrapper) listeners_).isWrapperFor(iface);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws Exception {
        if (iface.isInstance(this)) return (T) this;
        if (iface.isInstance(listeners_)) return (T) listeners_;
        if (listeners_ instanceof Wrapper) {
            return ((Wrapper) listeners_).unwrap(iface);
        }
        return Wrapper.super.unwrap(iface);
    }
}
