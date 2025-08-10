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
package com.devives.commons.io.store.bytes;

import com.devives.commons.collection.BufferedList;
import com.devives.commons.collection.SerializedLists;
import com.devives.commons.io.store.ArrayChunkManager;
import com.devives.commons.io.store.ChunkedByteStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class ArrayChunkManagerTest {

//    @BeforeEach
//    public void beforeEach() {
//    }

    @Test
    public void getChunkCount_afterRemoveLastElement_lastChunkNotRemoved() throws Exception {
        ArrayChunkManager chunkManager = new ArrayChunkManager(8);
        ChunkedByteStore chunkedByteStore = new ChunkedByteStore(chunkManager);
        List<Long> list = SerializedLists.ofLongs().setChunkedByteStore(chunkedByteStore).build();
        list.addAll(Arrays.asList(0L, 1L, 2L));
        Assertions.assertEquals(3, chunkManager.getChunkCount());
        list.remove(2);
        Assertions.assertEquals(2, chunkManager.getChunkCount());
        list.remove(1);
        Assertions.assertEquals(1, chunkManager.getChunkCount());
        list.remove(0);
        Assertions.assertEquals(0, chunkManager.getChunkCount());
    }

    @Test
    public void getChunkCount_afterRemoveMiddleElement_middleChunkRemoved() throws Exception {
        ArrayChunkManager chunkManager = new ArrayChunkManager(8);
        ChunkedByteStore chunkedByteStore = new ChunkedByteStore(chunkManager);
        List<Long> list = SerializedLists.ofLongs().setChunkedByteStore(chunkedByteStore).build();
        list.addAll(Arrays.asList(0L, 1L, 2L, 3L));
        Assertions.assertEquals(4, chunkManager.getChunkCount());
        list.remove(3);
        Assertions.assertEquals(3, chunkManager.getChunkCount());
        list.remove(2);
        Assertions.assertEquals(2, chunkManager.getChunkCount());
    }

    @Test
    public void test_0() throws Exception {
        ArrayChunkManager chunkManager = new ArrayChunkManager(16);
        ChunkedByteStore chunkedByteStore = new ChunkedByteStore(chunkManager);
        BufferedList<Long> list = SerializedLists.ofLongs().setChunkedByteStore(chunkedByteStore).setBuffered().build();
        list.getBufferController().setBufferSize(6);
        list.addAll(Arrays.asList(0L, 1L, 2L, 3L, 4L, 5L));
        list.getBufferController().flushBuffer();
        list.remove(3L);
        list.getBufferController().flushBuffer();
        Assertions.assertEquals(3, chunkManager.getChunkCount());
        list.remove(4L);
        list.getBufferController().flushBuffer();
        Assertions.assertEquals(2, chunkManager.getChunkCount());
        Assertions.assertArrayEquals(new Long[]{0L, 1L, 2L, 5L}, list.toArray(list.toArray(new Long[0])));
    }

    @Test
    public void test_1() throws Exception {
        ArrayChunkManager chunkManager = new ArrayChunkManager(24);
        ChunkedByteStore chunkedByteStore = new ChunkedByteStore(chunkManager);
        List<Long> list = SerializedLists.ofLongs().setChunkedByteStore(chunkedByteStore).build();
        list.addAll(Arrays.asList(0L, 1L, 2L, 3L, 4L, 5L));
        Assertions.assertEquals(2, chunkManager.getChunkCount());
        list.addAll(2, Arrays.asList(6L, 7L, 8L, 9L, 10L, 11L, 12L));
        Assertions.assertEquals(5, chunkManager.getChunkCount());

    }


}
