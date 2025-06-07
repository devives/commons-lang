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

/**
 * An event that represents data.
 *
 * @param <T> the type of data.
 *
 * @since 0.2.0
 */
public class DataEvent<T> extends BaseEvent {

    /**
     * The data associated with this event.
     */
    private final T data_;

    /**
     * Returns the data associated with this event.
     *
     * @return the data associated with this event.
     */
    public T getData() {
        return data_;
    }

    /**
     * Constructs a new DataEvent with the specified source and data.
     *
     * @param source the source of the event.
     * @param data the data associated with the event.
     */
    public DataEvent(Object source, T data) {
        super(source);
        data_ = data;
    }
}

