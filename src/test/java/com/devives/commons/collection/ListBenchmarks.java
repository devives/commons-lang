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

import com.devives.commons.lang.ExceptionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Сравнение реализаций интерфейса {@link List}.
 */
@Disabled("For manual running.")
public class ListBenchmarks extends ListBenchmarksBase {

    @Nested
    public class FileListTest extends Commons {

        public FileListTest() {
            list = ExceptionUtils.passChecked(() -> SerializedLists.ofLongs().setFileStorePath(tempPath).build());
        }

        @AfterEach
        void tearDown() throws Exception {
            ((AutoCloseable) list).close();
        }

    }

    @Nested

    public class BufferedBiFileListTest extends Commons {

        public BufferedBiFileListTest() {
            list = ExceptionUtils.passChecked(() -> SerializedLists.ofLongs().setBiFileStorePath(tempPath).setBuffered().build());
        }

        @AfterEach
        void tearDown() throws Exception {
            ((AutoCloseable) list).close();
        }

    }

    @Nested
    public class BufferedPersistentArrayListTest extends Commons {

        public BufferedPersistentArrayListTest() {
            list = SerializedLists.ofLongs().build();
        }

    }

    @Nested
    public class BufferedListOfArrayListTest extends Commons {

        public BufferedListOfArrayListTest() {
            list = BufferedLists.of(new ArrayList<Long>()).build();
        }

    }

    @Nested
    //@Disabled
    public class BufferedListOfLinkedListTest extends Commons {

        public BufferedListOfLinkedListTest() {
            list = BufferedLists.of(new LinkedList<Long>()).build();
        }

    }

    @Nested
    //@Disabled("Зависает на вставке 1 000 000 элементов в начало.")
    public class ArrayListTest extends Commons {

        public ArrayListTest() {
            list = new ArrayList<>();
        }

    }

    @Nested
    //@Disabled("Зависает на последовательном получении 1 000 000 элементов по индексу.")
    public class LinkedListTest extends Commons {

        public LinkedListTest() {
            list = new LinkedList<>();
        }

    }

}
