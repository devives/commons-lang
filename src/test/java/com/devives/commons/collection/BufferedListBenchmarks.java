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
import com.devives.commons.collection.store.SerializedStore;
import com.devives.commons.collection.store.serializer.BinarySerializer;
import com.devives.commons.collection.store.serializer.LongBinarySerializer;
import com.devives.commons.io.store.AlignedByteStore;
import com.devives.commons.io.store.BiFileByteStore;
import com.devives.commons.io.store.FileByteStore;
import com.devives.commons.lang.ExceptionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Сравнение реализаций буферизированных списков.
 */
//@Disabled("For manual runnig.")
public class BufferedListBenchmarks extends BufferedListBenchmarksBase {

    @BeforeAll
    static void beforeAll() throws InterruptedException {
        //Thread.sleep(5000);
    }

    @Nested
    public class BufferedBiFileListTest extends Commons {

        public BufferedBiFileListTest() throws Exception {
            list = SerializedLists.ofLongs().setBiFileStorePath(tempPath).setBuffered().build();
        }

        @AfterEach
        void tearDown() throws Exception {
            ((AutoCloseable) list).close();
        }
    }

    @Nested
    public class BufferedFileListTest extends Commons {

        public BufferedFileListTest() throws Exception {
            list = SerializedLists.ofLongs().setFileStorePath(tempPath).setBuffered().build();
            BufferedListUtils.setBufferSize(list, 32000);
        }

        @AfterEach
        void tearDown() throws Exception {
            ((AutoCloseable) list).close();
        }
    }

    @Nested
    @Disabled
    public class BufferedListOfFileListTest extends Commons {

        private CloseableList<Long> fileList_;

        public BufferedListOfFileListTest() throws Exception {
            fileList_ = SerializedLists.ofLongs().setFileStorePath(tempPath).build();
            list = BufferedLists.of(fileList_).build();
        }

        @AfterEach
        void tearDown() throws Exception {
            fileList_.close();
        }
    }

    @Nested
    @Disabled
    public class BufferedListOfBiFileListTest extends Commons {
        private final CloseableList<Long> fileList_;

        public BufferedListOfBiFileListTest() {
            fileList_ = ExceptionUtils.passChecked(() -> SerializedLists.ofLongs().setBiFileStorePath(tempPath).build());
            list = BufferedLists.of(fileList_).build();
        }

        @AfterEach
        void tearDown() throws Exception {
            fileList_.close();
        }
    }

    @Nested
    @Disabled("Медленно из-за отсутствия буфера записи в файл")
    public class BufferedListOfBiFileStoreTest extends Commons {
        private final BiFileByteStore fileStore_;

        public BufferedListOfBiFileStoreTest() {
            fileStore_ = ExceptionUtils.passChecked(() -> BiFileByteStore.createAt(tempPath));
            BinarySerializer<Long> binarySerializer = new LongBinarySerializer();
            AlignedByteStore alignedByteStore = new AlignedByteStore(fileStore_, binarySerializer.getElementSize());
            SerializedStore<Long> serializedStore = new SerializedStore<>(binarySerializer, alignedByteStore);
            list = new BufferedStoreAsListAdapter<>(new BufferedObjectStore<>(serializedStore));
        }

        @AfterEach
        void tearDown() throws Exception {
            fileStore_.close();
        }
    }

    @Nested
    @Disabled("Медленно из-за отсутствия буфера записи в файл")
    public class BufferedListOfFileStoreTest extends Commons {
        private final FileByteStore fileStore_;

        public BufferedListOfFileStoreTest() {
            fileStore_ = ExceptionUtils.passChecked(() -> FileByteStore.createAt(tempPath));
            BinarySerializer<Long> binarySerializer = new LongBinarySerializer();
            AlignedByteStore alignedByteStore = new AlignedByteStore(fileStore_, binarySerializer.getElementSize());
            SerializedStore<Long> serializedStore = new SerializedStore<>(binarySerializer, alignedByteStore);
            list = new BufferedStoreAsListAdapter<>(new BufferedObjectStore<>(serializedStore));
        }

        @AfterEach
        void tearDown() throws Exception {
            fileStore_.close();
        }
    }

    @Nested
    public class BufferedOffHeapChunkedListTest extends Commons {

        public BufferedOffHeapChunkedListTest() throws IOException {
            list = SerializedLists.ofLongs().setOffHeapChunkedByteStore(1024 * 1024).setBuffered().build();
        }
    }

    @Nested
    public class BufferedHeapChunkedListTest extends Commons {

        public BufferedHeapChunkedListTest() throws IOException {
            list = SerializedLists.ofLongs().setHeapChunkedByteStore(1024 * 1024).setBuffered().build();
        }
    }

    @Nested
    public class BufferedArrayChunkedListTest extends Commons {

        public BufferedArrayChunkedListTest() {
            list = SerializedLists.ofLongs().setBuffered().build();
        }
    }


    @Nested
    class BufferedFileChunkedListTest extends Commons {

        public BufferedFileChunkedListTest() throws Exception {
            list = SerializedLists.ofLongs()
                    .setFileChunkedByteStore(256 * 1024, tempPath, 10)
                    .setBuffered()
                    .build();
        }

        @AfterEach
        void tearDown() throws Exception {
            list.clear();
            ((CloseableList) list).close();
        }

    }

    @Nested
    public class BufferedSerializedArrayListTest extends Commons {
        public BufferedSerializedArrayListTest() {
            list = SerializedLists.ofLongs().setBuffered().build();
        }
    }

    @Nested
    public class BufferedListOfArrayListTest extends Commons {
        public BufferedListOfArrayListTest() {
            list = BufferedLists.of(new ArrayList<Long>()).build();
        }
    }

    @Nested
    @Disabled("Медленно из-за отсутствия буфера")
    public class ArrayListTest extends Commons {
        public ArrayListTest() {
            list = new ArrayList<>();
        }
    }


}
