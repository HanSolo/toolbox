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

import eu.hansolo.toolbox.evt.EvtPriority;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.observables.ObservableMatrix;

import java.util.Objects;


public class MatrixItemChangeEvt<T> extends ChangeEvt {
    public static final EvtType<MatrixItemChangeEvt> ANY          = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<MatrixItemChangeEvt> ITEM_ADDED   = new EvtType<>(MatrixItemChangeEvt.ANY, "ITEM_ADDED");
    public static final EvtType<MatrixItemChangeEvt> ITEM_CHANGED = new EvtType<>(MatrixItemChangeEvt.ANY, "ITEM_CHANGED");
    public static final EvtType<MatrixItemChangeEvt> ITEM_REMOVED = new EvtType<>(MatrixItemChangeEvt.ANY, "ITEM_REMOVED");

    private final T   oldItem;
    private final T   item;
    private final int x;
    private final int y;


    // ******************** Constructors **************************************
    public MatrixItemChangeEvt(final ObservableMatrix<T> src, final EvtType<MatrixItemChangeEvt> evtType, final int x, final int y, final T oldItem, final T item) {
        super(src, evtType);
        this.x       = x;
        this.y       = y;
        this.oldItem = oldItem;
        this.item    = item;
    }
    public MatrixItemChangeEvt(final ObservableMatrix<T> src, final EvtType<? extends MatrixItemChangeEvt<T>> evtType, final EvtPriority priority, final int x, final int y, final T oldItem, final T item) {
        super(src, evtType, priority);
        this.x       = x;
        this.y       = y;
        this.oldItem = oldItem;
        this.item    = item;
    }


    // ******************** Methods *******************************************
    @Override public EvtType<? extends MatrixItemChangeEvt<T>> getEvtType() { return (EvtType<? extends MatrixItemChangeEvt<T>>) super.getEvtType(); }

    public int getX() { return x; }

    public int getY() { return y; }

    public T getOldItem() { return oldItem; }

    public T getItem() { return item; }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        MatrixItemChangeEvt<?> that = (MatrixItemChangeEvt<?>) o;
        return Objects.equals(oldItem, that.oldItem) && Objects.equals(item, that.item) && x == that.x && y == that.y;
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), oldItem, item, x, y);
    }
}
