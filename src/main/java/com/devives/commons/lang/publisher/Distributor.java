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

import java.util.function.Consumer;

/**
 * An interface defines the strategy for distributing events to listeners of type {@code <I>}.
 * <p>
 * Implementations control how events are delivered to listeners, including:
 * <ul>
 *   <li>The order of listener notification</li>
 *   <li>Error handling during distribution</li>
 *   <li>Threading and synchronization behavior</li>
 *   <li>Listener existence validation</li>
 * </ul>
 * @param <I> the type of listener.
 * @see Publisher
 *
 * @since 0.2.0
 */
public interface Distributor<I> {

    /**
     * Distributes an event to all listeners in the collection by applying the given consumer.
     * <p>
     * The exact distribution behavior (ordering, error handling, etc.) is implementation-specific.
     *
     * @param consumer  the operation to apply to each listener (non-null)
     * @param listeners the collection of listeners to notify (non-null)
     * @throws RuntimeException if implementation-specific error handling allows it to propagate
     */
    void distribute(final Consumer<I> consumer, final Listeners<I> listeners);
}
