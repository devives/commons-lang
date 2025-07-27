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

/**
 * Aligned byte storage.
 * <p>
 * Each stored element has a fixed length. This allows you to unambiguously calculate the position of the beginning
 * of the element in the storage.
 * <p>
 * The class does not implement the storage functionality, but only imposes additional restrictions on the sizes
 * of the read and written data buffers.
 */
public final class AlignedByteStore implements ByteStore {

    private final ByteStore byteStore_;
    private final int elementSize_;

    /**
     * Creates an aligned byte store based on the specified random-access byte store.
     *
     * @param byteStore   byte storage.
     * @param elementSize element size.
     */
    public AlignedByteStore(ByteStore byteStore, int elementSize) {
        byteStore_ = Objects.requireNonNull(byteStore, "byteStore");
        if (elementSize < 1) {
            throw new IllegalArgumentException("The element size: " + elementSize + " < 1");
        }
        elementSize_ = elementSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(long fromIndex, ByteBuffer byteBuffer) {
        validateByteBufferRemaining(byteBuffer);
        long fromPosition = fromIndex * elementSize_;
        int nByte = byteStore_.read(fromPosition, byteBuffer);
        return nByte / elementSize_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(long fromIndex, ByteBuffer byteBuffer) {
        validateByteBufferRemaining(byteBuffer);
        long fromPosition = fromIndex * elementSize_;
        int nByte = byteStore_.read(fromPosition, byteBuffer);
        return nByte / elementSize_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int replaceRange(long fromIndex, long toIndex, ByteBuffer byteBuffer) {
        validateByteBufferRemaining(byteBuffer);
        long fromPosition = fromIndex * elementSize_;
        long toPosition = toIndex * elementSize_;
        return byteStore_.replaceRange(fromPosition, toPosition, byteBuffer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRange(long fromIndex, long toIndex) {
        long fromPosition = fromIndex * elementSize_;
        long toPosition = toIndex * elementSize_;
        byteStore_.removeRange(fromPosition, toPosition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long size() {
        return byteStore_.size() / elementSize_;
    }

    private void validateByteBufferRemaining(ByteBuffer byteBuffer) {
        long r = byteBuffer.remaining();
        long n = r % elementSize_;
        if (n != 0) {
            throw new IllegalArgumentException("The value `ByteBuffer.remaining()` is not a multiple of element size.");
        }
    }

    /**
     * Return the backing byte store.
     *
     * @return backing byte store.
     */
    public ByteStore getByteStore() {
        return byteStore_;
    }

    /**
     * Returns the size of the storage element.
     *
     * @return size in bytes.
     */
    public int getElementSize() {
        return elementSize_;
    }
}

