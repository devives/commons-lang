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

import com.devives.commons.collection.store.serializer.BinarySerializer;

import java.io.DataInput;
import java.util.Collection;
import java.util.Collections;

/**
 * Interface of object storage with indexed access.
 *
 * @param <E> Type of stored elements.
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public interface Store<E> {

    /**
     * Метод считывает из хранилища бинарные данные, соответствующие элементу с указанным индексом, и вызывает
     * метод преобразователя {@link BinarySerializer#deserialize(DataInput)} для десериализации объекта. Преобразователь может
     * вернуть новый или существующий объект.
     *
     * @param index Индекс от "0" до "size() - 1".
     * @return Объект, десериализованный преобразователем.
     */
    E get(int index);

    /**
     * Adds a collection of elements starting from the specified position.
     *
     * @param fromIndex Index of the first element to be removed.
     * @param elements  Collection of elements.
     * @throws NullPointerException if <tt>elements</tt> is <tt>null</tt>.
     */
    void addRange(int fromIndex, Collection<E> elements);

    /**
     * Replaces a collection of elements from <tt>fromIndex</tt> to <tt>toIndex</tt> position by elements
     * from <tt>elements</tt>.
     *
     * @param fromIndex Index of the first element to be removed.
     * @param toIndex   Index of the element following the last element to be removed.
     * @param elements  Collection of elements.
     * @throws NullPointerException if <tt>elements</tt> is <tt>null</tt>.
     */
    void replaceRange(int fromIndex, int toIndex, Collection<E> elements);

    /**
     * Removes a range of elements with the specified indexes from the storage.
     *
     * @param fromIndex Index of the first element to be removed.
     * @param toIndex   Index of the element FOLLOWING the last element to be removed.
     */
    void removeRange(int fromIndex, int toIndex);

    /**
     * Returns the number of elements in the storage.
     *
     * @return The count of elements.
     */
    int size();

    /**
     * Insert an <tt>element</tt> to the storage with given <tt>index</tt>.
     *
     * @param index   Index from "0" to "size()".
     * @param element Element.
     * @throws NullPointerException if <tt>element</tt> is <tt>null</tt>.
     */
    default void insert(int index, E element) {
        replaceRange(index, index, Collections.singleton(element));
    }

    /**
     * Adds an element to the end.
     *
     * @param element Element.
     * @throws NullPointerException if <tt>elements</tt> is <tt>null</tt>.
     */
    default boolean add(E element) {
        int index = size();
        replaceRange(index, index, Collections.singleton(element));
        return true;
    }

    /**
     * Removes the element with the specified index from the storage.
     *
     * @param index Index from "0" to "size() - 1".
     * @return Removed element.
     */
    default E remove(int index) {
        E result = get(index);
        removeRange(index, index + 1);
        return result;
    }

    /**
     * Gets a collection of elements from <tt>fromIndex</tt> to <tt>toIndex</tt> position and put them
     * in to <tt>store</tt>.
     *
     * @param fromIndex Index of the first element to be removed.
     * @param toIndex   Index of the element following the last element to be removed.
     * @param store     The collection of elements where the elements will be placed.
     * @throws NullPointerException if <tt>store</tt> is <tt>null</tt>.
     */
    default void getRange(int fromIndex, int toIndex, Store<E> store) {
        for (int i = fromIndex; i < Math.min(toIndex, size()); i++) {
            store.add(get(i));
        }
    }

    /**
     * Replaces an element at <tt>index</tt> position by <tt>element</tt>.
     *
     * @param element Element.
     * @throws NullPointerException if <tt>elements</tt> is <tt>null</tt>.
     */
    default void update(int index, E element) {
        replaceRange(index, index + 1, Collections.singleton(element));
    }

    /**
     * Clears the storage by removing all elements.
     */
    default void clear() {
        removeRange(0, size());
    }
}
