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
package com.devives.commons.lang.exception;

/**
 * Исключение выбрасывается проверками, требующим не пустую строку.
 */
public class EmptyStringException extends RuntimeException {

    /**
     * Создаёт исключение без сообщения.
     */
    public EmptyStringException() {
    }

    /**
     * Создаёт исключение с сообщением.
     *
     * @param message сообщение.
     */
    public EmptyStringException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с сообщением и причиной.
     *
     * @param message сообщение.
     * @param cause   причина.
     */
    public EmptyStringException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Создаёт исключение с причиной.
     *
     * @param cause причина.
     */
    public EmptyStringException(Throwable cause) {
        super(cause);
    }

}
