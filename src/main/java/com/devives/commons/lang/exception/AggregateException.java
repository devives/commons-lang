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

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Агрегирующее исключение.
 * <p>
 * Объединяет несколько последовательно возникших исключений в коллекции {@link Exception#getSuppressed()}.
 */
public class AggregateException extends RuntimeException {

    public AggregateException(String message) {
        super(message);
    }

    public <E extends Throwable> AggregateException(Collection<E> suppressed) {
        super();
        suppressed.forEach(this::addSuppressed);
    }

    public <E extends Throwable> AggregateException(E[] suppressed) {
        super();
        Stream.of(suppressed).forEach(this::addSuppressed);
    }

    public <E extends Throwable> AggregateException(String message, E[] suppressed) {
        super(message);
        Stream.of(suppressed).forEach(this::addSuppressed);
    }

    public <E extends Throwable> AggregateException(String message, Collection<E> suppressed) {
        super(message);
        suppressed.forEach(this::addSuppressed);
    }

}
