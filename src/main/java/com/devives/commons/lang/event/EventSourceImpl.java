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

import com.devives.commons.lang.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Обычная реализация
 */
class EventSourceImpl<E extends Event> implements EventSource<E> {
    /**
     * Множество слушателей
     */
    private final Listeners<EventListener<E>> eventListenerList_;
    private final Gun<E> gun_;

    protected EventSourceImpl(Listeners<EventListener<E>> listenerList, Gun<E> gun) {
        this.eventListenerList_ = Objects.requireNonNull(listenerList);
        this.gun_ = Objects.requireNonNull(gun);
    }

    /**
     * Добавить слушателя
     *
     * @param eventListener слушатель событий
     */
    @Override
    public void addEventListener(final EventListener<E> eventListener) {
        eventListenerList_.add(eventListener);
    }

    @Override
    public void addEventListenerBefore(EventListener<E> priorListener, EventListener<E> eventListener) {
        eventListenerList_.addBefore(priorListener, eventListener);
    }

    @Override
    public void addEventListenerFirst(EventListener<E> eventListener) {
        eventListenerList_.addFirst(eventListener);
    }

    /**
     * Удалить слушателя
     *
     * @param eventListener слушатель событий
     */
    @Override
    public void removeEventListener(EventListener<E> eventListener) {
        eventListenerList_.remove(eventListener);
    }

    /**
     * Удалить всех
     */
    @Override
    public void removeAll() {
        eventListenerList_.clear();
    }

    /**
     * Послать событие
     *
     * @param event событие
     */
    @Override
    public final void fireEvent(E event) {
        gun_.fire(eventListenerList_, event);
    }

    interface Gun<E extends Event> {
        void fire(final Listeners<EventListener<E>> eventListenerList, final E event);
    }

    static abstract class AbstractGun<E extends Event> implements Gun<E> {
        protected final boolean independentFire_;

        public AbstractGun(boolean independentFire) {
            independentFire_ = independentFire;
        }

        @Override
        public final void fire(Listeners<EventListener<E>> listenerList, E event) {
            if (independentFire_) {
                independentFire(listenerList, event);
            } else {
                dependentFire(listenerList, event);
            }
        }

        protected abstract void dependentFire(Listeners<EventListener<E>> listenerList, E event);

        protected abstract void independentFire(Listeners<EventListener<E>> listenerList, E event);
    }

    static final class ImmutableGun<E extends Event> extends AbstractGun<E> {
        public ImmutableGun(boolean independentFire) {
            super(independentFire);
        }

        protected void dependentFire(Listeners<EventListener<E>> listenerList, E event) {
            for (EventListener<E> eventListener : listenerList.toArray(new EventListener[0])) {
                eventListener.handleEvent(event);
            }
        }

        protected void independentFire(Listeners<EventListener<E>> listenerList, E event) {
            List<Exception> exceptionList = new ArrayList<>();
            for (EventListener<E> eventListener : listenerList.toArray(new EventListener[0])) {
                try {
                    eventListener.handleEvent(event);
                } catch (Exception e) {
                    exceptionList.add(e);
                }
            }
            ExceptionUtils.throwCollected(exceptionList);
        }
    }

    static final class MutableGun<E extends Event> extends AbstractGun<E> {
        public MutableGun(boolean independentFire) {
            super(independentFire);
        }

        protected void dependentFire(Listeners<EventListener<E>> listeners, E event) {
            EventListener<E>[] listenerArray = listeners.toArray(new EventListener[0]);
            for (EventListener<E> eventListener : listenerArray) {
                if (listeners.contains(eventListener)) {
                    eventListener.handleEvent(event);
                }
            }
        }

        protected void independentFire(Listeners<EventListener<E>> listeners, E event) {
            EventListener<E>[] listenerArray = listeners.toArray(new EventListener[0]);
            List<Exception> exceptionList = new ArrayList<>();
            for (EventListener<E> eventListener : listenerArray) {
                if (listeners.contains(eventListener)) {
                    try {
                        eventListener.handleEvent(event);
                    } catch (Exception e) {
                        exceptionList.add(e);
                    }
                }
            }
            ExceptionUtils.throwCollected(exceptionList);
        }
    }


}
