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
package com.devives.commons.state;

/**
 * Signals that an object is in a state that does not allow the requested operation.
 */
public class InvalidStateException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception without a message.
     */
    public InvalidStateException() {
    }

    /**
     * Creates an exception.
     *
     * @param message the detail message.
     */
    public InvalidStateException(String message) {
        super(message);
    }

}
