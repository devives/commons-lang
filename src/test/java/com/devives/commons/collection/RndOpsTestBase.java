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
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Disabled
public class RndOpsTestBase extends TempDirectoryTestBase {

    protected static AtomicLong SEQUENCE = new AtomicLong();
    protected List<Long> list;
    private boolean TRACE = false;

    @Test
    public void testRandomOperations() {
        // Создаем эталонный список
        List<Long> reference = new ArrayList<>();
        Random random = new Random(42); // для воспроизводимости тестов

        // Заполняем оба списка начальными данными
        for (int i = 0; i < 200000; i++) {
            //long value = random.nextLong();
            long value = SEQUENCE.incrementAndGet();
            list.add(value);
            reference.add(value);
        }

        // Выполняем случайные операции
        for (int i = 0; i < 100000; i++) {
            int operation = random.nextInt(4); // 0-get, 1-add, 2-set, 3-remove
            int index;
            if (reference.size() > 0) {
                index = random.nextInt(reference.size());
            } else {
                operation = 1;
                index = 0;
            }

            switch (operation) {
                case 0: // get
                    if (TRACE) System.out.println("get index: " + index);
                    long value = list.get(index);
                    Assertions.assertEquals(reference.get(index), value,
                            "Get operation failed at iteration " + i + ", index " + index);
                    break;

                case 1: // add
                    //long newValue = random.nextLong();
                    long newValue = SEQUENCE.incrementAndGet();
                    if (TRACE) System.out.println("add index: " + index + ", value: " + newValue);
                    list.add(index, newValue);
                    reference.add(index, newValue);
                    break;

                case 2: // set
                    //long setValue = random.nextLong();
                    long setValue = SEQUENCE.incrementAndGet();
                    if (TRACE) System.out.println("set index: " + index + ", value: " + setValue);
                    list.set(index, setValue);
                    reference.set(index, setValue);
                    break;

                case 3: // remove
                    if (TRACE) System.out.println("remove index: " + index);
                    list.remove(index);
                    reference.remove(index);
                    break;
            }

            // Проверяем размер после каждой операции
            Assertions.assertEquals(reference.size(), list.size(),
                    "Size mismatch at iteration " + i);

            if (TRACE) System.out.println("expected=" + Arrays.toString(reference.toArray()));
            if (TRACE) System.out.println("actual__=" + Arrays.toString(list.toArray()));
            if (TRACE) System.out.println("==========================================================");

            if (i % 1000 == 0) {
                for (int j = 0; j < reference.size(); j++) {
                    Assertions.assertEquals(reference.get(j), list.get(j),
                            "Content mismatch at iteration " + i + ", index " + j);
                }
            }

        }

        // Финальная проверка полного соответствия
        Assertions.assertEquals(reference.size(), list.size(), "Final size mismatch");
        for (int i = 0; i < reference.size(); i++) {
            Assertions.assertEquals(reference.get(i), list.get(i),
                    "Final content mismatch at index " + i);
        }
    }

}
