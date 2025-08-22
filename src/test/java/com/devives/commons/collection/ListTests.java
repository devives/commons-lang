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

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.devives.commons.collection.BufferedListUtils.addElements;

public class ListTests extends ListTestsBase {


    /**
     * Контрольный тест, для проверки тестов на выверенной структуре данных.
     */
    @Nested
    class ArrayListTest extends ListCommons {
        @BeforeEach
        void setUp() throws IOException {
            list = new ArrayList<>();
        }
    }


    /**
     * Контрольный тест, для проверки тестов на выверенной структуре данных.
     */
    @Nested
    public class LinkedListTest extends ListCommons {
        public LinkedListTest() {
            list = new LinkedList<>();
        }
    }

    @Nested
    public class FileListTest extends ListCommons {

        @Override
        protected boolean isNullsAllowed() {
            return false;
        }

        public FileListTest() throws IOException {
            list = SerializedLists.ofLongs().setFileStorePath(tempPath).build();
        }

        @AfterEach
        void tearDown() throws Exception {
            ((CloseableList<Long>) list).close();
        }
    }

    @Nested
    public class ChunkedSerializedArrayListTest extends ListCommons {

        @Override
        protected boolean isNullsAllowed() {
            return false;
        }

        public ChunkedSerializedArrayListTest() throws IOException {
            list = SerializedLists.ofLongs().setArrayChunkedByteStore(1024 * 1024)
                    .build();
        }

    }


    @Nested
    public class OffHeapChunkedListTest extends ListCommons {

        @Override
        protected boolean isNullsAllowed() {
            return false;
        }

        public OffHeapChunkedListTest() throws IOException {
            list = SerializedLists.ofLongs()
                    .setOffHeapChunkedByteStore(1024 * 1024)
                    .build();
        }

    }

    @Nested
    public class HeapChunkedListTest extends ListCommons {

        @Override
        protected boolean isNullsAllowed() {
            return false;
        }

        public HeapChunkedListTest() throws IOException {
            list = SerializedLists.ofLongs()
                    .setHeapChunkedByteStore(1024 * 1024)
                    .build();
        }

    }


    @Nested
    class ChunkedFileListTest extends ListCommons {

        @Override
        protected boolean isNullsAllowed() {
            return false;
        }

        @BeforeEach
        void setUp() throws Exception {
            list = SerializedLists.ofLongs().setFileChunkedByteStore(1024 * 1024, tempPath, 10).build();
        }

        @AfterEach
        void tearDown() throws Exception {
            list.clear();
            ((CloseableList) list).close();
        }

    }

    @Nested()
    class BufferedArrayList_Range_Test {

        private CloseableBufferedList<Long> list;

        @BeforeEach
        void setUp() throws IOException {
            list = (CloseableBufferedList<Long>) SerializedLists.ofLongs().setFileStorePath(tempPath).setBuffered().build();
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
