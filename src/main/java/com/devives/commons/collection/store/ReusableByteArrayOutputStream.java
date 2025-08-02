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

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * The class implements an output stream that stores data in a byte array.
 * <p>
 * Differences from {@link java.io.ByteArrayOutputStream}:
 * <ul>
 * <li>The methods of the class are not synchronized.
 * <li>The reference to the buffer and the number of bytes written can be changed.
 * </ul>
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
final class ReusableByteArrayOutputStream extends OutputStream {

    private byte[] bytes_;
    private int size_;

    public ReusableByteArrayOutputStream(byte[] buf) {
        this.bytes_ = Objects.requireNonNull(buf, "buf");
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity - bytes_.length > 0)
            grow(minCapacity);
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private void grow(int minCapacity) {
        int oldCapacity = bytes_.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        bytes_ = Arrays.copyOf(bytes_, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }

    public void write(int b) {
        ensureCapacity(size_ + 1);
        bytes_[size_] = (byte) b;
        size_ += 1;
    }

    public void write(byte b[], int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
            ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(size_ + len);
        System.arraycopy(b, off, bytes_, size_, len);
        size_ += len;
    }

    public void reset() {
        size_ = 0;
    }

    public int size() {
        return size_;
    }

    public void setSize(int size) {
        this.size_ = size;
    }

    public byte[] getByteArray() {
        return this.bytes_;
    }

    public void setByteArray(byte[] buf) {
        this.bytes_ = Objects.requireNonNull(buf);
        this.size_ = 0;
    }

    public String toString() {
        return new String(bytes_, 0, size_);
    }


}
