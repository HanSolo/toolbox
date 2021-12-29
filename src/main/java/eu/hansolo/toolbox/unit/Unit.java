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

package eu.hansolo.toolbox.unit;

import java.math.BigDecimal;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public class Unit {
    private          Category   category;
    private          String     unitShort;
    private          String     unitName;
    private volatile BigDecimal factor;
    private volatile BigDecimal offset;
    private volatile boolean    active;


    // ******************** Constructors **************************************
    public Unit(final Category category, final String unitShort, final String unitName, final double factor) {
        this(category, unitShort, unitName, factor, 0.0);
    }
    public Unit(final Category category, final String unitShort, final String unitName, final double factor, final boolean active) {
        this(category, unitShort, unitName, factor, 0.0, active);
    }
    public Unit(final Category category, final String unitShort, final String unitName, final BigDecimal factor) {
        this(category, unitShort, unitName, factor, new BigDecimal("0.0"), true);
    }
    public Unit(final Category category, final String unitShort, final String unitName, final BigDecimal factor, final boolean active) {
        this(category, unitShort, unitName, factor, new BigDecimal("0.0"), active);
    }
    public Unit(final Category category, final String unitShort, final String unitName, final double factor, final double offset) {
        this(category, unitShort, unitName, new BigDecimal(Double.toString(factor)), new BigDecimal(Double.toString(offset)), true);
    }
    public Unit(final Category category, final String unitShort, final String unitName, final double factor, final double offset, final boolean active) {
        this(category, unitShort, unitName, new BigDecimal(Double.toString(factor)), new BigDecimal(Double.toString(offset)), active);
    }
    public Unit(final Category category, final String unitShort, final String unitName, final BigDecimal factorBd, final BigDecimal offsetBd) {
        this(category, unitShort, unitName, factorBd, offsetBd, true);
    }
    public Unit(final Category category, final String unitShort, final String unitName, final BigDecimal factorBd, final BigDecimal offsetBd, final boolean active) {
        this.category  = category;
        this.unitShort = unitShort;
        this.unitName  = unitName;
        this.factor    = factorBd;
        this.offset    = offsetBd;
        this.active    = active;
    }


    // ******************** Methods *******************************************
    public final Category getCategory() { return category; }

    public final String getUnitShort() { return unitShort; }

    public final String getUnitName() { return unitName; }

    public final BigDecimal getFactor() { return factor; }
    public final void setFactor(final BigDecimal factor) { this.factor = factor; }
    public final void setFactor(final double factor) { this.factor = new BigDecimal(Double.toString(factor)); }

    public final BigDecimal getOffset() { return offset; }
    public final void setOffset(final BigDecimal offset) { this.offset = offset; }
    public final void setOffset(final double offset) { this.offset =  new BigDecimal(Double.toString(offset)); }

    public final boolean isActive() { return active; }
    public final void setActive(final boolean active) { this.active = active; }

    @Override public final String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN).append(category)
                                  .append(QUOTES).append("unit_short").append(QUOTES).append(COLON).append(QUOTES).append(unitShort).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("unit_name").append(QUOTES).append(COLON).append(QUOTES).append(unitName).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("factor").append(QUOTES).append(COLON).append(factor).append(COMMA)
                                  .append(QUOTES).append("offset").append(QUOTES).append(COLON).append(getOffset())
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
