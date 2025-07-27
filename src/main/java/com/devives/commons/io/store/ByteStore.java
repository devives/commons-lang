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

/**
 * The interface of random access byte storage.
 */
public interface ByteStore {

    /**
     * Reads a range of data starting from position <tt>fromPosition</tt> from the storage and writes it into <tt>byteBuffer</tt>.
     * Minimum length of read data: 0. Maximum length: {@link ByteBuffer#remaining()}.
     *
     * @param fromPosition position of the first byte to read: [{@code 0}..{@link #size()}].
     * @param byteBuffer   buffer where the data will be written.
     * @return number of bytes read.
     */
    int read(long fromPosition, ByteBuffer byteBuffer);

    /**
     * Writes a range of data from <tt>byteBuffer</tt> starting at position <tt>fromPosition</tt> with length {@link ByteBuffer#remaining()}.
     * Minimum length of written data: 0. Maximum length: {@link ByteBuffer#remaining()}.
     *
     * @param fromPosition position of the first byte to write: [{@code 0}..{@link #size()}].
     * @param byteBuffer   buffer from which data will be read.
     * @return number of bytes written.
     */
    int write(long fromPosition, ByteBuffer byteBuffer);

    /**
     * Inserts a range of data from <tt>byteBuffer</tt> starting at position <tt>fromPosition</tt> with length {@link ByteBuffer#remaining()}.
     * The data in the storage starting from position <tt>fromPosition</tt> will be shifted to the right.
     *
     * @param fromPosition position of the first byte to write: [{@code 0}..{@link #size()}].
     * @param byteBuffer   buffer from which data will be read.
     */
    default int insert(long fromPosition, ByteBuffer byteBuffer) {
        return replaceRange(fromPosition, fromPosition, byteBuffer);
    }

    /**
     * Replaces the data range <tt>[fromPosition..toPosition)</tt> with data from <tt>byteBuffer</tt> of length {@link ByteBuffer#remaining()}.
     *
     * @param fromPosition position of the first byte to write: [{@code 0}..{@link #size()}].
     * @param toPosition   position of the byte FOLLOWING the last byte to write: [fromPosition..{@link #size()}].
     * @param byteBuffer   buffer from which data will be read.
     * @return count of bytes written, which may be less than {@link ByteBuffer#remaining()} if the range is smaller than the buffer.
     */
    int replaceRange(long fromPosition, long toPosition, ByteBuffer byteBuffer);

    /**
     * Removes the data range <tt>[fromPosition..toPosition)</tt>.
     *
     * @param fromPosition position of the first byte to remove: [{@code 0}..{@link #size()}].
     * @param toPosition   position of the byte FOLLOWING the last byte to remove: [fromPosition..{@link #size()}].
     */
    void removeRange(long fromPosition, long toPosition);

    /**
     * Returns the current size of the stored data.
     *
     * @return size in bytes.
     */
    long size();
}