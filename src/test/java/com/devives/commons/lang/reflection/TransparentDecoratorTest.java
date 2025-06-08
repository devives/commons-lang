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
package com.devives.commons.lang.reflection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransparentDecoratorTest {

    @Test
    public void setTarget_TransparentDecorator() {
        SomeInterface proxy = ProxyBuilder
                .forClasses(SomeInterface.class)
                .setTarget(new TransparentDecorator(new SomeTarget()) {
                    public int foo() {
                        return 100500;
                    }
                })
                .build();
        Assertions.assertEquals(100500, proxy.foo()); // Will invoke TransparentDecorator#foo()
        Assertions.assertEquals(100000, proxy.doSome());  // Will invoke SomeTarget#doSome()
    }

    private interface SomeInterface {
        int foo();

        int doSome();
    }

    private static class SomeTarget {
        public final int foo() {
            return 100000;
        }

        public final int doSome() {
            return 100000;
        }
    }

}
