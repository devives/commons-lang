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

import java.util.Collection;
import java.util.Objects;

/**
 * The class implements an abstract buffered object store.
 * <p>
 * When an object is added to the storage, the object is cached. Depending on the implementation,
 * the cache may store a reference or perform serialization.
 * @param <E> Type of elements.
 */
abstract class AbstractBufferedStore<E> extends AbstractStore<E> implements BufferedStore<E> {
    protected final AbstractBuffer<E> buffer_;
    private int bufferSize_ = 512;
    private int bufferMaxSize_ = 1024;

    protected AbstractBufferedStore(AbstractBuffer<E> buffer) {
        buffer_ = Objects.requireNonNull(buffer, "buffer");
    }

    /**
     * {@inheritDoc}
     */
    public int getBufferSize() {
        return bufferSize_;
    }

    /**
     * {@inheritDoc}
     */
    public void setBufferSize(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("The buffer size '" + size + "' is lower then one.");
        }
        buffer_.commit();
        buffer_.clear();
        bufferSize_ = size;
        bufferMaxSize_ = bufferSize_ * 2;
    }

    /**
     * {@inheritDoc}
     */
    public int getBufferMaxSize() {
        return bufferMaxSize_;
    }

    /**
     * {@inheritDoc}
     */
    public void setBufferMaxSize(int maxSize) {
        if (maxSize < 2) {
            throw new IllegalArgumentException("The max buffer size '" + maxSize + "' is lower then two.");
        }
        if (maxSize <= bufferSize_) {
            throw new IllegalArgumentException("The max buffer size '" + maxSize + "'is lower or equals buffer size '" + bufferSize_ + "'.");
        }
        buffer_.commit();
        buffer_.clear();
        bufferMaxSize_ = maxSize;
    }

    /**
     * Calculates the starting index of a page.
     *
     * @param pageNumber page number.
     * @return element index.
     */
    protected final int getStartOfPage(int pageNumber) {
        return pageNumber * bufferSize_;
    }

    /**
     * Returns the page number that the element with the specified index corresponds to.
     *
     * @param index element index.
     * @return page number.
     */
    protected final int getPageNumber(int index) {
        return index / bufferSize_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(int index, E element) {
        if (!buffer_.canAdd(index)) {
            buffer_.commit();
            int startPageIndex = getStartOfPage(getPageNumber(index));
            buffer_.load(startPageIndex, startPageIndex + bufferSize_);
        }
        buffer_.add(index, element);
        if (buffer_.size() >= bufferMaxSize_) {
            buffer_.commit();
            buffer_.collapse(index, index);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E get(int index) {
        ensureElementLoad(index);
        E result = buffer_.get(index);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E remove(int index) {
        ensureElementLoad(index);
        E result = buffer_.remove(index);
        return result;
    }

    /**
     * Checks if the element is loaded into the buffer, if not - tries to load from the main element storage.
     *
     * @param index Index of the element.
     * @On I/O errors.
     */
    private void ensureElementLoad(int index) {
        if (!buffer_.contains(index)) {
            buffer_.commit();
            int startPageIndex = getStartOfPage(getPageNumber(index));
            buffer_.load(startPageIndex, startPageIndex + bufferSize_);
            if (!buffer_.contains(index)) {
                throw new IndexOutOfBoundsException("Index: " + index + ". Size: " + size() + ".");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRange(int fromIndex, Collection<E> elements) {
        for (E element : elements) {
            insert(fromIndex++, element);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceRange(int fromIndex, int toIndex, Collection<E> elements) {
        if (buffer_.contains(fromIndex) && buffer_.contains(toIndex - 1)) {
            buffer_.replaceRange(fromIndex, toIndex, elements);
        } else {
            buffer_.commit();
            buffer_.clear();
            buffer_.setOffsetAndLoadedCount(fromIndex, toIndex - fromIndex);
            addRange(fromIndex, elements);
            buffer_.commit();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRange(int fromIndex, int toIndex) {
        if (buffer_.contains(fromIndex) && buffer_.contains(toIndex - 1)) {
            buffer_.removeRange(fromIndex, toIndex);
        } else {
            buffer_.commit();
            buffer_.clear();
            buffer_.setOffsetAndLoadedCount(fromIndex, toIndex - fromIndex);
            buffer_.commit();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flushBuffer() {
        buffer_.commit();
        // Remove all records without changing mainOffset_, so that the next insertion occurs without loading data.
        buffer_.collapse();
    }

}
