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

import eu.hansolo.toolbox.properties.DoubleProperty;

import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;
import static eu.hansolo.toolbox.Constants.QUOTES_COLON;


public class Range {
    private static final String         ERR_MSG = "min must be smaller than max";
    private              double         _min;
    private              DoubleProperty min;
    private              double         _max;
    private              DoubleProperty max;


    // ******************** Constructors **************************************
    public Range(final double min, final double max) {
        if (min > max) { throw new IllegalArgumentException(ERR_MSG); }
        this._min = min;
        this._max = max;
    }


    // ******************** Methods *******************************************
    public double getMin() { return null == this.min ? this._min : this.min.get(); }
    public void setMin(final double min) {
        if (isValid(min, getMax())) {
            if (null == this.min) {
                this._min = min;
            } else {
                this.min.set(min);
            }
        } else {
            throw new IllegalArgumentException(ERR_MSG);
        }
    }
    public DoubleProperty minProperty() {
        if (null == this.min) {
            this.min = new DoubleProperty(this._min) {
                @Override public void set(final double value) {
                    if (isValid(value, getMax())) {
                        super.set(value);
                    } else {
                        throw new IllegalArgumentException(ERR_MSG);
                    }
                }
            };
        }
        return this.min;
    }

    public double getMax() { return null == this.max ? this._max : this.max.get(); }
    public void setMax(final double max) {
        if (getMin() < max) {
            if (null == this.max) {
                this._max = max;
            } else {
                this.max.set(max);
            }
        } else {
            throw new IllegalArgumentException(ERR_MSG);
        }
    }
    public DoubleProperty maxProperty() {
        if (null == this.max) {
            this.max = new DoubleProperty(this._max) {
                @Override public void set(final double value) {
                    if (isValid(getMin(), value)) {
                        super.set(value);
                    } else {
                        throw new IllegalArgumentException(ERR_MSG);
                    }
                }
            };
        }
        return this.max;
    }

    /**
     * Returns true if value is in range
     * @param value
     * @return true if value is in range
     */
    public boolean contains(final double value) { return value >= getMin() && value <= getMax(); }

    /**
     * Returns
     * -1 if value is smaller than minValue
     *  1 if value is greater than maxValue
     *  0 if value is in range
     * @param value
     * @return -1 if value is smaller than minValue, 1 if value is greater than maxValue, otherwise returns 0
     */
    public static int isValueInRange(final double value, final Range range) {
        if (value < range.getMin()) {
            return -1;
        } else if (value > range.getMax()) {
            return 1;
        } else {
            return 0;
        }
    }

    private boolean isValid(final double min, final double max) { return min < max; }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("min").append(QUOTES_COLON).append(getMax()).append(COMMA)
                                  .append(QUOTES).append("max").append(QUOTES_COLON).append(getMax())
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
