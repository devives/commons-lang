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
 * Base implementation of the event object interface.
 *
 * @since 0.2.0
 */
public class BaseEvent implements Event {

    private final Object source_;

    /**
     * Constructs a new BaseEvent with the specified source.
     *
     * @param source the source of the event.
     */
    public BaseEvent(Object source) {
        source_ = Objects.requireNonNull(source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getSource() {
        return source_;
    }

}
