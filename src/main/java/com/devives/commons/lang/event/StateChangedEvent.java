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

import java.util.Objects;

/**
 * An event that represents a state change.
 *
 * @param <T> the type of state.
 *
 * @since 0.2.0
 */
public class StateChangedEvent<T> extends BaseEvent {

    private final T state_;

    /**
     * Constructs a new StateChangedEvent with the specified source and state.
     *
     * @param source the source of the event.
     * @param state the state associated with the event.
     */
    public StateChangedEvent(Object source, T state) {
        super(source);
        state_ = Objects.requireNonNull(state, "state");
    }

    /**
     * Returns the state associated with this event.
     *
     * @return the state associated with this event.
     */
    public T getState() {
        return state_;
    }

}



