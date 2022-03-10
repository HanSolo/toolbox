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


public class MatrixChangeEvt<T> extends ChangeEvt {
    public static final EvtType<MatrixChangeEvt> ANY                   = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<MatrixChangeEvt> COLUMN_ADDED          = new EvtType<>(MatrixChangeEvt.ANY, "COLUMN_ADDED");
    public static final EvtType<MatrixChangeEvt> COLUMN_REMOVED        = new EvtType<>(MatrixChangeEvt.ANY, "COLUMN_REMOVED");
    public static final EvtType<MatrixChangeEvt> NO_OF_COLUMNS_CHANGED = new EvtType<>(MatrixChangeEvt.ANY, "NO_OF_COLUMNS_CHANGED");
    public static final EvtType<MatrixChangeEvt> COLUMNS_MIRRORED      = new EvtType<>(MatrixChangeEvt.ANY, "COLUMNS_MIRRORED");
    public static final EvtType<MatrixChangeEvt> ROW_ADDED             = new EvtType<>(MatrixChangeEvt.ANY, "ROW_ADDED");
    public static final EvtType<MatrixChangeEvt> ROW_REMOVED           = new EvtType<>(MatrixChangeEvt.ANY, "ROW_REMOVED");
    public static final EvtType<MatrixChangeEvt> NO_OF_ROWS_CHANGED    = new EvtType<>(MatrixChangeEvt.ANY, "NO_OF_ROWS_CHANGED");
    public static final EvtType<MatrixChangeEvt> ROWS_MIRRORED         = new EvtType<>(MatrixChangeEvt.ANY, "ROWS_MIRRORED");

    private final int columns;
    private final int rows;


    // ******************** Constructors **************************************
    public MatrixChangeEvt(final ObservableMatrix<T> src, final EvtType<MatrixChangeEvt> evtType, final int columns, final int rows) {
        super(src, evtType);
        this.columns = columns;
        this.rows    = rows;
    }
    public MatrixChangeEvt(final ObservableMatrix<T> src, final EvtType<? extends MatrixChangeEvt<T>> evtType, final EvtPriority priority, final int columns, final int rows) {
        super(src, evtType, priority);
        this.columns = columns;
        this.rows    = rows;
    }


    // ******************** Methods *******************************************
    @Override public EvtType<? extends MatrixChangeEvt<T>> getEvtType() { return (EvtType<? extends MatrixChangeEvt<T>>) super.getEvtType(); }

    public int getAddedColumns() { return columns; }

    public int getRemovedColumns() { return rows; }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        MatrixChangeEvt<?> that = (MatrixChangeEvt<?>) o;
        return columns == that.columns && rows == that.rows;
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), columns, rows);
    }
}
