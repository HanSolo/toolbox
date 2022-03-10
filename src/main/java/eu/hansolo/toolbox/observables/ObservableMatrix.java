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
import eu.hansolo.toolbox.evt.type.MatrixChangeEvt;
import eu.hansolo.toolbox.evt.type.MatrixItemChangeEvt;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ObservableMatrix<T> {
    private final    Class<T>                                                type;
    private          Map<EvtType, List<EvtObserver<MatrixChangeEvt<T>>>>     matrixObservers;
    private          Map<EvtType, List<EvtObserver<MatrixItemChangeEvt<T>>>> itemObservers;
    private          AtomicReference<T>[][]                                  matrix;
    private volatile int                                                     cols;
    private volatile int                                                     rows;
    private          boolean                                                 colsMirrored;
    private          boolean                                                 rowsMirrored;
    private          boolean                                                 resizeMatrixWhenInnerRowOrColIsRemoved;


    // ******************** Constructors **************************************
    public ObservableMatrix(Class<T> type, final int cols, final int rows) {
        this(type, cols, rows, false);
    }
    public ObservableMatrix(Class<T> type, final int cols, final int rows, final boolean resizeMatrixWhenInnerRowOrColIsRemoved) {
        this.type                                   = type;
        this.matrixObservers                        = new ConcurrentHashMap<>();
        this.itemObservers                          = new ConcurrentHashMap<>();
        this.matrix                                 = createArray(type, cols, rows);
        this.cols                                   = cols;
        this.rows                                   = rows;
        this.colsMirrored                           = false;
        this.rowsMirrored                           = false;
        this.resizeMatrixWhenInnerRowOrColIsRemoved = resizeMatrixWhenInnerRowOrColIsRemoved;
    }
    public ObservableMatrix(final ObservableMatrix<T> copyFromMatrix) {
        this.type                                   = copyFromMatrix.getType();
        this.matrixObservers                        = new ConcurrentHashMap<>();
        this.itemObservers                          = new ConcurrentHashMap<>();
        this.matrix                                 = createArray(type, copyFromMatrix.cols, copyFromMatrix.rows);
        this.cols                                   = copyFromMatrix.cols;
        this.rows                                   = copyFromMatrix.rows;
        this.colsMirrored                           = copyFromMatrix.colsMirrored;
        this.rowsMirrored                           = copyFromMatrix.rowsMirrored;
        this.resizeMatrixWhenInnerRowOrColIsRemoved = copyFromMatrix.resizeMatrixWhenInnerRowOrColIsRemoved;
        for (int y = 0 ; y < rows ; y++) {
            for (int x = 0 ; x < cols ; x++) {
                setItemAt(x, y, copyFromMatrix.getItemAt(x, y));
            }
        }
    }


    // ******************** Methods *******************************************
    /**
     * Returns the type of the matrix elements
     * @return the type of the matrix elements
     */
    public Class<T> getType() { return type; }

    /**
     * Returns the given item in the matrix at the given position defined by x and y
     * @param x Column used to return item
     * @param y Row used to return item
     * @return the given item in the matrix at the given position defined by x and y
     */
    public T getItemAt(final int x, final int y) {
        if (x < 0 || x > (cols - 1) || y < 0 || y > (rows - 1)) { throw new IllegalArgumentException("cols/rows cannot be smaller than 0/0 or larger than " + (cols - 1) + "/" + (rows - 1)); }
        return matrix[x][y].get();
    }

    /**
     * Sets the given item in the matrix at the given position defined by x and y
     * @param x Column where the given item will be inserted
     * @param y Row where the given item will be inserted
     * @param item
     */
    public void setItemAt(final int x, final int y, final T item) {
        setItemAt(x, y, item, true);
    }
    public void setItemAt(final int x, final int y, final T item, final boolean notify) {
        if (x < 0 || x > (cols - 1) || y < 0 || y > (rows - 1)) { throw new IllegalArgumentException("cols/rows cannot be smaller than 0"); }

        T oldItem = matrix[x][y].get();
        matrix[x][y].set(item);

        if (notify) {
            if (null == oldItem && item != null) {
                fireMatrixItemChangeEvt(new MatrixItemChangeEvt<>(ObservableMatrix.this, MatrixItemChangeEvt.ITEM_ADDED, x, y, oldItem, item));
            } else if (null != oldItem && item == null) {
                fireMatrixItemChangeEvt(new MatrixItemChangeEvt<>(ObservableMatrix.this, MatrixItemChangeEvt.ITEM_REMOVED, x, y, oldItem, item));
            } else if (null != oldItem && item != null) {
                fireMatrixItemChangeEvt(new MatrixItemChangeEvt<>(ObservableMatrix.this, MatrixItemChangeEvt.ITEM_CHANGED, x, y, oldItem, item));
            } else {
                return;
            }
        }
    }

    /**
     * Removes item at position defined by x and y
     * Item will be set to null
     * @param x Column where the item will be set to null
     * @param y Column where the item will be set to null
     */
    public void removeItemAt(final int x, final int y) { removeItemAt(x, y, true); }
    public void removeItemAt(final int x, final int y, final boolean notify) {
        if (x < 0 || x > (cols - 1) || y < 0 || y > (rows - 1)) { throw new IllegalArgumentException("cols/rows cannot be smaller than 0"); }
        T oldItem = matrix[x][y].get();
        matrix[x][y].set(null);
        if (notify) {
            fireMatrixItemChangeEvt(new MatrixItemChangeEvt<>(ObservableMatrix.this, MatrixItemChangeEvt.ITEM_REMOVED, x, y, oldItem, null));
        }
        checkForRemovedColumnsAndRows(x, y, notify);
    }

    /**
     * If item is found in matrix it's position in the matrix will be set to null
     * @param item Item to remove from matrix
     */
    public void removeItem(final T item) { removeItem(item, true); }
    public void removeItem(final T item, final boolean notify) {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                T matrixItem = matrix[x][y].get();
                if (null == matrixItem) {
                    continue;
                } else if (matrixItem.equals(item)) {
                    matrix[x][y] = null;
                    if (notify) {
                        fireMatrixItemChangeEvt(new MatrixItemChangeEvt<>(ObservableMatrix.this, MatrixItemChangeEvt.ITEM_REMOVED, x, y, item, null));
                    }
                    checkForRemovedColumnsAndRows(x, y, notify);
                    return;
                }
            }
        }
    }

    /**
     * Returns true if the given item will be found in the matrix
     * @param item
     * @return true if the given item will be found in the matrix
     */
    public boolean contains(final T item) {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (null != matrix[x][y] && matrix[x][y].equals(item)) { return true; }
            }
        }
        return false;
    }

    /**
     * Returns the indices of the given item as an array of int[],
     * where [0] contains the column and
     * [1] contains the row of the item
     * In case the item was not found the method will return [-1, -1]
     * @param item
     * @return the indices of the given item as an array of in[]
     */
    public int[] getIndicesOf(final T item) {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (matrix[x][y].equals(item)) { return new int[]{x, y}; }
            }
        }
        return new int[]{-1, - 1};
    }

    /**
     * Returns the 2-dimensional array of type <T>
     * @return the 2-dimensional array of type <T>
     */
    public AtomicReference<T>[][] getMatrix() { return matrix; }

    /**
     * Returns all items in matrix that are non null as list
     * @return all items in matrix that are non null as list
     */
    public List<AtomicReference<T>> getAllItems() { return stream().filter(Objects::nonNull).collect(Collectors.toList()); }

    /**
     * Returns all items in matrix as stream
     * @return all items in matrix as stream
     */
    public Stream<AtomicReference<T>> stream() { return Arrays.stream(matrix).flatMap(t -> Arrays.stream(t)); }

    public void reset() {
        if (rows == -1 || cols == -1) { throw new IllegalArgumentException("cols/rows cannot be smaller 0"); }
        for (int y = 0 ; y < rows ; y++) {
            for (int x = 0 ; x < cols ; x++) {
                matrix[x][y] = null;
            }
        }
    }

    /**
     * Get all items in column specified by index as list
     * @param col index of column to return as list
     * @return all items in column specified by index as list
     */
    public List<T> getCol(final int col) {
        if (rows == -1 || cols == -1 || col < 0 || col > cols) { throw new IllegalArgumentException("cols/rows cannot be smaller 0"); }
        List<T> c = new ArrayList<>();
        for (int y = 0 ; y < rows ; y++) { c.add(matrix[col][y].get()); }
        return c;
    }

    /**
     * Get all items in row specified by index as list
     * @param row index of row to return as list
     * @return all items in row specified by index as list
     */
    public List<T> getRow(final int row) {
        if (rows == -1 || cols == -1 || row < 0 || row > rows) { throw new IllegalArgumentException("cols/rows cannot be smaller 0"); }
        List<T> r = new ArrayList<>();
        for (int x = 0 ; x < cols ; x++) { r.add(matrix[x][row].get()); }
        return r;
    }

    /**
     * Returns true if all items in given column index equals null
     * @param col
     * @return true if all items in given column index equals null
     */
    public boolean isColEmpty(final int col) {
        List<T> c = getCol(col);
        long count = 0;
        for(T item : c) { if (null != item) { count++; }}
        return 0 == count;
    }

    /**
     * Returns true if all items in given row index equals null
     * @param row
     * @return true if all items in given row index equals null
     */
    public boolean isRowEmpty(final int row) {
        List<T> r = getRow(row);
        long count = 0;
        for (T item : r) { if (null != item) { count++; }}
        return 0 == count;
    }

    /**
     * Returns the number of columns in the matrix
     * @return the number of columsn of the matrix
     */
    public int getNoOfCols() { return cols; }

    /**
     * Sets the number of columsn in the matrix.
     * Existing entries will be copied to the new matrix.
     * If the number of columns is smaller than the old
     * one, all items in columns outside of the new matrix
     * will be lost.
     * @param cols
     */
    public void setCols(final int cols) { setCols(cols, true); }
    public void setCols(final int cols, final boolean notify) {
        if (rows == -1 || cols == -1 || this.cols == -1) { throw new IllegalArgumentException("cols/rows cannot be smaller 0"); }
        AtomicReference<T>[][] oldMatrix = new AtomicReference[cols][rows];

        for (int y = 0 ; y < this.rows ; y++) {
            for (int x = 0 ; x < this.cols ; x++) {
                oldMatrix[x][y].set(matrix[x][y].get());
            }
        }
        int oldCols = this.cols;
        this.cols   = cols;
        matrix = createArray(type, cols, rows);
        int c;
        int r  = rows;
        if (cols > this.cols) {
            c = oldCols;
        } else if (cols < this.cols) {
            c = cols;
        } else {
            c = cols;
        }
        for (int y = 0 ; y < r ; y++) {
            for (int x = 0 ; x < c ; x++) {
                matrix[x][y].set(oldMatrix[x][y].get());
            }
        }
        if (notify) {
            fireMatrixChangeEvt(new MatrixChangeEvt<>(ObservableMatrix.this, MatrixChangeEvt.NO_OF_COLUMNS_CHANGED, cols, -1));
        }
    }

    /**
     * Adds a column at the given position in the matrix and fills it with the items from the itemSupplier
     * @param at position of where to add the new column
     * @param itemSupplier supplier of items
     */
    public void addCol(final int at, final Supplier<T> itemSupplier) { addCol(at, itemSupplier, true); }
    public void addCol(final int at, final Supplier<T> itemSupplier, final boolean notify) {
        if (at < 0 || at > cols) { throw new IllegalArgumentException("index cannot be smaller or larger than cols"); }

        cols++;

        AtomicReference<T>[][] newMatrix = createArray(type, cols, rows);
        for (int y = 0 ; y < rows ; y++) {
            for (int x = 0 ; x < at ; x++) {
                newMatrix[x][y].set(matrix[x][y].get());
            }
        }
        for (int y = 0 ; y < rows ; y++) { newMatrix[at][y].set(itemSupplier.get()); }
        for (int y = 0 ; y < rows ; y++) {
            for (int x = at + 1 ; x < cols ; x++) {
                newMatrix[x][y].set(matrix[x - 1][y].get());
            }
        }

        matrix = newMatrix;

        if (notify) {
            fireMatrixChangeEvt(new MatrixChangeEvt<>(ObservableMatrix.this, MatrixChangeEvt.COLUMN_ADDED, at, -1));
        }
    }

    public void addCol(final int at, final List<T> items) { addCol(at, items, true); }
    public void addCol(final int at, final List<T> items, final boolean notify) {
        if (at < 0 || at > cols) { throw new IllegalArgumentException("index cannot be smaller or larger than cols"); }
        if (items.size() != rows) { throw new IllegalArgumentException("no of items must be equal to number of rows"); }

        cols++;

        AtomicReference<T>[][] newMatrix = createArray(type, cols, rows);
        for (int y = 0 ; y < rows ; y++) {
            for (int x = 0 ; x < at ; x++) {
                newMatrix[x][y].set(matrix[x][y].get());
            }
        }
        for (int y = 0 ; y < rows ; y++) { newMatrix[at][y].set(items.get(y)); }
        for (int y = 0 ; y < rows ; y++) {
            for (int x = at + 1 ; x < cols ; x++) {
                newMatrix[x][y].set(matrix[x - 1][y].get());
            }
        }

        matrix = newMatrix;

        if (notify) {
            fireMatrixChangeEvt(new MatrixChangeEvt<>(ObservableMatrix.this, MatrixChangeEvt.COLUMN_ADDED, at, -1));
        }
    }

    public void addNullCol(final int at) {
        addNullCol(at, true);
    }
    public void addNullCol(final int at, final boolean notify) {
        if (at < 0 || at > cols) { throw new IllegalArgumentException("index cannot be smaller or larger than cols"); }

        cols++;

        AtomicReference<T>[][] newMatrix = createArray(type, cols, rows);
        for (int y = 0 ; y < rows ; y++) {
            for (int x = 0 ; x < at ; x++) {
                newMatrix[x][y].set(matrix[x][y].get());
            }
        }
        for (int y = 0 ; y < rows ; y++) { newMatrix[at][y].set(null); }
        for (int y = 0 ; y < rows ; y++) {
            for (int x = at + 1 ; x < cols ; x++) {
                newMatrix[x][y].set(matrix[x - 1][y].get());
            }
        }

        matrix = newMatrix;
        if (notify) {
            fireMatrixChangeEvt(new MatrixChangeEvt<>(ObservableMatrix.this, MatrixChangeEvt.COLUMN_ADDED, at, -1));
        }
    }

    /**
     * Removes col at given index
     * @param at index of col that should be removed
     */
    public void removeCol(final int at) { removeCol(at, true); }
    public void removeCol(final int at, final boolean notify) {
        if (at < 0 || at > cols) { throw new IllegalArgumentException("index cannot be smaller or larger than cols"); }
        if (cols <= 1) { throw new IllegalArgumentException("there is just one column in the matrix"); }

        for (int y = 0 ; y < getNoOfRows() ; y++) { matrix[at][y] = null; }

        if (0 == at || (cols - 1) == at || resizeMatrixWhenInnerRowOrColIsRemoved) {
            cols--;

            AtomicReference<T>[][] newMatrix = createArray(type, cols, rows);
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x <= cols; x++) {
                    if (x < at) {
                        newMatrix[x][y].set(matrix[x][y].get());
                    } else if (x == at) {

                    } else {
                        newMatrix[x - 1][y].set(matrix[x][y].get());
                    }
                }
            }
            matrix = newMatrix;
        }
        if (notify) {
            fireMatrixChangeEvt(new MatrixChangeEvt<>(ObservableMatrix.this, MatrixChangeEvt.COLUMN_REMOVED, at, -1));
        }
    }

    /**
     * Adds a row at the given position in the matrix and fills it with the items from the itemSupplier
     * @param at position of where to add the new row
     * @param itemSupplier supplier of items
     */
    public void addRow(final int at, final Supplier<T> itemSupplier) { addRow(at, itemSupplier, true); }
    public void addRow(final int at, final Supplier<T> itemSupplier, final boolean notify) {
        if (at < 0 || at > rows) { throw new IllegalArgumentException("index cannot be smaller or larger than rows"); }

        rows++;

        AtomicReference<T>[][] newMatrix = createArray(type, cols, rows);
        for (int y = 0 ; y < at ; y++) {
            for (int x = 0 ; x < cols ; x++) {
                newMatrix[x][y].set(matrix[x][y].get());
            }
        }
        for (int x = 0 ; x < cols ; x++) { newMatrix[x][at].set(itemSupplier.get()); }
        for (int y = at + 1 ; y < rows ; y++) {
            for (int x = 0 ; x < cols ; x++) {
                newMatrix[x][y].set(matrix[x][y - 1].get());
            }
        }

        matrix = newMatrix;

        if (notify) {
            fireMatrixChangeEvt(new MatrixChangeEvt<>(ObservableMatrix.this, MatrixChangeEvt.ROW_ADDED, -1, at));
        }
    }

    public void addRow(final int at, final List<T> items) { addRow(at, items, true); }
    public void addRow(final int at, final List<T> items, final boolean notify) {
        if (at < 0 || at > rows) { throw new IllegalArgumentException("index cannot be smaller or larger than rows"); }
        if (items.size() != cols) { throw new IllegalArgumentException("now of items must be equal to number of columns"); }

        rows++;

        AtomicReference<T>[][] newMatrix = createArray(type, cols, rows);
        for (int y = 0 ; y < at ; y++) {
            for (int x = 0 ; x < cols ; x++) {
                newMatrix[x][y].set(matrix[x][y].get());
            }
        }
        for (int x = 0 ; x < cols ; x++) { newMatrix[x][at].set(items.get(x)); }
        for (int y = at + 1 ; y < rows ; y++) {
            for (int x = 0 ; x < cols ; x++) {
                newMatrix[x][y].set(matrix[x][y - 1].get());
            }
        }

        matrix = newMatrix;

        if (notify) {
            fireMatrixChangeEvt(new MatrixChangeEvt<>(ObservableMatrix.this, MatrixChangeEvt.ROW_ADDED, -1, at));
        }
    }

    public void addNullRow(final int at) {
        addNullRow(at, true);
    }
    public void addNullRow(final int at, final boolean notify) {
        if (at < 0 || at > rows) { throw new IllegalArgumentException("index cannot be smaller or larger than rows"); }

        rows++;

        AtomicReference<T>[][] newMatrix = createArray(type, cols, rows);
        for (int y = 0 ; y < at ; y++) {
            for (int x = 0 ; x < cols ; x++) {
                newMatrix[x][y].set(matrix[x][y].get());
            }
        }
        for (int x = 0 ; x < cols ; x++) { newMatrix[x][at].set(null); }
        for (int y = at + 1 ; y < rows ; y++) {
            for (int x = 0 ; x < cols ; x++) {
                newMatrix[x][y].set(matrix[x][y - 1].get());
            }
        }

        matrix = newMatrix;
        if (notify) {
            fireMatrixChangeEvt(new MatrixChangeEvt<>(ObservableMatrix.this, MatrixChangeEvt.ROW_ADDED, -1, at));
        }
    }

    /**
     * Removes row at given index
     * @param at index of row that should be removed
     */
    public void removeRow(final int at) { removeRow(at, true); }
    public void removeRow(final int at, final boolean notify) {
        if (at < 0 || at > rows) { throw new IllegalArgumentException("index cannot be smaller or larger than rows"); }
        if (rows <= 1) { throw new IllegalArgumentException("there is just one row in the matrix"); }

        for (int x = 0 ; x < getNoOfCols() ; x++) { matrix[x][at] = null; }

        if (0 == at || (rows - 1) == at || resizeMatrixWhenInnerRowOrColIsRemoved) {
            rows--;

            AtomicReference<T>[][] newMatrix = createArray(type, cols, rows);
            for (int y = 0; y <= rows; y++) {
                if (y < at) {
                    for (int x = 0; x < cols; x++) {
                        newMatrix[x][y].set(matrix[x][y].get());
                    }
                } else if (y == at) {

                } else {
                    for (int x = 0; x < cols; x++) {
                        newMatrix[x][y - 1].set(matrix[x][y].get());
                    }
                }
            }
            matrix = newMatrix;
        }

        if (notify) {
            fireMatrixChangeEvt(new MatrixChangeEvt<>(ObservableMatrix.this, MatrixChangeEvt.ROW_REMOVED, -1, at));
        }
    }

    /**
     * Returns the number of rows in the matrix
     * @return the number of rows in the matrix
     */
    public int getNoOfRows() { return rows; }

    /**
     * Sets the number of rows in the matrix.
     * Existing entries will be copied to the new matrix.
     * If the new number of rows is smaller than the old
     * one, all items in rows outside the new matrix will
     * be lost.
     * @param rows
     */
    public void setRows(final int rows) { setRows(rows, true); }
    public void setRows(final int rows, final boolean notify) {
        if (rows == -1 || cols == -1 || this.rows == -1) { throw new IllegalArgumentException("cols/rows cannot be smaller 0"); }
        AtomicReference<T>[][] oldMatrix = (AtomicReference<T>[][]) new Object[cols][rows];
        for (int y = 0 ; y < this.rows ; y++) {
            for (int x = 0 ; x < this.cols ; x++) {
                oldMatrix[x][y].set(matrix[x][y].get());
            }
        }
        int oldRows = this.rows;
        this.rows   = rows;
        matrix = createArray(type, cols, rows);

        int c  = cols;
        int r;
        if (rows > this.rows) {
            r = oldRows;
        } else if (rows < this.rows) {
            r = rows;
        } else {
            r = rows;
        }
        for (int y = 0 ; y < r ; y++) {
            for (int x = 0 ; x < c ; x++) {
                matrix[x][y].set(oldMatrix[x][y].get());
            }
        }
        if (notify) {
            fireMatrixChangeEvt(new MatrixChangeEvt<>(ObservableMatrix.this, MatrixChangeEvt.NO_OF_ROWS_CHANGED, -1, rows));
        }
    }

    public List<List<T>> getAllColumns() {
        List<List<T>> columns = new ArrayList<>();
        for (int i = 0; i < getNoOfCols() ; i++) { columns.add(getCol(i)); }
        return columns;
    }
    public List<Integer> getAllEmptyColumns() {
        List<Integer> emptyColumns = new ArrayList<>();
        for (int x = 0; x < getNoOfCols() ; x++) {
            if (getCol(x).stream().filter(Objects::nonNull).count() == 0) { emptyColumns.add(x); }
        }
        return emptyColumns;
    }

    public List<List<T>> getAllRows() {
        List<List<T>> rows = new ArrayList<>();
        for (int i = 0; i < getNoOfRows() ; i++) { rows.add(getRow(i)); }
        return rows;
    }
    public List<Integer> getAllEmptyRows() {
        List<Integer> emptyRows = new ArrayList<>();
        for (int y = 0; y < getNoOfRows() ; y++) {
            if (getRow(y).stream().filter(Objects::nonNull).count() == 0) { emptyRows.add(y); }
        }
        return emptyRows;
    }

    public void mirrorColumns() { mirrorColumns(true); }
    public void mirrorColumns(final boolean notify) {
        for(int i = 0; i < (matrix.length/2); i++) {
            AtomicReference<T>[] temp = matrix[i];
            matrix[i] = matrix[matrix.length - i - 1];
            matrix[matrix.length - i - 1] = temp;
        }
        colsMirrored = !colsMirrored;
        if (notify) {
            fireMatrixChangeEvt(new MatrixChangeEvt<>(ObservableMatrix.this, MatrixChangeEvt.COLUMNS_MIRRORED, cols, -1));
        }
    }

    public void mirrorRows() { mirrorRows(true); }
    public void mirrorRows(final boolean notify) {
        for (int j = 0; j < matrix.length; ++j) {
            AtomicReference<T>[] row = matrix[j];
            for(int i = 0; i < (row.length/2); i++) {
                T temp = row[i].get();
                row[i] = matrix[j][row.length - i - 1];
                row[row.length - i - 1].set(temp);
            }
        }
        rowsMirrored = !rowsMirrored;
        if (notify) {
            fireMatrixChangeEvt(new MatrixChangeEvt<>(ObservableMatrix.this, MatrixChangeEvt.ROWS_MIRRORED, -1, rows));
        }
    }

    public boolean getColsMirrored() { return colsMirrored; }
    public boolean getRowsMirrored() { return rowsMirrored; }

    public boolean getResizeMatrixWhenInnerRowOrColIsRemoved() { return resizeMatrixWhenInnerRowOrColIsRemoved; }
    public void setResizeMatrixWhenInnerRowOrColIsRemoved(final boolean resize) { resizeMatrixWhenInnerRowOrColIsRemoved = resize; }

    public boolean isEmpty() {
        return getAllEmptyColumns().size() == getNoOfCols() && getAllEmptyRows().size() == getNoOfRows();
    }


    // ******************** Private methods ***********************************
    /**
     * Method to reduce matrix size in case the first column is empty (all items == null)
     */
    private void shiftLeft() {
        for (int y = 0 ; y < rows ; y++) {
            for (int x = 1 ; x < cols ; x++) {
                matrix[x - 1][y] = matrix[x][y];
            }
        }
    }

    /**
     * Method to reduce matrix size in case the first row is empty (all items == null)
     */
    private void shiftUp() {
        for (int y = 1 ; y < rows ; y++) {
            for (int x = 0 ; x < cols ; x++) {
                matrix[x][y - 1] = matrix[x][y];
            }
        }
    }

    /**
     * Returns a 2-dimensional array of the given type <T> and size
     * @param type Class of generic type
     * @param cols Number of columns for the matrix
     * @param rows Number of rows for the matrix
     * @param <T>
     * @return a 2-dimensional array of the given type <T> and size
     */
    private static <T> AtomicReference<T>[][] createArray(final Class type, final int cols, final int rows) {
        if (null == type) { throw new IllegalArgumentException("type cannot be null"); }
        if ( cols < 1 || rows < 1) { throw new IllegalArgumentException("cols/rows cannot be smaller than 1"); }

        AtomicReference<T>[][] emptyMatrix = new AtomicReference[cols][rows];
        for (int y = 0 ; y < rows ; y++) {
            for (int x = 0 ; x < cols ; x++) {
                emptyMatrix[x][y] = new AtomicReference<>();
            }
        }
        return emptyMatrix;
    }

    private void checkForRemovedColumnsAndRows(final int removedItemCol, final int removedItemRow, final boolean notify) {
        int nullItemCounter = 0;
        for (int r = 0 ; r < rows ; r++) {
            if (null == getItemAt(removedItemCol, r)) { nullItemCounter++; }
        }
        if (nullItemCounter == rows) {
            removeCol(removedItemCol, notify);
            return;
        }

        nullItemCounter = 0;
        for (int c = 0 ; c < cols ; c++) {
            if (null == getItemAt(c, removedItemRow)) { nullItemCounter++; }
        }
        if (nullItemCounter == cols) {
            removeRow(removedItemRow, notify);
            return;
        }
    }



    // ******************** Event Handling ************************************
    // Matrix events
    public void addMatrixChangeObserver(final EvtType type, final EvtObserver<MatrixChangeEvt<T>> observer) {
        if (!matrixObservers.containsKey(type)) { matrixObservers.put(type, new CopyOnWriteArrayList<>()); }
        if (matrixObservers.get(type).contains(observer)) { return; }
        matrixObservers.get(type).add(observer);
    }
    public void removeMatrixChangeObserver(final EvtType type, final EvtObserver<MatrixChangeEvt<T>> observer) {
        if (matrixObservers.containsKey(type)) {
            if (matrixObservers.get(type).contains(observer)) {
                matrixObservers.get(type).remove(observer);
            }
        }
    }
    public void removeAllMatrixChangeObservers() { matrixObservers.clear(); }

    public void fireMatrixChangeEvt(final MatrixChangeEvt<T> evt) {
        final EvtType type = evt.getEvtType();
        matrixObservers.entrySet().stream().filter(entry -> entry.getKey().equals(MatrixChangeEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (matrixObservers.containsKey(type) && !type.equals(MatrixChangeEvt.ANY)) {
            matrixObservers.get(type).forEach(observer -> observer.handle(evt));
        }
    }

    // Matrix item events
    public void addMatrixItemChangeObserver(final EvtType type, final EvtObserver<MatrixItemChangeEvt<T>> observer) {
        if (!itemObservers.containsKey(type)) { itemObservers.put(type, new CopyOnWriteArrayList<>()); }
        if (itemObservers.get(type).contains(observer)) { return; }
        itemObservers.get(type).add(observer);
    }
    public void removeMatrixItemChangeObserver(final EvtType type, final EvtObserver<MatrixItemChangeEvt<T>> observer) {
        if (itemObservers.containsKey(type)) {
            if (itemObservers.get(type).contains(observer)) {
                itemObservers.get(type).remove(observer);
            }
        }
    }
    public void removeAllMatrixItemChangeObservers() { itemObservers.clear(); }

    public void fireMatrixItemChangeEvt(final MatrixItemChangeEvt<T> evt) {
        final EvtType type = evt.getEvtType();
        itemObservers.entrySet().stream().filter(entry -> entry.getKey().equals(MatrixItemChangeEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (itemObservers.containsKey(type) && !type.equals(MatrixItemChangeEvt.ANY)) {
            itemObservers.get(type).forEach(observer -> observer.handle(evt));
        }
    }


    @Override public String toString() {
        StringBuilder output = new StringBuilder();
        for (int y = 0 ; y < rows ; y++) {
            for (int x = 0 ; x < cols ; x++) {
                output.append(matrix[x][y]).append(" ");
            }
            output.append("\n");
        }
        return output.toString();
    }
}

