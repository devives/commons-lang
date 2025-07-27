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

import com.devives.commons.collection.store.BufferedSerializedStore;
import com.devives.commons.collection.store.BufferedStoreList;
import com.devives.commons.collection.store.SerializedStore;
import com.devives.commons.collection.store.serializer.BinarySerializer;
import com.devives.commons.io.store.AlignedByteStore;
import com.devives.commons.io.store.ArrayByteStore;

import java.util.Objects;

/**
 * Буферизированный список сериализованных элементов.
 *
 * @param <E> тип элементов.
 */
public class BufferedSerializedList<E> extends BufferedStoreList<E> {

    public BufferedSerializedList(BinarySerializer<E> binarySerializer) {
        super(buildBufferedSerializedStore(binarySerializer));
    }

    private static <E> BufferedSerializedStore<E> buildBufferedSerializedStore(BinarySerializer<E> binarySerializer) {
        Objects.requireNonNull(binarySerializer);
        SerializedStore<E> bufferStore = new SerializedStore<>(binarySerializer, new AlignedByteStore(new ArrayByteStore(), binarySerializer.getElementSize()));
        AlignedByteStore mainStore = new AlignedByteStore(new ArrayByteStore(), binarySerializer.getElementSize());
        return new BufferedSerializedStore(mainStore, bufferStore);
    }

}