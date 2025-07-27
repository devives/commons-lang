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

import com.devives.commons.collection.store.BufferController;
import com.devives.commons.collection.store.BufferedStore;
import com.devives.commons.io.store.FileByteStore;

/**
 * A buffered list of elements stored in a file on disk, in serialized form.
 * <p>
 * When data is inserted or deleted at the beginning or middle of the store, the tail of the data is shifted within the file.

 * @see FileByteStore
 */
public class BufferedFileList<E> extends FileList<E> implements AutoCloseable, BufferController {

    BufferedFileList(BufferedStore<E> bufferedStore, AutoCloseable... autoCloseable) {
        super(bufferedStore, autoCloseable);
    }

    @Override
    public BufferedStore<E> getStore() {
        return (BufferedStore<E>) super.getStore();
    }

    @Override
    public int getBufferSize() {
        return getStore().getBufferSize();
    }

    @Override
    public void setBufferSize(int size) {
        getStore().setBufferSize(size);
    }

    @Override
    public int getBufferMaxSize() {
        return getStore().getBufferMaxSize();
    }

    @Override
    public void setBufferMaxSize(int size) {
        getStore().setBufferMaxSize(size);
    }

    @Override
    public void flushBuffer() {
        getStore().flushBuffer();
    }
}
