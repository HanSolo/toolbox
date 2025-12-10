/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 Gerrit Grunwald.
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

package eu.hansolo.toolbox.nmea;

import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;
import static eu.hansolo.toolbox.Constants.QUOTES_COLON;
import static eu.hansolo.toolbox.Constants.QUOTES_COLON_QUOTES;
import static eu.hansolo.toolbox.Constants.QUOTES_COMMA;


public enum VTG implements NmeaSentence {
    INSTANCE;

    public final String name           = "VTG";
    public       double speedInKph     = 0.0;
    public       double speedInKnots   = 0.0;
    public       double trueCourse     = 0.0;
    public       double magneticCourse = 0.0;

    @Override
    public String getName() { return this.name; }

    @Override
    public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("name").append(QUOTES_COLON_QUOTES).append(this.name).append(QUOTES_COMMA)
                                  .append(QUOTES).append("speed_in_kph").append(QUOTES_COLON).append(this.speedInKph).append(COMMA)
                                  .append(QUOTES).append("speed_in_knots").append(QUOTES_COLON).append(this.speedInKnots).append(COMMA)
                                  .append(QUOTES).append("true_course").append(QUOTES_COLON).append(this.trueCourse).append(COMMA)
                                  .append(QUOTES).append("magnetic_course").append(QUOTES_COLON).append(this.magneticCourse).append(QUOTES)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
