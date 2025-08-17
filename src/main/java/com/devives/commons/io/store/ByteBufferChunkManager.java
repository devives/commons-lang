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
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Реализация менеджера чанков бинарных данных, хранимых в {@link ByteBuffer}.
 *
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public final class ByteBufferChunkManager extends AbstractChunkManager<ByteBufferChunkManager.ByteBufferChunk, ByteBufferChunkManager.ByteBufferChunkDescriptor> {

    private final Function<Integer, ByteBuffer> byteBufferFactory_;

    public ByteBufferChunkManager(int chunkMaxCapacity, Function<Integer, ByteBuffer> byteBufferFactory) {
        super(chunkMaxCapacity);
        byteBufferFactory_ = Objects.requireNonNull(byteBufferFactory, "byteBufferFactory");
    }

    public ByteBufferChunkManager(int chunkMaxCapacity, List<ByteBufferChunkDescriptor> chunkDescList, Function<Integer, ByteBuffer> byteBufferFactory) {
        super(chunkMaxCapacity, chunkDescList);
        byteBufferFactory_ = Objects.requireNonNull(byteBufferFactory, "byteBufferFactory");
    }

    @Override
    protected ByteBufferChunkDescriptor newChunkDescriptor(int capacity) {
        return new ByteBufferChunkDescriptor(capacity, byteBufferFactory_);
    }

    public final class ByteBufferChunkDescriptor extends AbstractChunkManager.AbstractChunkDescriptor<ByteBufferChunk> {

        private final ByteBufferChunk chunk_;

        public ByteBufferChunkDescriptor(int chunkCapacity, Function<Integer, ByteBuffer> byteBufferFactory) {
            chunk_ = new ByteBufferChunk(this, chunkCapacity, byteBufferFactory);
        }

        public ByteBufferChunk getChunk() {
            return chunk_;
        }

        @Override
        public long getSize() {
            return chunk_.size();
        }
    }


    public final class ByteBufferChunk extends AbstractChunkManager.AbstractChunk {

        private final ByteBufferStore byteStore_;

        public ByteBufferChunk(ByteBufferChunkDescriptor descriptor, int initialCapacity, Function<Integer, ByteBuffer> byteBufferFactory) {
            super(descriptor);
            byteStore_ = new ByteBufferStore(byteBufferFactory, initialCapacity);
        }

        @Override
        protected ByteStore getByteStore() {
            return byteStore_;
        }

        @Override
        protected void flushByteStore(ByteStore byteStore) {
            // Do nothing.
        }

        @Override
        public long size() {
            return byteStore_.size();
        }

    }

}
