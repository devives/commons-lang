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
package com.devives.commons.collection;

import com.devives.commons.collection.store.BufferedObjectStore;
import com.devives.commons.collection.store.BufferedStoreList;
import com.devives.commons.collection.store.ObjectStore;

import java.util.List;

/**
 * Буферизированный список элементов.
 * <p>
 * В качестве буфера и основного хранилища элементов используются списки {@link List}.
 * <p>
 * Структура обеспечивает ускорение массовых вставок и удалений элементов в начале/середине большого {@link java.util.ArrayList}.
 *
 * @param <E> Тип элемента
 */
public class BufferedList<E> extends BufferedStoreList<E> {

    /**
     * Создаёт буферизированный список.
     * <p>
     * В качестве буфера используется {@link java.util.ArrayList}.
     *
     * @param list основной список, который будет использоваться в качестве основного хранилища элементов.
     */
    public BufferedList(List<E> list) {
        super(new BufferedObjectStore<>(new ObjectStore<E>(list)));
    }

    /**
     * Создаёт буферизированный список.
     *
     * @param list       основной список, который будет использоваться в качестве основного хранилища элементов.
     * @param bufferList буферный список, который будет использоваться в качестве буфера.
     */
    public BufferedList(List<E> list, List<E> bufferList) {
        super(new BufferedObjectStore<E>(new ObjectStore<E>(list), new ObjectStore<E>(bufferList)));
    }

}