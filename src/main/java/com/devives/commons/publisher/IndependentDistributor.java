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
import com.devives.commons.lang.exception.AggregateException;
import com.devives.commons.listener.Listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * A {@link Distributor} implementation that delivers events to all listeners independently,
 * continuing distribution even if some listeners throw exceptions.
 *
 * @param <I> the type of listeners receiving events
 * @see PublisherBuilder#setIndependentDelivery()
 *
 * @since 0.2.0
 */
public final class IndependentDistributor<I> extends AbstractDistributor<I> {

    /**
     * Constructs an independent distributor with validation and error handling.
     *
     * @param listenerPresenceChecker predicate to validate listeners before notification (non-null)
     * @param errorHandler consumer to process all encountered exceptions (non-null)
     */
    public IndependentDistributor(BiPredicate<I, Listeners<I>> listenerPresenceChecker, Consumer<Exception> errorHandler) {
        super(listenerPresenceChecker, errorHandler);
    }

    /**
     * Delivers events to all valid listeners, collecting any exceptions into an {@link AggregateException}.
     *
     * @param consumer the operation to apply to each listener (non-null)
     * @param listeners the listener collection (non-null)
     */
    @Override
    protected void doDistribution(Consumer<I> consumer, Listeners<I> listeners) {
        List<Exception> exceptionList = null;
        Object[] listenerArray = listeners.toArray();
        for (Object oListener : listenerArray) {
            try {
                if (isListenerPresent((I) oListener, listeners)) {
                    consumer.accept((I) oListener);
                }
            } catch (Exception e) {
                if (exceptionList == null) {
                    exceptionList = new ArrayList<>();
                }
                exceptionList.add(e);
            }
        }
        if (exceptionList != null) {
            ExceptionUtils.throwCollected(exceptionList);
        }
    }

}

