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
import java.util.ConcurrentModificationException;
import java.util.Objects;

/**
 * Abstract buffer of &lt;E&gt; elements .
 *
 * @param <E> Type of elements.
 */
abstract class AbstractBuffer<E> {

    private final Store<E> store_;
    /**
     * The index of the first item loaded from main store.
     */
    protected int mainOffset_ = -1;
    /**
     * The count of the items loaded from main store.
     */
    protected int mainCount_ = 0;
    /**
     * The flag indicates that the elements in the buffer have changed since they were loaded from the main store.
     */
    protected boolean modified_ = false;

    /**
     * Constructor.
     *
     * @param store storage witch will contain a buffered elements.
     */
    protected AbstractBuffer(Store<E> store) {
        store_ = Objects.requireNonNull(store, "store");
    }

    /**
     * Global index of the first element loaded from the storage.
     *
     * @return global index.
     */
    public int getOffset() {
        return mainOffset_;
    }

    /**
     * Number of elements loaded from the storage.
     *
     * @return number of elements.
     */
    public int getLoadedCount() {
        return mainCount_;
    }

    void setOffsetAndLoadedCount(int offset, int loaded) {
        mainOffset_ = offset;
        mainCount_ = loaded;
        modified_ = true;
    }

    /**
     * Current number of elements in the buffer.
     *
     * @return number of elements.
     */
    public int size() {
        return store_.size();
    }

    /**
     * The method checks if the element with the index <tt>index</tt> is contained in the buffer.
     *
     * @param index global index of the element.
     * @return true if it is contained, otherwise false.
     */
    public boolean contains(int index) {
        return mainOffset_ <= index && index < mainOffset_ + size();
    }

    /**
     * The method checks if it is possible to insert an element with the index <tt>index</tt> into the buffer.
     * <p>
     * Insertion is possible if the corresponding data page is loaded into the buffer.
     *
     * @param index global index of the element.
     * @return true if it is possible, otherwise false.
     */
    public boolean canAdd(int index) {
        return mainOffset_ <= index && index <= mainOffset_ + size();
    }

    /**
     * The method loads a range of elements into the buffer.
     *
     * @param fromIndex index of the first element to be loaded.
     * @param toIndex   index FOLLOWING the last element to be loaded.
     */
    public abstract void load(int fromIndex, int toIndex);

    /**
     * The method saves the modified data contained in the buffer to the main storage.
     */
    public abstract void commit();

    /**
     * Return a flag indicating that the elements in the buffer have changed since they were loaded from the main store.
     *
     * @return true if there are changes, otherwise false.
     */
    public boolean isModified() {
        return modified_;
    }

    /**
     * The method removes an element with the specified index from the buffer.
     *
     * @param index global index of the element.
     * @return removed element.
     */
    public E remove(int index) {
        E result = get(index);
        int relativeIndex = index - mainOffset_;
        store_.removeRange(relativeIndex, relativeIndex + 1);
        modified_ = true;
        return result;
    }

    /**
     * The method replaces a range of elements in the buffer with a new collection of elements.
     *
     * @param fromIndex index of the first element to be replaced.
     * @param toIndex   index FOLLOWING the last element to be replaced.
     * @param elements  collection of elements to replace.
     */
    public void replaceRange(int fromIndex, int toIndex, Collection<E> elements) {
        int relativeFromIndex = fromIndex - mainOffset_;
        int relativeToIndex = toIndex - mainOffset_;
        store_.replaceRange(relativeFromIndex, relativeToIndex, elements);
        modified_ = true;
    }

    /**
     * The method removes a range of elements from the buffer.
     *
     * @param fromIndex index of the first element to be removed.
     * @param toIndex   index FOLLOWING the last element to be removed.
     */
    public void removeRange(int fromIndex, int toIndex) {
        int relativeFromIndex = fromIndex - mainOffset_;
        int relativeToIndex = toIndex - mainOffset_;
        store_.removeRange(relativeFromIndex, relativeToIndex);
        modified_ = true;
    }

    /**
     * The method adds an element to the buffer at the specified index.
     *
     * @param index   global index of the element.
     * @param element element to be added.
     */
    public void add(int index, E element) {
        int relativeIndex = index - mainOffset_;
        store_.insert(relativeIndex, element);
        modified_ = true;
    }

    /**
     * The method returns an element from the buffer with the specified index.
     *
     * @param index global index of the element.
     * @return element.
     */
    public E get(int index) {
        int relativeIndex = index - mainOffset_;
        return store_.get(relativeIndex);
    }

    /**
     * The method collapses the buffer content to the specified boundaries.
     * <p>
     * Elements with an index less than <tt>fromIndex</tt> and greater than or equal to <tt>toIndex</tt> are thrown out of the buffer.
     * {@link #getOffset()} is set to <tt>fromIndex</tt>.
     *
     * @param fromIndex index of the first element.
     * @param toIndex   index FOLLOWING the last element.
     */
    public void collapse(int fromIndex, int toIndex) {
        if (modified_) {
            throw new ConcurrentModificationException("Can't collapse modified buffer.");
        }
        int relativeFromIndex = fromIndex - mainOffset_;
        int relativeToIndex = toIndex - mainOffset_;
        store_.removeRange(relativeToIndex, size());
        store_.removeRange(0, relativeFromIndex);
        mainOffset_ = fromIndex;
        mainCount_ = toIndex - fromIndex;
        modified_ = false;
    }

    /**
     * The method collapses the buffer content to the specified boundaries.
     * <p>
     * All elements are thrown out of the buffer. {@link #getOffset()} does not change.
     */
    public void collapse() {
        int pos = mainOffset_ + size();
        collapse(pos, pos);
    }

    /**
     * Clear the buffer.
     */
    public void clear() {
        store_.clear();
        mainOffset_ = -1;
        mainCount_ = 0;
        modified_ = false;
    }
}