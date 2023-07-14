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

package eu.hansolo.toolbox.evt.type;

import eu.hansolo.toolbox.evt.EvtPriority;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.properties.ReadOnlyProperty;

import java.util.Objects;


public class InvalidationEvt<T> extends ChangeEvt {
    public static final EvtType<InvalidationEvt> ANY         = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<InvalidationEvt> INVALIDATED = new EvtType<>(InvalidationEvt.ANY, "INVALIDATED");


    // ******************** Constructors **************************************
    public InvalidationEvt(final EvtType<? extends InvalidationEvt<T>> evtType) {
        super(evtType);
    }
    public InvalidationEvt(final ReadOnlyProperty src, final EvtType<? extends InvalidationEvt<T>> evtType) {
        super(src, evtType);
    }
    public InvalidationEvt(final ReadOnlyProperty src, final EvtType<? extends InvalidationEvt<T>> evtType, final EvtPriority priority) {
        super(src, evtType, priority);
    }


    // ******************** Methods *******************************************
    @Override public EvtType<? extends InvalidationEvt<T>> getEvtType() { return (EvtType<? extends InvalidationEvt<T>>) super.getEvtType(); }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        InvalidationEvt<?> that = (InvalidationEvt<?>) o;
        return Objects.equals(this, that);
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
