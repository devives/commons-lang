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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Реализация менеджера чанков бинарных данных, хранимых в файлах на диске.
 * <pre>{@code
 *
 * }</pre>
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public final class FileChunkManager
        extends AbstractChunkManager<FileChunkManager.FileChunk, FileChunkManager.FileChunkDescriptor>
        implements Closeable {

    private final Path directoryPath_;
    private final LimitedLinkedHashMap chunkMap_;

    public FileChunkManager(int chunkMaxCapacity, Path directoryPath, int openedFileMaxCount) {
        super(chunkMaxCapacity);
        directoryPath_ = Objects.requireNonNull(directoryPath, "The path to work directory can not be null.");
        try {
            if (!Files.exists(directoryPath)) {
                throw new IOException(String.format("Directory '%s' is not exist.", directoryPath.toAbsolutePath()));
            }
            if (!Files.isDirectory(directoryPath)) {
                throw new IOException(String.format("Path '%s' is not a directory.", directoryPath.toAbsolutePath()));
            }
            if (openedFileMaxCount < 1) {
                throw new IllegalArgumentException(String.format("The value of the 'openedFileMaxCount' argument must be greater than zero.", openedFileMaxCount));
            }
            chunkMap_ = new LimitedLinkedHashMap(openedFileMaxCount);
        } catch (IOException e) {
            throw ExceptionUtils.asUnchecked(e);
        }
    }

    public FileChunkManager(int chunkMaxCapacity, List<FileChunkManager.FileChunkDescriptor> chunkDescList, Path directoryPath, int openedFileMaxCount) {
        super(chunkMaxCapacity, chunkDescList);
        directoryPath_ = Objects.requireNonNull(directoryPath, "The path to work directory can not be null.");
        try {
            if (!Files.exists(directoryPath)) {
                throw new IOException(String.format("Directory '%s' is not exist.", directoryPath.toAbsolutePath()));
            }
            if (!Files.isDirectory(directoryPath)) {
                throw new IOException(String.format("Path '%s' is not a directory.", directoryPath.toAbsolutePath()));
            }
            if (openedFileMaxCount < 1) {
                throw new IllegalArgumentException(String.format("The value of the 'openedFileMaxCount' argument must be greater than zero.", openedFileMaxCount));
            }
            chunkMap_ = new LimitedLinkedHashMap(openedFileMaxCount);
        } catch (IOException e) {
            throw ExceptionUtils.asUnchecked(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            clear();
            chunkMap_.values().forEach(store -> {
                try {
                    ((FileByteStore) store).close();
                    Arrays.stream(((FileByteStore) store).getFiles()).forEach(File::delete);
                } catch (Exception e) {
                    throw ExceptionUtils.asUnchecked(e);
                }
            });
        } finally {
            chunkMap_.clear();
        }
    }

    @Override
    protected FileChunkDescriptor newChunkDescriptor(int capacity) {
        try {
            return new FileChunkDescriptor(File.createTempFile("chunk", ".bin", directoryPath_.toFile()));
        } catch (IOException e) {
            throw ExceptionUtils.asUnchecked(e);
        }
    }

    @Override
    protected void onRemoveChunkDescriptor(int index, FileChunkDescriptor desc) {
        try {
            FileByteStore store = (FileByteStore) chunkMap_.remove(desc.getFile());
            if (store != null) {
                store.close();
            }
            boolean result = desc.getFile().delete();
            assert result;
        } catch (Exception e) {
            throw ExceptionUtils.asUnchecked(e);
        }
    }

    public final class FileChunkDescriptor extends AbstractChunkManager.AbstractChunkDescriptor<FileChunk> {

        private final File file_;
        private long size_ = 0;

        public FileChunkDescriptor(File file) {
            this.file_ = file;
        }

        public FileChunk getChunk() {
            return new FileChunk(this);
        }

        public File getFile() {
            return file_;
        }

        @Override
        public long getSize() {
            return size_;
        }

        void setSize(long size) {
            size_ = size;
        }
    }

    public final class FileChunk extends AbstractChunkManager.AbstractChunk {

        public FileChunk(FileChunkDescriptor descriptor) {
            super(descriptor);
        }

        @Override
        protected FileChunkDescriptor getDescriptor() {
            return (FileChunkDescriptor) super.getDescriptor();
        }

        @Override
        protected ByteStore getByteStore() {
            try {
                final FileChunkDescriptor descriptor = getDescriptor();
                return chunkMap_.computeIfAbsent(descriptor.getFile(), file -> {
                    FileByteStore byteStore = new FileByteStore(file,
                            StandardOpenOption.READ,
                            StandardOpenOption.WRITE,
                            StandardOpenOption.CREATE);
                    byteStore.size_ = descriptor.getSize();
                    return byteStore;
                });
            } catch (Exception e) {
                throw ExceptionUtils.asUnchecked(e);
            }
        }

        @Override
        protected void flushByteStore(ByteStore byteStore) {
            try {
                getDescriptor().setSize(byteStore.size());
            } catch (Exception e) {
                throw ExceptionUtils.asUnchecked(e);
            }
        }
    }

    private static final class LimitedLinkedHashMap extends LinkedHashMap<File, ByteStore> {

        private final int maxSize_;

        public LimitedLinkedHashMap(int maxSize) {
            if (maxSize <= 0) {
                throw new IllegalArgumentException("Max size must be greater than zero.");
            }
            this.maxSize_ = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<File, ByteStore> eldest) {
            boolean result = size() > maxSize_;
            if (result) {
                try {
                    ((FileByteStore) eldest.getValue()).close();
                } catch (Exception e) {
                    throw ExceptionUtils.asUnchecked(e);
                }
            }
            return result;
        }
    }
}
