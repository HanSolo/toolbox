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


public enum GSA implements NmeaSentence {
    INSTANCE;

    public final String name = "GSA";
    public       int    fix  = 1;
    public       int[]  prns = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    public       double pdop = 0;
    public       double hdop = 0;

    @Override
    public String getName() { return this.name; }

    @Override
    public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("name").append(QUOTES_COLON_QUOTES).append(this.name).append(QUOTES_COMMA)
                                  .append(QUOTES).append("fix").append(QUOTES_COLON_QUOTES).append(this.fix).append(" (").append(Fix.values()[this.fix - 1]).append(")").append(QUOTES_COMMA)
                                  .append(QUOTES).append("prns").append(QUOTES_COLON_QUOTES).append(this.prns[0]).append(COMMA).append(this.prns[1]).append(COMMA).append(this.prns[2]).append(COMMA).append(this.prns[3]).append(COMMA).append(this.prns[4]).append(COMMA).append(this.prns[5]).append(COMMA).append(this.prns[6]).append(COMMA).append(this.prns[7]).append(COMMA).append(this.prns[8]).append(COMMA).append(this.prns[9]).append(COMMA).append(this.prns[10]).append(COMMA).append(this.prns[11]).append(QUOTES_COMMA)
                                  .append(QUOTES).append("pdop").append(QUOTES_COLON).append(this.pdop).append(COMMA)
                                  .append(QUOTES).append("hdop").append(QUOTES_COLON).append(this.hdop)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
