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
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteStoreTests extends TempDirectoryTestBase {

    class CommonTests {

        protected ByteStore store;
        protected ByteBuffer bb1;
        protected ByteBuffer bb2;
        protected ByteBuffer bb3;
        protected ByteBuffer bb4;

        @BeforeEach
        void setUp() throws IOException {
            bb1 = ByteBuffer.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 1});
            bb2 = ByteBuffer.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 2});
            bb3 = ByteBuffer.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 3});
            bb4 = ByteBuffer.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 4});
        }

        @Nested
        class ReadTest {

            @BeforeEach
            void setUp() throws IOException {
                store.insert(0, bb1);
                store.insert(8, bb2);
                store.insert(16, bb3);
                store.insert(24, bb4);
            }

            @Test
            public void get_8_ExpectedValue() throws Exception {
                ByteBuffer bbTest = ByteBuffer.allocate(8);
                store.read(8, bbTest);
                Assertions.assertArrayEquals(bb2.array(), bbTest.array());
            }

        }

        @Nested
        class ReadEmptyTest {

            @Test
            public void read_EmptyStore_ZeroBytesRead() throws Exception {
                ByteBuffer bbTest = ByteBuffer.allocate(16192);
                int n = store.read(0, bbTest);
                Assertions.assertEquals(0, n);
            }

        }

        @Nested
        class SizeTest {

            @BeforeEach
            void setUp() throws IOException {
                store.insert(0, bb1);
                store.insert(8, bb2);
                store.insert(16, bb3);
                store.insert(24, bb4);
            }

            @Test
            public void size_AfterSetUp_32() throws Exception {
                Assertions.assertEquals(32, store.size());
            }

            @Test
            public void size_AfterRemoveAll_0() throws Exception {
                store.removeRange(0, 32);
                Assertions.assertEquals(0, store.size());
            }

            @Test
            public void size_AfterRemoveFirst_16() throws Exception {
                store.removeRange(0, 16);
                Assertions.assertEquals(16, store.size());
            }

            @Test
            public void size_AfterRemoveLast_16() throws Exception {
                store.removeRange(16, 32);
                Assertions.assertEquals(16, store.size());
            }

            @Test
            public void size_AfterRemoveMiddle_16() throws Exception {
                store.removeRange(8, 24);
                Assertions.assertEquals(16, store.size());
            }

            @Test
            public void size_AfterReplaceRange_48() throws Exception {
                ByteBuffer bbTest = ByteBuffer.allocate(24);
                store.replaceRange(24, 32, bbTest);
                Assertions.assertEquals(48, store.size());
            }

            @Test
            public void size_AfterReplaceRange_16() throws Exception {
                ByteBuffer bbTest = ByteBuffer.allocate(8);
                store.replaceRange(8, 32, bbTest);
                Assertions.assertEquals(16, store.size());
            }


        }

        @Nested
        class AddTest {

            @Test
            public void insert_ToBegin_ExpectedFirst() throws Exception {
                store.insert(0, bb2);
                store.insert(8, bb3);
                store.insert(0, bb1);

                ByteBuffer bbTest = ByteBuffer.allocate(8);
                store.read(0, bbTest);
                Assertions.assertArrayEquals(bb1.array(), bbTest.array());
            }

            @Test
            public void insert_ToMiddle_ExpectedSecond() throws Exception {
                store.insert(0, bb1);
                store.insert(8, bb3);
                store.insert(8, bb2);

                ByteBuffer bbTest = ByteBuffer.allocate(8);
                store.read(8, bbTest);
                Assertions.assertArrayEquals(bb2.array(), bbTest.array());
            }

            @Test
            public void insert_ToMiddle_ExpectedThird() throws Exception {
                store.insert(0, bb1);
                store.insert(8, bb3);
                store.insert(8, bb2);

                ByteBuffer bbTest = ByteBuffer.allocate(8);
                store.read(16, bbTest);
                Assertions.assertArrayEquals(bb3.array(), bbTest.array());
            }

            @Test
            public void insert_ToMiddleWithBufferExpand_ExpectedLast() throws Exception {
                store.insert(0, bb1);
                store.insert(8, bb2);

                ByteBuffer bb = (ByteBuffer) ByteBuffer.allocate(24).putLong(5).putLong(6).putLong(7).flip();
                store.insert(8, bb);

                ByteBuffer bbTest = ByteBuffer.allocate(24);
                store.read(8, bbTest);
                Assertions.assertArrayEquals(bb.array(), bbTest.array());
                Assertions.assertEquals(40, store.size());
            }

            @Test
            public void insert_ToEnd_Expected() throws Exception {
                store.insert(0, bb1);
                store.insert(8, bb2);
                store.insert(16, bb3);

                ByteBuffer bbTest = ByteBuffer.allocate(8);
                store.read(16, bbTest);

                Assertions.assertArrayEquals(bb3.array(), bbTest.array());
            }

        }

        @Nested
        public class ReplaceTest {

            @BeforeEach
            void setUp() throws IOException {
                store.insert(0, bb1);
                store.insert(8, bb2);
                store.insert(16, bb3);
                store.insert(24, bb4);
            }

            @Test
            public void replace_FirstTwo_ExpectedFirstsTwo() throws Exception {
                ByteBuffer bb = (ByteBuffer) ByteBuffer.allocate(16).putLong(5).putLong(6).flip();
                store.replaceRange(0, 16, bb);

                ByteBuffer bbTest = ByteBuffer.allocate(16);
                store.read(0, bbTest);

                Assertions.assertArrayEquals(bb.array(), bbTest.array());
            }

            @Test
            public void replace_MiddleTwo_ExpectedMiddlesTwo() throws Exception {
                ByteBuffer bb = (ByteBuffer) ByteBuffer.allocate(16).putLong(5).putLong(6).flip();
                store.replaceRange(8, 24, bb);

                ByteBuffer bbTest = ByteBuffer.allocate(16);
                store.read(8, bbTest);

                Assertions.assertArrayEquals(bb.array(), bbTest.array());
            }

            @Test
            public void replace_MiddleTwo_LastNonChanged() throws Exception {
                ByteBuffer bb = (ByteBuffer) ByteBuffer.allocate(16).putLong(5).putLong(6).flip();
                store.replaceRange(8, 24, bb);

                ByteBuffer bbTest = ByteBuffer.allocate(8);
                store.read(24, bbTest);

                Assertions.assertArrayEquals(bb4.array(), bbTest.array());
            }

            @Test
            public void replace_LastTwo_ExpectedLastTwo() throws Exception {
                ByteBuffer bb = (ByteBuffer) ByteBuffer.allocate(16).putLong(5).putLong(6).flip();
                store.replaceRange(16, 32, bb);

                ByteBuffer bbTest = ByteBuffer.allocate(16);
                store.read(16, bbTest);

                Assertions.assertArrayEquals(bb.array(), bbTest.array());
            }

            @Test
            public void replace_LastTwo_ExpectedSize() throws Exception {
                ByteBuffer bb = (ByteBuffer) ByteBuffer.allocate(16).putLong(5).putLong(6).flip();
                store.replaceRange(16, 32, bb);

                Assertions.assertEquals(32, store.size());
            }

            @Test
            public void replace_ToTheEnd_ExpectedAll() throws Exception {
                ByteBuffer bb = (ByteBuffer) ByteBuffer.allocate(16).putLong(5).putLong(6).flip();
                store.replaceRange(32, 32, bb);

                ByteBuffer bbTest = ByteBuffer.allocate(16);
                store.read(32, bbTest);

                Assertions.assertArrayEquals(bb.array(), bbTest.array());
            }

            @Test
            public void replace_All_ExpectedSize() throws Exception {
                ByteBuffer bb = (ByteBuffer) ByteBuffer.allocate(16).putLong(5).putLong(6).flip();
                store.replaceRange(0, 32, bb);

                Assertions.assertEquals(16, store.size());
            }

            @Test
            public void replace_All_ExpectedAll() throws Exception {
                ByteBuffer bb = (ByteBuffer) ByteBuffer.allocate(16).putLong(5).putLong(6).flip();
                store.replaceRange(0, 32, bb);

                ByteBuffer bbTest = ByteBuffer.allocate(16);
                store.read(0, bbTest);

                Assertions.assertArrayEquals(bb.array(), bbTest.array());
            }

        }

        @Nested
        public class RemoveTest {

            @BeforeEach
            void setUp() throws IOException {
                store.insert(0, bb1);
                store.insert(8, bb2);
                store.insert(16, bb3);
                store.insert(24, bb4);
            }

            @Test
            public void remove_First_Expectations() throws Exception {
                store.removeRange(0, 8);

                Assertions.assertEquals(24, store.size());

                ByteBuffer bbTest = ByteBuffer.allocate(8);
                store.read(0, bbTest);
                Assertions.assertArrayEquals(bb2.array(), bbTest.array());
            }

            @Test
            public void remove_Middle_Expectations() throws Exception {
                store.removeRange(8, 16);

                Assertions.assertEquals(24, store.size());

                ByteBuffer bbTest = ByteBuffer.allocate(8);
                store.read(0, bbTest);
                Assertions.assertArrayEquals(bb1.array(), bbTest.array());

                bbTest = ByteBuffer.allocate(8);
                store.read(8, bbTest);
                Assertions.assertArrayEquals(bb3.array(), bbTest.array());
            }

            @Test
            public void remove_Last_Expectations() throws Exception {
                store.removeRange(24, 32);

                Assertions.assertEquals(24, store.size());

                ByteBuffer bbTest = ByteBuffer.allocate(8);
                store.read(16, bbTest);
                Assertions.assertArrayEquals(bb3.array(), bbTest.array());
            }
        }
    }

    @Nested
    class BiFileByteStoreTest extends CommonTests {
        @BeforeEach
        void setUp() throws IOException {
            File file1 = File.createTempFile("list", ".bin", tempPath.toFile());
            File file2 = File.createTempFile("list", ".bin", tempPath.toFile());
            store = new BiFileByteStore(file1, file2);
            super.setUp();
        }

        @AfterEach
        void tearDown() throws Exception {
            //Thread.sleep(60000);
            ((BiFileByteStore) store).close();
        }

    }

    @Nested
    class FileByteStoreTest extends CommonTests {
        @BeforeEach
        void setUp() throws IOException {
            File file1 = File.createTempFile("list", ".bin", tempPath.toFile());
            store = new FileByteStore(file1);
            super.setUp();
        }

        @AfterEach
        void tearDown() throws Exception {
            //Thread.sleep(60000);
            ((FileByteStore) store).close();
        }

    }


//    @Nested
//    class H2ByteStoreTest extends CommonTests {
//
//        private MVStore mvStore;
//
//        @BeforeEach
//        void setUp() throws IOException {
//            super.setUp();
//            String dbPath = tempPath.resolve("H2ByteStore.mv.db").toAbsolutePath().toString();
//            Files.deleteIfExists(Paths.get(dbPath));
//            mvStore = new MVStore.Builder()
//                    .autoCommitDisabled()
//                    .cacheConcurrency(16)
//                    .cacheSize(5)
//                    .autoCommitBufferSize(0)
//                    .fileName(dbPath)
//                    .open();
//            //store = new H2ByteStore(mvStore.openMap("keys"), mvStore.openMap("values"));
//            store = new ChunkedByteStore(new H2ChunkManager(mvStore.openMap("store-chunks"), 4096));
//        }
//
//        @AfterEach
//        void tearDown() {
//            if (mvStore != null) {
//                mvStore.closeImmediately();
//            }
//        }
//
//        @Test
//        void write_8096() throws IOException {
//            byte[] inBytes = new byte[8096];
//            Arrays.fill(inBytes, 0, 4000, (byte) 1);
//            Arrays.fill(inBytes, 4000, 8000, (byte) 2);
//            Arrays.fill(inBytes, 8000, 8096, (byte) 3);
//            store.write(0, ByteBuffer.wrap(inBytes));
//            ByteBuffer outByteBuffer = ByteBuffer.allocate(8096);
//            int read = store.read(0, outByteBuffer);
//            Assertions.assertArrayEquals(inBytes, outByteBuffer.array());
//
//            outByteBuffer = ByteBuffer.allocate(4000);
//            read = store.read(4000, outByteBuffer);
//            inBytes = new byte[4000];
//            Arrays.fill(inBytes, (byte) 2);
//            Assertions.assertArrayEquals(inBytes, outByteBuffer.array());
//        }
//    }
//
//    @Nested
//    class ChunkedH2ByteStoreTest extends CommonTests {
//
//        private MVStore mvStore;
//
//        @BeforeEach
//        void setUp() throws IOException {
//            super.setUp();
//            String dbPath = tempPath.resolve("H2ByteStore.mv.db").toAbsolutePath().toString();
//            Files.deleteIfExists(Paths.get(dbPath));
//            mvStore = new MVStore.Builder()
//                    .autoCommitDisabled()
//                    .cacheConcurrency(16)
//                    .cacheSize(5)
//                    .autoCommitBufferSize(0)
//                    .fileName(dbPath)
//                    .open();
//            store = new ChunkedByteStore(new H2ChunkManager(mvStore.openMap("byte-store-chunks"), 4096));
//
//        }
//
//        @AfterEach
//        void tearDown() {
//            if (mvStore != null) {
//                mvStore.closeImmediately();
//            }
//        }
//
//        @Test
//        void write_8096() throws IOException {
//            byte[] inBytes = new byte[8096];
//            Arrays.fill(inBytes, 0, 4000, (byte) 1);
//            Arrays.fill(inBytes, 4000, 8000, (byte) 2);
//            Arrays.fill(inBytes, 8000, 8096, (byte) 3);
//            store.write(0, ByteBuffer.wrap(inBytes));
//            ByteBuffer outByteBuffer = ByteBuffer.allocate(8096);
//            int read = store.read(0, outByteBuffer);
//            Assertions.assertArrayEquals(inBytes, outByteBuffer.array());
//
//            outByteBuffer = ByteBuffer.allocate(4000);
//            read = store.read(4000, outByteBuffer);
//            inBytes = new byte[4000];
//            Arrays.fill(inBytes, (byte) 2);
//            Assertions.assertArrayEquals(inBytes, outByteBuffer.array());
//        }
//    }

    @Nested
    class ChunkedByteStoreTest extends CommonTests {
        @BeforeEach
        void setUp() throws IOException {
            store = new ChunkedByteStore(new ArrayChunkManager(8));
            super.setUp();
        }
    }

    @Nested
    class ArrayStoreTest extends CommonTests {
        @BeforeEach
        void setUp() throws IOException {
            store = new ArrayByteStore(16);
            super.setUp();
        }
    }

    @Nested
    class HeapByteBufferStoreTest extends CommonTests {
        @BeforeEach
        void setUp() throws IOException {
            store = new ByteBufferStore(ByteBuffer.allocate(8));
            super.setUp();
        }
    }

    @Nested
    class DirectByteBufferStoreTest extends CommonTests {
        @BeforeEach
        void setUp() throws IOException {
            store = new ByteBufferStore(ByteBuffer.allocateDirect(8));
            super.setUp();
        }
    }
}
