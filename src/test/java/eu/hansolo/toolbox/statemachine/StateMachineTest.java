/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 Gerrit Grunwald.
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

import eu.hansolo.toolbox.properties.ObjectProperty;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;


public class StateMachineTest {

    @Test
    void testStateMachine() {
        System.out.println("\n-------------------- state machine test --------------------");
        enum MyState implements State {
            // Available states
            IDLE("IDLE"), BUSY("BUSY"), ERROR("ERROR"), FINISHED("FINISHED");


            // Definition of state transitions
            static {
                IDLE.canTransitionTo(IDLE, BUSY, ERROR);
                BUSY.canTransitionTo(IDLE, BUSY, ERROR);
                ERROR.canTransitionTo(IDLE, ERROR);
            }

            private final String name;
            private       Set    transitions;


            // ******************** Constructor ***************************************
            MyState(String name) {
                this.name = name;
            }


            // ******************** Private Methods ***********************************
            private void canTransitionTo(final MyState... transitions) { this.transitions = EnumSet.copyOf(Arrays.asList(transitions)); }


            // ******************** Public Methods ************************************
            @Override public Set<State> getTransitions() { return this.transitions; }

            @Override public boolean canChangeTo(final State state) { return this.transitions.contains(state); }

            @Override public String getName() { return this.name; }
        }
        StateMachine<MyState> stateMachine = new StateMachine<>() {
            private ObjectProperty<MyState> state = new ObjectProperty<>(MyState.IDLE);


            // ******************** Public Methods ****************************
            @Override public State getState() { return this.state.get(); }

            @Override public void setState(final MyState state) throws StateChangeException {
                if (this.state.get().canChangeTo(state)) {
                    this.state.set(state);
                } else {
                    throw new StateChangeException("Not allowed to change from " + this.state.get().getName() + " to " + state.getName());
                }
            }
            @Override public ObjectProperty<MyState> stateProperty() { return state; }
        };

        // Add listener to state property
        stateMachine.stateProperty().addObserver(e -> System.out.println("State changed from " + e.getOldValue().getName() + " to " + e.getValue().getName()));

        // Test different state changes
        try {
            stateMachine.setState(MyState.BUSY);
        } catch (StateChangeException e) {
            System.out.println(e.getMessage() + " -> StateMachine still in state: " + stateMachine.getState().getName());
        }
        assert stateMachine.getState() == MyState.BUSY;

        try {
            stateMachine.setState(MyState.IDLE);
        } catch (StateChangeException e) {
            System.out.println(e.getMessage() + " -> StateMachine still in state: " + stateMachine.getState().getName());
        }
        assert stateMachine.getState() == MyState.IDLE;

        try {
            stateMachine.setState(MyState.ERROR);
        } catch (StateChangeException e) {
            System.out.println(e.getMessage() + " -> StateMachine still in state: " + stateMachine.getState().getName());
        }
        assert stateMachine.getState() == MyState.ERROR;

        try {
            stateMachine.setState(MyState.BUSY);
            System.out.println(stateMachine.getState());
        } catch (StateChangeException e) {
            assert stateMachine.getState() == MyState.ERROR;
            System.out.println(e.getMessage() + " -> StateMachine still in state: " + stateMachine.getState().getName());
        }
    }
}
