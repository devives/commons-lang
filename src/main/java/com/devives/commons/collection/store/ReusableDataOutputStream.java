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
package com.devives.commons.collection.store;

import java.io.DataOutputStream;

/**
 * The class implements an output stream that can have the number of bytes written reset.
 * @author Vladimir Ivanov {@code <ivvlev@devives.com>}
 * @since 0.3.0
 */
final class ReusableDataOutputStream extends DataOutputStream {

    public ReusableDataOutputStream(ReusableByteArrayOutputStream out) {
        super(out);
    }

    /**
     * Resets the number of bytes written.
     */
    public void reset() {
        ((ReusableByteArrayOutputStream)out).reset();
        this.written = 0;
    }
}

