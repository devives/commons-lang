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
import java.util.Objects;
import java.util.function.Function;

/**
 * Byte storage based on a byte array.
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public final class ByteBufferStore extends AbstractByteStore {

    private final static int DEFAULT_CAPACITY = 16 * 1024;
    private Function<Integer, ByteBuffer> byteBufferFactory_;
    private ByteBuffer byteBuffer_;

    /**
     * Creates a byte storage based on an array of length {@link #DEFAULT_CAPACITY}.
     */
    public ByteBufferStore() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Creates a byte storage based on an array of the specified length.
     *
     * @param initialCapacity Buffer size. Optimal: 16..32Kb.
     */
    public ByteBufferStore(int initialCapacity) {
        this(ByteBuffer::allocate, ByteBuffer.allocate(initialCapacity), 0);
    }

    /**
     * Creates a byte storage based on byte buffer.
     *
     * @param byteBufferFactory byte buffer factory
     */
    public ByteBufferStore(Function<Integer, ByteBuffer> byteBufferFactory) {
        byteBufferFactory_ = Objects.requireNonNull(byteBufferFactory);
        byteBuffer_ = byteBufferFactory.apply(DEFAULT_CAPACITY);
        size_ = 0;
    }

    /**
     * Creates a byte storage based on byte buffer.
     *
     * @param byteBufferFactory byte buffer factory
     * @param initialCapacity   initial capacity of the byte buffer.
     */
    public ByteBufferStore(Function<Integer, ByteBuffer> byteBufferFactory, int initialCapacity) {
        byteBufferFactory_ = Objects.requireNonNull(byteBufferFactory);
        byteBuffer_ = byteBufferFactory.apply(initialCapacity);
        size_ = 0;
    }

    /**
     * Creates a byte storage based on the specified array.
     *
     * @param byteBufferFactory ByteBuffer factory. It will be use when expanding of the buffer is required.
     * @param byteBuffer        initial byte buffer.
     * @param size              initial data size in buffer.
     */
    public ByteBufferStore(Function<Integer, ByteBuffer> byteBufferFactory, ByteBuffer byteBuffer, int size) {
        byteBufferFactory_ = Objects.requireNonNull(byteBufferFactory);
        byteBuffer_ = Objects.requireNonNull(byteBuffer);
        size_ = size;
    }

    /**
     * Returns the size of the array.
     *
     * @return array size.
     */
    protected int capacity() {
        return byteBuffer_.capacity();
    }

    /**
     * Return the current byte buffer.
     *
     * @return byte array.
     */
    public byte[] getByteArray() {
        return byteBuffer_.array();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int internalRead(long fromPosition, ByteBuffer byteBuffer) {
        int intFromPosition = Math.toIntExact(fromPosition);
        int intSize = Math.toIntExact(size_);
        int count = Math.min(intSize - intFromPosition, byteBuffer.remaining());
        byteBuffer_.limit(intFromPosition + count);
        byteBuffer_.position(intFromPosition);
        byteBuffer.put(byteBuffer_);
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
        byteBuffer_.position(intFromPosition);
        byteBuffer_.limit(intFromPosition + count);
        try {
            byteBuffer_.put(byteBuffer);
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    private void internalTruncate(long newSize) {
        if (newSize * 2 <= byteBuffer_.capacity()) {
            byteBuffer_.position(0);
            byteBuffer_.limit( Math.toIntExact(newSize));
            byteBuffer_ = byteBuffer_.compact();
        }
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
        if (toPosition == size()) {
            writingCount = internalWrite(fromPosition, byteBuffer);
            internalTruncate(fromPosition + writingCount);
        } else if (intSize + diffCount <= capacity()) {
            // There is enough space for the remaining and new data.
            // Shift the existing data left/right depending on the sign of `diffCount`.
            ByteBuffer dupBB = byteBuffer_.duplicate();
            byteBuffer_.limit(intSize + diffCount);
            byteBuffer_.position(intFromPosition + writingCount);
            dupBB.limit(intSize);
            dupBB.position(intToPosition);
            byteBuffer_.put(dupBB);
            if (writingCount > 0) {
                internalWrite(fromPosition, byteBuffer);
            }
        } else {
            int newCapacity = calcNewCapacity(intSize + writingCount);
            ByteBuffer newBytes = ByteBuffer.allocate(newCapacity);
            if (fromPosition > 0) {
                byteBuffer_.limit(intFromPosition);
                byteBuffer_.position(0);
                newBytes.put(byteBuffer_);
            }
            if (writingCount > 0) {
                newBytes.put(byteBuffer);
            }
            byteBuffer_.limit(byteBuffer_.capacity());
            byteBuffer_.position(intToPosition);
            newBytes.put(byteBuffer_);
            byteBuffer_ = newBytes;
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
        if (minCapacity > byteBuffer_.capacity()) {
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
        //bytes_ = Arrays.copyOf(bytes_, newCapacity);
        ByteBuffer newBB = ByteBuffer.allocate(newCapacity);
        byteBuffer_.limit((int) size());
        byteBuffer_.position(0);
        newBB.put(byteBuffer_);
        byteBuffer_ = newBB;
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


