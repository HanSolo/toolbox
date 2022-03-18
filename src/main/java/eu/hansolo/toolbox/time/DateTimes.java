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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public enum DateTimes {

    /**
     * <p>Format : yyyy-MM-dd HH:mm:ss.SSSS</p>
     * <p>Example: 2022-03-30 23:59:59.9876</p>
     */
    yyyy_MM_dd_HH_mm_ss_SSSS("yyyy-MM-dd HH:mm:ss.SSSS"),

    /**
     * <p>Format : yyyy-MM-dd HH:mm:ss</p>
     * <p>Example: 2022-03-30 23:59:59</p>
     */
    yyyy_MM_dd_HH_mm_ss("yyyy-MM-dd HH:mm:ss"),

    /**
     * <p>Format : yyyy-MM-dd HH:mm</p>
     * <p>Example: 2022-03-30 23:59</p>
     */
    yyyy_MM_dd_HH_mm("yyyy-MM-dd HH:mm"),

    /**
     * <p>Format : yyyy-MM-dd HH</p>
     * <p>Example: 2022-03-30 23</p>
     */
    yyyy_MM_dd_HH("yyyy-MM-dd HH"),

    /**
     * <p>Format : MM-dd HH:mm:ss.SSSS</p>
     * <p>Example: 03-30 23:59:59.9876</p>
     */
    MM_dd_HH_mm_ss_SSSS("MM-dd HH:mm:ss.SSSS"),

    /**
     * <p>Format : MM-dd HH:mm:ss</p>
     * <p>Example: 03-30 23:59:59</p>
     */
    MM_dd_HH_mm_ss("MM-dd HH:mm:ss"),

    /**
     * <p>Format : MM-dd HH:mm</p>
     * <p>Example: 03-30 23:59</p>
     */
    MM_dd_HH_mm("MM-dd HH:mm"),

    /**
     * <p>Format : MM-dd HH</p>
     * <p>Example: 03-30 23</p>
     */
    MM_dd_HH("MM-dd HH"),

    /**
     * <p>Format : yyyyMMdd HHmmss.SSSS</p>
     * <p>Example: 20220330 235959.9876</p>
     */
    yyyyMMdd_HHmmss_SSSS("yyyyMMdd HHmmss.SSSS"),

    /**
     * <p>Format : yyyyMMdd HHmmss</p>
     * <p>Example: 20220330 235959</p>
     */
    yyyyMMdd_HHmmss("yyyyMMdd HHmmss"),

    /**
     * <p>Format : yyyyMMdd HHmm</p>
     * <p>Example: 20220330 2359</p>
     */
    yyyyMMdd_HHmm("yyyyMMdd HHmm"),

    /**
     * <p>Format : MMdd HHmmss.SSSS</p>
     * <p>Example: 0330 235959.9876</p>
     */
    MMdd_HHmmss_SSSS("MMdd HHmmss.SSSS"),

    /**
     * <p>Format : MMdd HHmmss</p>
     * <p>Example: 0330 235959</p>
     */
    MMdd_HHmmss("MMdd HHmmss"),

    /**
     * <p>Format : MMdd HHmm</p>
     * <p>Example: 03-30 23:59</p>
     */
    MMdd_HHmm("MMdd HHmm"),

    /**
     * <p>Format : MMdd HH</p>
     * <p>Example: 0330 23</p>
     */
    MMdd_HH("MMdd HH"),

    /**
     * <p>Format : dd-MM-yyyy HH:mm:ss.SSSS</p>
     * <p>Example: 30-03-2022 23:59:59.9876</p>
     */
    dd_MM_yyyy_HH_mm_ss_SSSS("dd-MM-yyyy HH:mm:ss.SSSS"),

    /**
     * <p>Format : dd-MM-yyyy HH:mm:ss</p>
     * <p>Example: 30-03-2022 23:59:59</p>
     */
    dd_MM_yyyy_HH_mm_ss("dd-MM-yyyy HH:mm:ss"),

    /**
     * <p>Format : dd-MM-yyyy HH:mm</p>
     * <p>Example: 30-03-2022 23:59</p>
     */
    dd_MM_yyyy_HH_mm("dd-MM-yyyy HH:mm"),

    /**
     * <p>Format : dd-MM-yyyy HH</p>
     * <p>Example: 30-03-2022 23</p>
     */
    dd_MM_yyyy_HH("dd-MM-yyyy HH"),

    /**
     * <p>Format : dd-MM HH:mm:ss.SSSS</p>
     * <p>Example: 30-03 23:59:59.9876</p>
     */
    dd_MM_HH_mm_ss_SSSS("dd-MM HH:mm:ss.SSSS"),

    /**
     * <p>Format : dd-MM HH:mm:ss</p>
     * <p>Example: 30-03 23:59:59</p>
     */
    dd_MM_HH_mm_ss("dd-MM HH:mm:ss"),

    /**
     * <p>Format : dd-MM HH:mm</p>
     * <p>Example: 30-03 23:59</p>
     */
    dd_MM_HH_mm("dd-MM HH:mm"),

    /**
     * <p>Format : dd-MM HH</p>
     * <p>Example: 30-03 23</p>
     */
    dd_MM_HH("dd-MM HH"),

    /**
     * <p>Format : MM-dd-yyyy HH:mm:ss.SSSS</p>
     * <p>Example: 03-30-2022 23:59:59.9876</p>
     */
    MM_dd_yyyy_HH_mm_ss_SSSS("MM-dd-yyyy HH:mm:ss.SSSS"),

    /**
     * <p>Format : MM-dd-yyyy HH:mm:ss</p>
     * <p>Example: 03-30-2022 23:59:59</p>
     */
    MM_dd_yyyy_HH_mm_ss("MM-dd-yyyy HH:mm:ss"),

    /**
     * <p>Format : MM-dd-yyyy HH:mm</p>
     * <p>Example: 03-30-2022 23:59</p>
     */
    MM_dd_yyyy_HH_mm("MM-dd-yyyy HH:mm"),

    /**
     * <p>Format : MM-dd-yyyy HH</p>
     * <p>Example: 03-30-2022 23</p>
     */
    MM_dd_yyyy_HH("MM-dd-yyyy HH"),

    /**
     * <p>Format : MMM dd, yyyy HH:mm:ss.SSSS</p>
     * <p>Example: Mar 30, 2022 23:59:59.9876</p>
     */
    MMM_dd_yyyy_HH_mm_ss_SSSS("MMM dd, yyyy HH:mm:ss.SSSS"),

    /**
     * <p>Format : MMM dd, yyyy HH:mm:ss</p>
     * <p>Example: Mar 30, 2022 23:59:59</p>
     */
    MMM_dd_yyyy_HH_mm_ss("MMM dd, yyyy HH:mm:ss"),

    /**
     * <p>Format : MMM dd, yyyy HH:mm</p>
     * <p>Example: Mar 30, 2022 23:59</p>
     */
    MMM_dd_yyyy_HH_mm("MMM dd, yyyy HH:mm"),

    /**
     * <p>Format : MMM dd, yyyy HH</p>
     * <p>Example: Mar 30, 2022 23</p>
     */
    MMM_dd_yyyy_HH("MMM dd, yyyy HH"),

    /**
     * <p>Format : MMM dd HH:mm:ss.SSSS</p>
     * <p>Example: Mar 30 23:59:59.9876</p>
     */
    MMM_dd_HH_mm_ss_SSSS("MMM dd HH:mm:ss.SSSS"),

    /**
     * <p>Format : MMM dd HH:mm:ss</p>
     * <p>Example: Mar 30 23:59:59</p>
     */
    MMM_dd_HH_mm_ss("MMM dd HH:mm:ss"),

    /**
     * <p>Format : MMM dd HH:mm</p>
     * <p>Example: Mar 30 23:59</p>
     */
    MMM_dd_HH_mm("MMM dd HH:mm"),

    /**
     * <p>Format : MMM dd HH</p>
     * <p>Example: Mar 30 23</p>
     */
    MMM_dd_HH("MMM dd HH"),

    /**
     * <p>Format : dd MMMM yyyy HH:mm:ss.SSSS</p>
     * <p>Example: 30 March 2022 23:59:59.9876</p>
     */
    dd_MMMM_yyyy_HH_mm_ss_SSSS("dd MMMM yyyy HH:mm:ss.SSSS"),

    /**
     * <p>Format : dd MMMM yyyy HH:mm:ss</p>
     * <p>Example: 30 March 2022 23:59:59</p>
     */
    dd_MMMM_yyyy_HH_mm_ss("dd MMMM yyyy HH:mm:ss"),

    /**
     * <p>Format : dd MMMM yyyy HH:mm</p>
     * <p>Example: 30 March 2022 23:59</p>
     */
    dd_MMMM_yyyy_HH_mm("dd MMMM yyyy HH:mm"),

    /**
     * <p>Format : dd MMMM yyyy HH</p>
     * <p>Example: 30 March 2022 23</p>
     */
    dd_MMMM_yyyy_HH("dd MMMM yyyy HH"),

    /**
     * <p>Format : dd MMMM HH:mm:ss.SSSS</p>
     * <p>Example: 30 March 23:59:59.9876</p>
     */
    dd_MMMM_HH_mm_ss_SSSS("dd MMMM HH:mm:ss.SSSS"),

    /**
     * <p>Format : dd MMMM HH:mm:ss</p>
     * <p>Example: 30 March 23:59:59</p>
     */
    dd_MMMM_HH_mm_ss("dd MMMM HH:mm:ss"),

    /**
     * <p>Format : dd MMMM HH:mm</p>
     * <p>Example: 30 March 23:59</p>
     */
    dd_MMMM_HH_mm("dd MMMM HH:mm"),

    /**
     * <p>Format : dd MMMM HH</p>
     * <p>Example: 30 March 23</p>
     */
    dd_MMMM_HH("dd MMMM HH");


    private final DateTimeFormatter formatter;


    DateTimes(final String formatString) {
        formatter = DateTimeFormatter.ofPattern(formatString);
    }


    public String format(final ZonedDateTime dateTime) { return formatter.format(dateTime); }

    public String format(final ZonedDateTime zonedDateTime, final ZoneId zoneId) { return format(zonedDateTime.toLocalDateTime(), zoneId); }
    public String format(final LocalDateTime dateTime) { return format(dateTime, ZoneId.systemDefault()); }
    public String format(final LocalDateTime dateTime, final ZoneId zoneId) {
        return format(ZonedDateTime.of(dateTime.toLocalDate(), dateTime.toLocalTime(), zoneId));
    }

    public static long toEpoch(final ZonedDateTime zonedDateTime) { return toEpoch(zonedDateTime.toLocalDateTime()); }
    public static long toEpoch(final ZonedDateTime zonedDateTime, final ZoneId zoneId) { return toEpoch(zonedDateTime.toLocalDateTime(), zoneId); }
    public static long toEpoch(final LocalDateTime dateTime) { return toEpoch(dateTime, ZoneId.systemDefault()); }
    public static long toEpoch(final LocalDateTime dateTime, final ZoneId zoneId) {
        return ZonedDateTime.of(dateTime, zoneId).toInstant().getEpochSecond();
    }

    public static ZonedDateTime fromEpoch(final long epoch) { return fromEpoch(epoch, ZoneId.systemDefault()); }
    public static ZonedDateTime fromEpoch(final long epoch, final ZoneId zoneId) {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(epoch), zoneId);
    }
}
