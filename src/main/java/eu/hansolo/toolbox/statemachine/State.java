/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2022 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.toolbox.statemachine;

import java.util.Set;


public interface State {

    /**
     * Returns a set of states to which one can change from this state
     * @return a set of states to which one can change from this state
     */
    Set<State> getTransitions();

    /**
     * Returns true if it is possible to change from this state to the given state
     * @param state
     * @return true if is is possible to change from this state to the given state
     */
    boolean canChangeTo(final State state);

    /**
     * Returns the name of this state
     * @return the name of this state
     */
    String getName();
}
