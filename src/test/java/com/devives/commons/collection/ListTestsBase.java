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
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.*;

import static com.devives.commons.collection.BufferedListUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class ListTestsBase extends TempDirectoryTestBase {

    protected class ListCommons {

        protected List<Long> list;

        @Nested
        class Modifications {

            @Test
            void add_Twice_10_000_ExpectedSize() throws IOException {
                addElements(list, 10_000);
                addElements(list, 10_000);
                Assertions.assertEquals(20_000, list.size());
            }

            @Nested
            class OneElementTest {

                @BeforeEach
                void setUp() throws IOException {
                    list.add(1L);
                }

                @Test
                public void remove_CorrectIndex_ExpectedValue() throws Exception {
                    assertEquals(1L, list.remove(0));
                }

                @Test
                public void remove_IncorrectIndex_ExceptionThrown() throws Exception {
                    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
                }

                @Test
                public void removeAll__ExpectedSize() throws Exception {
                    list.addAll(Arrays.asList(2L, 3L, 4L, 5L, 1L, 2L, 3L, 4L, 5L));
                    list.removeAll(Arrays.asList(3L, 5L, 8L));
                    Assertions.assertEquals(6, list.size());
                }

                @Test
                public void retainAll__ExpectedSize() throws Exception {
                    list.addAll(Arrays.asList(2L, 3L, 4L, 5L, 1L, 2L, 3L, 4L, 5L));
                    list.retainAll(Arrays.asList(3L, 5L));
                    Assertions.assertEquals(4, list.size());
                }

                @Test
                public void contains_ExistentValue_True() throws Exception {
                    assertTrue(list.contains(1L));
                }

                @Test
                public void contains_NonExistentValue_False() throws Exception {
                    assertFalse(list.contains(2L));
                }

                @Test
                public void add_BeforeFirst_ExpectedValue() throws Exception {
                    list.add(0, 2L);
                    assertEquals(2L, list.get(0));
                }

                @Test
                public void add_AfterLastByIndex_ExpectedValue() throws Exception {
                    list.add(1, 2L);
                    assertEquals(2L, list.get(1));
                }

                @Test
                public void add_AddToTheEnd_ExpectedValue() throws Exception {
                    list.add(1, 2L);
                    assertEquals(2L, list.get(1));
                }

                @Test
                public void add_LastElement_IndexOutOfBoundsException() throws Exception {
                    assertThrows(java.lang.IndexOutOfBoundsException.class, () -> list.add(2, 2L));
                }

                @Test
                public void add_Null_NullPointerException() throws Exception {
                    if (isNullsAllowed()) {
                        list.add(null);
                    } else {
                        assertThrows(java.lang.Exception.class, () -> list.add(null));
                    }
                }

                @Test
                public void addAll_NullInCollection_NullPointerException() throws Exception {
                    if (isNullsAllowed()) {
                        list.addAll(Arrays.asList(2L, null));
                    } else {
                        assertThrows(java.lang.Exception.class, () -> list.addAll(Arrays.asList(2L, null)));
                    }
                }

                @Test
                public void addAll_NullInCollectionByIndex_NullPointerException() throws Exception {
                    if (isNullsAllowed()) {
                        list.addAll(0, Arrays.asList(2L, null));
                    } else {
                        assertThrows(java.lang.Exception.class, () -> list.addAll(0, Arrays.asList(2L, null)));
                    }
                }

                @Test
                public void coModification_remove_ThrowConcurrentModificationException() throws Exception {
                    Iterator<Long> iterator = list.iterator();
                    if (list instanceof AbstractSequentialList) {
                        while (iterator.hasNext()) {
                            iterator.next();
                            list.remove(0);
                        }
                    } else {
                        Assertions.assertThrows(ConcurrentModificationException.class, () -> {
                            while (iterator.hasNext()) {
                                iterator.next();
                                list.remove(0);
                            }
                        });
                    }
                }

            }

            @Nested
            class TwoElementsTest {

                @BeforeEach
                void setUp() throws IOException {
                    list.add(1L);
                    list.add(2L);
                }

                @Test
                public void add_MiddleElement_ExpectedValue() throws Exception {
                    list.add(1, 3L);
                    assertEquals(3L, list.get(1));
                }

                @Test
                public void addAll_EmptyCollection_ReturnFalse() throws Exception {
                    assertFalse(list.addAll(0, Collections.emptyList()));
                }

                @Test
                public void addAll_NonEmptyCollection_ReturnTrue() throws Exception {
                    assertTrue(list.addAll(0, Arrays.asList(3L, 4L)));
                }

                @Test
                public void addAll_BeforeFirst_ExpectedOrder() throws Exception {
                    list.addAll(0, Arrays.asList(3L, 4L));
                    Long[] expected = new Long[]{3L, 4L, 1L, 2L};
                    Long[] actual = new Long[]{list.get(0), list.get(1), list.get(2), list.get(3)};
                    assertArrayEquals(expected, actual);
                }

                @Test
                public void addAll_BeforeLast_ExpectedOrder() throws Exception {
                    list.addAll(1, Arrays.asList(3L, 4L));
                    Long[] expected = new Long[]{1L, 3L, 4L, 2L};
                    Long[] actual = new Long[]{list.get(0), list.get(1), list.get(2), list.get(3)};
                    assertArrayEquals(expected, actual);
                }

                @Test
                public void addAll_AfterLast_ExpectedOrder() throws Exception {
                    list.addAll(2, Arrays.asList(3L, 4L));
                    Long[] expected = new Long[]{1L, 2L, 3L, 4L};
                    Long[] actual = new Long[]{list.get(0), list.get(1), list.get(2), list.get(3)};
                    assertArrayEquals(expected, actual);
                }

                @Test
                public void addAll_AfterLastByIndex_ExpectedOrder() throws Exception {
                    list.addAll(Arrays.asList(3L, 4L));
                    Long[] expected = new Long[]{1L, 2L, 3L, 4L};
                    Long[] actual = new Long[]{list.get(0), list.get(1), list.get(2), list.get(3)};
                    assertArrayEquals(expected, actual);
                }

                @Test
                public void coModification_remove_NoException() throws Exception {
                    Iterator<Long> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        iterator.next();
                        list.remove(0);
                    }
                }

            }

            @Nested
            class ThreeElementsTest {

                @BeforeEach
                void setUp() throws IOException {
                    list.add(1L);
                    list.add(2L);
                    list.add(3L);
                }

                @Test
                public void remove_FirstElement_ExpectedValue() throws Exception {
                    assertEquals(1, list.remove(0));
                }

                @Test
                public void remove_MiddleElement_ExpectedValue() throws Exception {
                    assertEquals(2, list.remove(1));
                }

                @Test
                public void remove_LastElement_ExpectedValue() throws Exception {
                    assertEquals(3, list.remove(2));
                }

                @Test
                public void size_AfterRemoveFirstElement_ExpectedSize() throws Exception {
                    list.remove(0);
                    assertEquals(2, list.size());
                }

                @Test
                public void size_AfterRemoveMiddleElement_ExpectedSize() throws Exception {
                    list.remove(1);
                    assertEquals(2, list.size());
                }

                @Test
                public void size_AfterRemoveLastElement_ExpectedSize() throws Exception {
                    list.remove(2);
                    assertEquals(2, list.size());
                }

                @Test
                public void get_LastAfterRemoveFirst_ExpectedValue() throws Exception {
                    list.remove(0);
                    assertEquals(3, list.get(1));
                }

                @Test
                public void get_LastAfterRemoveMiddle_ExpectedValue() throws Exception {
                    list.remove(1);
                    assertEquals(3, list.get(1));
                }

                @Test
                public void get_LastAfterRemoveLast_ExpectedValue() throws Exception {
                    list.remove(2);
                    assertEquals(2, list.get(1));
                }

                @Test
                public void set__ExpectedValue() throws Exception {
                    list.set(1, 100501L);
                    assertEquals(100501L, list.get(1));
                }

                @Test
                public void coModification_remove_ThrowConcurrentModificationException() throws Exception {
                    Iterator<Long> iterator = list.iterator();
                    Assertions.assertThrows(ConcurrentModificationException.class, () -> {
                        while (iterator.hasNext()) {
                            iterator.next();
                            list.remove(0);
                        }
                    });
                }

            }

            @Nested
            class Add {
                @Test
                public void add_10_000_ToBegin_Duration() throws Exception {
                    int size = 10_000;
                    addToBeginElements(list, size);
                }

                @Test
                public void add_10_000_ToEnd_Duration() throws Exception {
                    int size = 10_000;
                    addElements(list, size);
                }

                @Test
                public void add_1000_InToMiddleOf_10_000_Duration() throws Exception {
                    int size = 10_000;
                    addElements(list, size);
                    for (int i = 0; i < 1000; i++) {
                        list.add(size / 2, (long) i);
                    }
                }

                @Test
                public void addAll_1000_InToMiddleOf_10_000_Duration() throws Exception {
                    int size = 10_000;
                    addElements(list, size);
                    List<Long> items = new LinkedList<>();
                    for (int i = 0; i < 1000; i++) {
                        items.add((long) i);
                    }
                    list.addAll(size / 2, items);
                }
            }
        }

        protected boolean isNullsAllowed() {
            return true;
        }

        @Nested
        class EmptyList {

            @Nested
            class CommonsTests {
                @Test
                public void isEmpty_ExpectedTrue() throws Exception {
                    assertTrue(list.isEmpty());
                }

                @Test
                public void size_ExpectedZero() throws Exception {
                    assertEquals(0, list.size());
                }

                @Test
                public void remove_ExpectedFalse() throws Exception {
                    assertEquals(false, list.remove(0L));
                }

                @Test
                public void indexOf_ExpectedMinusOne() throws Exception {
                    assertEquals(-1, list.indexOf(100500L));
                }

                @Test
                public void lastIndexOf_ExpectedMinusOne() throws Exception {
                    assertEquals(-1, list.lastIndexOf(100500L));
                }

                @Test
                public void hasNext__True() throws Exception {
                    assertFalse(list.iterator().hasNext());
                }

                @Test
                public void for__ExpectedCount() throws Exception {
                    int count = 0;
                    for (Long value : list) {
                        count++;
                    }
                    assertEquals(list.size(), count);
                }

                @Test
                public void remove__ExpectedCount() throws Exception {
                    Iterator<Long> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        iterator.next();
                        iterator.remove();
                    }
                    assertEquals(0, list.size());
                }
            }

            @Nested
            class AfterCreateTest extends CommonsTests {
                @BeforeEach
                void setUp() throws IOException {
                    // Do nothing.
                }
            }

            @Nested
            class AfterClearTest extends CommonsTests {

                @BeforeEach
                void setUp() throws IOException {
                    list.add(0L);
                    list.add(1L);
                    list.add(2L);
                    list.clear();
                }
            }

            @Nested
            class AfterRemoveAllTest extends CommonsTests {
                @BeforeEach
                void setUp() throws IOException {
                    list.add(0L);
                    list.add(1L);
                    list.add(2L);
                    list.remove(0);
                    list.remove(0);
                    list.remove(0);
                }
            }
        }


        @Nested
        class IndexOf {

            @BeforeEach
            void setUp() throws IOException {
                addElements(list, 10_000);
                Assertions.assertEquals(10_000, list.size());
                addElements(list, 10_000);
                Assertions.assertEquals(20_000, list.size());
            }

            @Test
            public void indexOf_ExpectedValue9999L() throws Exception {
                int index = list.indexOf(9999L);
                assertEquals(9999L, index);
            }

            @Test
            public void lastIndexOf_ExpectedValue() throws Exception {
                int index = list.lastIndexOf(0L);
                assertEquals(10000L, index);
            }

        }

        @Nested
        class SubList {

            @BeforeEach
            void setUp() throws IOException {
                setBufferSize(list, 10);
                addElements(list, 40);
            }

            @Test
            public void sublist_10_to_20_ExpectedSize() throws Exception {
                List<Long> subList = list.subList(10, 20);
                Assertions.assertEquals(10, subList.size());
            }

            @Test
            public void sublist_15_to_25_ExpectedSize() throws Exception {
                List<Long> subList = list.subList(15, 25);
                Assertions.assertEquals(10, subList.size());
            }

            @Test
            public void sublist_15_to_25_ExpectedValue() throws Exception {
                List<Long> subList = list.subList(15, 25);
                Assertions.assertEquals(20, subList.get(5));
            }

            @Test
            public void sublist_5_to_35_ExpectedValue() throws Exception {
                int shift = 5;
                List<Long> subList = list.subList(shift, shift + 30);
                Assertions.assertEquals(25, subList.get(20));
            }

            @Test
            public void sublist_5_to_35_ExpectedSize() throws Exception {
                List<Long> subList = list.subList(5, 35);
                subList.clear();
                Assertions.assertEquals(10, list.size());
                Assertions.assertEquals(39, list.get(list.size() - 1));
            }

        }

        @Nested
        class IteratorTests {

            @Nested
            class SingleElementTest {

                @BeforeEach
                void setUp() throws IOException {
                    list.add(1L);
                    flush(list);
                }

                @Test
                public void hasNext_True() throws Exception {
                    Iterator<Long> iterator = list.iterator();
                    assertTrue(iterator.hasNext());
                }

                @Test
                public void for_ExpectedCount() throws Exception {
                    int count = 0;
                    for (Long value : list) {
                        count++;
                    }
                    assertEquals(list.size(), count);
                }

                @Test
                public void remove_ExpectedCount() throws Exception {
                    Iterator<Long> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        iterator.next();
                        iterator.remove();
                    }
                    assertEquals(0, list.size());
                }
            }

            @Nested
            class MultipleElementTest {

                @BeforeEach
                void setUp() throws IOException {
                    setBufferSize(list, 10);
                    addElements(list, 100);
                }

                @Test
                public void hasNext_ExpectedTrue() throws Exception {
                    Iterator<Long> iterator = list.iterator();
                    assertTrue(iterator.hasNext());
                }

                @Test
                public void for_ExpectedIterationCount() throws Exception {
                    int count = 0;
                    for (Long value : list) {
                        assertEquals(Long.valueOf(count), value);
                        count++;
                    }
                    assertEquals(list.size(), count);
                }

                @Test
                public void for_ExpectedValue() throws Exception {
                    int count = 0;
                    for (Long value : list) {
                        assertEquals(Long.valueOf(count), value);
                        count++;
                    }
                }

                @Test
                public void remove_ExpectedValue() throws Exception {
                    int count = list.size();
                    long expectedValue = 0;
                    Iterator<Long> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        Long value = iterator.next();
                        assertEquals(expectedValue, value);
                        expectedValue++;
                        iterator.remove();
                        count--;
                    }
                }

                @Test
                public void remove_ExpectedIterationCount() throws Exception {
                    int expectedCount = list.size();
                    int count = 0;
                    Iterator<Long> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        iterator.next();
                        iterator.remove();
                        count++;
                    }
                    assertEquals(expectedCount, count);
                }

                @Test
                public void remove_IsEmpty() throws Exception {
                    Iterator<Long> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        iterator.next();
                        iterator.remove();
                    }
                    assertTrue(list.isEmpty());
                }

            }
        }

        @Nested
        class ListIteratorTests {

            @BeforeEach
            void setUp() throws IOException {
                setBufferSize(list, 2);
                addElements(list, 5);
            }

            @Test
            public void remove_Backward_IsEmpty() throws Exception {
                ListIterator<Long> iterator = list.listIterator(list.size());
                while (iterator.hasPrevious()) {
                    iterator.previous();
                    iterator.remove();
                }
                assertTrue(list.isEmpty());
            }

            @Test
            public void remove_Backward_ExpectedValue() throws Exception {
                long expectedValue = list.size();
                ListIterator<Long> iterator = list.listIterator(list.size());
                while (iterator.hasPrevious()) {
                    Long value = iterator.previous();
                    iterator.remove();
                    expectedValue--;
                    assertEquals(expectedValue, value);
                }
            }

            @Test
            public void remove_BackwardFromNonLast_ExpectedSize() throws Exception {
                ListIterator<Long> iterator = list.listIterator(list.size() - 1);
                while (iterator.hasPrevious()) {
                    iterator.previous();
                    iterator.remove();
                }
                assertEquals(1, list.size());
            }

            @Test
            public void next_ExpectedIndex() throws Exception {
                // В метод передаётся первый элемент, который будет возвращён из `listIterator.next()`.
                ListIterator<Long> listIterator = list.listIterator(5);
                int index = 4;
                while (listIterator.hasNext()) {
                    Long value = listIterator.next();
                    index++;
                    if (value == 2) {
                        break;
                    }
                }
                assertEquals(4, index);
            }

            @Test
            public void previous_ExpectedIndex() throws Exception {
                // В метод передаётся первый элемент, который будет возвращён из `listIterator.next()`.
                ListIterator<Long> listIterator = list.listIterator(5);
                int index = 5;
                while (listIterator.hasPrevious()) {
                    Long value = listIterator.previous();
                    index--;
                    if (value == 3L) {
                        break;
                    }
                }
                assertEquals(3L, index);

            }
        }


    }

}
