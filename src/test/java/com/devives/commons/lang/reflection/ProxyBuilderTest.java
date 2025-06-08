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

import java.util.concurrent.ConcurrentHashMap;


public class ProxyBuilderTest {

    private final TestInterface2 testObject = ProxyBuilder.forClasses(TestInterface2.class)
            .setTarget(new Object() {
                public String function1() {
                    return "OK";
                }

                public int intFunction1() {
                    return 100500;
                }

                public String function2(Long arg1, String arg2) {
                    return arg2 + arg1;
                }

                public void procedure2(Long arg1, String arg2) {
                    Assertions.assertEquals((Long) 100500L, arg1);
                    Assertions.assertEquals("Строка", arg2);
                }

                public void close() {

                }
            }).build();


    @Test
    public void invokeImplementedMethods_NoExceptions() {
        Assertions.assertAll(
                () -> Assertions.assertEquals("OK", testObject.function1()),
                () -> Assertions.assertEquals("Строка100500", testObject.function2(100500L, "Строка")),
                () -> Assertions.assertEquals(100500, testObject.intFunction1()),
                () -> testObject.procedure2(100500L, "Строка"),
                () -> Assertions.assertNotNull(testObject.getClass()),
                () -> Assertions.assertNotNull(testObject.hashCode()),
                () -> Assertions.assertNotNull(testObject.toString()),
                () -> Assertions.assertFalse(testObject.equals(1L))
        );
    }

    @Test
    public void invokeNotImplementedMethod_ThrowException() {
        Assertions.assertThrows(RuntimeException.class, testObject::nonImplementedMethod);
    }

    @Test
    public void equals_This_True() {
        Assertions.assertEquals(testObject, testObject);
    }

    @Test
    public void equals_Null_False() {
        Assertions.assertNotEquals(testObject, null);
    }

    @Test
    public void equals_Object_False() {
        Assertions.assertNotEquals(testObject, new Object());
    }

    @Test
    public void equals_TwoProxiesOneStub_True() {
        Object target = new TestStubOfInterface1();
        TestInterface1 proxy1 = ProxyBuilder.forClasses(TestInterface1.class).setTarget(target).build();
        TestInterface1 proxy2 = ProxyBuilder.forClasses(TestInterface1.class).setTarget(target).build();
        Assertions.assertEquals(proxy1, proxy2);
    }

    @Test
    public void setMethodMapFactory() {
        Object target = new TestStubOfInterface1();
        TestInterface1 proxy1 = ProxyBuilder.forClasses(TestInterface1.class).setTarget(target).setMethodMapFactory(ConcurrentHashMap::new).build();
        Assertions.assertNotEquals("OK", proxy1.function1());
        Assertions.assertNotEquals("OK", proxy1.function1());
    }

    @Test
    public void equals_TwoProxiesTwoStub_False() {
        Object stub1 = new TestStubOfInterface1();
        Object stub2 = new TestStubOfInterface1();
        TestInterface1 proxy1 = ProxyBuilder.forClasses(TestInterface1.class).setTarget(stub1).build();
        TestInterface1 proxy2 = ProxyBuilder.forClasses(TestInterface1.class).setTarget(stub2).build();
        Assertions.assertNotEquals(proxy1, proxy2);
    }

    @Test
    public void equals_TwoProxiesTwoStubWithEquals_True() {
        Object stub1 = new TestStubOfInterface1() {
            @Override
            public boolean equals(Object obj) {
                return function1().equals(((TestStubOfInterface1) obj).function1());
            }
        };
        Object stub2 = new TestStubOfInterface1() {
            @Override
            public boolean equals(Object obj) {
                return function1().equals(((TestStubOfInterface1) obj).function1());
            }
        };
        TestInterface1 proxy1 = ProxyBuilder.forClasses(TestInterface1.class).setTarget(stub1).build();
        TestInterface1 proxy2 = ProxyBuilder.forClasses(TestInterface1.class).setTarget(stub2).build();
        Assertions.assertEquals(proxy1, proxy2);
    }

    @Test
    public void hashCode_TwoProxiesTwoStubWithEquals_True() {
        Object stub1 = new TestStubOfInterface1() {
            @Override
            public boolean equals(Object obj) {
                return function1().equals(((TestStubOfInterface1) obj).function1());
            }
        };
        Object stub2 = new TestStubOfInterface1() {
            @Override
            public boolean equals(Object obj) {
                return function1().equals(((TestStubOfInterface1) obj).function1());
            }
        };
        TestInterface1 proxy1 = ProxyBuilder.forClasses(TestInterface1.class).setTarget(stub1).build();
        TestInterface1 proxy2 = ProxyBuilder.forClasses(TestInterface1.class).setTarget(stub2).build();

        Assertions.assertAll(
                () -> Assertions.assertEquals(stub1.hashCode(), proxy1.hashCode()),
                () -> Assertions.assertEquals(stub2.hashCode(), proxy2.hashCode()),
                () -> Assertions.assertNotEquals(stub1.hashCode(), stub2.hashCode()),
                () -> Assertions.assertNotEquals(proxy1.hashCode(), proxy2.hashCode())
        );
    }

    @Test
    public void build_TestInterface1() {
        TestInterface1 proxy = ProxyBuilder.forClasses(TestInterface1.class).build();
        Assertions.assertTrue(proxy instanceof TestInterface1);
    }

    @Test
    public void build_TestInterface1AndTestInterface2() {
        TestInterface1 proxy = ProxyBuilder.forClasses(TestInterface1.class, TestInterface2.class).build();
        Assertions.assertAll(
                () -> Assertions.assertTrue(proxy instanceof TestInterface1),
                () -> Assertions.assertTrue(proxy instanceof TestInterface2)
        );
    }


    public interface TestInterface1 extends AutoCloseable {
        String function1();

        void nonImplementedMethod();

        int intFunction1();
    }

    public interface TestInterface2 extends TestInterface1 {

        String function2(Long arg1, String arg2);

        void procedure2(Long arg1, String arg2);

    }

    private class TestStubOfInterface1 extends Object {

        public void close() throws Exception {

        }

        public String function1() {
            return "value1";
        }

        public int intFunction1() {
            return 100500;
        }

    }

}
