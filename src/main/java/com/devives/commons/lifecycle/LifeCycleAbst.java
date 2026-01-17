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
package com.devives.commons.lifecycle;

import com.devives.commons.publisher.Publisher;
import com.devives.commons.publisher.Publishers;

public abstract class LifeCycleAbst extends LifeCycleBaseAbst {

    public LifeCycleAbst() {
        this(States.STOPPED);
    }

    public LifeCycleAbst(State initialState) {
        this(new StateHolderImpl(initialState),
                Publishers.<Listener<LifeCycle>>builder().setIndependentDelivery().build());
    }

    public LifeCycleAbst(StateHolder stateHolder, Publisher<Listener<LifeCycle>> publisher) {
        super(stateHolder, publisher);
    }

}
