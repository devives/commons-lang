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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static com.devives.commons.collection.BufferedListUtils.addElements;
import static com.devives.commons.collection.BufferedListUtils.addToBeginElements;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Сравнение реализаций буферизированных списков.
 */
//@Disabled("For manual runnig.")
public class BufferedListBenchmarksBase extends TempDirectoryTestBase {

    protected static final int TEST_COUNT = 1_000_000;

    @BeforeAll
    static void beforeAll() throws InterruptedException {
        //Thread.sleep(5000);
    }

    protected class Commons {

        protected List<Long> list;

        @Nested
        class Remove {

            abstract class AbstractTests {
                @Test
                public void remove_FromEnd_Duration() throws Exception {
                    int size = list.size();
                    while (size > 0) {
                        list.remove(size - 1);
                        size--;
                    }
                }

                @Test
                public void remove_FromBegin_Duration() throws Exception {
                    int size = list.size();
                    while (size > 0) {
                        list.remove(0);
                        size--;
                    }
                }

            }

            @Nested
            class From_N extends AbstractTests {
                public From_N() {
                    addElements(list, TEST_COUNT);
                }
            }

        }

        @Nested
        class Add {

            @ParameterizedTest
            @ValueSource(ints = {TEST_COUNT})
            public void add_N_ToEnd_Duration(int count) throws Exception {
                addElements(list, count);
                assertEquals(count, list.size());
            }

            @ParameterizedTest
            @ValueSource(ints = {TEST_COUNT})
            public void add_N_ToBegin_Duration(int count) throws Exception {
                addToBeginElements(list, count);
                assertEquals(count, list.size());
            }

            abstract class AddToMiddle {

                @Test
                public void add_10_000_InToMiddle_Duration() throws Exception {
                    int size = list.size();
                    int position = size / 2;
                    //List<Long> expactedSubList = new ArrayList<>(10_000);
                    for (long i = 0; i < 10_000; i++) {
                        list.add(position, i);
                        //expactedSubList.add(0, i);
                    }
                    BufferedListUtils.flush(list);
                    assertEquals(size + 10_000, list.size());
                    //Assertions.assertArrayEquals(expactedSubList.toArray(), list.subList(position, position + 10_000).toArray());
                }
            }

            @Nested
            class AddToMiddleOf_N extends AddToMiddle {
                public AddToMiddleOf_N() {
                    addElements(list, TEST_COUNT);
                }
            }

        }

        @Nested
        class Iterate {

            abstract class IterateCommons {

                @Test
                public void iterate_For_Duration() throws Exception {
                    for (long value : list) {
                    }
                }

                @Test
                public void iterate_ForI_Duration() throws Exception {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i);
                    }
                }

                @Test
                public void iterate_ForEach_Duration() throws Exception {
                    list.forEach(value -> {
                    });
                }
            }

            @Nested
            class Iterate_N extends IterateCommons {
                public Iterate_N() {
                    addElements(list, TEST_COUNT);
                }
            }

        }

        @Nested
        class IndexOf {

            abstract class IndexOfCommons {
                @Test
                public void indexOfLast_Duration() throws Exception {
                    int index = list.indexOf(Long.valueOf(list.size() - 1));
                    assertEquals(list.size() - 1, index);
                }
            }

            @Nested
            class IndexOf_N extends IndexOfCommons {
                public IndexOf_N() {
                    addElements(list, TEST_COUNT);
                }
            }

        }
    }


}
