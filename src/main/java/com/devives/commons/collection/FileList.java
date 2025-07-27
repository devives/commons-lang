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
import com.devives.commons.collection.store.Store;
import com.devives.commons.collection.store.StoreList;
import com.devives.commons.collection.store.serializer.*;
import com.devives.commons.io.store.*;
import com.devives.commons.lang.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Список элементов, в сериализованном виде, хранимых в файле на диске.
 * <p>
 * When data is inserted or deleted at the beginning or middle of the store, the tail of the data is shifted within the file.
 * @see FileByteStore
 */
public class FileList<E> extends StoreList<E> implements AutoCloseable {
    /**
     * Массив, объектов, которые необходимо закрыть при закрытии текущего экземпляра.
     */
    private final AutoCloseable[] autoCloseableArray_;
    /**
     * Флаг, указывающий, что экземпляр открыт.
     */
    private boolean opened_ = true;

    /**
     * @param store
     * @param autoCloseable Массив, объектов, которые необходимо закрыть при закрытии текущего экземпляра.
     */
    FileList(Store<E> store, AutoCloseable... autoCloseable) {
        super(store);
        autoCloseableArray_ = Objects.requireNonNull(autoCloseable);
    }

    /**
     * Возвращает флаг открытости списка.
     *
     * @return true, если список открыт, иначе false.
     */
    public boolean isOpened() {
        return opened_;
    }

    @Override
    public void close() throws Exception {
        opened_ = false;
        List<Exception> exceptions = new ArrayList<>();
        for (AutoCloseable closeable : autoCloseableArray_) {
            try {
                closeable.close();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        ExceptionUtils.throwCollected(exceptions);
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
            AbstractFileByteStore fileByteStore = fileByteStore_ != null ? fileByteStore_ : new FileByteStore(File.createTempFile("list", ".bin"));
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
            AbstractFileByteStore fileByteStore = fileByteStore_ != null ? fileByteStore_ : new FileByteStore(File.createTempFile("list", ".bin"));
            AlignedByteStore mainStore = new AlignedByteStore(fileByteStore, binarySerializer_.getElementSize());
            SerializedStore<E> bufferStore = new SerializedStore<>(binarySerializer_, new AlignedByteStore(new ArrayByteStore(), binarySerializer_.getElementSize()));
            BufferedSerializedStore<E> elementStore = new BufferedSerializedStore(mainStore, bufferStore);
            return new BufferedFileList(elementStore, fileByteStore);
        }


    }

    public static abstract class AbstractBuilder<E, SELF extends AbstractBuilder<E, SELF>> {
        protected final BinarySerializer<E> binarySerializer_;
        protected AbstractFileByteStore fileByteStore_;
        protected ByteStore bufferByteStore_;

        AbstractBuilder(BinarySerializer<E> binarySerializer) {
            binarySerializer_ = Objects.requireNonNull(binarySerializer, "binarySerializer");
        }

        public SELF setFileByteStore(Path tempDirectory) throws IOException {
            Objects.requireNonNull(tempDirectory);
            if (!Files.exists(tempDirectory)) {
                throw new IOException(String.format("Directory '%s' not exists.", tempDirectory.toAbsolutePath()));
            }
            File file = File.createTempFile("list", ".bin", tempDirectory.toFile());
            setFileByteStore(file);
            return (SELF) this;
        }

        public SELF setFileByteStore(File file) throws IOException {
            Objects.requireNonNull(file, "file");
            Path tempDirectory = file.toPath().toAbsolutePath().getParent();
            if (!Files.isDirectory(tempDirectory)) {
                throw new IOException(String.format("Path '%s' is not a directory.", tempDirectory));
            }
            if (!Files.exists(tempDirectory)) {
                throw new IOException(String.format("Directory '%s' not exists.", tempDirectory.toAbsolutePath()));
            }
            fileByteStore_ = new FileByteStore(file);
            return (SELF) this;
        }

        public SELF setBiFileByteStore(Path tempDirectory) throws IOException {
            Objects.requireNonNull(tempDirectory);
            if (!Files.exists(tempDirectory)) {
                throw new IOException(String.format("Directory '%s' not exists.", tempDirectory.toAbsolutePath()));
            }
            if (!Files.isDirectory(tempDirectory)) {
                throw new IOException(String.format("Path '%s' is not a directory.", tempDirectory.toAbsolutePath()));
            }
            File file1 = File.createTempFile("list", ".bin", tempDirectory.toFile());
            File file2 = File.createTempFile("list", ".bin", tempDirectory.toFile());

            setBiFileByteStore(file1, file2);
            return (SELF) this;
        }

        public SELF setBiFileByteStore(File file1, File file2) throws IOException {
            Objects.requireNonNull(file1, "file1");
            Objects.requireNonNull(file2, "file2");
            Path tempDirectory = file1.toPath().toAbsolutePath().getParent();
            if (!Files.isDirectory(tempDirectory)) {
                throw new IOException(String.format("Path '%s' is not a directory.", tempDirectory));
            }
            if (!Files.exists(tempDirectory)) {
                throw new IOException(String.format("Directory '%s' not exists.", tempDirectory.toAbsolutePath()));
            }
            tempDirectory = file2.toPath().toAbsolutePath().getParent();
            if (!Files.isDirectory(tempDirectory)) {
                throw new IOException(String.format("Path '%s' is not a directory.", tempDirectory));
            }
            if (!Files.exists(tempDirectory)) {
                throw new IOException(String.format("Directory '%s' not exists.", tempDirectory.toAbsolutePath()));
            }
            fileByteStore_ = new BiFileByteStore(file1, file2);
            return (SELF) this;
        }

        public BufferedBuilder<E> setBuffered() {
            return setBufferByteStore(new ArrayByteStore());
        }

        public BufferedBuilder<E> setBufferByteStore(ByteStore bufferByteStore) {
            Objects.requireNonNull(bufferByteStore, "bufferByteStore");
            BufferedBuilder<E> bufferedBuilder = new BufferedBuilder<>(binarySerializer_);
            bufferedBuilder.fileByteStore_ = fileByteStore_;
            bufferedBuilder.bufferByteStore_ = bufferByteStore;
            return bufferedBuilder;
        }

        public abstract FileList<E> build() throws IOException;

    }
}
