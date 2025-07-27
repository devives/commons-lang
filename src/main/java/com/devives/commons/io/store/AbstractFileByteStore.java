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

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Objects;

/**
 * Abstract implementation of byte storage based on {@link File}.
 */
public abstract class AbstractFileByteStore extends AbstractByteStore implements AutoCloseable {

    protected final File[] files_;
    protected boolean opened_ = true;

    protected AbstractFileByteStore(File[] files) {
        files_ = Objects.requireNonNull(files);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void close() throws Exception {
        opened_ = false;
        internalClose();
    }

    /**
     * Method is calling while {@link #close()} called.
     *
     * @throws Exception if exception thrown
     */
    protected void internalClose() throws Exception {

    }

    public File[] getFiles() {
        return files_;
    }

    /**
     * Indicate that buffer is open and operable.
     *
     * @return <tt>true</tt>, if {@link #close()} not called, otherwise <tt>false</tt>.
     */
    public final boolean isOpened() {
        return opened_;
    }

    /**
     * The source of {@link FileChannel} instance.
     */
    protected static final class FileChannelSource implements AutoCloseable {
        private final File file_;
        private final OpenOption[] options_;
        private FileChannel channel_;
        private boolean opened_ = true;

        public FileChannelSource(File file, OpenOption... options) {
            file_ = Objects.requireNonNull(file, "file");
            options_ = Objects.requireNonNull(options, "options");
        }

        public FileChannel openChannel(OpenOption ... options) throws IOException {
            return FileChannel.open(file_.toPath(), options);
        }

        protected FileChannel getOrOpenChannel() throws IOException {
            if (this.channel_ == null) {
                validateOpened();
                this.channel_ = openChannel(options_);
            }
            return this.channel_;
        }

        protected void closeChannel() throws IOException {
            try {
                if (channel_ != null && channel_.isOpen()) {
                    channel_.close();
                } else if (Arrays.binarySearch(options_, StandardOpenOption.DELETE_ON_CLOSE) >= 0) {
                    Files.deleteIfExists(file_.toPath());
                }
            } finally {
                this.channel_ = null;
            }
        }

        protected void validateOpened() {
            if (!opened_) {
                throw new RuntimeException("ByteStore is closed.");
            }
        }

        public void close() throws Exception {
            opened_ = false;
            closeChannel();
        }

        public FileChannel getChannel() {
            return channel_;
        }

    }

}
