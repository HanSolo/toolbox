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

package eu.hansolo.toolbox;


import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.evt.type.ListChangeEvt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;


public class ObservableList<T> extends ArrayList<T> implements List<T>, RandomAccess, Cloneable {
    private static final int                                            DEFAULT_CAPACITY = 16;
    private        final ArrayList<T>                                   list;
    private              Map<EvtType, List<EvtObserver<ListChangeEvt>>> observers;


    // ******************** Constructors **************************************
    public ObservableList() {
        this(DEFAULT_CAPACITY);
    }
    public ObservableList(final int capacity) {
        this.list      = new ArrayList<>(capacity);
        this.observers = new ConcurrentHashMap<>();
    }
    public ObservableList(final List<T> list) {
        this.list      = new ArrayList<>(list);
        this.observers = new ConcurrentHashMap<>();
    }


    // ******************** Methods *******************************************
    public T get(final int index) { return list.get(index); }

    public T set(final int index, final T element) {
        final List<T> removedItems = List.of(list.get(index));
        final List<T> addedItems   = List.of(list.set(index, element));
        fireListChangeEvt(new ListChangeEvt(ObservableList.this, ListChangeEvt.CHANGED, addedItems, removedItems));
        return addedItems.get(0);
    }


    public boolean add(final T element) {
        final boolean result = list.add(element);
        fireListChangeEvt(new ListChangeEvt(ObservableList.this, ListChangeEvt.ADDED, result ? List.of(element) : List.of(), List.of()));
        return result;
    }

    public void add(final int index, final T element) {
        list.add(index, element);
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.ADDED, List.of(element), List.of()));
    }

    public boolean addAll(final Collection<? extends T> collection) {
        final boolean result = list.addAll(collection);
        fireListChangeEvt(new ListChangeEvt<T>(ObservableList.this, ListChangeEvt.ADDED, result ? new ArrayList<>(collection) : List.of(), List.of()));
        return result;
    }

    public boolean addAll(final int index, final Collection<? extends T> collection) {
        final boolean result = list.addAll(index, collection);
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.ADDED, result ? new ArrayList<>(collection) : List.of(), List.of()));
        return result;
    }


    public T remove(final int index) {
        final T e = list.remove(index);
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.REMOVED, List.of(), List.of(e)));
        return e;
    }

    public boolean remove(final Object obj) {
        final boolean result = list.remove(obj);
        final List<T> removedItems = new ArrayList<>();
        if (result) { removedItems.add((T) obj); }
        fireListChangeEvt(new ListChangeEvt<T>(ObservableList.this, ListChangeEvt.REMOVED, List.of(), removedItems));
        return result;
    }

    public boolean removeAll(final Collection<?> collection) {
        final boolean result = list.removeAll(collection);
        fireListChangeEvt(new ListChangeEvt<T>(ObservableList.this, ListChangeEvt.REMOVED, List.of(), result ? new ArrayList<>((Collection<? extends T>) collection) : List.of()));
        return result;
    }

    public void clear() {
        ObservableList<T> clone = clone();
        list.clear();
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.REMOVED, List.of(), clone));
    }


    public boolean retainAll(final Collection<?> collection) {
        final ObservableList<T> clone        = clone();
        final boolean           result       = list.retainAll(collection);
        final List<T>           removedItems = clone.stream().filter(item -> !list.contains(item)).collect(Collectors.toList());
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.REMOVED, List.of(), removedItems));
        return result;
    }


    public boolean contains(final Object obj) { return indexOf(obj) >= 0; }

    public int indexOf(final Object obj) { return list.indexOf(obj); }

    public int lastIndexOf(final Object obj) { return list.lastIndexOf(obj); }


    public void trimToSize() { list.trimToSize(); }

    public void ensureCapacity(final int minCapacity) { list.ensureCapacity(minCapacity); }

    public int size() { return list.size(); }

    public boolean isEmpty() { return list.isEmpty(); }


    public Object[] toArray() { return list.toArray(); }

    public <T> T[] toArray(final T[] a) { return list.toArray(a); }


    public ListIterator<T> listIterator(final int index) { return list.listIterator(index); }

    public ListIterator<T> listIterator() { return list.listIterator(); }

    public Iterator<T> iterator() { return list.iterator(); }


    public List<T> subList(final int fromIndex, final int toIndex) { return list.subList(fromIndex, toIndex); }


    @Override public boolean equals(final Object obj) { return list.equals(obj); }

    @Override public int hashCode() { return Objects.hash(super.hashCode(), list); }

    @Override public ObservableList<T> clone() {
        try {
            ObservableList<T> clone = new ObservableList<>();
            clone.addAll(list);
            return clone;
        } catch (Exception e) {
            throw new InternalError(e);
        }
    }

    @Override public void forEach(final Consumer<? super T> action) { list.forEach(action); }

    @Override public Spliterator<T> spliterator() { return list.spliterator(); }

    @Override public boolean removeIf(final Predicate<? super T> filter) {
        final boolean result = list.removeIf(filter);
        if (result) { fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.REMOVED, null, null)); }
        return list.removeIf(filter);
    }

    @Override public void replaceAll(final UnaryOperator<T> operator) {
        list.replaceAll(operator);
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.CHANGED, null, null));
    }

    @Override public void sort(final Comparator<? super T> comparator) {
        list.sort(comparator);
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.CHANGED, List.of(), List.of()));
    }


    // ******************** Event Handling ************************************
    public void addListChangeObserver(final EvtType type, final EvtObserver<ListChangeEvt> observer) {
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
    }
    public void removeListChangeObserver(final EvtType type, final EvtObserver<ListChangeEvt> observer) {
        if (observers.containsKey(type)) {
            if (observers.get(type).contains(observer)) {
                observers.get(type).remove(observer);
            }
        }
    }
    public void removeAllListChangeObservers() { observers.clear(); }

    public void fireListChangeEvt(final ListChangeEvt evt) {
        final EvtType type = evt.getEvtType();
        observers.entrySet().stream().filter(entry -> entry.getKey().equals(ListChangeEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (observers.containsKey(type) && !type.equals(ListChangeEvt.ANY)) {
            observers.get(type).forEach(observer -> observer.handle(evt));
        }
    }
}
