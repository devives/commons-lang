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

import com.devives.commons.io.store.AlignedByteStore;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * The class implements a buffered storage of objects in serialized form.
 *
 * При добавлении объекта в хранилище, он сразу же сериализуется в бинарную форму и помещается в буфер.
 *
 * @param <E> Type of elements.
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public final class BufferedSerializedStore<E> extends AbstractBufferedStore<E> implements Serialized {
    /**
     * Main element storage.
     */
    private final AlignedByteStore mainAlignedByteStore_;

    /**
     * Creates an instance of a buffered store of serialized items.
     *
     * @param bufferSerializedStore the store of the items buffer.
     * @param mainAlignedByteStore  the main store of items.
     */
    public BufferedSerializedStore(AlignedByteStore mainAlignedByteStore, SerializedStore<E> bufferSerializedStore) {
        super(new Buffer(bufferSerializedStore, mainAlignedByteStore));
        mainAlignedByteStore_ = Objects.requireNonNull(mainAlignedByteStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return (int) mainAlignedByteStore_.size() + (buffer_.size() - buffer_.getLoadedCount());
    }

    /**
     * The class implements a buffer for storing elements.
     *
     * @param <E> type of element.
     */
    private final static class Buffer<E> extends AbstractBuffer<E> {

        private final SerializedStore<E> bufferStore_;
        private final AlignedByteStore mainAlignedByteStore_;

        public Buffer(SerializedStore<E> bufferStore, AlignedByteStore mainAlignedByteStore) {
            super(bufferStore);
            bufferStore_ = Objects.requireNonNull(bufferStore, "bufferStore");
            mainAlignedByteStore_ = Objects.requireNonNull(mainAlignedByteStore, "mainAlignedByteStore");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void load(int fromIndex, int toIndex) {
            int requiredSize = (toIndex - fromIndex) * mainAlignedByteStore_.getElementSize();
            ByteBuffer byteBuffer = ByteBuffer.allocate(requiredSize);
            int count = mainAlignedByteStore_.read(fromIndex, byteBuffer);
            byteBuffer.limit(count * mainAlignedByteStore_.getElementSize());
            byteBuffer.position(0);
            bufferStore_.getAlignedByteStore().replaceRange(0, size(), byteBuffer);
            mainCount_ = count;
            mainOffset_ = fromIndex;
            modified_ = false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void commit() {
            if (modified_) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(bufferStore_.getByteArray());
                byteBuffer.limit(bufferStore_.size() * bufferStore_.getElementSize());
                mainAlignedByteStore_.replaceRange(mainOffset_, mainOffset_ + mainCount_, byteBuffer);
                mainCount_ = bufferStore_.size();
                modified_ = false;
            }
        }

    }

}
