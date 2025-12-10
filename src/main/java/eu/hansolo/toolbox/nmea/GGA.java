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


public enum GGA implements NmeaSentence {
    INSTANCE;

    public final String name               = "GGA";
    public       String time               = "000000";
    public       int    hour               = 0;
    public       int    minute             = 0;
    public       int    second             = 0;
    public       double latitude           = 0;
    public       char   latitudeIndicator  = ' ';
    public       double longitude          = 0;
    public       char   longitudeIndicator = ' ';
    public       int    quality            = 0;
    public       int    satellitesTracked  = 0;
    public       double hdop               = 0;
    public       double altitude           = 0;

    @Override
    public String getName() { return this.name; }

    @Override
    public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("name").append(QUOTES_COLON_QUOTES).append(this.name).append(QUOTES_COMMA)
                                  .append(QUOTES).append("time").append(QUOTES_COLON_QUOTES).append(this.time).append(" (").append(this.hour).append(":").append(this.minute).append(":").append(this.second).append(")").append(QUOTES_COMMA)
                                  .append(QUOTES).append("latitude").append(QUOTES_COLON).append(this.latitude).append(COMMA)
                                  .append(QUOTES).append("latitude_indicator").append(QUOTES_COLON_QUOTES).append(this.latitudeIndicator).append(QUOTES_COMMA)
                                  .append(QUOTES).append("longitude").append(QUOTES_COLON).append(this.longitude).append(COMMA)
                                  .append(QUOTES).append("longitude_indicator").append(QUOTES_COLON_QUOTES).append(this.longitudeIndicator).append(QUOTES_COMMA)
                                  .append(QUOTES).append("quality").append(QUOTES_COLON_QUOTES).append(this.quality).append(" (").append(FixQuality.values()[this.quality]).append(")").append(QUOTES_COMMA)
                                  .append(QUOTES).append("satellites_tracked").append(QUOTES_COLON).append(this.satellitesTracked).append(COMMA)
                                  .append(QUOTES).append("hdop").append(QUOTES_COLON).append(this.hdop).append(COMMA)
                                  .append(QUOTES).append("altitude").append(QUOTES_COLON).append(this.altitude)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
