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


public enum GSV implements NmeaSentence {
    INSTANCE;

    public final String name                 = "GSV";
    public       int    noOfGsvSentences     = 0;
    public       int    currentSentence      = 0;
    public       int    lastSentence         = 0;
    public       int    noOfSatellitesInView = 0;

    @Override
    public String getName() { return this.name; }

    @Override
    public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("name").append(QUOTES_COLON_QUOTES).append(this.name).append(QUOTES_COMMA)
                                  .append(QUOTES).append("no_of_gsv_sentences").append(QUOTES_COLON).append(this.noOfGsvSentences).append(COMMA)
                                  .append(QUOTES).append("current_sentence").append(QUOTES_COLON).append(this.currentSentence).append(COMMA)
                                  .append(QUOTES).append("last_sentence").append(QUOTES_COLON).append(this.lastSentence).append(COMMA)
                                  .append(QUOTES).append("satellites_in_view").append(QUOTES_COLON).append(this.noOfSatellitesInView)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
