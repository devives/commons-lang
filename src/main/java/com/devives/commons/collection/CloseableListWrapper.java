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

import com.devives.commons.lang.ExceptionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class CloseableListWrapper<E> extends AbstractListWrapper<E> implements CloseableList<E> {

    /**
     * Массив, объектов, которые необходимо закрыть при закрытии текущего экземпляра.
     */
    private final AutoCloseable[] autoCloseableArray_;
    /**
     * Флаг, указывающий, что экземпляр открыт.
     */
    private boolean closed_ = false;

    /**
     * @param list
     * @param autoCloseable Массив, объектов, которые необходимо закрыть при закрытии текущего экземпляра.
     */
    CloseableListWrapper(List<E> list, AutoCloseable... autoCloseable) {
        super(list);
        autoCloseableArray_ = Objects.requireNonNull(autoCloseable);
    }

    /**
     * Возвращает флаг открытости списка.
     *
     * @return true, если список открыт, иначе false.
     */
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
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
