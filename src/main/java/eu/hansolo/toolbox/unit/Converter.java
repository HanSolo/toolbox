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

import eu.hansolo.toolbox.Helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public class Converter {
    public static final  String[]                          ABBREVIATIONS      = { "k", "M", "G", "T", "P", "E", "Z", "Y" };
    public static final  int                               MAX_NO_OF_DECIMALS = 12;
    private static final EnumMap<Category, UnitDefinition> BASE_UNITS         = new EnumMap<>(Category.class) {
        {
            put(Category.ACCELERATION, UnitDefinition.METER_PER_SQUARE_SECOND);
            put(Category.ANGLE, UnitDefinition.RADIAN);
            put(Category.AREA, UnitDefinition.SQUARE_METER);
            put(Category.CURRENT, UnitDefinition.AMPERE);
            put(Category.DATA, UnitDefinition.BIT);
            put(Category.ELECTRIC_CHARGE, UnitDefinition.ELEMENTARY_CHARGE);
            put(Category.ENERGY, UnitDefinition.JOULE);
            put(Category.FORCE, UnitDefinition.NEWTON);
            put(Category.HUMIDITY, UnitDefinition.PERCENTAGE);
            put(Category.LENGTH, UnitDefinition.METER);
            put(Category.LUMINANCE, UnitDefinition.CANDELA_SQUARE_METER);
            put(Category.LUMINOUS_FLUX, UnitDefinition.LUX);
            put(Category.MASS, UnitDefinition.KILOGRAM);
            put(Category.PRESSURE, UnitDefinition.PASCAL);
            put(Category.SPEED, UnitDefinition.METER_PER_SECOND);
            put(Category.TEMPERATURE, UnitDefinition.KELVIN);
            put(Category.TEMPERATURE_GRADIENT, UnitDefinition.KELVIN_PER_SECOND);
            put(Category.TIME, UnitDefinition.SECOND);
            put(Category.TORQUE, UnitDefinition.NEWTON_METER);
            put(Category.VOLUME, UnitDefinition.CUBIC_METER);
            put(Category.VOLTAGE, UnitDefinition.VOLT);
            put(Category.WORK, UnitDefinition.WATT);
            put(Category.BLOOD_GLUCOSE, UnitDefinition.MILLIMOL_PER_LITER);
        }
    };
    private              UnitDefinition                    baseUnitDefinition;
    private              Unit                              bean;
    private              Locale                            locale;
    private              int                               decimals;
    private              String                            formatString;


    // ******************** Constructors **************************************
    public Converter(final Category category) {
        this(category, BASE_UNITS.get(category));
    }
    public Converter(final Category category, final UnitDefinition baseUnitDefinition) {
        this.baseUnitDefinition = baseUnitDefinition;
        bean                    = BASE_UNITS.get(category).UNIT;
        locale             = Locale.US;
        decimals           = 2;
        formatString       = "%.2f";
    }


    // ******************** Methods *******************************************
    public Category getCategory() { return bean.getCategory(); }

    public UnitDefinition getBaseUnitDefinition() { return baseUnitDefinition; }
    public void setBaseUnitDefinition(final UnitDefinition baseUnitDefinition) {
        if (baseUnitDefinition.UNIT.getCategory() == getCategory()) { this.baseUnitDefinition = baseUnitDefinition; }
    }

    public BigDecimal getFactor() { return bean.getFactor(); }

    public BigDecimal getOffset() { return bean.getOffset(); }

    public String getUnitName() { return bean.getUnitName(); }

    public String getUnitShort() { return bean.getUnitShort(); }

    public Locale getLocale() { return locale; }
    public void setLocale(final Locale locale) { this.locale = locale; }

    public int getDecimals() { return decimals; }
    public void setDecimals(final int decimals) {
        if (decimals < 0 ) {
            this.decimals = 0;
        } else if (decimals > MAX_NO_OF_DECIMALS) {
            this.decimals = MAX_NO_OF_DECIMALS;
        } else {
            this.decimals = decimals;
        }
        formatString = new StringBuilder("%.").append(this.decimals).append("f").toString();
    }

    public String getFormatString() { return formatString; }

    public final boolean isActive() { return bean.isActive(); }
    public final void setActive(final boolean active) { bean.setActive(active); }

    public final double convert(final double value, final UnitDefinition unitDefinition) {
        if (unitDefinition.UNIT.getCategory() != getCategory()) { throw new IllegalArgumentException("units have to be of the same type"); }
        return ((((value + baseUnitDefinition.UNIT.getOffset().doubleValue()) * baseUnitDefinition.UNIT.getFactor().doubleValue()) + bean.getOffset().doubleValue()) * bean.getFactor().doubleValue()) / unitDefinition.UNIT
        .getFactor().doubleValue() - unitDefinition.UNIT.getOffset().doubleValue();
    }

    public final String convertToString(final double value, final UnitDefinition unitDefinition) {
        return String.join(" ", String.format(locale, formatString, convert(value, unitDefinition)), unitDefinition.UNIT.getUnitShort());
    }

    public final double convertToBaseUnit(final double value, final UnitDefinition unitDefinition) {
        return ((((value + unitDefinition.UNIT.getOffset().doubleValue()) * unitDefinition.UNIT.getFactor().doubleValue()) + bean.getOffset().doubleValue()) * bean.getFactor().doubleValue()) / baseUnitDefinition.UNIT
        .getFactor().doubleValue() - baseUnitDefinition.UNIT.getOffset().doubleValue();
    }

    public final Pattern getPattern() {
        final StringBuilder PATTERN_BUILDER = new StringBuilder();
        PATTERN_BUILDER.append("^([-+]?\\d*\\.?\\d*)\\s?(");

        for (UnitDefinition unitDefinition : UnitDefinition.values()) {
            PATTERN_BUILDER.append(unitDefinition.UNIT.getUnitShort().replace("*", "\\*")).append("|");
        }

        PATTERN_BUILDER.deleteCharAt(PATTERN_BUILDER.length() - 1);

        //PATTERN_BUILDER.append("){1}$");
        PATTERN_BUILDER.append(")?$");

        return Pattern.compile(PATTERN_BUILDER.toString());
    }

    public final List<Unit> getAvailableUnits(final Category category) {
        return getAllUnitDefinitions().get(category).stream().map(unitDefinition -> unitDefinition.UNIT).collect(Collectors.toList());
    }

    public final EnumMap<Category, ArrayList<UnitDefinition>> getAllUnitDefinitions() {
        final EnumMap<Category, ArrayList<UnitDefinition>> UNIT_TYPES    = new EnumMap<>(Category.class);
        final ArrayList<Category>                          CATEGORY_LIST = new ArrayList<>(Category.values().length);
        CATEGORY_LIST.addAll(Arrays.asList(Category.values()));
        CATEGORY_LIST.forEach(category -> UNIT_TYPES.put(category, new ArrayList<>()));
        for (UnitDefinition unitDefinition : UnitDefinition.values()) {
            UNIT_TYPES.get(unitDefinition.UNIT.getCategory()).add(unitDefinition);
        }
        return UNIT_TYPES;
    }

    public final EnumMap<Category, ArrayList<UnitDefinition>> getAllActiveUnitDefinitions() {
        final EnumMap<Category, ArrayList<UnitDefinition>> UNIT_DEFINITIONS = new EnumMap<>(Category.class);
        final ArrayList<Category>                          CATEGORY_LIST    = new ArrayList<>(Category.values().length);
        CATEGORY_LIST.addAll(Arrays.asList(Category.values()));
        CATEGORY_LIST.forEach(category -> UNIT_DEFINITIONS.put(category, new ArrayList<>()));
        for (UnitDefinition unitDefinition : UnitDefinition.values()) {
            if (unitDefinition.UNIT.isActive()) { UNIT_DEFINITIONS.get(unitDefinition.UNIT.getCategory()).add(unitDefinition); }
        }
        return UNIT_DEFINITIONS;
    }

    public static final String format(final double number, final int decimals) {
        return format(number, Helper.clamp(0, 12, decimals), Locale.US);
    }
    public static final String format(final double number, final int decimals, final Locale locale) {
        String formatString = new StringBuilder("%.").append(Helper.clamp(0, 12, decimals)).append("f").toString();
        double value;
        for(int i = ABBREVIATIONS.length - 1 ; i >= 0; i--) {
            value = Math.pow(1000, i+1);
            if (Double.compare(number, -value) <= 0 || Double.compare(number, value) >= 0) {
                return String.format(locale, formatString, (number / value)) + ABBREVIATIONS[i];
            }
        }
        return String.format(locale, formatString, number);
    }


    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("category").append(QUOTES).append(COLON).append(QUOTES).append(getCategory()).append(QUOTES)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
