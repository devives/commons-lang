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

import com.devives.commons.lang.exception.AggregateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventSourcesTest {

    @Test
    public void build_setSynchronized_nonNull() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().setSynchronized().build();
        Assertions.assertNotNull(eventSource);
    }

    @Test
    public void build_SetMutableWhileFire_nonNull() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().setMutableWhileFire().build();
        Assertions.assertNotNull(eventSource);
    }

    @Test
    public void build_SetIndependentFire_nonNull() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().setIndependentFire().build();
        Assertions.assertNotNull(eventSource);
    }

    @Test
    public void fire_ImmutableWhileFire_Listener2Fired() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().build();
        EventListenerImpl listener2 = new EventListenerImpl();
        eventSource.addEventListener(event -> eventSource.removeEventListener(listener2));
        eventSource.addEventListener(listener2);
        eventSource.fireEvent(new BaseEvent(this));
        Assertions.assertTrue(listener2.isFired());
    }

    @Test
    public void fire_MutableWhileFire_Listener2NotFired() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().setMutableWhileFire().build();
        EventListenerImpl listener2 = new EventListenerImpl();
        eventSource.addEventListener(event -> eventSource.removeEventListener(listener2));
        eventSource.addEventListener(listener2);
        eventSource.fireEvent(new BaseEvent(this));
        Assertions.assertFalse(listener2.isFired());
    }

    @Test
    public void fire_DependentFire_Listener2NotFired() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().build();
        EventListenerImpl listener2 = new FailureEventListenerImpl();
        eventSource.addEventListener(new FailureEventListenerImpl());
        eventSource.addEventListener(listener2);
        Assertions.assertThrows(RuntimeException.class, () -> eventSource.fireEvent(new BaseEvent(this)));
        Assertions.assertFalse(listener2.isFired());
    }

    @Test
    public void fire_IndependentFire_Listener2Fired() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().setIndependentFire().build();
        EventListenerImpl listener2 = new FailureEventListenerImpl();
        eventSource.addEventListener(new FailureEventListenerImpl());
        eventSource.addEventListener(listener2);
        Assertions.assertThrows(AggregateException.class, () -> eventSource.fireEvent(new BaseEvent(this)));
        Assertions.assertTrue(listener2.isFired());
    }

    @Test
    public void fire_MutableDependentFire_Listener2NotFired() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().setSynchronized().setMutableWhileFire().build();
        EventListenerImpl listener2 = new FailureEventListenerImpl();
        eventSource.addEventListener(new FailureEventListenerImpl());
        eventSource.addEventListener(listener2);
        Assertions.assertThrows(RuntimeException.class, () -> eventSource.fireEvent(new BaseEvent(this)));
        Assertions.assertFalse(listener2.isFired());
    }

    @Test
    public void fire_MutableIndependentFire_Listener2Fired() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().setSynchronized().setMutableWhileFire().setIndependentFire().build();
        EventListenerImpl listener2 = new FailureEventListenerImpl();
        eventSource.addEventListener(new FailureEventListenerImpl());
        eventSource.addEventListener(listener2);
        Assertions.assertThrows(AggregateException.class, () -> eventSource.fireEvent(new BaseEvent(this)));
        Assertions.assertTrue(listener2.isFired());
    }

    private static class EventListenerImpl implements EventListener<Event> {
        private volatile boolean fired_ = false;

        @Override
        public void handleEvent(Event event) {
            fired_ = true;
        }

        public boolean isFired() {
            return fired_;
        }
    }

    private static class FailureEventListenerImpl extends EventListenerImpl {

        @Override
        public void handleEvent(Event event) {
            super.handleEvent(event);
            throw new RuntimeException("Test exception");
        }
    }
}
