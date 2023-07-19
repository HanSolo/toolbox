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

package eu.hansolo.toolbox.unit;

import eu.hansolo.toolbox.Constants;
import org.junit.jupiter.api.Test;

import static eu.hansolo.toolbox.unit.Category.BLOOD_GLUCOSE;
import static eu.hansolo.toolbox.unit.Category.LENGTH;
import static eu.hansolo.toolbox.unit.Category.TEMPERATURE;
import static eu.hansolo.toolbox.unit.Category.VOLUME;
import static eu.hansolo.toolbox.unit.UnitDefinition.CELSIUS;
import static eu.hansolo.toolbox.unit.UnitDefinition.CENTIMETER;
import static eu.hansolo.toolbox.unit.UnitDefinition.CUBIC_METER;
import static eu.hansolo.toolbox.unit.UnitDefinition.FAHRENHEIT;
import static eu.hansolo.toolbox.unit.UnitDefinition.INCHES;
import static eu.hansolo.toolbox.unit.UnitDefinition.KELVIN;
import static eu.hansolo.toolbox.unit.UnitDefinition.LITER;
import static eu.hansolo.toolbox.unit.UnitDefinition.METER;
import static eu.hansolo.toolbox.unit.UnitDefinition.MILLIGRAM_PER_DECILITER;
import static eu.hansolo.toolbox.unit.UnitDefinition.MILLIMOL_PER_LITER;
import static eu.hansolo.toolbox.unit.UnitDefinition.NANOMETER;


public class UnitTest {

    @Test
    void testUnit() {
        System.out.println("\n-------------------- converter demo --------------------");
        Converter temperatureConverter = new Converter(TEMPERATURE, CELSIUS); // Type Temperature with BaseUnit Celsius
        double    celsius              = 32.0;
        double    fahrenheit           = temperatureConverter.convert(celsius, FAHRENHEIT);
        double    kelvin               = temperatureConverter.convert(celsius, KELVIN);
        assert Double.compare(fahrenheit, 89.60000000000042) == 0;
        assert Double.compare(kelvin, 305.15) == 0;

        Converter lengthConverter = new Converter(LENGTH, METER); // Type Length with BaseUnit Meter
        double    meter           = 1.0;
        double    inches          = lengthConverter.convert(meter, INCHES);
        double    nanometer       = lengthConverter.convert(inches, NANOMETER);

        assert Double.compare(39.37007874015748, inches) == 0;
        assert Double.compare(3.937007874015748E10, nanometer) == 0;

        Converter volumeConverter = new Converter(VOLUME, CUBIC_METER);
        double    cubicMeter      = 3;
        double    liters          = volumeConverter.convert(cubicMeter, LITER);

        assert Double.compare(3.0, cubicMeter) == 0;
        assert Double.compare(3000.0, liters) == 0;

        Converter literConverter = new Converter(VOLUME, LITER);
        double liter = 3000;
        double cubicMeters = literConverter.convert(liter, CUBIC_METER);

        assert Double.compare(3.0, cubicMeter) == 0;

        Converter glucoseConverter = new Converter(BLOOD_GLUCOSE, MILLIMOL_PER_LITER);
        double millimolPerLiter = 6.0;
        double milligramPerDeciliter = glucoseConverter.convert(millimolPerLiter, MILLIGRAM_PER_DECILITER);

        assert Double.compare(108.10810810810811, milligramPerDeciliter) == 0;

        Converter mgdlConverter = new Converter(BLOOD_GLUCOSE, MILLIGRAM_PER_DECILITER);
        double mgdl = 108.108108;
        double mmol = mgdlConverter.convert(mgdl, MILLIMOL_PER_LITER);

        assert Double.compare(5.999999994, mmol) == 0;

        // Convert meter to centimeter
        assert "100.00 cm".equals(lengthConverter.convertToString(meter, CENTIMETER));

        // Shorten long numbers
        assert Converter.format(1_500_000, 1).equals("1.5M");

        assert Converter.format(1_000_000,0).equals("1M");
    }
}
