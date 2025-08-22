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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static com.devives.commons.collection.BufferedListUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Сравнение реализаций интерфейса {@link List}.
 */
public class ListBenchmarksBase extends TempDirectoryTestBase {

    protected class Commons {

        protected final int TEST_LIST_SIZE = 200_000;
        protected List<Long> list;

        @Nested
        class Remove {

            public Remove() {
                addElements(list, TEST_LIST_SIZE);
            }

            @Test
            public void remove_100_first_Duration() throws Exception {
                for (int i = 0; i < 100; i++) {
                    list.remove(0);
                }
            }

            @Test
            public void remove_firstAndLast_Duration() throws Exception {
                long first = list.remove(0);
                list.get(list.size() - 1);
                long last = list.remove(list.size() - 1);
                //flush(list);
                Assertions.assertAll(
                        () -> Assertions.assertEquals(0, first),
                        () -> Assertions.assertEquals(list.size() + 1, last)
                );
            }

            @Test
            public void remove_100_middle_Duration() throws Exception {
                for (int i = 0; i < 100; i++) {
                    list.remove(TEST_LIST_SIZE / 2);
                }
            }

            @Test
            public void remove_100_last_Duration() throws Exception {
                for (int i = 0; i < 100; i++) {
                    list.remove(list.size() - 1);
                }
            }

        }

        @Nested
        class Get {

            private final int test_list_size = 200_000;

            public Get() {
                addElements(list, test_list_size);
            }

            @Test
            public void get_First_200_Of_1_000_000_Duration() throws Exception {
                for (int i = 0; i < 200; i++) {
                    list.get(i);
                }
            }

            @Test
            public void get_Middle_200_Of_1_000_000_Duration() throws Exception {
                int middle = list.size() / 2;
                for (int i = middle - 100; i < middle + 100; i++) {
                    list.get(i);
                }
            }

            @Test
            public void get_Last_200_Of_1_000_000_Duration() throws Exception {
                int middle = list.size() / 2;
                for (int i = list.size() - 200; i < list.size(); i++) {
                    list.get(i);
                }
            }
        }

        @Nested
        class Add {

            @Test
            public void add_200_000_ToEnd_Duration() throws Exception {
                addElements(list, TEST_LIST_SIZE);
                assertEquals(list.size(), TEST_LIST_SIZE);
            }

            @Test
            public void add_200_000_ToBegin_Duration() throws Exception {
                addToBeginElements(list, TEST_LIST_SIZE);
            }

            @Test
            @Disabled
            public void add_1_000_000_ToEnd_Duration() throws Exception {
                addElements(list, 1_000_000);
            }

            @Test
            @Disabled
            public void addRandom_200_000_BuffSize_Duration() throws Exception {
                //todo
            }
        }

        @Nested
        class AddToMiddle {

            public AddToMiddle() {
                addElements(list, TEST_LIST_SIZE);
            }

            @Test
            public void add_100_InToMiddleOf_200_000_Duration() throws Exception {
                for (int i = 0; i < 100; i++) {
                    list.add(TEST_LIST_SIZE / 2, (long) i);
                }
                flush(list);
            }

            @Test
            @Disabled
            public void addAll_100_InToMiddleOf_1_000_000_Duration() throws Exception {
                List<Long> items = new LinkedList<>();
                for (int i = 0; i < 100; i++) {
                    items.add((long) i);
                }
                list.addAll(TEST_LIST_SIZE / 2, items);
                flush(list);
            }
        }

        class Iterate {

            abstract class IterateCommons {

                @Test
                public void iterate_For_Duration() throws Exception {
                    for (long value : list) {
                    }
                }

                @Test
                public void iterate_ForEach_Duration() throws Exception {
                    list.forEach(value -> {
                    });
                }
            }

            @Nested
            @Disabled
            class Iterate_10_000 extends IterateCommons {
                public Iterate_10_000() {
                    addElements(list, 10_000);
                }
            }

            @Nested
            @Disabled
            class Iterate_100_000 extends IterateCommons {
                public Iterate_100_000() {
                    addElements(list, 100_000);
                }
            }

            @Nested
            class Iterate_200_000 extends IterateCommons {
                public Iterate_200_000() {
                    addElements(list, TEST_LIST_SIZE);
                }
            }

            @Nested
            @Disabled
            class Iterate_1_000_000 extends IterateCommons {
                public Iterate_1_000_000() {
                    addElements(list, 1_000_000);
                }
            }

            @Nested
            @Disabled
            class Iterate_2_000_000 extends IterateCommons {
                public Iterate_2_000_000() {
                    addElements(list, 2_000_000);
                }
            }

        }

        @Nested
        class IndexOf {

            abstract class IndexOfCommons {

                @Test
                public void indexOf_Duration() throws Exception {
                    int index = list.indexOf(Long.valueOf(list.size() - 1));
                    assertEquals(list.size() - 1, index);
                }
            }

            @Nested
            class IndexOf_200_000 extends IndexOfCommons {

                public IndexOf_200_000() {
                    addElements(list, TEST_LIST_SIZE);
                }

            }

            @Nested
            @Disabled
            class IndexOf_1_000_000 extends IndexOfCommons {

                public IndexOf_1_000_000() {
                    addElements(list, 1_000_000);
                }

            }

            @Nested
            @Disabled
            class IndexOf_10_000_000 extends IndexOfCommons {

                public IndexOf_10_000_000() {
                    addElements(list, 10_000_000);
                }

            }
        }
    }
}
