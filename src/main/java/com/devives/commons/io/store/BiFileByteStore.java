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
 * Implementation of byte storage based on two {@link File} files.
 * May be useful when hardware/driver does not support transferring data within one file.
 * <p>
 * Read and write operations to the end are performed within one file.
 * If writing to the middle is required, the data is transferred to the second file, which becomes active.
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public final class BiFileByteStore extends AbstractFileByteStore {
    private FileChannelSource activeFileChannelSource_;
    private FileChannelSource tempFileChannelSource_;
    private boolean closeTemp_ = false;

    /**
     * Construct ByteStore based on two files.
     *
     * @param file1 primary file
     * @param file2 secondary file
     */
    public BiFileByteStore(File file1, File file2) {
        this(file1, file2,
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.DELETE_ON_CLOSE);
    }

    /**
     * @param file1   file.
     * @param file2   file.
     * @param options open options.
     */
    public BiFileByteStore(File file1, File file2, OpenOption... options) {
        super(new File[]{Objects.requireNonNull(file1, "file1"), Objects.requireNonNull(file2, "file2")});
        activeFileChannelSource_ = new FileChannelSource(file1, options);
        tempFileChannelSource_ = new FileChannelSource(file2, options);
    }

    /**
     * Return current primary {@link FileChannel}.
     *
     * @return {@link FileChannel}
     */
    private FileChannel getActiveFileChannel() throws IOException {
        return activeFileChannelSource_.getOrOpenChannel();
    }

    /**
     * Return current secondary {@link FileChannel}.
     *
     * @return {@link FileChannel}
     */
    private FileChannel getTempFileChannel() throws IOException {
        return tempFileChannelSource_.getOrOpenChannel();
    }

    /**
     * Change an active file store, by switching primary and secondary file stores.
     *
     * @throws IOException
     */
    private void switchFileChannels() throws IOException {
        FileChannelSource fileChannelSource = activeFileChannelSource_;
        activeFileChannelSource_ = tempFileChannelSource_;
        tempFileChannelSource_ = fileChannelSource;
        if (closeTemp_) {
            tempFileChannelSource_.closeChannel();
        } else {
            tempFileChannelSource_.getChannel().truncate(0);
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
                n = getActiveFileChannel().read(byteBuffer, fromPosition);
                byteBuffer.limit(limit);
            } else {
                n = getActiveFileChannel().read(byteBuffer, fromPosition);
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
            return getActiveFileChannel().write(byteBuffer, fromPosition);
        } catch (IOException e) {
            throw ExceptionUtils.asUnchecked(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int internalReplaceRange(long fromPosition, long toPosition, ByteBuffer byteBuffer) {
        try {
            FileChannel activeChannel = getActiveFileChannel();
            int writingCount = byteBuffer.remaining();
            if (toPosition == size()) {
                // Вставка или замена в конец
                activeChannel.write(byteBuffer, fromPosition);
                activeChannel.truncate(fromPosition + writingCount);
            } else {
                FileChannel tempChannel = getTempFileChannel();

                // 1. Копируем head (0..fromPosition)
                if (fromPosition > 0) {
                    long copied = 0;
                    while (copied < fromPosition) {
                        long n = activeChannel.transferTo(copied, fromPosition - copied, tempChannel);
                        if (n <= 0) throw new IOException("Failed to copy head");
                        copied += n;
                    }
                }

                // 2. Пишем новые данные
                if (writingCount > 0) {
                    int n = tempChannel.write(byteBuffer);
                    if (n != writingCount) throw new IOException("Expected: " + writingCount + ", written: " + n);
                }

                // 3. Копируем tail (toPosition..size)
                long tail = size_ - toPosition;
                if (tail > 0) {
                    long copied = 0;
                    while (copied < tail) {
                        long n = activeChannel.transferTo(toPosition + copied, tail - copied, tempChannel);
                        if (n <= 0) throw new IOException("Failed to copy tail");
                        copied += n;
                    }
                }

                // 4. Переключаем файлы
                switchFileChannels();
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
    protected void internalClose() throws Exception {
        ExceptionUtils.collectAndThrow(
                activeFileChannelSource_::close,
                tempFileChannelSource_::close,
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
    public static BiFileByteStore createAt(Path tempDirectory) throws IOException {
        Objects.requireNonNull(tempDirectory);
        if (!Files.exists(tempDirectory)) {
            throw new IOException(String.format("Directory '%s' not exists.", tempDirectory.toAbsolutePath()));
        }
        if (!Files.isDirectory(tempDirectory)) {
            throw new IOException(String.format("Path '%s' is not a directory.", tempDirectory.toAbsolutePath()));
        }
        File file1 = File.createTempFile("list", ".bin", tempDirectory.toFile());
        File file2 = File.createTempFile("list", ".bin", tempDirectory.toFile());
        return new BiFileByteStore(file1, file2);
    }

}
