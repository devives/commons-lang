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

/**
 * Источник событий
 */
public interface EventSource<E extends Event> {
    /**
     * Добавить слушателя в конец
     *
     * @param eventListener слушатель событий
     */
    void addEventListener(EventListener<E> eventListener);

    /**
     * Добавить слушателя в начало
     *
     * @param eventListener слушатель событий
     */
    void addEventListenerFirst(EventListener<E> eventListener);

    /**
     * Добавить слушателя перед существующим
     *
     * @param currentListener существующий слушатель
     * @param eventListener   добавляемый слушатель
     */
    void addEventListenerBefore(EventListener<E> currentListener, EventListener<E> eventListener);

    /**
     * Удалить слушателя
     *
     * @param eventListener слушатель событий
     */
    void removeEventListener(EventListener<E> eventListener);

    /**
     * Удалить всех слушателей
     */
    void removeAll();

    /**
     * Послать событие
     *
     * @param event событие
     */
    void fireEvent(E event);

    /**
     * Instantiate new instance of {@link EventSourceBuilder}.
     *
     * @return new instance of {@link EventSourceBuilder}.
     */
    static <E extends Event> EventSourceBuilder<E> builder() {
        return new EventSourceBuilder<>();
    }

}
