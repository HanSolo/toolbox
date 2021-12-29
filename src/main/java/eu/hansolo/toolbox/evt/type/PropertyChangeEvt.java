/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
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

package eu.hansolo.toolbox.evt.type;

import eu.hansolo.toolbox.evt.EvtPriority;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.properties.ReadOnlyProperty;


public class PropertyChangeEvt<T> extends ChangeEvt {
    public static final EvtType<PropertyChangeEvt> ANY     = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<PropertyChangeEvt> CHANGED = new EvtType<>(PropertyChangeEvt.ANY, "CHANGED");

    private final T oldValue;
    private final T value;


    // ******************** Constructors **************************************
    public PropertyChangeEvt(final EvtType<? extends PropertyChangeEvt<T>> evtType, final T oldValue, final T value) {
        super(evtType);
        this.value    = value;
        this.oldValue = oldValue;
    }
    public PropertyChangeEvt(final ReadOnlyProperty src, final EvtType<? extends PropertyChangeEvt<T>> evtType, final T oldValue, final T value) {
        super(src, evtType);
        this.value    = value;
        this.oldValue = oldValue;
    }
    public PropertyChangeEvt(final ReadOnlyProperty src, final EvtType<? extends PropertyChangeEvt<T>> evtType, final EvtPriority priority, final T oldValue, final T value) {
        super(src, evtType, priority);
        this.value    = value;
        this.oldValue = oldValue;
    }


    // ******************** Methods *******************************************
    public EvtType<? extends PropertyChangeEvt<T>> getEvtType() { return (EvtType<? extends PropertyChangeEvt<T>>) super.getEvtType(); }

    public T getOldValue() { return oldValue; }

    public T getValue() { return value; }
}
