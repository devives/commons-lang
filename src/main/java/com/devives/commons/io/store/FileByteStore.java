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

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * A single file-based {@link File} byte store.
 * <p>
 * When inserting or deleting data at the beginning or middle of a store, the tail of the data is moved.
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public final class FileByteStore extends AbstractFileByteStore {
    private final FileChannelSource fileChannelSource_;
    private int transferBufferSize_ = 64 * 1024; // 64 KiB

    /**
     * @param file file.
     */
    public FileByteStore(File file) {
        this(file,
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.DELETE_ON_CLOSE
        );
    }

    /**
     *
     * @param file file.
     * @param options open options.
     */
    public FileByteStore(File file, OpenOption... options) {
        super(new File[]{Objects.requireNonNull(file, "file")});
        fileChannelSource_ = new FileChannelSource(file, options);
    }

    /**
     * Return current {@link FileChannel}.
     *
     * @return {@link FileChannel}
     */
    protected FileChannel getFileChannel() {
        try {
            return fileChannelSource_.getOrOpenChannel();
        } catch (IOException e) {
            throw ExceptionUtils.asUnchecked(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int internalRead(long fromPosition, ByteBuffer byteBuffer) {
        try {
            int n;
            int available = Math.toIntExact(size_ - fromPosition);
            if (available < byteBuffer.remaining()) {
                int limit = byteBuffer.limit();
                byteBuffer.limit(available);
                n = getFileChannel().read(byteBuffer, fromPosition);
                byteBuffer.limit(limit);
            } else {
                n = getFileChannel().read(byteBuffer, fromPosition);
            }
            return n;
        } catch (IOException e) {
            throw ExceptionUtils.asUnchecked(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int internalWrite(long fromPosition, ByteBuffer byteBuffer) {
        try {
            return getFileChannel().write(byteBuffer, fromPosition);
        } catch (IOException e) {
            throw ExceptionUtils.asUnchecked(e);
        }

    }

    private void internalTruncate(long newSize) throws IOException {
        getFileChannel().truncate(newSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int internalReplaceRange(long fromPosition, long toPosition, ByteBuffer byteBuffer) {
        try {
            int writingCount = byteBuffer.remaining();
            long removingCount = toPosition - fromPosition;
            long diffCount = writingCount - removingCount;
            FileChannel fileChannel = getFileChannel();

            if (toPosition == size()) {
                // Вставка/замена в конец
                long n = fileChannel.write(byteBuffer, fromPosition);
                if (n != writingCount) throw new IOException("Expected: " + writingCount + ", written: " + n);
                if (diffCount < 0) fileChannel.truncate(fromPosition + writingCount);
            } else if (diffCount == 0) {
                // Простая замена
                if (writingCount > 0) {
                    long n = fileChannel.write(byteBuffer, fromPosition);
                    if (n != writingCount) throw new IOException("Expected: " + writingCount + ", written: " + n);
                }
            } else if (diffCount > 0) {
                // Вставка: сдвигаем хвост вправо
                long tail = size_ - toPosition;
                ByteBuffer buf = ByteBuffer.allocate(transferBufferSize_);
                for (long i = tail; i > 0; ) {
                    int len = (int) Math.min(i, buf.capacity());
                    long src = toPosition + i - len;
                    buf.clear().limit(len);
                    fileChannel.read(buf, src);
                    buf.flip();
                    fileChannel.write(buf, src + diffCount);
                    i -= len;
                }
                long n = fileChannel.write(byteBuffer, fromPosition);
                if (n != writingCount) throw new IOException("Expected: " + writingCount + ", written: " + n);
            } else {
                // Удаление: сдвигаем хвост влево
                long tail = size_ - toPosition;
                ByteBuffer buf = ByteBuffer.allocate(transferBufferSize_);
                for (long i = 0; i < tail; i += buf.capacity()) {
                    int len = (int) Math.min(tail - i, buf.capacity());
                    buf.clear().limit(len);
                    fileChannel.read(buf, toPosition + i);
                    buf.flip();
                    fileChannel.write(buf, fromPosition + writingCount + i);
                }
                if (writingCount > 0) {
                    long n = fileChannel.write(byteBuffer, fromPosition);
                    if (n != writingCount) throw new IOException("Expected: " + writingCount + ", written: " + n);
                    }
                fileChannel.truncate(size_ + diffCount);
            }
            return writingCount;
        } catch (IOException e) {
            throw ExceptionUtils.asUnchecked(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void internalClose() {
        ExceptionUtils.collectAndThrow(
                fileChannelSource_::close,
                super::internalClose
        );
    }

    /**
     * Абстрактный фабричный метод списка, хранимого в файле на диске.
     *
     * @param tempDirectory путь к каталогу, в котором будут созданы временные файлы списка.
     * @return новый экземпляр списка
     * @throws IOException при ошибках создания временных файлов.
     */
    public static FileByteStore createAt(Path tempDirectory) throws IOException {
        Objects.requireNonNull(tempDirectory);
        if (!Files.exists(tempDirectory)) {
            throw new IOException(String.format("Directory '%s' is not exist.", tempDirectory.toAbsolutePath()));
        }
        if (!Files.isDirectory(tempDirectory)) {
            throw new IOException(String.format("Path '%s' is not a directory.", tempDirectory.toAbsolutePath()));
        }
        File file = File.createTempFile("list", ".bin", tempDirectory.toFile());
        return new FileByteStore(file);
    }

}
