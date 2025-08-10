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

import com.devives.commons.lang.ExceptionUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Закрываемый адаптер хранилища объектов {@link Store} к интерфейсу {@link List}.
 * Обладает коллекцией {@link AutoCloseable} объектов, которые будут закрыты при вызове {@link #close()}.
 *
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public abstract class CloseableStoreAsListAdapter<E> extends StoreAsListAdapter<E> implements Closeable {
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
    protected CloseableStoreAsListAdapter(Store<E> store, AutoCloseable... autoCloseable) {
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
    public void close() throws IOException {
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

}
