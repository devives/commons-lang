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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Arrays;

import static com.devives.commons.collection.BufferedListUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class BufferedListTestBase extends ListTestsBase {

    class BufferedListCommons extends ListCommons {

        @AfterEach
        void tearDown() throws Exception {
            if (list instanceof AutoCloseable) {
                ((AutoCloseable) list).close();
            }
        }

        @Nested
        class Add {
            @ParameterizedTest
            @ValueSource(ints = {10, 50, 100, 200, 500, 1000})
            public void insert_1000_InToMiddle_BufferSize_Duration(int bufferSize) throws Exception {
                int size = 10_000;
                addElements(list, size);
                setBufferSize(list, bufferSize);
                for (int i = 0; i < 1000; i++) {
                    list.add(size / 2, (long) i);
                }
            }
        }

        @Nested
        class BufferTest {
            @BeforeEach
            void setUp() throws IOException {
                setBufferSize(list, 10);
                addElements(list, 31);
            }

            @Test
            public void get_19_ExpectedValue() throws Exception {
                assertEquals(19, list.get(19));
            }

            @Test
            public void add_19_ExpectedValue() throws Exception {
                list.add(19, 10019L);
                list.add(19, 20019L);
                // Get выполнит flush и загрузку 1-й страницы.
                list.get(0);
                // Remove выполнит загрузку 2-й страницы. И удаление 20019L.
                assertEquals(20019L, list.remove(19));
                assertEquals(10019L, list.get(19));
            }


            @Test
            public void flushBuffer_ExpectedValue() throws Exception {
                flush(list);
                int expectedIndex = list.size() + 1;
                list.add(100500L);
                flush(list);
                list.add(100501L);
                Assertions.assertEquals(expectedIndex, list.indexOf(100501L));
            }

            @Test
            public void flushBuffer_Twice_ExpectedValue() throws Exception {
                flush(list);
                int expectedIndex = list.size() + 1;
                list.add(100500L);
                flush(list);
                list.add(100501L);
                flush(list);
                Assertions.assertEquals(expectedIndex, list.indexOf(100501L));
            }

            @Test
            void addAll_SizeIncrease() throws IOException {
                list.addAll(5, Arrays.asList(55L, 56L, 57L, 58L));
                Assertions.assertEquals(35, list.size());
                Assertions.assertEquals(55L, list.get(5));
                Assertions.assertEquals(58L, list.get(8));
            }
        }

    }

}
