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
package com.devives.commons.collection.store;

import com.devives.commons.collection.BufferController;

/**
 * Интерфейс хранилища элементов с поддержкой буферизации.
 * <p>
 * Объединяет возможности управления буфером ({@link BufferController})
 * и стандартного хранилища ({@link Store}).
 *
 * @param <E> тип элементов, хранимых в буфере и основном хранилище
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public interface BufferedStore<E> extends Store<E>, BufferController {
}
