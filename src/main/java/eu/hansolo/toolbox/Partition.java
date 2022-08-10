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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public final class Partition<T> extends AbstractList<List<T>> {
    private final List<T> list;
    private final int     chunkSize;


    // ******************** Constructors **************************************
    public Partition(final Collection<T> list, final int chunkSize) {
        this.list      = new ArrayList<>(list);
        this.chunkSize = chunkSize;
    }


    // ******************** Methods *******************************************
    public static <T> Partition<T> ofSize(List<T> list, int chunkSize) { return new Partition<>(list, chunkSize); }


    @Override public List<T> get(final int index) {
        final int start = index * chunkSize;
        final int end   = Math.min(start + chunkSize, list.size());

        if (start > end) { throw new IndexOutOfBoundsException("Index " + index + " is out of the list range <0," + (size() - 1) + ">"); }

        return new ArrayList<>(list.subList(start, end));
    }

    @Override public int size() { return (int) Math.ceil((double) list.size() / (double) chunkSize); }
}
