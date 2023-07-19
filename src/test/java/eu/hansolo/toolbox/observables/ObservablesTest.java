/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 Gerrit Grunwald.
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

import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.evt.type.ListChangeEvt;
import eu.hansolo.toolbox.evt.type.MapChangeEvt;
import eu.hansolo.toolbox.evt.type.MatrixItemChangeEvt;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;


public class ObservablesTest {

    @Test
    void testObservableList() {
        System.out.println("\n-------------------- observable list demo --------------------");
        ObservableList<String> observableList = new ObservableList<>();
        assert observableList.isEmpty();

        observableList.addListChangeObserver(ListChangeEvt.ANY, e -> {
            EvtType<? extends ListChangeEvt<String>> type = e.getEvtType();
            if (ListChangeEvt.CHANGED.equals(type)) {
                System.out.println("List changed");
            } else if (ListChangeEvt.ADDED.equals(type)) {
                e.getAddedElements().forEach(item -> System.out.println("Added: " + item));
            } else if (ListChangeEvt.REMOVED.equals(type)) {
                e.getRemovedElements().forEach(item -> System.out.println("Removed: " + item));
            }
        });
        System.out.println("---------- adding ----------");
        observableList.add("Gerrit");
        observableList.add("Sandra");
        observableList.add("Lilli");
        observableList.add("Anton");
        observableList.add("Neo");
        assert observableList.contains("Gerrit");
        assert observableList.contains("Neo");

        System.out.println("---------- remove 1 ----------");
        observableList.remove("Neo");
        assert !observableList.contains("Neo");
        assert observableList.size() == 4;

        System.out.println("---------- add list of 3 ----------");
        observableList.addAll(List.of("Test", "Test2", "Test3"));
        assert observableList.contains("Test2");
        assert observableList.size() == 7;

        System.out.println("---------- remove 1 ----------");
        observableList.remove("Test2");
        assert observableList.size() == 6;

        System.out.println("---------- add 1 ----------");
        observableList.add(2, "Neo");
        assert observableList.contains("Neo");
        assert observableList.indexOf("Neo") == 2;

        System.out.println("---------- print all ----------");
        observableList.forEach(item -> System.out.println(item));

        System.out.println("---------- retain all (Gerrit, Sandra, Lilli, Anton, Neo) ----------");
        List<String> keep = List.of("Gerrit", "Sandra", "Lilli", "Anton", "Neo");
        observableList.retainAll(keep);
        assert observableList.size() == 5;

        System.out.println("---------- clear ----------");
        observableList.clear();
        assert observableList.isEmpty();
    }

    @Test
    void testObservableMap() {
        System.out.println("\n-------------------- observable map demo --------------------");
        ObservableMap<String, Integer> observableMap = new ObservableMap<>();
        observableMap.addMapChangeObserver(MapChangeEvt.ANY, e -> {
            EvtType<? extends MapChangeEvt<String, Integer>> type = e.getEvtType();
            if (MapChangeEvt.MODIFIED.equals(type)) {
                e.getModifiedEntries().forEach(entry -> System.out.println("Modified: " + entry.getKey() + " -> " + entry.getValue()));
            } else if (MapChangeEvt.ADDED.equals(type)) {
                e.getAddedEntries().forEach(entry -> System.out.println("Added   : " + entry.getKey() + " -> " + entry.getValue()));
            } else if (MapChangeEvt.REMOVED.equals(type)) {
                e.getRemovedEntries().forEach(entry -> System.out.println("Removed : " + entry.getKey() + " -> " + entry.getValue()));
            }
        });
        // Add single entries
        observableMap.put("Gerrit", 52);
        observableMap.put("Sandra", 50);
        observableMap.put("Lilli", 18);
        observableMap.put("Anton", 13);
        observableMap.put("Neo", 3);
        System.out.println("---------- remove 1 ----------");
        observableMap.remove("Neo");
        assert !observableMap.containsKey("Neo");

        System.out.println("---------- add map of 3 ----------");
        observableMap.putAll(Map.of("Test", 1, "Test2", 2, "Test3", 3));
        assert observableMap.containsKey("Test2");
        assert observableMap.get("Test2") == 2;

        System.out.println("---------- remove 1 ----------");
        observableMap.remove("Test2");
        assert !observableMap.containsKey("Test2");

        System.out.println("---------- add 1 ----------");
        observableMap.put("Neo", 3);
        assert observableMap.containsKey("Neo");
        assert observableMap.get("Neo") == 3;

        System.out.println("---------- print all ----------");
        observableMap.entrySet().forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
        System.out.println("---------- clear ----------");
        observableMap.clear();
        assert observableMap.isEmpty();
    }

    @Test
    void testObservableMatrix() {
        System.out.println("\n-------------------- observable matrix demo --------------------");
        final Random rnd  = new Random();
        final int    cols = 3;
        final int    rows = 2;
        ObservableMatrix<Integer> integerMatrix = new ObservableMatrix<>(Integer.class, cols, rows);
        integerMatrix.addMatrixItemChangeObserver(MatrixItemChangeEvt.ANY, e -> {
            EvtType<? extends MatrixItemChangeEvt<Integer>> type = e.getEvtType();
            if (MatrixItemChangeEvt.ITEM_ADDED.equals(type)) {
                System.out.println("Item added  : " + e.getItem() + " at " + e.getX() + ", " + e.getY());
            } else if (MatrixItemChangeEvt.ITEM_REMOVED.equals(type)) {
                System.out.println("Item removed: " + e.getOldItem() + " at " + e.getX() + ", " + e.getY());
            } else if (MatrixItemChangeEvt.ITEM_CHANGED.equals(type)) {
                System.out.println("Item changed: " + e.getItem() + " at " + e.getX() + ", " + e.getY());
            }
        });

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Integer value = rnd.nextInt(10);
                integerMatrix.setItemAt(x, y, value);
            }
        }
        assert integerMatrix.getItemAt(2, 1) != 0;

        integerMatrix.removeItemAt(0, 0);
        assert integerMatrix.getItemAt(0, 0) == null;

        integerMatrix.setItemAt(2, 0, 5);
        assert integerMatrix.getItemAt(2, 0) == 5;
    }
}
