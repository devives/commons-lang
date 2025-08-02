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

import com.devives.commons.collection.store.CloseableStoreAsListAdapter;
import com.devives.commons.collection.store.Store;
import com.devives.commons.io.store.FileByteStore;

/**
 * Список элементов, в сериализованном виде, хранимых в файле на диске.
 * <p>
 * When data is inserted or deleted at the beginning or middle of the store, the tail of the data is shifted within the file.
 * <pre>{@code
 * Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"));
 * list = FileList.ofLongs().setFileByteStore(tempPath).build();
 * }</pre>
 * @see FileByteStore
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
public class FileList<E> extends CloseableStoreAsListAdapter<E> {

    /**
     * @param store
     * @param autoCloseable Массив, объектов, которые необходимо закрыть при закрытии текущего экземпляра.
     */
    FileList(Store<E> store, AutoCloseable... autoCloseable) {
        super(store, autoCloseable);
    }
}
