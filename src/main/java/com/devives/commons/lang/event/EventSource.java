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
package com.devives.commons.lang.event;

import com.devives.commons.lang.listener.Listeners;

/**
 * An interface that provides a way to send events to listeners.
 *
 * @param <E> the type of event.
 *
 * @since 0.2.0
 */
public interface EventSource<E extends Event> {

    /**
     * Returns the collection of listeners.
     *
     * @return the collection of listeners.
     */
    Listeners<EventListener<E>> getListeners();

    /**
     * Sends an event to all listeners in the collection.
     *
     * @param event the event to send.
     */
    void send(E event);

}


