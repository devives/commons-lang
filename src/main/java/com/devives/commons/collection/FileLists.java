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
import com.devives.commons.collection.store.SerializedStore;
import com.devives.commons.collection.store.serializer.*;
import com.devives.commons.io.store.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * Точка входа для создания списков элементов, сериализованных в файл на диске.
 *
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @see FileByteStore
 * @see FileList
 * @see BufferedFileList
 * @since 0.3.0
 */
public final class FileLists {

    private FileLists() {
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

    public static final class Builder<E> extends AbstractBuilder<E, Builder<E>> {
        public Builder(BinarySerializer<E> binarySerializer) {
            super(binarySerializer);
        }

        @Override
        public FileList<E> build() throws IOException {
            AbstractFileByteStore fileByteStore = buildByteStore();
            AlignedByteStore mainStore = new AlignedByteStore(fileByteStore, binarySerializer_.getElementSize());
            SerializedStore<E> serializedStore = new SerializedStore<>(binarySerializer_, mainStore);
            return new FileList(serializedStore, fileByteStore);
        }

    }

    public static final class BufferedBuilder<E> extends AbstractBuilder<E, BufferedBuilder<E>> {
        public BufferedBuilder(BinarySerializer<E> binarySerializer) {
            super(binarySerializer);
        }

        @Override
        public BufferedFileList<E> build() throws IOException {
            AbstractFileByteStore fileByteStore = buildByteStore();
            AlignedByteStore mainStore = new AlignedByteStore(fileByteStore, binarySerializer_.getElementSize());
            SerializedStore<E> bufferStore = new SerializedStore<>(binarySerializer_, new AlignedByteStore(new ArrayByteStore(), binarySerializer_.getElementSize()));
            BufferedSerializedStore<E> elementStore = new BufferedSerializedStore(mainStore, bufferStore);
            return new BufferedFileList(elementStore, fileByteStore);
        }


    }

    public static abstract class AbstractBuilder<E, SELF extends AbstractBuilder<E, SELF>> {
        protected final BinarySerializer<E> binarySerializer_;
        protected ByteStore bufferByteStore_;
        protected OpenOption[] openOptions_ = new OpenOption[]{
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.DELETE_ON_CLOSE};
        protected File file1_;
        protected File file2_;

        AbstractBuilder(BinarySerializer<E> binarySerializer) {
            binarySerializer_ = Objects.requireNonNull(binarySerializer, "binarySerializer");
        }

        protected AbstractFileByteStore buildByteStore() throws IOException {
            AbstractFileByteStore fileByteStore;
            if (file1_ != null && file2_ != null) {
                fileByteStore = new BiFileByteStore(file1_, file2_, openOptions_);
            } else {
                file1_ = (file1_ != null)
                        ? file1_
                        : File.createTempFile("list", ".bin");
                fileByteStore = new FileByteStore(file1_, openOptions_);
            }
            return fileByteStore;
        }

        public SELF setOpenOptions(OpenOption... openOptions) {
            openOptions_ = openOptions;
            return (SELF) this;
        }

        private void validateDirectoryPath(Path tempDirectory) throws IOException {
            if (!Files.isDirectory(tempDirectory)) {
                throw new IOException(String.format("Path '%s' is not a directory.", tempDirectory));
            }
            if (!Files.exists(tempDirectory)) {
                throw new IOException(String.format("Directory '%s' not exists.", tempDirectory.toAbsolutePath()));
            }
        }

        public SELF setFileStorePath(Path directoryPath) throws IOException {
            Objects.requireNonNull(directoryPath);
            validateDirectoryPath(directoryPath.toAbsolutePath());
            File file = File.createTempFile("list", ".bin", directoryPath.toFile());
            setFileStoreFile(file);
            return (SELF) this;
        }

        public SELF setFileStoreFile(File file) throws IOException {
            Objects.requireNonNull(file, "file");
            validateDirectoryPath(file.toPath().toAbsolutePath().getParent());
            file1_ = file;
            file2_ = null;
            return (SELF) this;
        }

        public SELF setBiFileStorePath(Path directoryPath) throws IOException {
            Objects.requireNonNull(directoryPath);
            validateDirectoryPath(directoryPath);
            File file1 = File.createTempFile("list", ".bin", directoryPath.toFile());
            File file2 = File.createTempFile("list", ".bin", directoryPath.toFile());
            setBiFileStoreFiles(file1, file2);
            return (SELF) this;
        }

        public SELF setBiFileStoreFiles(File file1, File file2) throws IOException {
            Objects.requireNonNull(file1, "file1");
            Objects.requireNonNull(file2, "file2");
            validateDirectoryPath(file1.toPath().toAbsolutePath().getParent());
            validateDirectoryPath(file2.toPath().toAbsolutePath().getParent());
            file1_ = file1;
            file2_ = file2;
            return (SELF) this;
        }

        public BufferedBuilder<E> setBuffered() {
            return setBufferByteStore(new ArrayByteStore());
        }

        public BufferedBuilder<E> setBufferByteStore(ByteStore bufferByteStore) {
            Objects.requireNonNull(bufferByteStore, "bufferByteStore");
            BufferedBuilder<E> bufferedBuilder = new BufferedBuilder<>(binarySerializer_);
            bufferedBuilder.file1_ = file1_;
            bufferedBuilder.file2_ = file2_;
            bufferedBuilder.openOptions_ = openOptions_;
            bufferedBuilder.bufferByteStore_ = bufferByteStore;
            return bufferedBuilder;
        }

        public abstract FileList<E> build() throws IOException;

    }
}
