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

package eu.hansolo.toolbox.properties;

import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.type.PropertyChangeEvt;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;


public abstract class ReadOnlyProperty<T extends Object> {
    protected CopyOnWriteArrayList<EvtObserver<PropertyChangeEvt<T>>> observers;
    protected Object                                                  bean;
    protected String                                                  name;
    protected T                                                       initialValue;
    protected T                                                       value;
    protected Property<T>                                             propertyToUpdate;
    protected boolean                                                 bidirectional;


    // ******************** Constructors **************************************
    public ReadOnlyProperty() {
        this(null, null, null, null);
    }
    public ReadOnlyProperty(final T value) {
        this(null, null, value, value);
    }
    public ReadOnlyProperty(final Object bean, final String name, final T value) {
        this(bean, name, value, value);
    }
    public ReadOnlyProperty(final Object bean, final String name, final T value, final T initialValue) {
        this.bean             = bean;
        this.name             = name;
        this.value            = value;
        this.initialValue     = initialValue;
        this.propertyToUpdate = null;
        this.bidirectional    = false;
    }


    // ******************** Methods *******************************************
    public final T getValue() { return value; }

    public final T getInitialValue() { return initialValue; }

    public final boolean isSet() { return Objects.equals(getValue(), getInitialValue()); }

    protected void willChange(final T oldValue, final T newValue) {}

    protected void didChange(final T oldValue, final T newValue) {}

    protected void invalidated() {}

    public Object getBean() { return bean; }

    public String getName() { return name; }

    protected void setPropertyToUpdate(final Property<T> property) {
        this.propertyToUpdate = property;
        this.bidirectional    = false;
    }
    protected void unsetPropertyToUpdate() {
        this.propertyToUpdate = null;
        this.bidirectional    = false;
    }


    // ******************** Event Handling ************************************
    public void addOnChange(final EvtObserver<PropertyChangeEvt<T>> observer) {
        addObserver(observer);
    }
    public void addObserver(final EvtObserver<PropertyChangeEvt<T>> observer) {
        if (null == observer) { return; }
        if (null == observers) { observers = new CopyOnWriteArrayList<>(); }
        if (observers.contains(observer)) { return; }
        observers.add(observer);
    }
    public void removeObserver(final EvtObserver<PropertyChangeEvt<T>> observer) {
        if (null == observers || null == observer) { return; }
        if (observers.contains(observer)) { observers.remove(observer); }
    }
    public void removeAllObservers() {
        if (null == observers) { return; }
        observers.clear();
    }

    public void fireEvent(final PropertyChangeEvt<T> evt) {
        if (null == observers || null == evt) { return; }
        observers.forEach(observer -> observer.handle(evt));
    }
}
