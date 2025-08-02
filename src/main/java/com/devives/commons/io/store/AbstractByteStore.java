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

import com.devives.commons.lang.ExceptionUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * An abstract implementation of random-access byte storage.
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public abstract class AbstractByteStore implements ByteStore {

    protected final static ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);
    protected long size_ = 0;

    protected AbstractByteStore() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long size() {
        return size_;
    }

    /**
     * Reads a range of bytes from storage.
     *
     * @param fromPosition the first position of the data to read.
     * @param toPosition the NEXT position of the data to read.
     * @return an array of bytes read.
     */
    public final byte[] readRange(long fromPosition, long toPosition) {
        validateFromToPosition(fromPosition, toPosition);
        validateFromPosition(fromPosition);
        validateToPosition(toPosition);
        int expectedCount = Math.toIntExact(toPosition - fromPosition);
        byte[] bytes = new byte[expectedCount];
        int actualCount = internalRead(fromPosition, ByteBuffer.wrap(bytes));
        if (actualCount != expectedCount) {
            throw ExceptionUtils.asUnchecked(new IOException("Expected count: " + expectedCount + ". Read count: " + actualCount + " ."));
        }
        return bytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int read(long fromPosition, ByteBuffer byteBuffer) {
        validateFromPosition(fromPosition);
        return internalRead(fromPosition, byteBuffer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int write(long fromPosition, ByteBuffer byteBuffer) {
        validateFromPosition(fromPosition);
        int written = internalWrite(fromPosition, byteBuffer);
        size_ = Math.max(size_, fromPosition + written);
        assert size_ >= 0 : "Size cannot be negative: " + size_;
        return written;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeRange(long fromPosition, long toPosition) {
        replaceRange(fromPosition, toPosition, EMPTY_BYTE_BUFFER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int replaceRange(long fromPosition, long toPosition, ByteBuffer byteBuffer) {
        validateFromToPosition(fromPosition, toPosition);
        validateFromPosition(fromPosition);
        validateToPosition(toPosition);
        long removingCount = toPosition - fromPosition;
        long addingCount = byteBuffer.remaining();
        int written = internalReplaceRange(fromPosition, toPosition, byteBuffer);
        size_ += (addingCount - removingCount);
        assert size_ >= 0 : "Size cannot be negative: " + size_;
        return written;
    }

    /**
     * The method reads data from the storage into the buffer <tt>byteBuffer</tt> starting from the position <tt>fromPosition</tt>.
     * <p>
     * The maximum size of the read data is equal to {@link ByteBuffer#remaining()}.<br>
     *
     * @param fromPosition the starting position of the write.
     * @param byteBuffer   the buffer.
     * @return the number of bytes written.
     */
    protected abstract int internalRead(long fromPosition, ByteBuffer byteBuffer);

    /**
     * The method writes data to the storage starting from the position <tt>fromPosition</tt>.
     * <p>
     * The maximum size of the data to be written is equal to {@link ByteBuffer#remaining()}.<br>
     * If there is data in the storage starting from <tt>fromPosition</tt>, it will be overwritten.<br>
     * If the end position of the write, equal to {@code fromPosition + ByteBuffer#remaining()}, is greater than the current {@link #size()},
     * the size of the storage will be increased.
     *
     * @param fromPosition the starting position of the write.
     * @param byteBuffer   the buffer with the data.
     * @return the number of bytes written.
     */
    protected abstract int internalWrite(long fromPosition, ByteBuffer byteBuffer);

    /**
     * The method is called when a range of bytes is replacing.
     *
     * @param fromPosition the index of the first byte of the data to be replaced.
     * @param toPosition   the index of the byte FOLLOWING the last byte of the data to be written.
     * @param byteBuffer   the buffer with the data to be written.
     * @return the number of bytes written.
     */
    protected abstract int internalReplaceRange(long fromPosition, long toPosition, ByteBuffer byteBuffer);

    protected final void validateFromToPosition(long fromPosition, long toPosition) {
        if (fromPosition > toPosition) {
            throw new IllegalArgumentException("fromPosition(" + fromPosition +
                    ") > toIndex(" + toPosition + ")");
        }
    }

    protected final void validateFromPosition(long fromPosition) {
        if ((fromPosition < 0) || (fromPosition > size())) {
            throw new IndexOutOfBoundsException("From position: " + fromPosition + ". Size: " + size() + ".");
        }
    }

    protected final void validateToPosition(long toPosition) {
        if ((toPosition < 0) || (toPosition > size())) {
            throw new IndexOutOfBoundsException("To position: " + toPosition + ". Size: " + size() + ".");
        }
    }

}
