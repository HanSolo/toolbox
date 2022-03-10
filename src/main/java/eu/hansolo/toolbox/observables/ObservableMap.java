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

package eu.hansolo.toolbox.observables;

import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.evt.type.MapChangeEvt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;


public class ObservableMap<K,V> implements Map<K,V>, Cloneable {
    private static final int                                                DEFAULT_CAPACITY = 16;
    private static final float                                              DEFAULT_LOAD_FACTOR = 0.75f;
    private        final HashMap<K,V>                                       map;
    private              Map<EvtType, List<EvtObserver<MapChangeEvt<K,V>>>> observers;


    // ******************** Constructors **************************************
    public ObservableMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
    public ObservableMap(final int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }
    public ObservableMap(final int capacity, final float loadFactor) {
        this.map       = new HashMap<>(capacity, loadFactor);
        this.observers = new ConcurrentHashMap<>();
    }
    public ObservableMap(final Map<? extends K, ? extends V> map) {
        this.map       = new HashMap<>(map);
        this.observers = new ConcurrentHashMap<>();
    }


    // ******************** Methods *******************************************
    public V get(final Object key) { return this.map.get(key); }

    public V put(K key, V value) {
        final V result;
        if (this.map.containsKey(key)) {
            result = this.map.put(key, value);
            fireMapChangeEvt(new MapChangeEvt<>(ObservableMap.this, MapChangeEvt.MODIFIED, List.of(), List.of(Map.entry(key, value)), List.of()));
            return result;
        } else {
            result = this.map.put(key, value);
            fireMapChangeEvt(new MapChangeEvt<>(ObservableMap.this, MapChangeEvt.ADDED, List.of(Map.entry(key, value)), List.of(), List.of()));
            return result;
        }
    }

    public void putAll(final Map<? extends K, ? extends V> map) {
        //this.map.putAll(map);
        map.entrySet().forEach(entry -> put(entry.getKey(), entry.getValue()));
    }

    public V remove(final Object key) {
        final V result;
        if (map.containsKey(key)) {
            final Entry<K,V> removedEntry = Map.entry((K) key, map.get(key));
            result = map.remove(key);
            fireMapChangeEvt(new MapChangeEvt<>(ObservableMap.this, MapChangeEvt.REMOVED, List.of(), List.of(), List.of(removedEntry)));
        } else {
            result = map.remove(key);
        }
        return result;
    }

    public void clear() {
        List<Entry<K,V>> removedEntries = new ArrayList<>(entrySet());
        map.clear();
        fireMapChangeEvt(new MapChangeEvt<K,V>(ObservableMap.this, MapChangeEvt.REMOVED, List.of(), List.of(), removedEntries));
    }


    public boolean containsKey(final Object key) { return this.map.containsKey(key); }

    public boolean containsValue(final Object value) { return map.containsValue(value); }


    public Set<K> keySet() { return map.keySet(); }

    public Collection<V> values() { return map.values(); }

    public Set<Map.Entry<K,V>> entrySet() { return map.entrySet(); }


    public int size() { return this.map.size(); }

    public boolean isEmpty() { return this.map.isEmpty(); }


    @Override public V getOrDefault(final Object key, final V defaultValue) { return getOrDefault(key, defaultValue); }

    @Override public V putIfAbsent(final K key, final V value) {
        final V result = map.putIfAbsent(key, value);
        if (null == result) {
            fireMapChangeEvt(new MapChangeEvt<>(ObservableMap.this, MapChangeEvt.ADDED, List.of(Map.entry(key, value)), List.of(), List.of()));
        } else {
            fireMapChangeEvt(new MapChangeEvt<>(ObservableMap.this, MapChangeEvt.MODIFIED, List.of(), List.of(Map.entry(key, result)), List.of()));
        }
        return result;
    }

    @Override public boolean remove(final Object key, final Object value) {
        final boolean result = map.remove(key, value);
        if (result) {
            fireMapChangeEvt(new MapChangeEvt<K,V>(ObservableMap.this, MapChangeEvt.REMOVED, List.of(), List.of(), List.of((Entry<K, V>) Map.entry(key, value))));
        }
        return result;
    }

    @Override public boolean replace(final K key, final V oldValue, final V newValue) {
        final boolean result = map.replace(key, oldValue, newValue);
        if (result) {
            fireMapChangeEvt(new MapChangeEvt<>(ObservableMap.this, MapChangeEvt.MODIFIED, List.of(), List.of(Map.entry(key, newValue)), List.of()));
        }
        return result;
    }

    @Override public V replace(final K key, final V value) {
        final V result = map.replace(key, value);
        if (map.containsKey(key)) {
            fireMapChangeEvt(new MapChangeEvt<>(ObservableMap.this, MapChangeEvt.MODIFIED, List.of(), List.of(Map.entry(key, value)), List.of()));
        }
        return result;
    }

    @Override public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) { return map.computeIfAbsent(key, mappingFunction); }

    @Override public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) { return map.computeIfPresent(key, remappingFunction); }

    @Override public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) { return map.compute(key, remappingFunction); }

    @Override public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) { return map.merge(key, value, remappingFunction); }

    @Override public void forEach(final BiConsumer<? super K, ? super V> action) { map.forEach(action); }

    @Override public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) { map.replaceAll(function); }

    @Override public ObservableMap<K,V> clone() {
        try {
            ObservableMap<K,V> clone = (ObservableMap<K, V>) super.clone();
            clone.putAll(map);
            return clone;
        } catch (Exception e) {
            throw new InternalError(e);
        }
    }


    // ******************** Event Handling ************************************
    public void addMapChangeObserver(final EvtType type, final EvtObserver<MapChangeEvt<K,V>> observer) {
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
    }
    public void removeMapChangeObserver(final EvtType type, final EvtObserver<MapChangeEvt<K,V>> observer) {
        if (observers.containsKey(type)) {
            if (observers.get(type).contains(observer)) {
                observers.get(type).remove(observer);
            }
        }
    }
    public void removeAllMapChangeObservers() { observers.clear(); }

    public void fireMapChangeEvt(final MapChangeEvt<K,V> evt) {
        final EvtType type = evt.getEvtType();
        observers.entrySet().stream().filter(entry -> entry.getKey().equals(MapChangeEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (observers.containsKey(type) && !type.equals(MapChangeEvt.ANY)) {
            observers.get(type).forEach(observer -> observer.handle(evt));
        }
    }
}
