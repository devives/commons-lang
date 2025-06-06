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

import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * The default implementation of {@link Distributor} that delivers events to listeners sequentially,
 * stopping distribution if any listener throws an exception.
 *
 * @param <I> the type of listeners that will receive events
 * @see Distributor
 * @see PublisherBuilder
 *
 * @since 0.2.0
 */
public final class DefaultDistributor<I> extends AbstractDistributor<I> {

    /**
     * Constructs a new distributor with the specified validation and error handling.
     *
     * @param listenerPresenceChecker predicate that checks if a listener should be notified
     *        (non-null, typically checks {@link Listeners#contains(Object)})
     * @param errorHandler consumer that processes exceptions during distribution (non-null)
     */
    public DefaultDistributor(BiPredicate<I, Listeners<I>> listenerPresenceChecker, Consumer<Exception> errorHandler) {
        super(listenerPresenceChecker, errorHandler);
    }

    /**
     * Sequentially delivers the event to all valid listeners, stopping on first error.
     * <p>
     * Implementation notes:
     * <ol>
     *   <li>Obtains a snapshot of listeners via {@link ArraySource#toArray()}</li>
     *   <li>Validates each listener using the presence checker</li>
     *   <li>Applies the consumer to valid listeners in array order</li>
     *   <li>Stops and delegates to error handler on first exception</li>
     * </ol>
     *
     * @param consumer the operation to apply to each listener (non-null)
     * @param listeners the collection of listeners (non-null)
     */
    @Override
    protected void doDistribution(Consumer<I> consumer, Listeners<I> listeners) {
        Object[] listenerArray = listeners.toArray();
        for (Object oListener : listenerArray) {
            if (isListenerPresent((I) oListener, listeners)) {
                consumer.accept((I) oListener);
            }
        }
    }

}

