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

import java.util.Objects;

/**
 * An event that represents an error.
 *
 * @param <T> the type of throwable.
 *
 * @since 0.2.0
 */
public class ErrorEvent<T extends Throwable> extends BaseEvent {

    private final T throwable_;

    /**
     * Returns the throwable associated with this event.
     *
     * @return the throwable associated with this event.
     */
    public T getThrowable() {
        return throwable_;
    }

    /**
     * Constructs a new ErrorEvent with the specified source and throwable.
     *
     * @param source the source of the event.
     * @param throwable the throwable associated with the event.
     */
    public ErrorEvent(Object source, T throwable) {
        super(source);
        throwable_ = Objects.requireNonNull(throwable, "throwable");
    }
}

