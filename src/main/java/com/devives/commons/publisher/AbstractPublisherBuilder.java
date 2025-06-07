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
package com.devives.commons.publisher;

import com.devives.commons.lang.ExceptionUtils;
import com.devives.commons.listener.Listeners;
import com.devives.commons.listener.ListenersBuilder;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * Abstract {@link Publisher} builder.
 */
public abstract class AbstractPublisherBuilder<I, B, SELF extends AbstractPublisherBuilder> {

    /**
     * The default error handler witch throw an exception.
     */
    private static final Consumer<Exception> DEFAULT_ERROR_HANDLER = (exception) -> {
        throw ExceptionUtils.asUnchecked(exception);
    };

    private final ListenersBuilder<I> listenersBuilder_ = Listeners.builder();
    private boolean listenerPresenceCheck_ = false;
    private boolean independentDelivery_ = false;
    private Consumer<Exception> errorHandler_ = DEFAULT_ERROR_HANDLER;

    protected AbstractPublisherBuilder() {
    }

    /**
     * Provides access to the listeners builder.
     *
     * @param builderConsumer lambda with the listeners builder.
     * @return this builder.
     */
    public SELF listeners(Consumer<ListenersBuilder<I>> builderConsumer) {
        builderConsumer.accept(listenersBuilder_);
        return (SELF) this;
    }

    /**
     * Enables checking the presence of a listener in the collection {@link Publisher#getListeners()}, before calling the handler.
     *
     * @param value new value.
     * @return this builder.
     */
    public SELF setListenerPresenceCheck(boolean value) {
        listenerPresenceCheck_ = value;
        return (SELF) this;
    }

    /**
     * Enables checking the presence of a listener in the collection {@link Publisher#getListeners()}, before calling the handler.
     *
     * @return this builder.
     *
     * @since 0.2.0
     */
    public SELF setListenerPresenceCheck() {
        return setListenerPresenceCheck(true);
    }

    /**
     * Make delivery event independent one another.
     * <p>
     * Dependent delivery - if one of listeners throw exception, next listeners in collection will not fire, exception will throw.<br>
     * Independent delivery - all listeners will be firing independent one another. At the end will throw aggregate exception with all failures.
     *
     * @param value new value.
     * @return this builder.
     */
    public SELF setIndependentDelivery(boolean value) {
        independentDelivery_ = value;
        return (SELF) this;
    }

    /**
     * Set IndependentDelivery to true.
     *
     * @return this builder.
     * @see #setIndependentDelivery(boolean)
     */
    public SELF setIndependentDelivery() {
        return setIndependentDelivery(true);
    }

    /**
     * Set an error handler.
     *
     * @param errorHandler error handler.
     * @return this builder.
     */
    public SELF setErrorHandler(Consumer<Exception> errorHandler) {
        errorHandler_ = Objects.requireNonNull(errorHandler);
        return (SELF) this;
    }

    /**
     * Build {@link Publisher} instance.
     *
     * @return new {@link Publisher} instance.
     */
    public B build() {
        Listeners<I> listeners = listenersBuilder_.build();

        BiPredicate<I, Listeners<I>> listenerPresenceChecker = listenerPresenceCheck_
                ? ((aListener, aListeners) -> aListeners.contains(aListener))
                : ((aListener, aListeners) -> true);

        Distributor<I> distributor = independentDelivery_
                ? new IndependentDistributor<>(listenerPresenceChecker, errorHandler_)
                : new DefaultDistributor<>(listenerPresenceChecker, errorHandler_);

        return newInstance(listeners, distributor);
    }

    protected abstract B newInstance(Listeners<I> listeners, Distributor<I> distributor);
}

