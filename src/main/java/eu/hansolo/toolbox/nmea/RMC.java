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


public enum RMC implements NmeaSentence {
    INSTANCE;

    public final String name               = "RMC";
    public       String time               = "000000";
    public       int    hour               = 0;
    public       int    minute             = 0;
    public       int    second             = 0;
    public       char   status             = 'V';
    public       double latitude           = 0.0;
    public       double longitude          = 0.0;
    public       char   latitudeIndicator  = ' ';
    public       char   longitudeIndicator = ' ';
    public       double speedInKph         = 0.0;
    public       double speedInKnots       = 0.0;
    public       double course             = 0.0;
    public       String date               = "010170";
    public       int    day                = 1;
    public       int    month              = 1;
    public       int    year               = 70;
    public       String mode               = "";

    @Override
    public String getName() { return this.name; }

    @Override
    public String toString() {
        StringBuilder txtBuilder = new StringBuilder().append(CURLY_BRACKET_OPEN)
                                                      .append(QUOTES).append("name").append(QUOTES_COLON_QUOTES).append(this.name).append(QUOTES_COMMA)
                                                      .append(QUOTES).append("time").append(QUOTES_COLON_QUOTES).append(this.time).append(" (").append(this.hour).append(":").append(this.minute).append(":").append(this.second).append(")").append(QUOTES_COMMA)
                                                      .append(QUOTES).append("status").append(QUOTES_COLON_QUOTES).append(this.status).append(QUOTES_COMMA)
                                                      .append(QUOTES).append("latitude").append(QUOTES_COLON).append(this.latitude).append(COMMA)
                                                      .append(QUOTES).append("longitude").append(QUOTES_COLON).append(this.longitude).append(COMMA)
                                                      .append(QUOTES).append("latitude_indicator").append(QUOTES_COLON_QUOTES).append(this.latitudeIndicator).append(QUOTES_COMMA)
                                                      .append(QUOTES).append("longitude_indicator").append(QUOTES_COLON_QUOTES).append(this.longitudeIndicator).append(QUOTES_COMMA)
                                                      .append(QUOTES).append("speed_in_kph").append(QUOTES_COLON).append(this.speedInKph).append(COMMA)
                                                      .append(QUOTES).append("speed_in_knots").append(QUOTES_COLON).append(this.speedInKnots).append(COMMA)
                                                      .append(QUOTES).append("course").append(QUOTES_COLON).append(this.course).append(COMMA)
                                                      .append(QUOTES).append("date").append(QUOTES_COLON_QUOTES).append(this.date).append(" (").append(day).append(".").append(month).append(".").append(year).append(")").append(QUOTES_COMMA)
                                                      .append(QUOTES).append("mode").append(QUOTES_COLON_QUOTES).append(this.mode)
                                                      .append(" (");
        switch (mode) {
            case "A" -> txtBuilder.append("Autonomous Mode)");
            case "D" -> txtBuilder.append("Differential Mode)");
            case "E" -> txtBuilder.append("Estimated Mode)");
            case "N" -> txtBuilder.append("Data not valid)");
            case "S" -> txtBuilder.append("Simulated Mode)");
            default  -> txtBuilder.append("-)");
        }
        txtBuilder.append(QUOTES).append(CURLY_BRACKET_CLOSE);

        return txtBuilder.toString();
    }
}
