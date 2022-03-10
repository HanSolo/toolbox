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

import eu.hansolo.toolbox.observables.ObservableMap;
import eu.hansolo.toolbox.evt.EvtPriority;
import eu.hansolo.toolbox.evt.EvtType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;


public class MapChangeEvt<K,V> extends ChangeEvt {
    public static final EvtType<MapChangeEvt> ANY      = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<MapChangeEvt> MODIFIED = new EvtType<>(MapChangeEvt.ANY, "MODIFIED");
    public static final EvtType<MapChangeEvt> ADDED    = new EvtType<>(MapChangeEvt.ANY, "ADDED");
    public static final EvtType<MapChangeEvt> REMOVED  = new EvtType<>(MapChangeEvt.ANY, "REMOVED");

    private final List<Entry<K,V>> addedEntries;
    private final List<Entry<K,V>> modifiedEntries;
    private final List<Entry<K,V>> removedEntries;


    // ******************** Constructors **************************************
    public MapChangeEvt(final Map<K,V> src, final EvtType<MapChangeEvt> evtType, final List<Entry<K,V>> addedEntries, final List<Entry<K,V>> modifiedEntries, final List<Entry<K,V>> removedEntries) {
        super(src, evtType);
        this.addedEntries    = null == addedEntries    ? List.of() : new ArrayList<>(addedEntries);
        this.modifiedEntries = null == modifiedEntries ? List.of() : new ArrayList<>(modifiedEntries);
        this.removedEntries  = null == removedEntries  ? List.of() : new ArrayList<>(removedEntries);
    }
    public MapChangeEvt(final Map<K,V> src, final EvtType<? extends MapChangeEvt<K,V>> evtType, final EvtPriority priority, final List<Entry<K,V>> addedEntries, final List<Entry<K,V>> modifiedEntries, final List<Entry<K,V>> removedEntries) {
        super(src, evtType, priority);
        this.addedEntries    = null == addedEntries    ? List.of() : new ArrayList<>(addedEntries);
        this.modifiedEntries = null == modifiedEntries ? List.of() : new ArrayList<>(modifiedEntries);
        this.removedEntries  = null == removedEntries  ? List.of() : new ArrayList<>(removedEntries);
    }


    // ******************** Methods *******************************************
    @Override public EvtType<? extends MapChangeEvt<K,V>> getEvtType() { return (EvtType<? extends MapChangeEvt<K,V>>) super.getEvtType(); }

    public List<Entry<K,V>> getAddedEntries() { return addedEntries; }

    public List<Entry<K,V>> getModifiedEntries() { return modifiedEntries; }

    public List<Entry<K,V>> getRemovedEntries() { return removedEntries; }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        MapChangeEvt<?,?> that = (MapChangeEvt<?,?>) o;
        return Objects.equals(addedEntries, that.addedEntries) && Objects.equals(modifiedEntries, that.modifiedEntries) && Objects.equals(removedEntries, that.removedEntries);
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), addedEntries, modifiedEntries, removedEntries);
    }
}
