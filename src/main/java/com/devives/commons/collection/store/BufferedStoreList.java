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

import java.util.List;

/**
 * Адаптер буферизированного хранилища объектов {@link BufferedStore} к интерфейсу {@link List}.
 *
 * @param <E> тип элемента списка.
 */
public class BufferedStoreList<E> extends StoreList<E> implements BufferController {
    /**
     * Буферизированное хранилище элементов.
     */
    private final BufferedStore<E> bufferedStore_;

    /**
     *
     * @param bufferedStore Хранилище элементов.
     */
    public BufferedStoreList(BufferedStore<E> bufferedStore) {
        super(bufferedStore);
        bufferedStore_ = bufferedStore;
    }

    @Override
    public int getBufferSize() {
        return bufferedStore_.getBufferSize();
    }

    @Override
    public void setBufferSize(int size) {
        bufferedStore_.setBufferSize(size);
    }

    @Override
    public int getBufferMaxSize() {
        return bufferedStore_.getBufferMaxSize();
    }

    @Override
    public void setBufferMaxSize(int size) {
        bufferedStore_.setBufferMaxSize(size);
    }

    @Override
    public void flushBuffer() {
        bufferedStore_.flushBuffer();
    }

}
