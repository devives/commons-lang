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
 * A class that extends AbstractPublisher and provides a way to publish events to listeners.
 *
 * @param <I> the type of listener.
 *
 * @since 0.2.0
 */
final class DefaultPublisher<I> extends AbstractPublisher<I> implements Publisher<I> {

    /**
     * Constructs a new DefaultPublisher with the specified listeners and distributor.
     *
     * @param listeners the collection of listeners.
     * @param distributor the distributor.
     */
    protected DefaultPublisher(Listeners<I> listeners, Distributor<I> distributor) {
        super(listeners, distributor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(Consumer<I> consumer) {
        getDistributor().distribute(consumer, getListeners());
    }

}


