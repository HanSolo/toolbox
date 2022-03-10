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

package eu.hansolo.toolbox.evt.type;

import eu.hansolo.toolbox.observables.ObservableList;
import eu.hansolo.toolbox.evt.EvtPriority;
import eu.hansolo.toolbox.evt.EvtType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ListChangeEvt<T> extends ChangeEvt {
    public static final EvtType<ListChangeEvt> ANY     = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<ListChangeEvt> CHANGED = new EvtType<>(ListChangeEvt.ANY, "CHANGED");
    public static final EvtType<ListChangeEvt> ADDED   = new EvtType<>(ListChangeEvt.ANY, "ADDED");
    public static final EvtType<ListChangeEvt> REMOVED = new EvtType<>(ListChangeEvt.ANY, "REMOVED");

    private final List<T> addedItems;
    private final List<T> removedItems;


    // ******************** Constructors **************************************
    public ListChangeEvt(final ObservableList<T> src, final EvtType<ListChangeEvt> evtType, final List<T> addedItems, final List<T> removedItems) {
        super(src, evtType);
        this.addedItems   = null == addedItems   ? List.of() : new ArrayList<>(addedItems);
        this.removedItems = null == removedItems ? List.of() : new ArrayList<>(removedItems);
    }
    public ListChangeEvt(final ObservableList<T> src, final EvtType<? extends ListChangeEvt<T>> evtType, final EvtPriority priority, final List<T> addedItems, final List<T> removedItems) {
        super(src, evtType, priority);
        this.addedItems   = null == addedItems   ? List.of() : new ArrayList<>(addedItems);
        this.removedItems = null == removedItems ? List.of() : new ArrayList<>(removedItems);
    }


    // ******************** Methods *******************************************
    @Override public EvtType<? extends ListChangeEvt<T>> getEvtType() { return (EvtType<? extends ListChangeEvt<T>>) super.getEvtType(); }

    public List<T> getAddedItems() { return addedItems; }

    public List<T> getRemovedItems() { return removedItems; }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        ListChangeEvt<?> that = (ListChangeEvt<?>) o;
        return Objects.equals(addedItems, that.addedItems) && Objects.equals(removedItems, that.removedItems);
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), addedItems, removedItems);
    }
}
