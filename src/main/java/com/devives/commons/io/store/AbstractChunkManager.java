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
import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактная реализация менеджера чанков бинарных данных.
 * <p>
 *
 * @param <C> тип чанков
 * @param <D> тип описателей чанков
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public abstract class AbstractChunkManager<
        C extends AbstractChunkManager.AbstractChunk,
        D extends AbstractChunkManager.AbstractChunkDescriptor>
        implements ChunkManager<C> {

    private final List<D> chunkDescList_ = new ArrayList<>();
    protected final int chunkMaxCapacity_;
    protected long version_ = 0;

    /**
     * Создаёт экземпляр менеджера чанков.
     *
     * @param chunkMaxCapacity capacity, in bytes.
     */
    protected AbstractChunkManager(int chunkMaxCapacity) {
        if (chunkMaxCapacity <= 0) {
            throw new IllegalArgumentException("Chunk max capacity must be greater than zero.");
        }
        this.chunkMaxCapacity_ = chunkMaxCapacity;
    }

    @Override
    public int getChunkCount() {
        return chunkDescList_.size();
    }

    @Override
    public int getChunkMaxCapacity() {
        return chunkMaxCapacity_;
    }

    @Override
    public Locator<C> getChunkByPosition(long position) {
        if (position < 0) {
            throw new IndexOutOfBoundsException("position: " + position);
        }
        long offset = 0;
        int index = -1;
        for (D desc : chunkDescList_) {
            index++;
            if (offset <= position && position <= offset + desc.getSize() - 1) {
                return newChunkLocator(version_, index, offset);
            }
            offset += desc.getSize();
        }
        throw new IndexOutOfBoundsException("position: " + position + " size: " + offset);
    }

    @Override
    public Locator<C> findChunkByPosition(long position) {
        if (position < 0) {
            throw new IndexOutOfBoundsException("position: " + position);
        }
        long offset = 0;
        int index = -1;
        for (D desc : chunkDescList_) {
            index++;
            if (offset <= position && position <= offset + desc.getSize() - 1) {
                return newChunkLocator(version_, index, offset);
            }
            offset += desc.getSize();
        }
        if (position > offset) {
            throw new IndexOutOfBoundsException("position: " + position + " size: " + offset);
        }
        return null;
    }

    @Override
    public Locator<C> getOrCreateChunkByPosition(long position) {
        if (position < 0) {
            throw new IndexOutOfBoundsException("position: " + position);
        }
        long offset = 0;
        int index = -1;
        for (D desc : chunkDescList_) {
            index++;
            long size = (chunkDescList_.size() == index + 1) ? chunkMaxCapacity_ : desc.getSize();
            if (offset <= position && position <= offset + size - 1) {
                return newChunkLocator(version_, index, offset);
            }
            offset += desc.getSize();
        }
        if (position > offset) {
            throw new IndexOutOfBoundsException("position: " + position + " size: " + offset);
        }
        index++;
        addChunkDescriptor(newChunkDescriptor(chunkMaxCapacity_));
        return newChunkLocator(version_, index, offset);
    }

    protected abstract D newChunkDescriptor(int capacity);

    protected Locator<C> newChunkLocator(long version, int index, long offset) {
        return new ChunkLocator(version, index, offset);
    }

    protected final void addChunkDescriptor(D desc) {
        addChunkDescriptor(chunkDescList_.size(), desc);
    }

    protected final void addChunkDescriptor(int index, D desc) {
        try {
            onAddChunkDescriptor(index, desc);
            chunkDescList_.add(index, desc);
        } finally {
            version_++;
        }
    }

    protected void onAddChunkDescriptor(int index, D desc) {

    }

    protected final D removeChunkDescriptor(int index) {
        try {
            D desc = chunkDescList_.remove(index);
            onRemoveChunkDescriptor(index, desc);
            return desc;
        } finally {
            version_++;
        }
    }

    protected void onRemoveChunkDescriptor(int index, D desc) {
        
    }

    @Override
    public Locator<C> findNextChunk(Locator<C> locator) {
        int index = locator.getIndex() + 1;
        Locator<C> resultLocator = null;
        if (index > 0 && index < chunkDescList_.size()) {
            long offset = locator.getOffset() + locator.getChunk().size();
            resultLocator = newChunkLocator(version_, index, offset);
        }
        return resultLocator;
    }

    @Override
    public Locator<C> getOrCreateNextChunk(Locator<C> locator) {
        int index = locator.getIndex() + 1;
        long offset = locator.getOffset() + locator.getChunk().size();
        if (index >= chunkDescList_.size()) {
            addChunkDescriptor(newChunkDescriptor(chunkMaxCapacity_));
        }
        return newChunkLocator(version_, index, offset);
    }

    @Override
    public Locator<C> insertPriorChunk(Locator<C> locator, int requiredCapacity) {
        int index = locator.getIndex();
        long offset = locator.getOffset();
        addChunkDescriptor(index, newChunkDescriptor(Math.min(requiredCapacity, chunkMaxCapacity_)));
        return newChunkLocator(version_, index, offset);
    }

    @Override
    public Locator<C> insertNextChunk(Locator<C> locator, int requiredCapacity) {
        int index = locator.getIndex() + 1;
        long offset = locator.getOffset() + locator.getChunk().size();
        addChunkDescriptor(index, newChunkDescriptor(Math.min(requiredCapacity, chunkMaxCapacity_)));
        return newChunkLocator(version_, index, offset);
    }

    @Override
    public Locator<C> splitChunk(Locator<C> locator, long position) {
        Chunk chunk = locator.getChunk();
        int locatorIndex = locator.getIndex();
        long locatorOffset = locator.getOffset();
        if (position == locatorOffset) {
            // Если позиция совпадает с началом чанка, добавляем новый чанк перед текущим.
            int newIndex = locatorIndex;
            D newChunkDescriptor = newChunkDescriptor(chunkMaxCapacity_);
            addChunkDescriptor(newIndex, newChunkDescriptor);
        } else if (position == locatorOffset + chunk.size()) {
            // Если позиция совпадает с концом чанка, добавляем новый чанк за текущим.
            int newIndex = locatorIndex + 1;
            D newChunkDescriptor = newChunkDescriptor(chunkMaxCapacity_);
            addChunkDescriptor(newIndex, newChunkDescriptor);
        } else {
            int moveSize = (int) (chunk.size() - position + locatorOffset);
            int nextIndex = locatorIndex + 1;
            addChunkDescriptor(nextIndex, newChunkDescriptor(chunkMaxCapacity_));
            Locator nextLocator = newChunkLocator(version_, nextIndex, position);
            Chunk nextChunk = nextLocator.getChunk();
            java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocate(Math.min(moveSize, 64 * 1024));
            int written = 0;
            while (moveSize > 0) {
                int readSize = chunk.read(position - locatorOffset + written, byteBuffer);
                if (readSize <= 0) {
                    break;
                }
                byteBuffer.flip();
                written += nextChunk.write(written, byteBuffer);
                moveSize -= readSize;
                byteBuffer.clear();
            }
            chunk.replaceRange(position - locatorOffset, chunk.size(), java.nio.ByteBuffer.allocate(0));
        }
        return newChunkLocator(version_, locatorIndex, locatorOffset);
    }

    @Override
    public Locator<C> removeChunk(Locator<C> locator) {
        int index = locator.getIndex();
        long offset = locator.getOffset();
        removeChunkDescriptor(index);
        if (chunkDescList_.isEmpty()) {
            return null;
        } else {
            return newChunkLocator(version_, index, offset);
        }
    }

    @Override
    public void clear() {
        while (!chunkDescList_.isEmpty()) {
            int index = chunkDescList_.size() - 1;
            removeChunkDescriptor(index);
        }
        version_ = 0;
    }

    protected class ChunkLocator implements Locator<C> {
        private final long version_;
        private final long offset_;
        private final int index_;

        public ChunkLocator(long version, int index, long offset) {
            version_ = version;
            offset_ = offset;
            index_ = index;
        }

        @Override
        public C getChunk() {
            validateVersion();
            return (C) chunkDescList_.get(index_).getChunk();
        }

        public int getIndex() {
            validateVersion();
            return index_;
        }

        public long getOffset() {
            validateVersion();
            return offset_;
        }

        private void validateVersion() {
            if (version_ != this.version_) {
                throw new IllegalStateException("Chunk version mismatch: expected " + version_ + ", but was " + this.version_);
            }
        }
    }

    public abstract static class AbstractChunkDescriptor<C extends Chunk> implements ChunkDescriptor<C> {
    }

    public abstract class AbstractChunk implements Chunk {

        private final ChunkDescriptor descriptor_;

        public AbstractChunk(ChunkDescriptor descriptor) {
            descriptor_ = descriptor;
        }

        protected ChunkDescriptor getDescriptor() {
            return descriptor_;
        }

        protected abstract ByteStore getByteStore();

        protected abstract void flushByteStore(ByteStore byteStore);

        @Override
        public int read(long fromPosition, ByteBuffer byteBuffer) {
            return getByteStore().read(fromPosition, byteBuffer);
        }

        @Override
        public int write(long fromPosition, ByteBuffer byteBuffer) {
            int written = 0;
            int intFromPosition = Math.toIntExact(fromPosition);
            if (chunkMaxCapacity_ - intFromPosition < byteBuffer.remaining()) {
                int origLimit = byteBuffer.limit();
                try {
                    // Мы уже точно знаем, что в буфере данных больше, чем вмещается в заменяемый диапазон.
                    byteBuffer.limit(byteBuffer.position() + (chunkMaxCapacity_ - intFromPosition));
                    ByteStore byteStore = getByteStore();
                    written = byteStore.write(fromPosition, byteBuffer);
                    flushByteStore(byteStore);
                } finally {
                    byteBuffer.limit(origLimit);
                }
            } else {
                ByteStore byteStore = getByteStore();
                written = byteStore.write(fromPosition, byteBuffer);
                flushByteStore(byteStore);
            }
            return written;
        }

        @Override
        public int replaceRange(long fromPosition, long toPosition, ByteBuffer byteBuffer) {
            int written = 0;
            int intFromPosition = Math.toIntExact(fromPosition);
            if (chunkMaxCapacity_ - intFromPosition < byteBuffer.remaining()) {
                int origLimit = byteBuffer.limit();
                try {
                    // Мы уже точно знаем, что в буфере данных больше, чем вмещается в заменяемый диапазон.
                    byteBuffer.limit(byteBuffer.position() + (chunkMaxCapacity_ - intFromPosition));
                    ByteStore byteStore = getByteStore();
                    written = byteStore.replaceRange(fromPosition, toPosition, byteBuffer);
                    flushByteStore(byteStore);
                } finally {
                    byteBuffer.limit(origLimit);
                }
            } else {
                ByteStore byteStore = getByteStore();
                written = byteStore.replaceRange(fromPosition, toPosition, byteBuffer);
                flushByteStore(byteStore);
            }
            return written;
        }

        @Override
        public void removeRange(long fromPosition, long toPosition) {
            ByteStore byteStore = getByteStore();
            byteStore.removeRange(fromPosition, toPosition);
            flushByteStore(byteStore);
        }

        @Override
        public long size() {
            return descriptor_.getSize();
        }
    }

}

