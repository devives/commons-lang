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

import com.devives.commons.io.store.ChunkManager.Locator;
import com.devives.commons.lang.tuple.Tuple;
import com.devives.commons.lang.tuple.Tuple2;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

public final class ChunkedByteStore extends AbstractByteStore {

    private final ChunkManager<?> chunkManager_;
    private boolean TRACE = false;

    public <C extends Chunk> ChunkedByteStore(ChunkManager<C> chunkManager) {
        chunkManager_ = Objects.requireNonNull(chunkManager, "chunkManager");
    }

    @Override
    protected int internalRead(long fromPosition, ByteBuffer outByteBuffer) {
        int wasRead = 0;
        if (outByteBuffer.remaining() > 0) {
            Locator locator = chunkManager_.findChunkByPosition(fromPosition);
            if (locator != null) {
                long beginPosition = fromPosition - locator.getOffset();
                while (locator != null && outByteBuffer.remaining() > 0) {
                    wasRead += locator.getChunk().read(beginPosition, outByteBuffer);
                    if (outByteBuffer.remaining() > 0) {
                        locator = chunkManager_.findNextChunk(locator);
                        beginPosition = 0;
                    }
                }
            }
        }
        return wasRead;
    }

    @Override
    protected int internalWrite(long fromPosition, ByteBuffer inByteBuffer) {
        return doWriteAndReturnLocator(fromPosition, inByteBuffer)._1;
    }

    @Override
    protected int internalReplaceRange(long fromPosition, long toPosition, ByteBuffer inByteBuffer) {
        int written = 0;
        long removingCount = toPosition - fromPosition;
        long writingCount = inByteBuffer.remaining();
        long diffCount = writingCount - removingCount;
        if (fromPosition == 0 && toPosition == size()) {
            // Полная замена данных с возможным изменением размера.
            chunkManager_.clear();
            written = doWriteAndReturnLocator(fromPosition, inByteBuffer)._1;
        } else if (diffCount == 0) {
            // Замена данных произвольного диапазона без изменения размера.
            written = doWriteAndReturnLocator(fromPosition, inByteBuffer)._1;
        } else if (diffCount < 0) {
            // Запись в произвольное место с уменьшением размера.
            written = doWriteAndDecreaseSize(fromPosition, inByteBuffer, diffCount);
        } else if (toPosition == size()) {
            // Запись c произвольного места до конца с возможным увеличением размера.
            written = doWriteAndReturnLocator(fromPosition, inByteBuffer)._1;
        } else /*if (diffCount > 0)*/ {
            // Запись в произвольное место с увеличением размера.
            Tuple2<Integer, Locator> writtenAndLocator = doWriteAndReturnLocator(fromPosition, toPosition, inByteBuffer);
            written += writtenAndLocator._1;
            written += doInsert(toPosition, writtenAndLocator._2, inByteBuffer);
        }
        return written;
    }

    /**
     * Пишет данные в хранилище и возвращает количество записанных байт и локатор последнего чанка, в который была
     * произведена запись.
     *
     * @param fromPosition позиция начала записи в хранилище.
     * @param inByteBuffer буфер с данными для записи.
     * @return кортеж из количества записанных байт и последнего локатора, в который производилась запись.
     * @throws IndexOutOfBoundsException если позиция вне диапазона.
     */
    private Tuple2<Integer, Locator> doWriteAndReturnLocator(long fromPosition, ByteBuffer inByteBuffer) {
        if (TRACE) {
            System.out.println("doWriteAndReturnLocator(" + fromPosition + ", " + Arrays.toString(Arrays.copyOf(inByteBuffer.array(), inByteBuffer.limit())) + ")");
        }
        int written = 0;
        Locator locator = null;
        if (inByteBuffer.remaining() > 0) {
            locator = chunkManager_.getOrCreateChunkByPosition(fromPosition);
            boolean isLastChunk = locator.getIndex() == chunkManager_.getChunkCount() - 1;
            long beginPosition = fromPosition - locator.getOffset();
            while (inByteBuffer.remaining() > 0) {
                Chunk chunk = locator.getChunk();
                if (isLastChunk) {
                    written += chunk.write(beginPosition, inByteBuffer);
                } else {
                    int origLimit = inByteBuffer.limit();
                    // Число доступных байт в буфере меньше или равно размеру заменяемого диапазона.
                    long available = chunk.size() - beginPosition;
                    inByteBuffer.limit(Math.toIntExact(Math.min(inByteBuffer.position() + available, origLimit)));
                    try {
                        written += chunk.write(beginPosition, inByteBuffer);
                    } finally {
                        inByteBuffer.limit(origLimit);
                    }
                }
                if (inByteBuffer.remaining() > 0) {
                    locator = chunkManager_.getOrCreateNextChunk(locator);
                    isLastChunk = locator.getIndex() == chunkManager_.getChunkCount() - 1;
                    beginPosition = 0;
                }
            }
        }
        if (TRACE) {
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) locator.getChunk().size());
            locator.getChunk().read(0, byteBuffer);
            System.out.println("doWriteAndReturnLocator(" + fromPosition + ", " + Arrays.toString(Arrays.copyOf(byteBuffer.array(), byteBuffer.limit())));
        }
        return Tuple.of(written, locator);
    }

    /**
     * Пишет данные в хранилище и возвращает количество записанных байт и локатор последнего чанка, в который была
     * произведена запись.
     *
     * @param fromPosition позиция начала записи в хранилище.
     * @param inByteBuffer буфер с данными для записи.
     * @return кортеж из количества записанных байт и последнего локатора, в который производилась запись.
     * @throws IndexOutOfBoundsException если позиция вне диапазона.
     */
    private Tuple2<Integer, Locator> doWriteAndReturnLocator(long fromPosition, long toPosition, ByteBuffer inByteBuffer) {
        if (TRACE) {
            System.out.println("doWriteAndReturnLocator(" + fromPosition + ", " + toPosition + ", " + Arrays.toString(Arrays.copyOf(inByteBuffer.array(), inByteBuffer.limit())) + ")");
        }
        int written = 0;
        Locator locator = null;
        // Записываем столько байт из буфера, сколько помещается в заменяемый диапазон.
        if (toPosition - fromPosition > 0) {
            // Для этого, временно, изменяем лимит буфера.
            int origLimit = inByteBuffer.limit();
            // Число доступных байт в буфере меньше или равно размеру заменяемого диапазона.
            inByteBuffer.limit(Math.toIntExact((long) inByteBuffer.position() + (toPosition - fromPosition)));
            try {
                Tuple2<Integer, Locator> writtenAndLocator = doWriteAndReturnLocator(fromPosition, inByteBuffer);
                written = writtenAndLocator._1;
                locator = writtenAndLocator._2;
            } finally {
                inByteBuffer.limit(origLimit);
            }
        }
        if (TRACE) {
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) locator.getChunk().size());
            locator.getChunk().read(0, byteBuffer);
            System.out.println("doWriteAndReturnLocator(" + fromPosition + ", " + Arrays.toString(Arrays.copyOf(byteBuffer.array(), byteBuffer.limit())));
        }
        return Tuple.of(written, locator);
    }

    private int doWriteAndDecreaseSize(long fromPosition, ByteBuffer inByteBuffer, long diffCount) {
        if (TRACE) {
            System.out.println("doWriteAndDecreaseSize(" + fromPosition + ", " + Arrays.toString(Arrays.copyOf(inByteBuffer.array(), inByteBuffer.limit())) + ")");
        }
        Tuple2<Integer, Locator> writtenAndLocator = doWriteAndReturnLocator(fromPosition, inByteBuffer);
        int written = writtenAndLocator._1;
        Locator locator = writtenAndLocator._2;
        if (locator == null) {
            // Локатор может быть null, только если запись не произошла, например, при нулевом размере буфера.
            locator = chunkManager_.getChunkByPosition(fromPosition);
        }
        long clearRemain = -diffCount;
        while (clearRemain > 0) {
            Chunk chunk = locator.getChunk();
            long beginPosition = fromPosition + written - locator.getOffset();
            long endPosition = beginPosition + Math.min(chunk.size() - beginPosition, clearRemain);
            if (endPosition - beginPosition == chunk.size()) {
                locator = chunkManager_.removeChunk(locator);
                clearRemain -= (endPosition - beginPosition);
            } else {
                chunk.replaceRange(beginPosition, endPosition, EMPTY_BYTE_BUFFER);
                clearRemain -= (endPosition - beginPosition);
                if (clearRemain > 0) {
                    locator = chunkManager_.findNextChunk(locator);
                }
            }
            if (clearRemain == 0 || locator == null) {
                break;
            }
        }
        if (TRACE) {
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) locator.getChunk().size());
            locator.getChunk().read(0, byteBuffer);
            System.out.println("doWriteAndDecreaseSize(" + fromPosition + ", " + Arrays.toString(Arrays.copyOf(byteBuffer.array(), byteBuffer.limit())));
        }
        return written;
    }


    /**
     * Метод не использует {@link ByteBuffer#slice()}, что бы не создавать новых объектов и не нагружать GC.
     * @param fromPosition position of the first byte to write: [{@code 0}..{@link #size()}].
     * @param locator      locator of the chunk where data will be inserted first of all or <tt>null</tt>.
     * @param inByteBuffer buffer from which data will be read.
     * @return count of bytes written.
     */
    private int doInsert(long fromPosition, Locator locator, ByteBuffer inByteBuffer) {
        if (TRACE) {
            System.out.println("doInsert(" + fromPosition + ", " +
                    Arrays.toString(Arrays.copyOf(inByteBuffer.array(), inByteBuffer.limit())) + ")");
        }

        int written = 0;
        // Если в буфере остались байты, то необходимо вставить их в хранилище.
        if (inByteBuffer.remaining() > 0) {
            if (locator == null) {
                // Получаем локатор, если запись не произошла.
                locator = chunkManager_.getChunkByPosition(fromPosition);
            }
            // Определить число доступных байт для записи в Chunk
            Chunk chunk = locator.getChunk();
            long availableInChunk = chunkManager_.getChunkMaxCapacity() - chunk.size();

            if (inByteBuffer.remaining() <= availableInChunk) {
                // Если места достаточно, вставляем все данные.
                long beginPosition = fromPosition - locator.getOffset();
                written += chunk.replaceRange(beginPosition, beginPosition, inByteBuffer);
                if (TRACE) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate((int) locator.getChunk().size());
                    locator.getChunk().read(0, byteBuffer);
                    System.out.println("doWriteAndIncreaseSize(" + fromPosition + ", " + Arrays.toString(Arrays.copyOf(byteBuffer.array(), byteBuffer.limit())));
                }
                return written;
            }

            long beginPosition = fromPosition - locator.getOffset();
            // Если места недостаточно, сначала разделяем чанк
            locator = chunkManager_.splitChunk(locator, locator.getOffset() + beginPosition);
            chunk = locator.getChunk();
            availableInChunk = chunkManager_.getChunkMaxCapacity() - chunk.size();

            // Вычисляем размеры головы.
            int headAvailable = (int) Math.min(availableInChunk, inByteBuffer.remaining());
            // Записываем голову. Может случиться, что вернётся полный чанк.
            if (headAvailable > 0) {
                ByteBuffer headBuffer = inByteBuffer.slice();
                headBuffer.limit(headAvailable);
                written += chunk.write(chunk.size(), headBuffer);
                inByteBuffer.position(inByteBuffer.position() + headAvailable);
            }

            while (inByteBuffer.remaining() > 0) {
                // Мы получим созданный при разделении чанк.
                locator = chunkManager_.getOrCreateNextChunk(locator);
                chunk = locator.getChunk();
                long available = chunkManager_.getChunkMaxCapacity() - chunk.size();
                if (inByteBuffer.remaining() > available) {
                    locator = chunkManager_.insertPriorChunk(locator, chunkManager_.getChunkMaxCapacity());
                    chunk = locator.getChunk();
                    written += chunk.write(0, inByteBuffer);
                } else {
                    written += chunk.insert(0, inByteBuffer);
                }
            }

            if (TRACE) {
                ByteBuffer byteBuffer = ByteBuffer.allocate((int) locator.getChunk().size());
                locator.getChunk().read(0, byteBuffer);
                System.out.println("doWriteAndIncreaseSize(" + fromPosition + ", " + Arrays.toString(Arrays.copyOf(byteBuffer.array(), byteBuffer.limit())));
            }
        }
        return written;
    }

}
