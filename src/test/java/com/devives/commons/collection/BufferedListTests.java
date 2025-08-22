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
import com.devives.commons.collection.store.StoreAsListAdapter;
import com.devives.commons.collection.store.serializer.BinarySerializer;
import com.devives.commons.collection.store.serializer.LongBinarySerializer;
import com.devives.commons.io.store.*;
import com.devives.commons.lang.ExceptionUtils;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.devives.commons.collection.BufferedListUtils.addElements;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BufferedListTests extends BufferedListTestBase {

    @Nested
    class BufferedFileChunkedListTest extends BufferedListCommons {

        private FileChunkManager fileChunkManager = new FileChunkManager(1024 * 1024, tempPath, 10);

        @BeforeEach
        void setUp() throws Exception {
            BinarySerializer<Long> binarySerializer = new LongBinarySerializer();
            ChunkedByteStore chunkedByteStore = new ChunkedByteStore(fileChunkManager);
            AlignedByteStore alignedByteStore = new AlignedByteStore(chunkedByteStore, binarySerializer.getElementSize());
            SerializedStore<Long> serializedStore = new SerializedStore<>(binarySerializer, alignedByteStore);
            list = new BufferedStoreAsListAdapter<>(new BufferedObjectStore<>(serializedStore));
            ((BufferedList) list).getBufferController().setBufferSize(2048);
        }

        @AfterEach
        void tearDown() throws Exception {
            list.clear();
            fileChunkManager.close();
        }

    }

    @Nested
    class BufferedFileListTest extends BufferedListCommons {
        @Override
        protected boolean isNullsAllowed() {
            return false;
        }

        @BeforeEach
        void setUp() throws Exception {
            list = SerializedLists.ofLongs().setFileStorePath(tempPath).setBuffered().build();
        }

    }

    @Nested
            //@Disabled
    class BufferedBiFileListTest extends BufferedListCommons {
        @Override
        protected boolean isNullsAllowed() {
            return false;
        }

        @BeforeEach
        void setUp() throws Exception {
            list = SerializedLists.ofLongs().setBiFileStorePath(tempPath).setBuffered().build();
        }
    }

    @Nested
    class BufferedArraySerializedListTest extends BufferedListCommons {
        @Override
        protected boolean isNullsAllowed() {
            return false;
        }

        @BeforeEach
        void setUp() throws Exception {
            list = SerializedLists.ofLongs().setBuffered().build();
        }

    }

    @Nested
    class BufferedListOfArrayListTest extends BufferedListCommons {

        @BeforeEach
        void setUp() throws Exception {
            list = BufferedLists.of(new ArrayList<Long>()).build();
            ((BufferedList<Long>) list).getBufferController().setBufferSize(2048);
        }

    }

    @Nested
    class BufferedListOfBiFileListTest extends BufferedListCommons {

        private BiFileByteStore fileStore_;

        @BeforeEach
        void setUp() throws Exception {
            fileStore_ = ExceptionUtils.passChecked(() -> BiFileByteStore.createAt(tempPath));
            BinarySerializer<Long> binarySerializer = new LongBinarySerializer();
            AlignedByteStore alignedByteStore = new AlignedByteStore(fileStore_, binarySerializer.getElementSize());
            SerializedStore<Long> serializedStore = new SerializedStore<>(binarySerializer, alignedByteStore);
            list = new BufferedStoreAsListAdapter<>(new BufferedObjectStore<>(serializedStore));
            ((BufferedList) list).getBufferController().setBufferSize(2048);
        }

        @AfterEach
        void tearDown() throws Exception {
            fileStore_.close();
        }

    }


    @Nested
    class ByteBufferListTest extends BufferedListCommons {
        @Override
        protected boolean isNullsAllowed() {
            return false;
        }

        @BeforeEach
        void setUp() throws Exception {
            ByteStore byteStore = new ByteBufferStore(ByteBuffer::allocate, 8);
            BinarySerializer<Long> binarySerializer = new LongBinarySerializer();
            AlignedByteStore alignedByteStore = new AlignedByteStore(byteStore, binarySerializer.getElementSize());
            SerializedStore<Long> serializedStore = new SerializedStore<>(binarySerializer, alignedByteStore);
            list = new StoreAsListAdapter<>(serializedStore);
        }

    }

    @Nested
    class BufferedListOfByteBufferListTest extends BufferedListCommons {

        @BeforeEach
        void setUp() throws Exception {
            ByteStore byteStore = new ByteBufferStore(ByteBuffer::allocate, 8);
            BinarySerializer<Long> binarySerializer = new LongBinarySerializer();
            AlignedByteStore alignedByteStore = new AlignedByteStore(byteStore, binarySerializer.getElementSize());
            SerializedStore<Long> serializedStore = new SerializedStore<>(binarySerializer, alignedByteStore);
            list = new BufferedStoreAsListAdapter<>(new BufferedObjectStore<>(serializedStore));
            ((BufferedList) list).getBufferController().setBufferSize(2048);
        }

    }


    @Nested
    class BufferedListOfFileListTest extends BufferedListCommons {

        private FileByteStore fileStore_;

        @BeforeEach
        void setUp() throws Exception {
            fileStore_ = ExceptionUtils.passChecked(() -> FileByteStore.createAt(tempPath));
            BinarySerializer<Long> binarySerializer = new LongBinarySerializer();
            AlignedByteStore alignedByteStore = new AlignedByteStore(fileStore_, binarySerializer.getElementSize());
            SerializedStore<Long> serializedStore = new SerializedStore<>(binarySerializer, alignedByteStore);
            BufferedObjectStore bufferedObjectStore = new BufferedObjectStore<>(serializedStore);
            list = new BufferedStoreAsListAdapter<>(bufferedObjectStore);
            ((BufferedList) list).getBufferController().setBufferSize(2048);
        }

        @AfterEach
        void tearDown() throws Exception {
            fileStore_.close();
        }

    }

    @Nested
    class BufferedLinkedListTest extends BufferedListCommons {

        @BeforeEach
        void setUp() throws Exception {
            list = BufferedLists.of(new LinkedList<Long>()).setBufferList(new LinkedList<>()).build();
            ((BufferedList) list).getBufferController().setBufferSize(2048);
        }

    }


    @Nested()
    class BufferedArrayList_BufferSize_2_Test extends ListCommons {

        @BeforeEach
        void setUp() throws Exception {
            list = BufferedLists.of(new LinkedList<Long>()).build();
            ((BufferedList) list).getBufferController().setBufferSize(2);
        }

        @Nested
        class Size {
            @Test
            public void size_AfterAdd_Expected10() throws Exception {
                addElements(list, 5);
                addElements(list, 5);
                assertEquals(10, list.size());
            }

            @Test
            public void size_AfterFlush_ExpectedValue10() throws Exception {
                addElements(list, 5);
                addElements(list, 5);
                ((BufferedList) list).getBufferController().flushBuffer();
                assertEquals(10, list.size());
            }
        }

        @Nested
        class IndexOf extends ListCommons.IndexOf {
            @Test
            public void indexOf_ExpectedValue4() throws Exception {
                addElements(list, 5);
                addElements(list, 5);
                ((BufferedList) list).getBufferController().flushBuffer();
                assertEquals(4L, list.indexOf(4L));
            }
        }
    }

    @Nested()
    class BufferedArrayList_Range_Test {

        private CloseableBufferedList<Long> list;

        @BeforeEach
        void setUp() throws IOException {
            list = SerializedLists.ofLongs().setFileStorePath(tempPath).setBuffered().build();
            addElements(list, 20000);
        }

        @AfterEach
        void tearDown() throws Exception {
            list.close();
        }

        @Test
        public void removeRange_10000_20000_ExpectedIndex() throws Exception {
            list.subList(10000, 20000).clear();
            Assertions.assertEquals(10000, list.size());
            Assertions.assertEquals(9999L, list.get(9999));
        }

        @Test
        public void removeRange_0_10000_ExpectedIndex() throws Exception {
            list.subList(0, 10000).clear();
            Assertions.assertEquals(10000, list.size());
            Assertions.assertEquals(19999L, list.get(9999));
        }

        @Test
        public void removeRange_0_20000_ExpectedIndex() throws Exception {
            list.subList(0, 20000).clear();
            Assertions.assertEquals(0, list.size());
        }

        @Test
        public void removeRange_0_19999_ExpectedIndex() throws Exception {
            list.subList(0, 19999).clear();
            Assertions.assertEquals(1, list.size());
            Assertions.assertEquals(19999L, list.get(0));
        }

        @Test
        public void removeRange_1_19998_ExpectedIndex() throws Exception {
            list.subList(1, 19999).clear();
            Assertions.assertEquals(2, list.size());
            Assertions.assertEquals(0L, list.get(0));
            Assertions.assertEquals(19999L, list.get(1));
        }
    }

}
