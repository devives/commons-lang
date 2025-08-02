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


import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Адаптер хранилища объектов {@link Store} к интерфейсу {@link List}.
 *
 * @param <E> тип элемента списка.
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public class StoreAsListAdapter<E> extends AbstractList<E> {
    /**
     * Хранилище сериализованных элементов.
     */
    private final Store<E> store_;

    /**
     *
     * @param store Хранилище элементов.
     */
    public StoreAsListAdapter(Store<E> store) {
        store_ = Objects.requireNonNull(store);
    }

    public Store<E> getStore() {
        return store_;
    }

    @Override
    public void add(int index, E element) {
        rangeCheckForAdd(index);
        modCount++;
        store_.insert(index, element);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        modCount++;
        return super.addAll(index, c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        modCount++;
        return super.addAll(c);
    }

    @Override
    public E set(int index, E element) {
        rangeCheck(index);
        E oldElement = store_.get(index);
        store_.update(index, element);
        return oldElement;
    }

    @Override
    public E remove(int index) {
        rangeCheck(index);
        modCount++;
        E oldElement = store_.remove(index);
        return oldElement;
    }

    /**
     * @param fromIndex index of first element to be removed
     * @param toIndex   index AFTER LAST element to be removed
     */
    @Override
    public void removeRange(int fromIndex, int toIndex) {
        rangeCheckForRemove(fromIndex);
        rangeCheckForRemove(toIndex);
        modCount++;
        store_.removeRange(fromIndex, toIndex);
    }

    @Override
    public E get(int index) {
        rangeCheck(index);
        return store_.get(index);
    }

    @Override
    public int size() {
        return store_.size();
    }

    /**
     * Checks if the given index is in range.  If not, throws an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     */
    private void rangeCheck(int index) {
        if (index >= size() || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * A version of rangeCheck used by add and addAll.
     */
    private void rangeCheckForAdd(int index) {
        if (index > size() || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * A version of rangeCheck used by remove and removeAll.
     */
    private void rangeCheckForRemove(int index) {
        if (index > size() || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     */
    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size();
    }

}
