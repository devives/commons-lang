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


import com.devives.commons.TempDirectoryTestBase;
import com.devives.commons.collection.store.serializer.LongBinarySerializer;
import com.devives.commons.collection.store.serializer.ObjectBinarySerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BiFileListTest<E> extends TempDirectoryTestBase {
//
//    @Test
//    public void test() throws Exception {
//        int i;
//        List<Object> list = SerializedLists.of(new ObjectSerializer<>(128), tempPath);
//        List<Object> testData = new ArrayList<>(Arrays.asList(getFullNonNullElements()));
//        list.addAll(testData);
//        int max = testData.size();
//        Object firstTestElement = getOtherNonNullElements()[0];
//        for (int j = 0; j < max; j++) {
//            testData.add(j, firstTestElement);
//            list.add(j, firstTestElement);
//
//            for (int k = 0; k < max; k++) {
//                Assertions.assertEquals(testData.get(k), list.get(k));
//            }
//            i = 0;
//            for (Object itm : list) {
//                Assertions.assertEquals(testData.get(i++), itm);
//            }
//        }
//    }
//
//    public E[] getFullNonNullElements() {
//        //return (E[]) (new Object[]{"Zero", "One", 2, "Three", 4, "One", (double) 5.0F, 6.0F, "Seven", "Eight", "Nine", 10, Short.valueOf((short) 11), 12L, "Thirteen", "14", "15", 16});
//        return (E[]) (new Object[]{"Zero", "One", 2L, "Three"});
//    }
//
//    public E[] getOtherNonNullElements() {
//        return (E[]) (new Object[]{0});
//    }
//
//    @Test
//    public void test2() throws Exception {
//        List<Long> list = SerializedLists.of(new LongBinarySerializer(), tempPath);
//        List<Long> testData = new ArrayList<>(Arrays.asList(111L, 222L, 333L));
//        list.addAll(testData);
//        list.add(0, 100500L);
//        Assertions.assertEquals(100500L, list.get(0));
//        Assertions.assertEquals(111L, list.get(1));
//        Assertions.assertEquals(222L, list.get(2));
//    }
//
//    @Test
//    public void test3() throws Exception {
//        List<Object> list = SerializedLists.of(new ObjectSerializer<>(128), tempPath);
//        List<Object> testData = new ArrayList<>(Arrays.asList(getFullNonNullElements()));
//        list.addAll(testData);
//        list.add(0, 100500L);
//        Assertions.assertEquals(100500L, list.get(0));
//        Assertions.assertEquals("Zero", list.get(1));
//        Assertions.assertEquals("One", list.get(2));
//    }

    @Test
    public void test4() throws Exception {
        List<Object> list = SerializedLists.of(new ObjectBinarySerializer(100)).setBiFileStorePath(tempPath).build();
        List<Object> testData = new ArrayList<>(Arrays.asList(111L, 222L, 333L));
        list.addAll(testData);
        Assertions.assertEquals(111L, list.get(0));
        Assertions.assertEquals(222L, list.get(1));
        Assertions.assertEquals(333L, list.get(2));
        list.add(0, 100500L);
        Assertions.assertEquals(4, list.size());
        Assertions.assertEquals(100500L, list.get(0));
        Assertions.assertEquals(111L, list.get(1));
        Assertions.assertEquals(222L, list.get(2));
        Assertions.assertEquals(333L, list.get(3));
    }

    @Test
    public void test5() throws Exception {
        List<Long> list = SerializedLists.of(new LongBinarySerializer()).setBiFileStorePath(tempPath).build();
        List<Long> testData = new ArrayList<>(Arrays.asList(111L, 222L, 333L));
        list.addAll(testData);
        Assertions.assertEquals(111L, list.get(0));
        Assertions.assertEquals(222L, list.get(1));
        Assertions.assertEquals(333L, list.get(2));
        list.add(0, 100500L);
        Assertions.assertEquals(4, list.size());
        Assertions.assertEquals(100500L, list.get(0));
        Assertions.assertEquals(111L, list.get(1));
        Assertions.assertEquals(222L, list.get(2));
        Assertions.assertEquals(333L, list.get(3));
    }

}
