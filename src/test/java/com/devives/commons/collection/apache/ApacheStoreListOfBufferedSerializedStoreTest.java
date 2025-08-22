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
package com.devives.commons.collection.apache;

import com.devives.commons.collection.store.BufferedSerializedStore;
import com.devives.commons.collection.store.SerializedStore;
import com.devives.commons.collection.store.StoreAsListAdapter;
import com.devives.commons.collection.store.serializer.BinarySerializer;
import com.devives.commons.collection.store.serializer.ObjectBinarySerializer;
import com.devives.commons.io.store.AlignedByteStore;
import com.devives.commons.io.store.ArrayByteStore;
import com.devives.commons.io.store.FileByteStore;
import com.devives.commons.lang.ExceptionUtils;
import org.junit.jupiter.api.AfterEach;

import java.util.ArrayList;
import java.util.List;

public class ApacheStoreListOfBufferedSerializedStoreTest<E> extends ApacheAbstractListTest {

    private List<FileByteStore> fileStoreList_ = new ArrayList<>();

    @Override
    public List<E> makeObject() {
        return ExceptionUtils.passChecked(() -> {
            FileByteStore fileStore = FileByteStore.createAt(tempPath);
            fileStoreList_.add(fileStore);
            BinarySerializer<E> binarySerializer = new ObjectBinarySerializer(128);
            AlignedByteStore mainAlignedByteStore = new AlignedByteStore(fileStore, binarySerializer.getElementSize());
            SerializedStore<E> bufferSerializedStore = new SerializedStore<>(binarySerializer, new AlignedByteStore(new ArrayByteStore(), binarySerializer.getElementSize()));
            BufferedSerializedStore<E> bufferedSerializedStore = new BufferedSerializedStore<>(mainAlignedByteStore, bufferSerializedStore);
            bufferedSerializedStore.getBufferController().setBufferSize(2);
            StoreAsListAdapter<E> list = new StoreAsListAdapter(bufferedSerializedStore);
            return list;
        });
    }

    @AfterEach
    void tearDown() {
        fileStoreList_.forEach(itm -> ExceptionUtils.passChecked(() -> itm.close()));
    }
}
