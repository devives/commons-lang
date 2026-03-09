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
package com.devives.commons.lifecycle;

public interface CloseableStates {

    /**
     * Indicates whether the resource is open.
     *
     * @return <code>true</code> if the resource is open else <code>false</code>.
     */
    boolean isOpening();

    /**
     * Indicates whether the resource is open.
     *
     * @return <code>true</code> if the resource is open else <code>false</code>.
     */
    boolean isOpened();

    /**
     * Indicates whether the resource is closing.
     *
     * @return <code>true</code> if the resource is closing else <code>false</code>.
     */
    boolean isClosing();

    /**
     * Indicates whether the resource has been closed.
     *
     * @return <code>true</code> if the resource is closed else <code>false</code>.
     */
    boolean isClosed();

    State OPENING = StateFactory.named("OPENING");
    State OPENED = StateFactory.named("OPENED");
    State CLOSING = StateFactory.named("CLOSING");
    State CLOSED = StateFactory.named("CLOSED");
}
