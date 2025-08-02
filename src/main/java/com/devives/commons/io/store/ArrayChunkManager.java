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

/**
 * Менеджер чанков, хранящих данные в массивах байт.
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public final class ArrayChunkManager extends AbstractChunkManager<ArrayChunkManager.ArrayChunk, ArrayChunkManager.ArrayChunkDescriptor> {

    /**
     * Создаёт экземпляр менеджера чанков.
     *
     * @param chunkMaxCapacity capacity, in bytes.
     */
    public ArrayChunkManager(int chunkMaxCapacity) {
        super(chunkMaxCapacity);
    }

    @Override
    protected ArrayChunkDescriptor newChunkDescriptor(int capacity) {
        return new ArrayChunkDescriptor(capacity);
    }

    public final class ArrayChunkDescriptor extends AbstractChunkManager.AbstractChunkDescriptor {

        private final ArrayChunk chunk_;

        public ArrayChunkDescriptor(int chunkCapacity) {
            chunk_ = new ArrayChunk(this, chunkCapacity);
        }

        public ArrayChunk getChunk() {
            return chunk_;
        }

        @Override
        public long getSize() {
            return chunk_.size();
        }
    }


    public final class ArrayChunk extends AbstractChunkManager.AbstractChunk {

        private final ArrayByteStore byteStore_;

        public ArrayChunk(ChunkDescriptor descriptor, int initialCapacity) {
            super(descriptor);
            byteStore_ = new ArrayByteStore(initialCapacity);
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
