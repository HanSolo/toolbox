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

import eu.hansolo.toolbox.evt.Evt;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.evt.type.MapChangeEvt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;


public class ObservableMap<K,V> implements Map<K,V>, Cloneable {
    private static final int                                                               DEFAULT_CAPACITY    = 16;
    private static final float                                                             DEFAULT_LOAD_FACTOR = 0.75f;
    private        final ConcurrentHashMap<K,V>                                            map;
    private              Map<EvtType<? extends Evt>, List<EvtObserver<MapChangeEvt<K,V>>>> observers;


    // ******************** Constructors **************************************
    public ObservableMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
    public ObservableMap(final int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }
    public ObservableMap(final int capacity, final float loadFactor) {
        this.map = new ConcurrentHashMap<>(capacity, loadFactor);
    }
    public ObservableMap(final int capacity, final float loadFactor, final int concurrencyLevel) {
        this.map = new ConcurrentHashMap<>(capacity, loadFactor, concurrencyLevel);
    }
    public ObservableMap(final Map<? extends K, ? extends V> map) {
        this.map = new ConcurrentHashMap<>(map);
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

    public boolean contains(final Object value) { return containsValue(value); }


    public KeySetView<K,V> keySet() { return map.keySet(); }

    public KeySetView<K,V> keySet(final V mappedValue) { return map.keySet(mappedValue); }

    public Collection<V> values() { return map.values(); }

    public Set<Map.Entry<K,V>> entrySet() { return map.entrySet(); }

    public Enumeration<K> keys() { return map.keys(); }

    public Enumeration<V> elements() { return map.elements(); }

    public long mappingCount() { return map.mappingCount(); }


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

    public void forEach(final long parallelismThreshold, BiConsumer<? super K, ? super V> action) { map.forEach(action); }

    public <U> void forEach(final long parallelismThreshold, final BiFunction<? super K, ? super V, ? extends U> transformer, final Consumer<? super U> action) {
        map.forEach(parallelismThreshold, transformer, action);
    }

    public <U> U search(final long parallelismThreshold, final BiFunction<? super K, ? super V, ? extends U> searchFunction) {
        return map.search(parallelismThreshold, searchFunction);
    }

    public <U> U reduce(final long parallelismThreshold, final BiFunction<? super K, ? super V, ? extends U> transformer, final BiFunction<? super U, ? super U, ? extends U> reducer) {
        return map.reduce(parallelismThreshold, transformer, reducer);
    }

    public double reduceToDouble(final long parallelismThreshold, final ToDoubleBiFunction<? super K, ? super V> transformer, final double basis, final DoubleBinaryOperator reducer) {
        return map.reduceToDouble(parallelismThreshold, transformer, basis, reducer);
    }

    public long reduceToLong(final long parallelismThreshold, final ToLongBiFunction<? super K, ? super V> transformer, final long basis, final LongBinaryOperator reducer) {
        return map.reduceToLong(parallelismThreshold, transformer, basis, reducer);
    }

    public int reduceToInt(final long parallelismThreshold, final ToIntBiFunction<? super K, ? super V> transformer, final int basis, final IntBinaryOperator reducer) {
        return map.reduceToInt(parallelismThreshold, transformer, basis, reducer);
    }

    public void forEachKey(final long parallelismThreshold, final Consumer<? super K> action) { map.forEachKey(parallelismThreshold, action); }

    public <U> void forEachKey(final long parallelismThreshold, final Function<? super K, ? extends U> transformer, final Consumer<? super U> action) { map.forEachKey(parallelismThreshold, transformer, action); }

    public <U> U searchKeys(final long parallelismThreshold, final Function<? super K, ? extends U> searchFunction) { return map.searchKeys(parallelismThreshold, searchFunction); }

    public K reduceKeys(final long parallelismThreshold, final BiFunction<? super K, ? super K, ? extends K> reducer) { return map.reduceKeys(parallelismThreshold, reducer); }

    public <U> U reduceKeys(final long parallelismThreshold, final Function<? super K, ? extends U> transformer, final BiFunction<? super U, ? super U, ? extends U> reducer) {
        return map.reduceKeys(parallelismThreshold, transformer, reducer);
    }

    public double reduceKeysToDouble(final long parallelismThreshold, final ToDoubleFunction<? super K> transformer, final double basis, final DoubleBinaryOperator reducer) {
        return map.reduceKeysToDouble(parallelismThreshold, transformer, basis, reducer);
    }

    public long reduceKeysToLong(final long parallelismThreshold, final ToLongFunction<? super K> transformer, final long basis, final LongBinaryOperator reducer) {
        return map.reduceKeysToLong(parallelismThreshold, transformer, basis, reducer);
    }

    public int reduceKeysToInt(final long parallelismThreshold, final ToIntFunction<? super K> transformer, final int basis, final IntBinaryOperator reducer) {
        return map.reduceKeysToInt(parallelismThreshold, transformer, basis, reducer);
    }

    public void forEachValue(final long parallelismThreshold, final Consumer<? super V> action) { map.forEachValue(parallelismThreshold, action); }

    public <U> void forEachValue(final long parallelismThreshold, final Function<? super V, ? extends U> transformer, final Consumer<? super U> action) {
        map.forEachValue(parallelismThreshold, transformer, action);
    }

    public <U> U searchValues(final long parallelismThreshold, final Function<? super V, ? extends U> searchFunction) {
        return map.searchValues(parallelismThreshold, searchFunction);
    }

    public V reduceValues(final long parallelismThreshold, final BiFunction<? super V, ? super V, ? extends V> reducer) {
        return map.reduceValues(parallelismThreshold, reducer);
    }

    public <U> U reduceValues(final long parallelismThreshold, final Function<? super V, ? extends U> transformer, final BiFunction<? super U, ? super U, ? extends U> reducer) {
        return map.reduceValues(parallelismThreshold, transformer, reducer);
    }

    public double reduceValuesToDouble(final long parallelismThreshold, final ToDoubleFunction<? super V> transformer, final double basis, final DoubleBinaryOperator reducer) {
        return map.reduceValuesToDouble(parallelismThreshold, transformer, basis, reducer);
    }

    public long reduceValuesToLong(final long parallelismThreshold, final ToLongFunction<? super V> transformer, final long basis, final LongBinaryOperator reducer) {
        return map.reduceValuesToLong(parallelismThreshold, transformer, basis, reducer);
    }

    public int reduceValuesToInt(final long parallelismThreshold, final ToIntFunction<? super V> transformer, final int basis, final IntBinaryOperator reducer) {
        return map.reduceValuesToInt(parallelismThreshold, transformer, basis, reducer);
    }

    public void forEachEntry(final long parallelismThreshold, final Consumer<? super Map.Entry<K,V>> action) { map.forEachEntry(parallelismThreshold, action); }

    public <U> void forEachEntry(final long parallelismThreshold, final Function<Map.Entry<K,V>, ? extends U> transformer, final Consumer<? super U> action) {
        map.forEachEntry(parallelismThreshold, transformer, action);
    }

    public <U> U searchEntries(final long parallelismThreshold, final Function<Map.Entry<K,V>, ? extends U> searchFunction) {
        return map.searchEntries(parallelismThreshold, searchFunction);
    }

    public Map.Entry<K,V> reduceEntries(final long parallelismThreshold, final BiFunction<Map.Entry<K,V>, Map.Entry<K,V>, ? extends Map.Entry<K,V>> reducer) {
        return map.reduceEntries(parallelismThreshold, reducer);
    }

    public <U> U reduceEntries(final long parallelismThreshold, final Function<Map.Entry<K,V>, ? extends U> transformer, final BiFunction<? super U, ? super U, ? extends U> reducer) {
        return map.reduceEntries(parallelismThreshold, transformer, reducer);
    }

    public double reduceEntriesToDouble(final long parallelismThreshold, final ToDoubleFunction<Map.Entry<K,V>> transformer, final double basis, final DoubleBinaryOperator reducer) {
        return map.reduceEntriesToDouble(parallelismThreshold, transformer, basis, reducer);
    }

    public long reduceEntriesToLong(final long parallelismThreshold, final ToLongFunction<Map.Entry<K,V>> transformer, final long basis, final LongBinaryOperator reducer) {
        return map.reduceEntriesToLong(parallelismThreshold, transformer, basis, reducer);
    }

    public int reduceEntriesToInt(final long parallelismThreshold, final ToIntFunction<Map.Entry<K,V>> transformer, final int basis, final IntBinaryOperator reducer) {
        return map.reduceEntriesToInt(parallelismThreshold, transformer, basis, reducer);
    }

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

    @Override public boolean equals(final Object obj) { return map.equals(obj); }

    @Override public int hashCode() { return map.hashCode(); }

    @Override public String toString() { return map.toString(); }


    // ******************** Event Handling ************************************
    public void addMapChangeObserver(final EvtType<? extends Evt> type, final EvtObserver<MapChangeEvt<K,V>> observer) {
        if (null == type || null == observer) { return; }
        if (null == observers) { this.observers = new ConcurrentHashMap<>(); }
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
    }
    public void removeMapChangeObserver(final EvtType<? extends Evt> type, final EvtObserver<MapChangeEvt<K,V>> observer) {
        if (null == observers || null == type || null == observer) { return; }
        if (observers.containsKey(type)) {
            if (observers.get(type).contains(observer)) {
                observers.get(type).remove(observer);
            }
        }
    }
    public void removeAllMapChangeObservers() {
        if (null == observers) { return; }
        observers.clear();
    }

    public void fireMapChangeEvt(final MapChangeEvt<K,V> evt) {
        if (null == observers) { return; }
        // Call all observers that have subscribed to specific event types
        observers.entrySet().stream()
                            .filter(entry -> !entry.getKey().equals(MapChangeEvt.ANY))
                            .forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        // Call all observers that have subscribed to ANY event type
        observers.entrySet().stream()
                            .filter(entry -> entry.getKey().equals(MapChangeEvt.ANY))
                            .forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
    }
}
