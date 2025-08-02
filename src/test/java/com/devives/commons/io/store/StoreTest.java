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
package com.devives.commons.io.store;

import com.devives.commons.TempDirectoryTestBase;
import com.devives.commons.collection.BufferController;
import com.devives.commons.collection.store.BufferedSerializedStore;
import com.devives.commons.collection.store.SerializedStore;
import com.devives.commons.collection.store.Store;
import com.devives.commons.collection.store.serializer.BinarySerializer;
import com.devives.commons.collection.store.serializer.LongBinarySerializer;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoreTest extends TempDirectoryTestBase {


    class CommonTests {

        protected Store<Long> store;

        @Nested
        class ReadTest {

            @BeforeEach
            void setUp() throws IOException {
                store.insert(0, 1L);
                store.insert(1, 2L);
                store.insert(2, 3L);
                store.insert(3, 4L);
            }

            @Test
            public void get_8_ExpectedValue() throws Exception {
                Assertions.assertEquals(2, store.get(1));
            }

        }

        @Nested
        class SizeTest {

            @BeforeEach
            void setUp() throws IOException {
                store.insert(0, 1L);
                store.insert(1, 2L);
                store.insert(2, 3L);
                store.insert(3, 4L);
            }

            @Test
            public void size_AfterSetUp_4() throws Exception {
                Assertions.assertEquals(4, store.size());
            }

            @Test
            public void size_AfterRemoveAll_0() throws Exception {
                store.removeRange(0, 4);
                Assertions.assertEquals(0, store.size());
            }

            @Test
            public void size_AfterRemoveFirst_2() throws Exception {
                store.removeRange(0, 2);
                Assertions.assertEquals(2, store.size());
            }

            @Test
            public void size_AfterRemoveLast_2() throws Exception {
                store.removeRange(2, 4);
                Assertions.assertEquals(2, store.size());
            }

            @Test
            public void size_AfterRemoveMiddle_2() throws Exception {
                store.removeRange(1, 3);
                Assertions.assertEquals(2, store.size());
            }

        }

        @Nested
        class AddTest {

            @Test
            public void add_ToBegin_ExpectedFirst() throws Exception {
                store.clear();
                store.add(2L);
                store.add(3L);
                store.insert(0, 1L);

                Assertions.assertEquals(1L, store.get(0));
                Assertions.assertEquals(2L, store.get(1));
                Assertions.assertEquals(3L, store.get(2));
            }

            @Test
            public void add_ToMiddle_ExpectedSecond() throws Exception {
                store.insert(0, 1L);
                store.insert(1, 3L);
                store.insert(1, 2L);

                Assertions.assertEquals(2L, store.get(1));
            }

            @Test
            public void add_ToMiddle_ExpectedThird() throws Exception {
                store.insert(0, 1L);
                store.insert(1, 3L);
                store.insert(1, 2L);

                Assertions.assertEquals(3L, store.get(2));
            }

            @Test
            public void add_ToMiddleWithBufferExpand_ExpectedLast() throws Exception {
                store.insert(0, 1L);
                store.insert(1, 2L);

                ByteBuffer bb = (ByteBuffer) ByteBuffer.allocate(24).putLong(5).putLong(6).putLong(7).flip();
                store.insert(1, 5L);
                store.insert(2, 6L);
                store.insert(3, 7L);

                Assertions.assertEquals(5L, store.get(1));
                Assertions.assertEquals(6L, store.get(2));
                Assertions.assertEquals(7L, store.get(3));
                Assertions.assertEquals(5, store.size());
            }

            @Test
            public void add_ToEnd_Expected() throws Exception {
                store.insert(0, 1L);
                store.insert(1, 2L);
                store.insert(2, 3L);

                Assertions.assertEquals(3L, store.get(2));
            }
        }

        @Nested
        public class RemoveTest {

            @BeforeEach
            void setUp() throws IOException {
                store.insert(0, 1L);
                store.insert(1, 2L);
                store.insert(2, 3L);
                store.insert(3, 4L);
            }

            @Test
            public void remove_First_Expectations() throws Exception {
                store.removeRange(0, 1);

                Assertions.assertAll(
                        () -> Assertions.assertEquals(3, store.size()),
                        () -> Assertions.assertEquals(2L, store.get(0))
                );
            }

            @Test
            public void remove_Middle_Expectations() throws Exception {
                store.removeRange(1, 2);
                Assertions.assertAll(
                        () -> Assertions.assertEquals(3, store.size()),
                        () -> Assertions.assertEquals(1L, store.get(0)),
                        () -> Assertions.assertEquals(3L, store.get(1))
                );
            }

            @Test
            public void remove_Last_Expectations() throws Exception {
                store.removeRange(3, 4);
                Assertions.assertAll(
                        () -> Assertions.assertEquals(3, store.size()),
                        () -> Assertions.assertEquals(3L, store.get(2))
                );
            }
        }

        @Nested
        public class ReplaceTest {


            @BeforeEach
            void setUp() throws IOException {
                for (int i = 0; i < 10000; i++) {
                    store.add((long) i);
                }
            }

            @Test
            public void set_SizeNotChangedAndExpectedValue() throws Exception {
                int expectedSize = store.size();
                long expectedValue = store.size();
                store.update(500, expectedValue);
                Assertions.assertAll(
                        () -> Assertions.assertEquals(expectedSize, store.size()),
                        () -> Assertions.assertEquals(expectedValue, store.get(500))
                );
            }

            @Test
            public void replaceAll_ByEmptyList_Expectations() throws Exception {
                List<Long> testData = new ArrayList<>();
                store.replaceRange(0, 10000, testData);
                Assertions.assertEquals(0, store.size());
            }

            @Test
            public void replaceRange_AllBy1024_Expectations() throws Exception {
                List<Long> testData = new ArrayList<>();
                for (int i = 0; i < 1024; i++) {
                    testData.add((long) i);
                }
                store.replaceRange(0, 10000, testData);
                Assertions.assertAll(
                        () -> Assertions.assertEquals(1024, store.size()),
                        () -> Assertions.assertEquals(0L, store.get(0)),
                        () -> Assertions.assertEquals(1023L, store.get(1023))
                );
            }

            @Test
            public void replaceRange_AllBy2000_Expectations() throws Exception {
                List<Long> testData = new ArrayList<>();
                for (int i = 0; i < 2000; i++) {
                    testData.add((long) i);
                }
                store.replaceRange(0, 10000, testData);
                Assertions.assertAll(
                        () -> Assertions.assertEquals(2000, store.size()),
                        () -> Assertions.assertEquals(0L, store.get(0)),
                        () -> Assertions.assertEquals(1023L, store.get(1023))
                );
            }

            @Test
            public void replaceRange_AllBy10000_Expectations() throws Exception {
                List<Long> testData = new ArrayList<>();
                for (int i = 10000; i < 20000; i++) {
                    testData.add((long) i);
                }
                store.replaceRange(0, 10000, testData);
                Assertions.assertAll(
                        () -> Assertions.assertEquals(10000, store.size()),
                        () -> Assertions.assertEquals(10000L, store.get(0)),
                        () -> Assertions.assertEquals(19999L, store.get(9999))
                );
            }

            @Test
            public void replaceRange_1000By10000_Expectations() throws Exception {
                List<Long> testData = new ArrayList<>();
                for (int i = 10000; i < 20000; i++) {
                    testData.add((long) i);
                }
                store.replaceRange(1000, 2000, testData);
                Assertions.assertAll(
                        () -> Assertions.assertEquals(19000, store.size()),
                        () -> Assertions.assertEquals(10000L, store.get(1000)),
                        () -> Assertions.assertEquals(19999L, store.get(10999))
                );
            }

        }
    }

    @Nested
    class SerializedFileStoreTest extends CommonTests {

        private FileByteStore fileByteStore_;

        @BeforeEach
        void setUp() throws IOException {
            File file = File.createTempFile("list", ".bin", tempPath.toFile());
            BinarySerializer binarySerializer = new LongBinarySerializer();
            fileByteStore_ = new FileByteStore(file);
            AlignedByteStore alignedByteStore = new AlignedByteStore(fileByteStore_, binarySerializer.getElementSize());
            store = new SerializedStore<Long>(binarySerializer, alignedByteStore);
        }

        @AfterEach
        void tearDown() throws Exception {
            //Thread.sleep(60000);
            fileByteStore_.close();
        }
    }


    @Nested
    class SerializedArrayStoreTest extends CommonTests {

        @BeforeEach
        void setUp() throws IOException {
            BinarySerializer binarySerializer = new LongBinarySerializer();
            ArrayByteStore byteStore = new ArrayByteStore();
            AlignedByteStore alignedByteStore = new AlignedByteStore(byteStore, binarySerializer.getElementSize());
            store = new SerializedStore<Long>(binarySerializer, alignedByteStore);
        }

    }

    @Nested
    class BufferedSerializedStoreTest extends CommonTests {

        @BeforeEach
        void setUp() throws IOException {
            BinarySerializer binarySerializer = new LongBinarySerializer();
            SerializedStore<Long> serializedStore = new SerializedStore<>(binarySerializer, new AlignedByteStore(new ArrayByteStore(), binarySerializer.getElementSize()));
            AlignedByteStore alignedByteStore = new AlignedByteStore(new ArrayByteStore(), binarySerializer.getElementSize());
            store = new BufferedSerializedStore<>(alignedByteStore, serializedStore);
            ((BufferController) store).setBufferSize(2);
        }

        @Nested
        class BufferOperations {

            @BeforeEach
            void setUp() throws IOException {
                for (int i = 0; i < 10; i++) {
                    store.add(Long.valueOf(i));
                }
                ((BufferController) store).flushBuffer();
            }

            @Test
            void size_Expected10() throws IOException {
                Assertions.assertEquals(10, store.size());
            }

            @Test
            void replaceRange_SizeIncrease() throws IOException {
                store.replaceRange(5, 7, Arrays.asList(55L, 56L, 57L, 58L));
                Assertions.assertEquals(12, store.size());
                Assertions.assertEquals(55L, store.get(5));
                Assertions.assertEquals(58L, store.get(8));
            }

            @Test
            void replaceRange_SizeNonChanged() throws IOException {
                store.replaceRange(5, 9, Arrays.asList(55L, 56L, 57L, 58L));
                Assertions.assertEquals(10, store.size());
                Assertions.assertEquals(55L, store.get(5));
                Assertions.assertEquals(58L, store.get(8));
            }

            @Test
            void replaceRange_SizeDecrease() throws IOException {
                store.replaceRange(5, 9, Arrays.asList(55L, 56L));
                Assertions.assertEquals(8, store.size());
                Assertions.assertEquals(55L, store.get(5));
                Assertions.assertEquals(56L, store.get(6));
            }


        }
    }

}
