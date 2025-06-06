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
import com.devives.commons.lang.listener.ListenersBuilder;

/**
 * A builder for creating configured {@link Publisher} instances that manage event distribution
 * to listeners of type {@code <I>}.
 * <p>
 * Provides fluent configuration for:
 * <ul>
 *   <li><b>Listener collection</b> - caching, synchronization, uniqueness, and indexing</li>
 *   <li><b>Delivery strategy</b> - sequential (fail-fast) or independent (continue-on-error)</li>
 *   <li><b>Error handling</b> - custom exception handling during event publication</li>
 *   <li><b>Presence validation</b> - optional listener existence checking before invocation</li>
 * </ul>
 *
 * <p>Instances of this class are mutable and not thread-safe. The built {@link Publisher}
 * instances are thread-safe if configured with synchronized listeners.</p>
 *
 * @param <I> the type of listener that will receive events
 * @see Publisher
 * @see ListenersBuilder
 *
 * @since 0.2.0
 */
public final class PublisherBuilder<I> extends AbstractPublisherBuilder<I, Publisher<I>, PublisherBuilder<I>> {

    /**
     * Creates the concrete {@link DefaultPublisher} instance using the configured
     * listeners and distributor strategy.
     *
     * @param listeners the configured listeners collection (non-null)
     * @param distributor the event distribution strategy (non-null)
     * @return a new fully configured publisher instance
     */
    @Override
    protected Publisher<I> newInstance(Listeners<I> listeners, Distributor<I> distributor) {
        return new DefaultPublisher<>(listeners, distributor);
    }
}
