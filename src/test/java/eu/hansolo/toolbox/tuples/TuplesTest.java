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

package eu.hansolo.toolbox.tuples;

import org.junit.jupiter.api.Test;


public class TuplesTest {

    @Test
    void testTuples() {
        System.out.println("\n-------------------- tuples test --------------------");
        Pair<Double, Integer> pair = new Pair(5.0, 3);
        assert pair.getA() == 5.0;
        assert pair.getB() == 3;

        Triplet<Double, Integer, Long> triplet = new Triplet(5.0, 3, 500L);
        assert triplet.getTypeAt(2).equals(Long.class);
        assert triplet.getC() == 500;


        Quartet<Double, Integer, String, Long> quartet = new Quartet(1.0, 5, "Test", 1000);
        assert quartet.size() == 4;
        assert quartet.getB() == 5;
        assert quartet.getTypeAt(1).equals(Integer.class);
        assert quartet.getValueAt(2).equals("Test");
    }
}
