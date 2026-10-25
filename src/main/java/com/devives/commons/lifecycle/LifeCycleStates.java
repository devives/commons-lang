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

import com.devives.commons.state.State;
import com.devives.commons.state.StateFactory;

/**
 * States of a life cycle object and the methods that check them.
 */
public interface LifeCycleStates {

    /**
     * Returns whether the component is starting.
     *
     * @return true if the component is starting.
     * @see #isStarted()
     */
    boolean isStarting();

    /**
     * Returns whether the component has been started.
     *
     * @return true if the component has been started.
     * @see LifeCycle#start()
     * @see #isStarting()
     */
    boolean isStarted();

    /**
     * Returns whether the component is stopping.
     *
     * @return true if the component is stopping.
     * @see #isStopped()
     */
    boolean isStopping();

    /**
     * Returns whether the component has been stopped.
     *
     * @return true if the component has been stopped.
     * @see LifeCycle#stop()
     * @see #isStopping()
     */
    boolean isStopped();

    /**
     * Returns whether the component has failed.
     *
     * @return true if the component has failed to start or has failed to stop.
     */
    boolean isFailed();

    /**
     * The component is starting.
     */
    State STARTING = StateFactory.named("STARTING");
    /**
     * The component has been started.
     */
    State STARTED = StateFactory.named("STARTED");
    /**
     * The component is stopping.
     */
    State STOPPING = StateFactory.named("STOPPING");
    /**
     * The component has been stopped.
     */
    State STOPPED = StateFactory.named("STOPPED");
    /**
     * The component has failed to start or to stop.
     */
    State FAILED = StateFactory.named("FAILED");

}
