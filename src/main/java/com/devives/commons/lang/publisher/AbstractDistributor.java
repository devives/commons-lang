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
package com.devives.commons.lang.publisher;

import com.devives.commons.lang.listener.Listeners;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * An abstract class that implements the Distributor interface and provides a way to distribute events to listeners.
 *
 * @param <I> the type of listener.
 */
public abstract class AbstractDistributor<I> implements Distributor<I> {
    private final BiPredicate<I, Listeners<I>> listenerPresenceChecker_;
    private final Consumer<Exception> errorHandler_;

    /**
     * Constructs a new AbstractDistributor with the specified listener presence checker and error handler.
     *
     * @param listenerPresenceChecker the listener presence checker.
     * @param errorHandler the error handler.
     */
    protected AbstractDistributor(BiPredicate<I, Listeners<I>> listenerPresenceChecker, Consumer<Exception> errorHandler) {
        listenerPresenceChecker_ = Objects.requireNonNull(listenerPresenceChecker);
        errorHandler_ = Objects.requireNonNull(errorHandler);
    }

    /**
     * Checks if a listener is present in the collection.
     *
     * @param listener the listener to check.
     * @param listeners the collection of listeners.
     * @return true if the listener is present, false otherwise.
     *
     * @since 0.2.0
     */
    protected final boolean isListenerPresent(I listener, Listeners<I> listeners) {
        return listenerPresenceChecker_.test(listener, listeners);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void distribute(Consumer<I> consumer, Listeners<I> listeners) {
        try {
            doDistribution(consumer, listeners);
        } catch (Exception exception) {
            errorHandler_.accept(exception);
        }
    }

    /**
     * Distributes an event to all listeners in the collection.
     *
     * @param consumer the consumer that will be called for each listener.
     * @param listeners the collection of listeners.
     */
    protected abstract void doDistribution(Consumer<I> consumer, Listeners<I> listeners);

}

