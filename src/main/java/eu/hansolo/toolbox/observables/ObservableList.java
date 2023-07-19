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
import eu.hansolo.toolbox.evt.type.ListChangeEvt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;


public class ObservableList<T> implements List<T>, RandomAccess, Cloneable {
    private final CopyOnWriteArrayList<T>                                          list;
    private       Map<EvtType<? extends Evt>, List<EvtObserver<ListChangeEvt<T>>>> observers;


    // ******************** Constructors **************************************
    public ObservableList() {
        this.list = new CopyOnWriteArrayList<>();
    }
    public ObservableList(final Collection<? extends T> collection) {
        this.list = new CopyOnWriteArrayList<>(collection);
    }
    public ObservableList(final T[] array) {
        this.list = new CopyOnWriteArrayList<>(array);
    }


    // ******************** Methods *******************************************
    @Override public T get(final int index) { return list.get(index); }

    @Override public T set(final int index, final T element) {
        final List<T> removedElements = List.of(list.get(index));
        final List<T> addedElements   = List.of(list.set(index, element));
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.CHANGED, addedElements, removedElements));
        return addedElements.get(0);
    }


    @Override public boolean add(final T element) {
        final boolean result = list.add(element);
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.ADDED, result ? List.of(element) : List.of(), List.of()));
        return result;
    }

    @Override public void add(final int index, final T element) {
        list.add(index, element);
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.ADDED, List.of(element), List.of()));
    }

    @Override public boolean addAll(final Collection<? extends T> collection) {
        final boolean result = list.addAll(collection);
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.ADDED, result ? new ArrayList<>(collection) : List.of(), List.of()));
        return result;
    }

    @Override public boolean addAll(final int index, final Collection<? extends T> collection) {
        final boolean result = list.addAll(index, collection);
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.ADDED, result ? new ArrayList<>(collection) : List.of(), List.of()));
        return result;
    }

    public boolean addIfAbsent(final T element) {
        final boolean result = list.addIfAbsent(element);
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.ADDED, result ? List.of(element) : List.of(), List.of()));
        return result;
    }

    public int addAllAbsent(final Collection<T> collection) {
        final List<T> addedElements = list.stream().filter(element -> !collection.contains(element)).collect(Collectors.toList());
        final int result = list.addAllAbsent(collection);
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.ADDED, addedElements, List.of()));
        return result;
    }


    @Override public T remove(final int index) {
        final T element = list.remove(index);
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.REMOVED, List.of(), List.of(element)));
        return element;
    }

    @Override public boolean remove(final Object obj) {
        final boolean result          = list.remove(obj);
        final List<T> removedElements = new ArrayList<>();
        if (result) { removedElements.add((T) obj); }
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.REMOVED, List.of(), removedElements));
        return result;
    }

    @Override public boolean removeAll(final Collection<?> collection) {
        final boolean result = list.removeAll(collection);
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.REMOVED, List.of(), result ? new ArrayList<>((Collection<? extends T>) collection) : List.of()));
        return result;
    }

    @Override public void clear() {
        final ObservableList<T> clone = clone();
        list.clear();
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.REMOVED, List.of(), clone));
    }


    @Override public boolean retainAll(final Collection<?> collection) {
        final ObservableList<T> clone           = clone();
        final boolean           result          = list.retainAll(collection);
        final List<T>           removedElements = clone.stream().filter(element -> !list.contains(element)).collect(Collectors.toList());
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.REMOVED, List.of(), removedElements));
        return result;
    }


    @Override public boolean contains(final Object obj) { return list.contains(obj); }

    @Override public boolean containsAll(final Collection<?> collection) { return list.containsAll(collection); }

    @Override public int indexOf(final Object obj) { return list.indexOf(obj); }

    public int indexOf(final T element, final int index) { return list.indexOf(element, index); }

    @Override public int lastIndexOf(final Object obj) { return list.lastIndexOf(obj); }

    public int lastIndexOf(final T element, final int index) { return list.lastIndexOf(element, index); }


    @Override public int size() { return list.size(); }

    @Override public boolean isEmpty() { return list.isEmpty(); }


    @Override public Object[] toArray() { return list.toArray(); }

    @Override public <U> U[] toArray(final U[] a) { return list.toArray(a); }


    @Override public Iterator<T> iterator() { return list.iterator(); }

    @Override public ListIterator<T> listIterator() { return list.listIterator(); }

    @Override public ListIterator<T> listIterator(final int index) { return list.listIterator(index); }

    @Override public Spliterator<T> spliterator() { return list.spliterator(); }


    @Override public List<T> subList(final int fromIndex, final int toIndex) { return list.subList(fromIndex, toIndex); }


    @Override public boolean equals(final Object obj) { return list.equals(obj); }

    @Override public int hashCode() { return list.hashCode(); }

    @Override public ObservableList<T> clone() {
        try {
            ObservableList<T> clone = new ObservableList<>();
            clone.addAll(list);
            return clone;
        } catch (Exception e) {
            throw new InternalError();
        }
    }


    public void forEach(final Consumer<? super T> action) { list.forEach(action); }

    public boolean removeIf(final Predicate<? super T> filter) {
        final boolean result = list.removeIf(filter);
        if (result) { fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.REMOVED, null, null)); }
        return result;
    }

    public void replaceAll(final UnaryOperator<T> operator) {
        list.replaceAll(operator);
        fireListChangeEvt(new ListChangeEvt<>(ObservableList.this, ListChangeEvt.CHANGED, null, null));
    }

    public void sort(final Comparator<? super T> comparator) { list.sort(comparator); }


    @Override public String toString() { return list.toString(); }


    // ******************** Event Handling ************************************
    public void addListChangeObserver(final EvtType<? extends Evt> type, final EvtObserver<ListChangeEvt<T>> observer) {
        if (null == type || null == observer) { return; }
        if (null == observers) { observers = new ConcurrentHashMap<>(); }
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
    }
    public void removeListChangeObserver(final EvtType<? extends Evt> type, final EvtObserver<ListChangeEvt<T>> observer) {
        if (null == observers || null == type || null == observer) { return; }
        if (observers.containsKey(type)) {
            if (observers.get(type).contains(observer)) {
                observers.get(type).remove(observer);
            }
        }
    }
    public void removeAllListChangeObservers() {
        if (null == observers) { return; }
        observers.clear();
    }

    public void fireListChangeEvt(final ListChangeEvt<T> evt) {
        if (null == observers) { return; }
        // Call all observers that have subscribed to specific event types
        observers.entrySet().stream()
                            .filter(entry -> !entry.getKey().equals(ListChangeEvt.ANY))
                            .filter(entry -> entry.getValue().contains(evt.getEvtType()))
                            .forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        // Call all observers that have subscribed to ANY event type
        observers.entrySet().stream()
                            .filter(entry -> entry.getKey().equals(ListChangeEvt.ANY))
                            .forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
    }
}
