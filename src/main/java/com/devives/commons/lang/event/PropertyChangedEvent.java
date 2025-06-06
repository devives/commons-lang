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
 * An event that represents a property change.
 *
 * @param <T> the type of property.
 *
 * @since 0.2.0
 */
public class PropertyChangedEvent<T> extends BaseEvent {

    private final T property_;

    /**
     * Constructs a new PropertyChangedEvent with the specified source and property.
     *
     * @param source   the source of the event.
     * @param property the property associated with the event.
     */
    public PropertyChangedEvent(Object source, T property) {
        super(source);
        property_ = Objects.requireNonNull(property, "property");
    }

    /**
     * Returns the property associated with this event.
     *
     * @return the property associated with this event.
     */
    public T getProperty() {
        return property_;
    }

}
