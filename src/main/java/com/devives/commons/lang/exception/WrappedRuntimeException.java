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
 * Данный класс исключений используется для проброса исключений через методы,
 * которые не имеет отмеченных исключений.
 * Например, из анонимного метода или обработчика событий.
 */
public final class WrappedRuntimeException extends RuntimeException {
    private WrappedRuntimeException() {
    }

    public WrappedRuntimeException(Throwable cause) {
        super(cause);
    }

    public WrappedRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Unwrap the cause exception.
     *
     * @param wrapper instance of WrappedRuntimeException
     * @return Exception
     * @throws Error                   if cause is Error.
     * @throws WrappedRuntimeException if cause is null.
     */
    public static Exception unwrap(WrappedRuntimeException wrapper) {
        final Throwable cause = wrapper.getCause();
        if (cause instanceof Exception) {
            return (Exception) cause;
        } else if (cause instanceof Error) {
            throw (Error) cause;
        } else {
            return wrapper;
        }
    }
}
