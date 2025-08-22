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

import com.devives.commons.lang.Wrapper;

import java.util.*;

class AbstractListWrapper<E> implements List<E>, Wrapper {

    protected final List<E> delegate_;

    public AbstractListWrapper(List<E> delegate) {
        this.delegate_ = Objects.requireNonNull(delegate, "Delegate list is null.");
    }

    @Override
    public int size() {
        return delegate_.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate_.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate_.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return delegate_.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate_.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate_.toArray(a);
    }

    @Override
    public String toString() {
        return delegate_.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                || ((obj instanceof AbstractListWrapper) && ((AbstractListWrapper) obj).delegate_.equals(delegate_))
                || delegate_.equals(obj);
    }

    @Override
    public int hashCode() {
        return delegate_.hashCode();
    }

    @Override
    public boolean add(E e) {
        return delegate_.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return delegate_.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate_.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return delegate_.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return delegate_.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate_.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate_.retainAll(c);
    }

    @Override
    public void clear() {
        delegate_.clear();
    }

    @Override
    public E get(int index) {
        return delegate_.get(index);
    }

    @Override
    public E set(int index, E element) {
        return delegate_.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        delegate_.add(index, element);
    }

    @Override
    public E remove(int index) {
        return delegate_.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return delegate_.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate_.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return delegate_.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return delegate_.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return delegate_.subList(fromIndex, toIndex);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws Exception {
        if (iface.isInstance(delegate_)) return (T) delegate_;
        return Wrapper.super.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return iface.isInstance(delegate_);
    }
}

