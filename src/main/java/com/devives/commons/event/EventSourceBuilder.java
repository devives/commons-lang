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
package com.devives.commons.event;

import com.devives.commons.listener.Listeners;
import com.devives.commons.publisher.AbstractPublisherBuilder;
import com.devives.commons.publisher.Distributor;

/**
 * Event source builder.
 *
 * @since 0.2.0
 */
public final class EventSourceBuilder<E extends Event> extends AbstractPublisherBuilder<EventListener<E>, EventSource<E>, EventSourceBuilder<E>> {

    /**
     * Creates the concrete {@link DefaultEventSource} instance using the configured
     * listeners and distributor strategy.
     *
     * @param listeners   the configured listeners collection (non-null)
     * @param distributor the event distribution strategy (non-null)
     * @return a new fully configured publisher instance
     */
    @Override
    protected EventSource<E> newInstance(Listeners<EventListener<E>> listeners, Distributor<EventListener<E>> distributor) {
        return new DefaultEventSource<>(listeners, distributor);
    }
}
