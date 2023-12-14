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

package eu.hansolo.toolbox;

import org.junit.jupiter.api.Test;


public class RangeTest {

    @Test
    void testRange() {
        Range tooLow;
        Range low;
        Range acceptableLow;
        Range normal;
        Range acceptableHigh;
        Range high;
        Range tooHigh;

        tooLow         = new Range(0, 55);
        low            = new Range(55, 70);
        acceptableLow  = new Range(60, 70);
        normal         = new Range(70, 180);
        acceptableHigh = new Range(120, 200);
        high           = new Range(200, 255);
        tooHigh        = new Range(250, 400);

        low.maxProperty().bindBidirectional(acceptableLow.minProperty());
        acceptableLow.maxProperty().bindBidirectional(normal.minProperty());
        acceptableHigh.minProperty().bindBidirectional(normal.maxProperty());
        high.minProperty().bindBidirectional(acceptableHigh.maxProperty());

        acceptableLow.setMin(65);
        assert low.getMax() == 65;

        normal.setMax(190);
        assert high.getMin() == 200;

        assert tooLow.contains(40);

        assert Range.isValueInRange(200, normal) == 1;
    }
}
