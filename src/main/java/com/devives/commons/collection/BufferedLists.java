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

import com.devives.commons.collection.store.BufferedObjectStore;
import com.devives.commons.collection.store.BufferedStoreAsListAdapter;
import com.devives.commons.collection.store.ObjectStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Точка входа для создания буферизированного списка элементов.
 * <p>
 * В качестве буфера и основного хранилища элементов используются списки {@link List}.
 * <p>
 * Структура обеспечивает ускорение массовых вставок и удалений элементов в начале/середине большого {@link java.util.ArrayList}.
 *
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public final class BufferedLists {

    private BufferedLists() {
    }

    public static <E> Builder<E> of(List<E> list) {
        return new Builder<>(list);
    }

    public static final class Builder<E> {

        private List<E> list_;
        private List<E> bufferList_;

        public Builder(List<E> list) {
            list_ = Objects.requireNonNull(list, "list");
        }

        public Builder<E> setBufferList(List<E> bufferList) {
            bufferList_ = Objects.requireNonNull(bufferList, "bufferList");
            return this;
        }

        public BufferedList<E> build() {
            bufferList_ = Optional.ofNullable(bufferList_).orElseGet(ArrayList::new);
            return new BufferedStoreAsListAdapter(new BufferedObjectStore<E>(new ObjectStore<E>(list_), new ObjectStore<E>(bufferList_)));
        }

    }
}