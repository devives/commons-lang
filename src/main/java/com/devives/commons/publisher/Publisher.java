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


import com.devives.commons.listener.Listeners;

import java.util.function.Consumer;

/**
 * Defines a contract for publishing events to registered listeners of type {@code <I>}.
 * <p>
 *
 * <h3>Event Publication</h3>
 * Events are published by providing a {@link Consumer} that defines how each listener
 * should process the event. For example:
 * <pre>{@code
 * publisher.publish(listener -> listener.onEvent(event));
 * }</pre>
 *
 * <h3>Thread Safety</h3>
 * Implementations should be thread-safe for both listener management and event publication,
 * unless explicitly documented otherwise.
 *
 * @param <I> the type of listeners managed by this publisher
 *
 * @see Publisher
 * @see PublisherBuilder
 * @see Listeners
 * @since 0.2.0
 */
public interface Publisher<I> {

    /**
     * Returns the collection of currently registered listeners.
     * <p>
     * The returned collection may or may not reflect modifications made after this call,
     * depending on implementation.
     *
     * @return the current listeners collection (non-null)
     */
    Listeners<I> getListeners();

    /**
     * Publishes an event to all registered listeners using the provided consumer.
     * <p>
     * The exact delivery behavior (ordering, error handling, etc.) depends on the
     * publisher's configuration.
     *
     * @param consumer the operation to perform on each listener (non-null)
     * @throws NullPointerException if the consumer is null
     * @throws RuntimeException if configured error handling allows it to propagate
     */
    void publish(Consumer<I> consumer);

    /**
     * Creates and returns a new {@link PublisherBuilder} for the specified listener type.
     * <p>
     * Used as an entry point for creating event publisher with flexible settings.
     * <p>
     * Example usage:
     * <pre>{@code
     * Publisher<EventListener> publisher = Publisher.<EventListener>builder()
     *     .listeners(builder -> builder.setSynchronized().setDistinct())
     *     .setIndependentDelivery()
     *     .build();
     * }</pre>
     * @param <I> the type of listener to which events will be sent.
     * @return a new instance of {@link PublisherBuilder}.
     */
    static <I> PublisherBuilder<I> builder() {
        return new PublisherBuilder<>();
    }
}

