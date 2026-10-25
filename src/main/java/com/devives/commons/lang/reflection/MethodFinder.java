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

import java.lang.reflect.Method;

/**
 * Declaration of the method for finding a suitable method for calling the class.
 */
@FunctionalInterface
public interface MethodFinder {
    /**
     * Finds a method of the class suitable for calling.
     *
     * @param clazz          The class in which the method is being searched
     * @param name           The name of the method being searched
     * @param parameterTypes The parameters of the method being searched
     * @return The found method or `null`.
     * @throws NoSuchMethodException If the method is not found
     * @throws SecurityException     If there is no access
     */
    Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException;

}

