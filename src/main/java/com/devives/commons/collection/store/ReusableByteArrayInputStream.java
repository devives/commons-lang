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
package com.devives.commons.collection.store;

import java.io.InputStream;

/**
 * Class implements an input stream from a byte array.
 * <p>
 * Differences from {@link java.io.ByteArrayInputStream}:
 * <ul>
 * <li>Class methods are not synchronized.
 * <li>Buffer reference and current position can be changed.
 * </ul>
 */
final class ReusableByteArrayInputStream extends InputStream {

    private byte[] bytes_;
    private int pos_;
    private int mark_ = 0;
    private int size_;

    public ReusableByteArrayInputStream(byte buf[]) {
        this.bytes_ = buf;
        this.pos_ = 0;
        this.size_ = buf.length;
    }

    public int read() {
        return (pos_ < size_) ? (bytes_[pos_++] & 0xff) : -1;
    }

    public int read(byte b[], int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }

        if (pos_ >= size_) {
            return -1;
        }

        int avail = size_ - pos_;
        if (len > avail) {
            len = avail;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(bytes_, pos_, b, off, len);
        pos_ += len;
        return len;
    }

    public long skip(long n) {
        long k = size_ - pos_;
        if (n < k) {
            k = n < 0 ? 0 : n;
        }

        pos_ += k;
        return k;
    }

    public int available() {
        return size_ - pos_;
    }

    public boolean markSupported() {
        return true;
    }

    public void mark(int readAheadLimit) {
        mark_ = pos_;
    }

    public void reset() {
        pos_ = mark_;
    }

    public void setByteArray(byte[] buf) {
        this.bytes_ = buf;
        this.pos_ = 0;
        this.size_ = buf.length;
        this.mark_ = 0;
    }

    public int position() {
        return this.pos_;
    }

    public void position(int pos) {
        this.mark_ = 0;
        this.pos_ = 0;
        skip(pos);
    }

    public void size(int size) {
        this.size_ = size;
    }

}
