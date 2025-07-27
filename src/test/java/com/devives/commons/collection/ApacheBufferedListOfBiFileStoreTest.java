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
import com.devives.commons.collection.store.SerializedStore;
import com.devives.commons.collection.store.StoreList;
import com.devives.commons.collection.store.serializer.BinarySerializer;
import com.devives.commons.collection.store.serializer.ObjectBinarySerializer;
import com.devives.commons.io.store.AlignedByteStore;
import com.devives.commons.io.store.BiFileByteStore;
import com.devives.commons.lang.ExceptionUtils;
import org.junit.jupiter.api.AfterEach;

import java.util.ArrayList;
import java.util.List;

public class ApacheBufferedListOfBiFileStoreTest<E> extends ApacheAbstractListTest {

    private List<BiFileByteStore> fileStoreList_ = new ArrayList<>();

    @Override
    public List<E> makeObject() {
        return ExceptionUtils.passChecked(() -> {
            BiFileByteStore fileStore = BiFileByteStore.createAt(tempPath);
            fileStoreList_.add(fileStore);
            BinarySerializer<E> binarySerializer = new ObjectBinarySerializer(128);
            AlignedByteStore alignedByteStore = new AlignedByteStore(fileStore, binarySerializer.getElementSize());
            SerializedStore<E> serializedStore = new SerializedStore<>(binarySerializer, alignedByteStore);
            BufferedList<E> list = new BufferedList(new StoreList(new BufferedObjectStore(serializedStore)));
            list.setBufferSize(2);
            return list;
        });
    }

    @AfterEach
    void tearDown() {
        fileStoreList_.forEach(itm -> ExceptionUtils.passChecked(() -> itm.close()));
    }

}
