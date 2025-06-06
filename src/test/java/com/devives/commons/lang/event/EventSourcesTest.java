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
    public void build_SetIndependentDelivery_nonNull() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().setIndependentDelivery().build();
        Assertions.assertNotNull(eventSource);
    }

    @Test
    public void send_NoListenerPresenceCheck_Listener2Fired() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().setListenerPresenceCheck(false).build();
        EventListenerImpl listener2 = new EventListenerImpl();
        eventSource.getListeners().add(event -> eventSource.getListeners().remove(listener2));
        eventSource.getListeners().add(listener2);
        eventSource.send(new BaseEvent(this));
        Assertions.assertTrue(listener2.isFired());
    }

    @Test
    public void send_ListenerPresenceCheck_Listener2NotFired() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().setListenerPresenceCheck().build();
        EventListenerImpl listener2 = new EventListenerImpl();
        eventSource.getListeners().add(event -> eventSource.getListeners().remove(listener2));
        eventSource.getListeners().add(listener2);
        eventSource.send(new BaseEvent(this));
        Assertions.assertFalse(listener2.isFired());
    }

    @Test
    public void send_DependentFire_Listener2NotFired() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().listeners(builder -> builder.setDistinct()).build();
        EventListenerImpl listener2 = new FailureEventListenerImpl();
        eventSource.getListeners().add(new FailureEventListenerImpl());
        eventSource.getListeners().add(listener2);
        Assertions.assertThrows(RuntimeException.class, () -> eventSource.send(new BaseEvent(this)));
        Assertions.assertFalse(listener2.isFired());
    }

    @Test
    public void send_IndependentFire_Listener2Fired() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().setIndependentDelivery().build();
        EventListenerImpl listener2 = new FailureEventListenerImpl();
        eventSource.getListeners().add(new FailureEventListenerImpl());
        eventSource.getListeners().add(listener2);
        Assertions.assertThrows(AggregateException.class, () -> eventSource.send(new BaseEvent(this)));
        Assertions.assertTrue(listener2.isFired());
    }

    @Test
    public void send_MutableDependentFire_Listener2NotFired() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().listeners(builder -> builder.setSynchronized()).setListenerPresenceCheck().build();
        EventListenerImpl listener2 = new FailureEventListenerImpl();
        eventSource.getListeners().add(new FailureEventListenerImpl());
        eventSource.getListeners().add(listener2);
        Assertions.assertThrows(RuntimeException.class, () -> eventSource.send(new BaseEvent(this)));
        Assertions.assertFalse(listener2.isFired());
    }

    @Test
    public void send_MutableIndependentFire_Listener2Fired() throws Exception {
        EventSource<Event> eventSource = EventSources.builder().listeners(builder -> builder.setSynchronized()).setListenerPresenceCheck().setIndependentDelivery().build();
        EventListenerImpl listener2 = new FailureEventListenerImpl();
        eventSource.getListeners().add(new FailureEventListenerImpl());
        eventSource.getListeners().add(listener2);
        Assertions.assertThrows(AggregateException.class, () -> eventSource.send(new BaseEvent(this)));
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
