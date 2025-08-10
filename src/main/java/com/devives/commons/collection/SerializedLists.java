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
package com.devives.commons.collection;

import com.devives.commons.collection.store.BufferedSerializedStore;
import com.devives.commons.collection.store.BufferedStoreAsListAdapter;
import com.devives.commons.collection.store.SerializedStore;
import com.devives.commons.collection.store.StoreAsListAdapter;
import com.devives.commons.collection.store.serializer.*;
import com.devives.commons.io.store.*;
import com.devives.commons.lang.ExceptionUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Точка входа для создания списка элементов, сериализованных в чанки.
 * <p>
 * Предоставляет методы для создания списков с различными типами данных и конфигурациями.
 * <pre>{@code
 * List<Long> list = SerializedChunkedLists.ofLongs().setOffHeapChunkManager(256 * 1024).setBuffered().build();
 * }</pre>
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public final class SerializedLists {

    private SerializedLists() {

    }

    public static Builder<Byte> ofBytes() {
        return new Builder<>(new ByteBinarySerializer());
    }

    public static Builder<Short> ofShorts() {
        return new Builder<>(new ShortBinarySerializer());
    }

    public static Builder<Integer> ofIntegers() {
        return new Builder<>(new IntegerBinarySerializer());
    }

    public static Builder<Long> ofLongs() {
        return new Builder<>(new LongBinarySerializer());
    }

    public static Builder<Float> ofFloats() {
        return new Builder<>(new FloatBinarySerializer());
    }

    public static Builder<Double> ofDoubles() {
        return new Builder<>(new DoubleBinarySerializer());
    }

    public static <E> Builder<E> of(BinarySerializer<E> binarySerializer) {
        return new Builder<E>(binarySerializer);
    }

    /**
     * Строитель списка разбитого на чанки.
     *
     * @param <E> тип элементов.
     */
    public static final class Builder<E> extends AbstractBuilder<E, Builder<E>> {

        /**
         * Конструктор строителя.
         *
         * @param binarySerializer сериализатор элементов.
         */
        private Builder(BinarySerializer<E> binarySerializer) {
            super(binarySerializer);
        }

        /**
         * @return текущий экземпляр строителя.
         */
        public RamBuilder<E> setHeapByteStore() {
            return new RamBuilder<>(
                    binarySerializer_,
                    new ByteBufferStore(ByteBuffer::allocate));
        }

        /**
         * @return текущий экземпляр строителя.
         */
        public RamBuilder<E> setHeapByteStore(int initialCapacity) {
            return new RamBuilder<>(
                    binarySerializer_,
                    new ByteBufferStore(ByteBuffer::allocate, initialCapacity));
        }

        /**
         * @return текущий экземпляр строителя.
         */
        public RamBuilder<E> setOffHeapByteStore() {
            return new RamBuilder<>(
                    binarySerializer_,
                    new ByteBufferStore(ByteBuffer::allocateDirect));
        }

        /**
         * @return текущий экземпляр строителя.
         */
        public RamBuilder<E> setOffHeapByteStore(int initialCapacity) {
            return new RamBuilder<>(
                    binarySerializer_,
                    new ByteBufferStore(ByteBuffer::allocateDirect, initialCapacity));
        }

        private void validateChunkMaxCapacity(int value) {
            if (value % binarySerializer_.getElementSize() != 0) {
                throw new IllegalArgumentException(
                        String.format("Illegal ChunkMaxCapacity value '%s'. The value must be a multiple of the element size '%s'.",
                                value,
                                binarySerializer_.getElementSize()
                        ));
            }
        }

        /**
         * @return текущий экземпляр строителя.
         */
        public RamBuilder<E> setArrayChunkedByteStore(int chunkMaxCapacity) {
            validateChunkMaxCapacity(chunkMaxCapacity);
            return setChunkedByteStore(
                    new ChunkedByteStore(
                            new ArrayChunkManager(chunkMaxCapacity)));
        }

        /**
         * @return текущий экземпляр строителя.
         */
        public RamBuilder<E> setHeapChunkedByteStore(int chunkMaxCapacity) {
            validateChunkMaxCapacity(chunkMaxCapacity);
            return setChunkedByteStore(
                    new ChunkedByteStore(
                            new ByteBufferChunkManager(chunkMaxCapacity, ByteBuffer::allocate)));
        }

        /**
         * @return текущий экземпляр строителя.
         */
        public RamBuilder<E> setOffHeapChunkedByteStore(int chunkMaxCapacity) {
            validateChunkMaxCapacity(chunkMaxCapacity);
            return setChunkedByteStore(
                    new ChunkedByteStore(
                            new ByteBufferChunkManager(chunkMaxCapacity, ByteBuffer::allocateDirect)));
        }

        /**
         * @return текущий экземпляр строителя.
         */
        public RamBuilder<E> setChunkedByteStore(ChunkedByteStore chunkedByteStore) {
            validateChunkMaxCapacity(chunkedByteStore.getChunkManager().getChunkMaxCapacity());
            RamBuilder<E> fileBuilder = new RamBuilder<E>(binarySerializer_, chunkedByteStore);
            return fileBuilder;
        }

        /**
         * @return текущий экземпляр строителя.
         */
        public FileBuilder<E> setFileChunkedByteStore(int chunkMaxCapacity, Path directoryPath, int activeFileMaxCount) {
            FileBuilder<E> fileBuilder = new FileBuilder<E>(binarySerializer_);
            fileBuilder.setByteStore(
                    new ChunkedByteStore(
                            new FileChunkManager(chunkMaxCapacity, directoryPath, activeFileMaxCount)));
            return fileBuilder;
        }

        public FileBuilder<E> setFileStorePath(Path directoryPath) throws IOException {
            FileBuilder<E> fileBuilder = new FileBuilder<E>(binarySerializer_);
            fileBuilder.setFileStorePath(directoryPath);
            return fileBuilder;
        }

        public FileBuilder<E> setFileStoreFile(File file) throws IOException {
            FileBuilder<E> fileBuilder = new FileBuilder<E>(binarySerializer_);
            fileBuilder.setFileStoreFile(file);
            return fileBuilder;
        }

        public FileBuilder<E> setBiFileStorePath(Path directoryPath) throws IOException {
            FileBuilder<E> fileBuilder = new FileBuilder<E>(binarySerializer_);
            fileBuilder.setBiFileStorePath(directoryPath);
            return fileBuilder;
        }

        public FileBuilder<E> setBiFileStoreFiles(File file1, File file2) throws IOException {
            FileBuilder<E> fileBuilder = new FileBuilder<E>(binarySerializer_);
            fileBuilder.setBiFileStoreFiles(file1, file2);
            return fileBuilder;
        }

        public BufferedRamBuilder<E> setBuffered() {
            return setBufferByteStore(new ArrayByteStore());
        }

        public BufferedRamBuilder<E> setBufferByteStore(ByteStore bufferByteStore) {
            Objects.requireNonNull(bufferByteStore, "bufferByteStore");
            BufferedRamBuilder<E> bufferedBuilder = new BufferedRamBuilder<>(binarySerializer_);
            bufferedBuilder.byteStore_ = byteStore_;
            bufferedBuilder.bufferByteStore_ = bufferByteStore;
            return bufferedBuilder;
        }

        public List<E> build() {
            int elementSize = binarySerializer_.getElementSize();

            ByteStore mainStore = byteStore_ != null ? byteStore_ : new ArrayByteStore();
            AlignedByteStore mainAlignedStore = new AlignedByteStore(mainStore, elementSize);
            SerializedStore<E> serializedStore = new SerializedStore<E>(binarySerializer_, mainAlignedStore);

            return new StoreAsListAdapter<>(serializedStore);
        }
    }


    public static final class RamBuilder<E> extends AbstractBuilder<E, Builder<E>> {

        protected RamBuilder(BinarySerializer<E> binarySerializer, ByteStore byteStore) {
            super(binarySerializer);
            byteStore_ = Objects.requireNonNull(byteStore, "byteStore");
        }

        public BufferedRamBuilder<E> setBuffered() {
            return setBufferByteStore(new ArrayByteStore());
        }

        public BufferedRamBuilder<E> setBufferByteStore(ByteStore bufferByteStore) {
            Objects.requireNonNull(bufferByteStore, "bufferByteStore");
            BufferedRamBuilder<E> bufferedBuilder = new BufferedRamBuilder<>(binarySerializer_);
            bufferedBuilder.byteStore_ = byteStore_;
            bufferedBuilder.bufferByteStore_ = bufferByteStore;
            return bufferedBuilder;
        }

        public List<E> build() {
            int elementSize = binarySerializer_.getElementSize();

            ByteStore mainByteStore = byteStore_ != null ? byteStore_ : new ArrayByteStore();
            AlignedByteStore mainAlignedStore = new AlignedByteStore(mainByteStore, elementSize);
            SerializedStore<E> serializedStore = new SerializedStore<E>(binarySerializer_, mainAlignedStore);

            return new StoreAsListAdapter<>(serializedStore);
        }

    }

    /**
     * Строитель буферизированного списка разбитого на чанки.
     *
     * @param <E> тип элементов.
     */
    public static final class BufferedRamBuilder<E> extends AbstractBuilder<E, BufferedRamBuilder<E>> {

        protected ByteStore bufferByteStore_;

        /**
         * Конструктор строителя.
         *
         * @param binarySerializer сериализатор элементов.
         */
        private BufferedRamBuilder(BinarySerializer<E> binarySerializer) {
            super(binarySerializer);
        }

        public BufferedList<E> build() {
            int elementSize = binarySerializer_.getElementSize();

            ByteStore bufferByteStore = bufferByteStore_ != null ? bufferByteStore_ : new ArrayByteStore();
            AlignedByteStore bufferAlignedStore = new AlignedByteStore(bufferByteStore, elementSize);
            SerializedStore<E> bufferSerializedStore = new SerializedStore<E>(binarySerializer_, bufferAlignedStore);

            ByteStore mainByteStore = byteStore_ != null ? byteStore_ : new ArrayByteStore();
            AlignedByteStore mainAlignedStore = new AlignedByteStore(mainByteStore, elementSize);

            BufferedSerializedStore<E> serializedStore = new BufferedSerializedStore<E>(mainAlignedStore, bufferSerializedStore);
            return new BufferedStoreAsListAdapter<>(serializedStore);
        }
    }


    public static final class FileBuilder<E> extends AbstractFileBuilder<E, FileBuilder<E>> {
        public FileBuilder(BinarySerializer<E> binarySerializer) {
            super(binarySerializer);
        }

        public BufferedFileBuilder<E> setBuffered() {
            return setBufferByteStore(new ArrayByteStore());
        }

        public BufferedFileBuilder<E> setBufferByteStore(ByteStore bufferByteStore) {
            Objects.requireNonNull(bufferByteStore, "bufferByteStore");
            BufferedFileBuilder<E> bufferedBuilder = new BufferedFileBuilder<>(binarySerializer_);
            bufferedBuilder.bufferByteStore_ = bufferByteStore;
            bufferedBuilder.byteStore_ = byteStore_;
            bufferedBuilder.file1_ = file1_;
            bufferedBuilder.file2_ = file2_;
            bufferedBuilder.openOptions_ = openOptions_;
            return bufferedBuilder;
        }

        @Override
        public CloseableList<E> build() throws IOException {
            int elementSize = binarySerializer_.getElementSize();

            ByteStore mainByteStore = Optional.ofNullable(byteStore_).orElseGet(() -> buildFileByteStore());
            AlignedByteStore mainAlignedStore = new AlignedByteStore(mainByteStore, elementSize);

            Closeable closeable = getCloseable(mainByteStore);

            SerializedStore<E> serializedStore = new SerializedStore<>(binarySerializer_, mainAlignedStore);
            return new CloseableListWrapper<>(new StoreAsListAdapter<>(serializedStore), closeable);
        }

    }

    public static final class BufferedFileBuilder<E> extends AbstractFileBuilder<E, BufferedFileBuilder<E>> {

        protected ByteStore bufferByteStore_;

        public BufferedFileBuilder(BinarySerializer<E> binarySerializer) {
            super(binarySerializer);
        }
        
        @Override
        public CloseableBufferedList<E> build() throws IOException {
            int elementSize = binarySerializer_.getElementSize();

            ByteStore bufferByteStore = bufferByteStore_ != null ? bufferByteStore_ : new ArrayByteStore();
            AlignedByteStore bufferAlignedStore = new AlignedByteStore(bufferByteStore, elementSize);
            SerializedStore<E> bufferSerializedStore = new SerializedStore<>(binarySerializer_, bufferAlignedStore);

            ByteStore mainByteStore = Optional.ofNullable(byteStore_).orElseGet(() -> buildFileByteStore());
            AlignedByteStore mainAlignedStore = new AlignedByteStore(mainByteStore, elementSize);

            Closeable closeable = getCloseable(mainByteStore);

            BufferedSerializedStore<E> serializedStore = new BufferedSerializedStore(mainAlignedStore, bufferSerializedStore);
            return new CloseableBufferedListWrapper<>(new BufferedStoreAsListAdapter<>(serializedStore), closeable);
        }

    }

    public static abstract class AbstractFileBuilder<E, SELF extends AbstractFileBuilder<E, SELF>> extends AbstractBuilder<E, SELF> {
        protected OpenOption[] openOptions_ = new OpenOption[]{
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.DELETE_ON_CLOSE};
        protected File file1_;
        protected File file2_;

        AbstractFileBuilder(BinarySerializer<E> binarySerializer) {
            super(binarySerializer);
        }

        protected final Closeable getCloseable(ByteStore byteStore) {
            Closeable closeable;
            if (byteStore instanceof Closeable) {
                closeable = (Closeable) byteStore;
            } else if (byteStore instanceof ChunkedByteStore) {
                closeable = (Closeable) ((ChunkedByteStore) byteStore).getChunkManager();
            } else {
                throw new IllegalStateException("The ByteStore or ChunkManager instances must supports java.io.Closeable.");
            }
            return closeable;
        }

        protected final AbstractFileByteStore buildFileByteStore() {
            AbstractFileByteStore fileByteStore;
            if (file1_ != null && file2_ != null) {
                fileByteStore = new BiFileByteStore(file1_, file2_, openOptions_);
            } else {
                file1_ = (file1_ != null)
                        ? file1_
                        : ExceptionUtils.passChecked(() -> File.createTempFile("list", ".bin"));
                fileByteStore = new FileByteStore(file1_, openOptions_);
            }
            return fileByteStore;
        }

        private void validateDirectoryPath(Path tempDirectory) throws IOException {
            if (!Files.isDirectory(tempDirectory)) {
                throw new IOException(String.format("Path '%s' is not a directory.", tempDirectory));
            }
            if (!Files.exists(tempDirectory)) {
                throw new IOException(String.format("Directory '%s' not exists.", tempDirectory.toAbsolutePath()));
            }
        }

        SELF setFileStorePath(Path directoryPath) throws IOException {
            Objects.requireNonNull(directoryPath);
            validateDirectoryPath(directoryPath.toAbsolutePath());
            File file = File.createTempFile("list", ".bin", directoryPath.toFile());
            setFileStoreFile(file);
            return (SELF) this;
        }

        SELF setFileStoreFile(File file) throws IOException {
            Objects.requireNonNull(file, "file");
            validateDirectoryPath(file.toPath().toAbsolutePath().getParent());
            byteStore_ = null;
            file1_ = file;
            file2_ = null;
            return (SELF) this;
        }

        SELF setFileByteStore(FileByteStore byteStore) throws IOException {
            Objects.requireNonNull(byteStore, "byteStore");
            byteStore_ = byteStore;
            file1_ = null;
            file2_ = null;
            return (SELF) this;
        }

        SELF setBiFileStorePath(Path directoryPath) throws IOException {
            Objects.requireNonNull(directoryPath);
            validateDirectoryPath(directoryPath);
            File file1 = File.createTempFile("list", ".bin", directoryPath.toFile());
            File file2 = File.createTempFile("list", ".bin", directoryPath.toFile());
            setBiFileStoreFiles(file1, file2);
            return (SELF) this;
        }

        SELF setBiFileStoreFiles(File file1, File file2) throws IOException {
            Objects.requireNonNull(file1, "file1");
            Objects.requireNonNull(file2, "file2");
            validateDirectoryPath(file1.toPath().toAbsolutePath().getParent());
            validateDirectoryPath(file2.toPath().toAbsolutePath().getParent());
            byteStore_ = null;
            file1_ = file1;
            file2_ = file2;
            return (SELF) this;
        }

        public SELF setOpenOptions(OpenOption... openOptions) {
            if (openOptions.length == 0) {
                throw new IllegalArgumentException("A non-empty array is required.");
            }
            openOptions_ = openOptions;
            return (SELF) this;
        }

        public abstract CloseableList<E> build() throws IOException;

    }

    /**
     * Абстрактный строитель списка разбитого на чанки.
     *
     * @param <E>    тип элементов
     * @param <SELF> тип строителя
     */
    public static abstract class AbstractBuilder<E, SELF extends AbstractBuilder<E, SELF>> {
        protected final BinarySerializer<E> binarySerializer_;
        protected ByteStore byteStore_;

        protected AbstractBuilder(BinarySerializer<E> binarySerializer) {
            binarySerializer_ = Objects.requireNonNull(binarySerializer, "binarySerializer");
        }

        SELF setByteStore(ByteStore ByteStore) {
            byteStore_ = Objects.requireNonNull(ByteStore, "ByteStore");
            return (SELF) this;
        }

    }
}
