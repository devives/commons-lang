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

import java.util.Objects;

/**
 * Transparent decorator.
 * <p>
 * Allows you to declare only part of the required interface methods in the decorator object. If the required interface method is not implemented in the decorator object,
 * the search for the required method will be performed on the delegate.
 *
 * @param <DELEGATE> delegate type.
 * @see ProxyBuilder#setTarget(Object)
 */
public class TransparentDecorator<DELEGATE> {

    private final DELEGATE delegate_;

    public TransparentDecorator(DELEGATE delegate) {
        delegate_ = Objects.requireNonNull(delegate, "delegate");
    }

    protected final DELEGATE getDelegate() {
        return delegate_;
    }

}
