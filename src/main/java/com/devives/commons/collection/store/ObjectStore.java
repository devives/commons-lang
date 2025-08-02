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

import java.util.*;

/**
 * A store of &lt;E&gt; elements based on {@link List}.
 * <p>
 * Commonly used as a buffer store of elements.
 *
 * @param <E> an element type.
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public final class ObjectStore<E> extends AbstractStore<E> {

    private final List<E> list_;

    public ObjectStore() {
        this(new ArrayList<>());
    }

    public ObjectStore(List<E> list) {
        list_ = Objects.requireNonNull(list, "list");
    }

    @Override
    public void insert(int index, E element) {
        list_.add(index, element);
    }

    @Override
    public E get(int index) {
        return list_.get(index);
    }

    @Override
    public E remove(int index) {
        return list_.remove(index);
    }

    @Override
    public void getRange(int fromIndex, int toIndex, Store<E> store) {
        int remain = toIndex - fromIndex;
        ListIterator<E> listIterator = list_.listIterator(fromIndex);
        while (remain > 0 && listIterator.hasNext()) {
            store.add(listIterator.next());
            remain--;
        }
    }

    @Override
    public void addRange(int fromIndex, Collection<E> elements) {
        list_.addAll(fromIndex, elements);
    }

    @Override
    public void replaceRange(int fromIndex, int toIndex, Collection<E> elements) {
        if (elements.isEmpty()) {
            removeRange(fromIndex, toIndex);
        } else if (toIndex - fromIndex >= elements.size()) {
            int copied = 0;
            Iterator<E> iterator = elements.iterator();
            ListIterator<E> listIterator = list_.listIterator(fromIndex);
            while (iterator.hasNext() && listIterator.hasNext()) {
                listIterator.next();
                listIterator.set(iterator.next());
                copied++;
            }
            if (toIndex - fromIndex > elements.size()) {
                removeRange(fromIndex + copied, toIndex);
            }
        } else {
            if (toIndex - fromIndex > 0) {
                removeRange(fromIndex, toIndex);
            }
            list_.addAll(fromIndex, elements);
        }
    }

    @Override
    public void removeRange(int fromIndex, int toIndex) {
        list_.subList(fromIndex, toIndex).clear();
    }

    @Override
    public int size() {
        return list_.size();
    }

    @Override
    public void clear() {
        list_.clear();
    }

    @Override
    public boolean add(E element) {
        return list_.add(element);
    }

}
