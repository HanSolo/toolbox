/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2022 Gerrit Grunwald.
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

package eu.hansolo.toolbox.time;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public enum Dates {

    /**
     * <p>Format : yyyy-MM-dd</p>
     * <p>Example: 2022-03-30</p>
     */
    yyyy_MM_dd("yyyy-MM-dd"),

    /**
     * <p>Format : yyyy-MM</p>
     * <p>Example: 2022-03</p>
     */
    yyyy_MM("yyyy-MM"),

    /**
     * <p>Format : yyyyMMdd</p>
     * <p>Example: 20220330</p>
     */
    yyyyMMdd("yyyyMMdd"),

    /**
     * <p>Format : yyyyMM</p>
     * <p>Example: 202203</p>
     */
    yyyyMM("yyyyMM"),

    /**
     * <p>Format : yyyy</p>
     * <p>Example: 2022</p>
     */
    yyyy("yyyy"),

    /**
     * <p>Format : yyyy-'W'w</p>
     * <p>Example: 2022-W01</p>
     */
    yyyy_w("yyyy-'W'w"),

    /**
     * <p>Format : yyyy'W'w</p>
     * <p>Example: 2022W01</p>
     */
    yyyyw("yyyy'W'w"),

    /**
     * <p>Format : yyyy-'W'w-e</p>
     * <p>Example: 2022-W01-2</p>
     */
    yyyy_w_e("yyyy-'W'w-e"),

    /**
     * <p>Format : yyyy'W'we</p>
     * <p>Example: 2022-W01-2</p>
     */
    yyyywe("yyyy'W'we"),

    /**
     * <p>Format : dd-MM-yyyy</p>
     * <p>Example: 30-03-2022</p>
     */
    dd_MM_yyyy("dd-MM-yyyy"),

    /**
     * <p>Format : MM-dd-yyyy</p>
     * <p>Example: 03-30-2022</p>
     */
    MM_dd_yyyy("MM-dd-yyyy"),

    /**
     * <p>Format : MMMM dd, yyyy</p>
     * <p>Example: March 30, 2022</p>
     */
    MMMM_dd_yyyy("MMMM dd, yyyy"),

    /**
     * <p>Format : MMMM dd</p>
     * <p>Example: March 30</p>
     */
    MMMM_dd("MMMM dd"),

    /**
     * <p>Format : MMM dd, yyyy</p>
     * <p>Example: Mar 30, 2022</p>
     */
    MMM_dd_yyyy("MMM dd, yyyy"),

    /**
     * <p>Format : MMM dd</p>
     * <p>Example: Mar 30</p>
     */
    MMM_dd("MMM dd"),

    /**
     * <p>Format : dd MMM yyyy</p>
     * <p>Example: 30 Mar 2022</p>
     */
    dd_MMM_yyyy("dd MMM yyyy"),

    /**
     * <p>Format : dd MMM</p>
     * <p>Example: 30 Mar</p>
     */
    dd_MMM("dd MMM"),

    /**
     * <p>Format : dd MMMM yyyy</p>
     * <p>Example: 30 March 2022</p>
     */
    dd_MMMM_yyyy("dd MMMM yyyy"),

    /**
     * <p>Format : dd MMMM</p>
     * <p>Example: 30 March</p>
     */
    dd_MMMM("dd MMMM");

    private final DateTimeFormatter formatter;


    Dates(final String formatString) {
        formatter = DateTimeFormatter.ofPattern(formatString);
    }


    public LocalDate parse(final String text) { return LocalDate.parse(text, formatter); }

    public Date parseDate(final String text) { return parseDate(text, ZoneId.systemDefault()); }
    public Date parseDate(final String text, final ZoneId zoneId) {
        return Date.from(parse(text).atStartOfDay()
                                      .atZone(zoneId)
                                      .toInstant());
    }


    public String format(final LocalDate date) { return formatter.format(date); }

    public String format(final Date date) { return format(date, ZoneId.systemDefault()); }
    public String format(final Date date, final ZoneId zoneId) {
        return format(date.toInstant()
                          .atZone(zoneId)
                          .toLocalDate());
    }
}
