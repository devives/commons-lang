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
package com.devives.commons.collection.store.serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongBinarySerializer implements BinarySerializer<Long> {

    @Override
    public void serialize(DataOutput output, Long element) throws IOException {
        output.writeLong(element);
        if (8 < getElementSize()) {
            byte[] emptyBytes = new byte[getElementSize() - 8];
            output.write(emptyBytes);
        }
    }

    @Override
    public Long deserialize(DataInput input) throws IOException {
        return input.readLong();
    }

    @Override
    public int getElementSize() {
        return 8;
    }
}
