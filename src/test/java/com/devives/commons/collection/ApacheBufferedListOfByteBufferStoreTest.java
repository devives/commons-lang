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
import com.devives.commons.collection.store.BufferedStoreList;
import com.devives.commons.collection.store.SerializedStore;
import com.devives.commons.collection.store.serializer.BinarySerializer;
import com.devives.commons.collection.store.serializer.ObjectBinarySerializer;
import com.devives.commons.io.store.AlignedByteStore;
import com.devives.commons.io.store.ByteBufferStore;
import com.devives.commons.io.store.ByteStore;
import com.devives.commons.lang.ExceptionUtils;

import java.nio.ByteBuffer;
import java.util.List;

public class ApacheBufferedListOfByteBufferStoreTest<E> extends ApacheAbstractListTest {

    @Override
    public List<E> makeObject() {
        return ExceptionUtils.passChecked(() -> {
            ByteStore fileStore = new ByteBufferStore(ByteBuffer.allocate(128));
            BinarySerializer<E> binarySerializer = new ObjectBinarySerializer(128);
            AlignedByteStore alignedByteStore = new AlignedByteStore(fileStore, binarySerializer.getElementSize());
            SerializedStore<E> serializedStore = new SerializedStore<>(binarySerializer, alignedByteStore);
            BufferedStoreList<E> list = new BufferedStoreList<E>(new BufferedObjectStore<E>(serializedStore));
            list.setBufferSize(2);
            return list;
        });
    }

}
