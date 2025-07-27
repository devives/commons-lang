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
package com.devives.commons.io.store;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * Byte storage based on a byte array.
 */
public final class ArrayByteStore extends AbstractByteStore {

    private final static int DEFAULT_CAPACITY = 16 * 1024;
    private byte[] bytes_;

    /**
     * Creates a byte storage based on an array of length {@link #DEFAULT_CAPACITY}.
     */
    public ArrayByteStore() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Creates a byte storage based on an array of the specified length.
     *
     * @param initialCapacity Buffer size. Optimal: 16..32Kb.
     */
    public ArrayByteStore(int initialCapacity) {
        this(new byte[initialCapacity]);
    }

    /**
     * Creates a byte storage based on the specified array.
     *
     * @param bytes byte array
     */
    protected ArrayByteStore(byte[] bytes) {
        bytes_ = Objects.requireNonNull(bytes);
    }

    /**
     * Returns the size of the array.
     *
     * @return array size.
     */
    protected int capacity() {
        return bytes_.length;
    }

    /**
     * Sets the size of the array.
     *
     * @param capacity new array size.
     * @throws IllegalArgumentException if the specified size is negative.
     */
    protected void setCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative: " + capacity);
        }
        if (capacity != bytes_.length) {
            bytes_ = Arrays.copyOf(bytes_, capacity);
        }
    }

    /**
     * Return the current byte buffer.
     *
     * @return byte array.
     */
    public byte[] getByteArray() {
        return bytes_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int internalRead(long fromPosition, ByteBuffer byteBuffer) {
        int intFromPosition = Math.toIntExact(fromPosition);
        int intSize = Math.toIntExact(size_);
        int count = Math.min(intSize - intFromPosition, byteBuffer.remaining());
        if (count <= 8) {
            byteBuffer.put(bytes_, intFromPosition, count);
        } else {
            int pos = byteBuffer.position();
            System.arraycopy(bytes_, intFromPosition, byteBuffer.array(), pos, count);
            byteBuffer.position(pos + count);
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int internalWrite(long fromPosition, ByteBuffer byteBuffer) {
        int intFromPosition = Math.toIntExact(fromPosition);
        int count = byteBuffer.remaining();
        ensureExplicitCapacity(intFromPosition + count);
        if (count <= 8) {
            byteBuffer.get(bytes_, intFromPosition, byteBuffer.remaining());
        } else {
            int pos = byteBuffer.position();
            System.arraycopy(byteBuffer.array(), pos, bytes_, intFromPosition, count);
            byteBuffer.position(pos + count);
        }
        return count;
    }

    private void internalTruncate(long newSize) {
        //bytes_ = Arrays.copyOf(bytes_, (int) newSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int internalReplaceRange(long fromPosition, long toPosition, ByteBuffer byteBuffer) {
        int intFromPosition = Math.toIntExact(fromPosition);
        int intToPosition = Math.toIntExact(toPosition);
        int intSize = Math.toIntExact(size_);
        int removingCount = intToPosition - intFromPosition;
        int writingCount = byteBuffer.remaining();
        int diffCount = writingCount - removingCount;
        if (intToPosition == intSize) {
            // Замена в конце с уменьшением длины.
            writingCount = internalWrite(fromPosition, byteBuffer);
            internalTruncate(fromPosition + writingCount);
        } else if (intSize + diffCount <= capacity()) {
            // There is enough space for the remaining and new data.
            // Shift the existing data left/right depending on the sign of `diffCount`.
            System.arraycopy(bytes_, intToPosition, bytes_, intFromPosition + writingCount, intSize - intToPosition);
            if (writingCount > 0) {
                internalWrite(fromPosition, byteBuffer);
            }
        } else {
            byte[] newBytes = new byte[calcNewCapacity(intSize + writingCount)];
            if (fromPosition > 0) {
                System.arraycopy(bytes_, 0, newBytes, 0, intFromPosition);
            }
            if (writingCount > 8) {
                System.arraycopy(byteBuffer.array(), byteBuffer.position(), newBytes, intFromPosition, writingCount);
                byteBuffer.position(byteBuffer.limit());
            } else if (writingCount > 0) {
                byteBuffer.get(newBytes, intFromPosition, byteBuffer.remaining());
            }
            System.arraycopy(bytes_, intToPosition, newBytes, intFromPosition + writingCount, intSize - intToPosition);
            bytes_ = newBytes;
        }
        return writingCount;
    }

    /**
     * The method makes sure that the array length is not less than the specified size. If the array length is less
     * than the specified size, increases the array length.
     *
     * @param minCapacity minimum required array size.
     */
    private void ensureExplicitCapacity(int minCapacity) {
        if (minCapacity > bytes_.length) {
            grow(minCapacity);
        }
    }

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void grow(int minCapacity) {
        int newCapacity = calcNewCapacity(minCapacity);
        bytes_ = Arrays.copyOf(bytes_, newCapacity);
    }

    private int calcNewCapacity(int minCapacity) {
        int oldCapacity = capacity();
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        return newCapacity;
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

}


