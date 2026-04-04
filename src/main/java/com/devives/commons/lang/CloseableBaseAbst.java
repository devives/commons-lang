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
package com.devives.commons.lang;

import com.devives.commons.state.InvalidStateException;
import com.devives.commons.state.StateHolder;
import com.devives.commons.state.Stateful;

/**
 * The class contains common code for all implementations of the CloseableObj.
 */
public abstract class CloseableBaseAbst extends Stateful implements CloseableStates {

    public CloseableBaseAbst(StateHolder stateHolder) {
        super(stateHolder);
    }

    @Override
    public boolean isOpening() {
        return getStateHolder().isExpected(OPENING);
    }

    @Override
    public boolean isOpened() {
        return getStateHolder().isExpected(OPENED);
    }

    @Override
    public boolean isClosing() {
        return getStateHolder().isExpected(CLOSING);
    }

    @Override
    public boolean isClosed() {
        return getStateHolder().isExpected(CLOSED);
    }

    /**
     * Checks whether the current state is equivalent to {@link CloseableStates#OPENED}.
     *
     * @throws InvalidStateException if object not opened.
     */
    protected void validateOpened() throws InvalidStateException {
        getStateHolder().validate(OPENED);
    }

    /**
     * Method is called before closing the object and makes a decision about whether the object can be closed.
     * <p><strong>Notes.</strong></p>
     * Do not write long running checks in this method. It will lock other threads, which checking object's state.
     *
     * @return {@code true} if the object can be closed, otherwise {@code false}.
     * @throws Exception if something went wrong.
     */
    protected boolean canBeClosed() throws Exception {
        return true;
    }

    /**
     * Perform closing an object.
     * <p>
     * The purpose of the method's existence is the ability to extend the logic of closing an object like:
     *
     * @throws Exception if something went wrong.
     */
    protected void doClose() throws Exception {
        beforeClose();
        onClose();
        afterClose();
    }

    /**
     * Calling before {@link #onClose()}
     *
     * @throws Exception if something went wrong.
     */
    protected void beforeClose() throws Exception {

    }

    /**
     * Called when the object is closing.
     * <p>
     * Override this method to release resources.
     * A single call to this method is guaranteed.
     *
     * @throws Exception if something went wrong.
     */
    protected abstract void onClose() throws Exception;

    /**
     * Calling after {@link #onClose()}
     *
     * @throws Exception if something went wrong.
     */
    protected void afterClose() throws Exception {

    }


}
