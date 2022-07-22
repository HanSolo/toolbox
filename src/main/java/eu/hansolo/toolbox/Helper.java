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

package eu.hansolo.toolbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collector;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import static eu.hansolo.toolbox.Constants.EPSILON;
import static eu.hansolo.toolbox.Constants.FLOAT_PATTERN;
import static eu.hansolo.toolbox.Constants.HEX_PATTERN;
import static eu.hansolo.toolbox.Constants.INT_PATTERN;
import static java.nio.charset.StandardCharsets.UTF_8;


public class Helper {
    private Helper() {}

    private static final Matcher  INT_MATCHER   = INT_PATTERN.matcher("");
    private static final Matcher  FLOAT_MATCHER = FLOAT_PATTERN.matcher("");
    private static final Matcher  HEX_MATCHER   = HEX_PATTERN.matcher("");

    public static final <T extends Number> T clamp(final T min, final T max, final T value) {
        if (value.doubleValue() < min.doubleValue()) return min;
        if (value.doubleValue() > max.doubleValue()) return max;
        return value;
    }

    public static final int clamp(final int min, final int max, final int value) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
    public static final long clamp(final long min, final long max, final long value) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
    public static final double clamp(final double min, final double max, final double value) {
        if (Double.compare(value, min) < 0) return min;
        if (Double.compare(value, max) > 0) return max;
        return value;
    }
    public static final Instant clamp(final Instant min, final Instant max, final Instant value) {
        if (value.isBefore(min)) return min;
        if (value.isAfter(max)) return max;
        return value;
    }
    public static final LocalDateTime clamp(final LocalDateTime min, final LocalDateTime max, final LocalDateTime value) {
        if (value.isBefore(min)) return min;
        if (value.isAfter(max)) return max;
        return value;
    }
    public static final LocalDate clamp(final LocalDate min, final LocalDate max, final LocalDate value) {
        if (value.isBefore(min)) return min;
        if (value.isAfter(max)) return max;
        return value;
    }

    public static final double clampMin(final double min, final double value) {
        if (value < min) return min;
        return value;
    }
    public static final double clampMax(final double max, final double value) {
        if (value > max) return max;
        return value;
    }

    public static final boolean almostEqual(final double value1, final double value2, final double epsilon) {
        return Math.abs(value1 - value2) < epsilon;
    }

    public static final double round(final double value, final int precision) {
        final int SCALE = (int) Math.pow(10, precision);
        return (double) Math.round(value * SCALE) / SCALE;
    }

    public static final double roundTo(final double value, final double target) { return target * (Math.round(value / target)); }

    public static final double roundToHalf(final double value) { return Math.round(value * 2) / 2.0; }

    public static final int roundDoubleToInt(final double value){
        double dAbs = Math.abs(value);
        int    i      = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return value < 0 ? -i : i;
        } else {
            return value < 0 ? -(i + 1) : i + 1;
        }
    }

    public static final boolean equals(final double a, final double b) { return a == b || Math.abs(a - b) < EPSILON; }

    public static final boolean biggerThan(final double a, final double b) { return (a - b) > EPSILON; }

    public static final boolean lessThan(final double a, final double b) { return (b - a) > EPSILON; }

    public static final boolean isPositiveInteger(final String text) {
        if (null == text || text.isEmpty()) { return false; }
        return Constants.POSITIVE_INTEGER_PATTERN.matcher(text).matches();
    }

    public static final String trimPrefix(final String text, final String prefix) {
        return text.replaceFirst(prefix, "");
    }

    public static final DateTimeFormatter getDateFormat(final Locale locale) {
        if (Locale.US == locale) {
            return DateTimeFormatter.ofPattern("MM/dd/YYYY");
        } else if (Locale.CHINA == locale) {
            return DateTimeFormatter.ofPattern("YYYY.MM.dd");
        } else {
            return DateTimeFormatter.ofPattern("dd.MM.YYYY");
        }
    }
    public static final DateTimeFormatter getLocalizedDateFormat(final Locale locale) {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale);
    }

    public static final String normalize(final String text) {
        String normalized = text.replace("\u00fc", "ue")
                                .replace("\u00f6", "oe")
                                .replace("\u00e4", "ae")
                                .replace("\u00df", "ss");

        normalized = normalized.replace("\u00dc(?=[a-z\u00fc\u00f6\u00e4\u00df ])", "Ue")
                               .replace("\u00d6(?=[a-z\u00fc\u00f6\u00e4\u00df ])", "Oe")
                               .replace("\u00c4(?=[a-z\u00fc\u00f6\u00e4\u00df ])", "Ae");

        normalized = normalized.replace("\u00dc", "UE")
                               .replace("\u00d6", "OE")
                               .replace("\u00c4", "AE");
        return normalized;
    }

    public static final int getDegrees(final double decDeg) { return (int) decDeg; }
    public static final int getMinutes(final double decDeg) { return (int) ((decDeg - getDegrees(decDeg)) * 60); }
    public static final double getSeconds(final double decDeg) { return (((decDeg - getDegrees(decDeg)) * 60) - getMinutes(decDeg)) * 60; }

    public static final double getDecimalDeg(final int degrees, final int minutes, final double seconds) {
        return (((seconds / 60) + minutes) / 60) + degrees;
    }

    public static final <T> Predicate<T> not(Predicate<T> predicate) { return predicate.negate(); }

    // Get last n elements from stream
    public static <T> Collector<T, ?, List<T>> lastN(int n) {
        return Collector.<T, Deque<T>, List<T>>of(ArrayDeque::new, (acc, t) -> {
            if(acc.size() == n)
                acc.pollFirst();
            acc.add(t);
        }, (acc1, acc2) -> {
            while(acc2.size() < n && !acc1.isEmpty()) {
                acc2.addFirst(acc1.pollLast());
            }
            return acc2;
        }, ArrayList::new);
    }

    public static final double getDoubleFromText(final String text) {
        if (null == text || text.isEmpty()) { return 0.0; }
        FLOAT_MATCHER.reset(text);
        String result = "";
        double number = 0;
        try {
            while(FLOAT_MATCHER.find()) {
                result = FLOAT_MATCHER.group(0);
            }
            number = Double.parseDouble(result);
        } catch (IllegalStateException | NumberFormatException ex) {
            return 0.0;
        }
        return number;
    }
    public static final int getIntFromText(final String text) {
        INT_MATCHER.reset(text);
        String result = "";
        int number = 0;
        try {
            while(INT_MATCHER.find()) {
                result = INT_MATCHER.group(0);
            }
            number = Integer.parseInt(result);
        } catch (IllegalStateException | NumberFormatException ex) {
            return 0;
        }

        return number;
    }

    public static final String getHexColorFromString(final String text) {
        HEX_MATCHER.reset(text);
        String result = "";
        try {
            while (HEX_MATCHER.find()) {
                result = HEX_MATCHER.group(0);
            }
        } catch (IllegalStateException ex) {
            return "-";
        }
        return result;
    }

    public static final String readFromInputStream(final InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public static final String readTextFile(final String filename) {
        if (null == filename || !new File(filename).exists()) { throw new IllegalArgumentException("File: " + filename + " not found or null"); }
        try {
            Path fileName = Path.of(filename);
            return Files.readString(fileName);
        } catch (IOException e) {
            return "";
        }
    }
    public static final void saveTxtFile(final String filename, final String text) {
        if (null == text || text.isEmpty()) { return; }
        try {
            Files.write(Paths.get("/" + filename), text.getBytes());
        } catch (IOException e) {
            //System.out.println("Error saving download text file. " + e);
        }
    }

    public static final LocalDate getFirstDayOfWeek(final int year, final int weekNumber, final Locale locale) {
        return LocalDate
        .of(year, 2, 1)
        .with(WeekFields.of(locale).getFirstDayOfWeek())
        .with(WeekFields.of(locale).weekOfWeekBasedYear(), weekNumber);
    }
    public static final long getEpochSecondsOfFirstDayOfWeek(final int year, final int weekNumber, final Locale locale) {
        return getFirstDayOfWeek(year, weekNumber, locale).atStartOfDay().toEpochSecond(ZoneOffset.UTC);
    }

    public static final LocalDate getLastDayOfWeek(final int year, final int weekNumber, final Locale locale) {
        return getFirstDayOfWeek(year, weekNumber, locale).plusDays(6);
    }
    public static final long getEpochSecondsOfLastDayOfWeek(final int year, final int weekNumber, final Locale locale) {
        return getLastDayOfWeek(year, weekNumber, locale).atStartOfDay().toEpochSecond(ZoneOffset.UTC);
    }

    public static final int getWeekOfYear(final ZonedDateTime zonedDateTime) { return getWeekOfYear(zonedDateTime.toInstant(), zonedDateTime.getZone()); }
    public static final int getWeekOfYeear(final Instant instant) { return getWeekOfYear(instant, ZoneId.systemDefault()); }
    public static final int getWeekOfYear(final Instant instant, final ZoneId zoneId) { return getWeekOfYear(LocalDate.ofInstant(instant, zoneId)); }
    public static final int getWeekOfYear(final LocalDateTime dateTime) { return getWeekOfYear(dateTime.toLocalDate()); }
    public static final int getWeekOfYear(final LocalDate date) { return date.get(ChronoField.ALIGNED_WEEK_OF_YEAR); }
    public static final int getWeekOfYear(final long epochSeconds) { return getWeekOfYear(epochSeconds, ZoneId.systemDefault()); }
    public static final int getWeekOfYear(final long epochSeconds, final ZoneId zoneId) {
        if (epochSeconds < 0) { throw new IllegalArgumentException("Epochseconds cannot be smaller than 0"); }
        return LocalDate.ofInstant(Instant.ofEpochSecond(epochSeconds), zoneId).get(ChronoField.ALIGNED_WEEK_OF_YEAR);
    }

    private static final NavigableMap<Long, String> SUFFIXES = new TreeMap<>(Map.of(1_000L, "k",
                                                                                    1_000_000L, "M",
                                                                                    1_000_000_000L, "G",
                                                                                    1_000_000_000_000L, "T",
                                                                                    1_000_000_000_000_000L, "P",
                                                                                    1_000_000_000_000_000_000L, "E"));
    public static final String shortenNumber(final long value) {
        return shortenNumber(value, Locale.US);
    }
    public static final String shortenNumber(final long value, final Locale locale) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) { return shortenNumber(Long.MIN_VALUE + 1, locale); }
        if (value < 0)               { return "-" + shortenNumber(-value, locale); }
        if (value < 1000)            { return Long.toString(value); }

        final Entry<Long, String> entry    = SUFFIXES.floorEntry(value);
        final Long                divideBy = entry.getKey();
        final String                 suffix     = entry.getValue();
        final long                   truncated  = value / (divideBy / 10);
        final boolean                hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        final java.text.NumberFormat formatter  = java.text.NumberFormat.getNumberInstance(locale);
        formatter.setMinimumFractionDigits(1);
        formatter.setMaximumFractionDigits(1);
        return hasDecimal ? formatter.format(truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static final <K, V extends Comparable<V>> V getMaxValueInMap(final Map<K, V> map) {
        Entry<K, V> maxEntry = Collections.max(map.entrySet(), Comparator.comparing(Entry::getValue));
        return maxEntry.getValue();
    }
    public static final <K, V extends Comparable<V>> K getKeyWithMaxValueInMap(final Map<K, V> map) {
        Entry<K, V> maxEntry = Collections.max(map.entrySet(), Comparator.comparing(Entry::getValue));
        return maxEntry.getKey();
    }

    public static final String secondsToHHMMString(final long seconds) {
        long[] hhmmss = secondsToHHMMSS(seconds);
        return String.format("%02d:%02d:%02d", hhmmss[0], hhmmss[1], hhmmss[2]);
    }
    public static final long[] secondsToHHMMSS(final long seconds) {
        long secs    = seconds % 60;
        long minutes = (secs / 60) % 60;
        long hours   = (secs / (60 * 60)) % 24;
        return new long[] { hours, minutes, secs };
    }

    public static final long getCRC32Checksum(final byte[] bytes) {
        Checksum crc32 = new CRC32();

        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }

    public static final String getMD5(final String text) { return bytesToHex(getMD5Bytes(text.getBytes(UTF_8))); }
    public static final String getMD5(final byte[] bytes) {
        return bytesToHex(getMD5Bytes(bytes));
    }
    public static final byte[] getMD5Bytes(final byte[] bytes) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error getting MD5 algorithm. " + e.getMessage());
            return new byte[]{};
        }
        return md.digest(bytes);
    }
    public static final String getMD5ForFile(final File file) throws Exception {
        final MessageDigest md  = MessageDigest.getInstance("MD5");
        final InputStream   fis = new FileInputStream(file);
        try {
            int n = 0;
            byte[] buffer = new byte[4096];
            while (n != -1) {
                n = fis.read(buffer);
                if (n > 0) {
                    md.update(buffer, 0, n);
                }
            }
        } finally {
            fis.close();
        }
        byte byteData[] = md.digest();
        return getMD5(bytesToHex(byteData));
    }

    public static final String getSHA1(final String text) { return bytesToHex(getSHA1Bytes(text.getBytes(UTF_8))); }
    public static final String getSHA1(final byte[] bytes) {
        return bytesToHex(getSHA1Bytes(bytes));
    }
    public static final byte[] getSHA1Bytes(final byte[] bytes) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error getting SHA-1 algorithm. " + e.getMessage());
            return new byte[]{};
        }
        return md.digest(bytes);
    }
    public static final String getSHA1ForFile(final File file) throws Exception {
        final MessageDigest md  = MessageDigest.getInstance("SHA-1");
        final InputStream   fis = new FileInputStream(file);
        try {
            int n = 0;
            byte[] buffer = new byte[4096];
            while (n != -1) {
                n = fis.read(buffer);
                if (n > 0) {
                    md.update(buffer, 0, n);
                }
            }
        } finally {
            fis.close();
        }
        byte byteData[] = md.digest();
        return getSHA1(bytesToHex(byteData));
    }

    public static final String getSHA256(final String text) { return bytesToHex(getSHA256Bytes(text.getBytes(UTF_8))); }
    public static final String getSHA256(final byte[] bytes) {
        return bytesToHex(getSHA256Bytes(bytes));
    }
    public static final byte[] getSHA256Bytes(final byte[] bytes) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error getting SHA2-256 algorithm. " + e.getMessage());
            return new byte[]{};
        }
        return md.digest(bytes);
    }
    public static final String getSHA256ForFile(final File file) throws Exception {
        final MessageDigest md  = MessageDigest.getInstance("SHA-256");
        final InputStream   fis = new FileInputStream(file);
        try {
            int n = 0;
            byte[] buffer = new byte[4096];
            while (n != -1) {
                n = fis.read(buffer);
                if (n > 0) {
                    md.update(buffer, 0, n);
                }
            }
        } finally {
            fis.close();
        }
        byte byteData[] = md.digest();
        return getSHA256(bytesToHex(byteData));
    }

    public static final String getSHA3_256(final String text) { return bytesToHex(getSHA3_256Bytes(text.getBytes(UTF_8))); }
    public static final String getSHA3_256(final byte[] bytes) {
        return bytesToHex(getSHA3_256Bytes(bytes));
    }
    public static final byte[] getSHA3_256Bytes(final byte[] bytes) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA3-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error getting SHA3-256 algorithm. " + e.getMessage());
            return new byte[]{};
        }
        return md.digest(bytes);
    }
    public static final String getSHA3_256ForFile(final File file) throws Exception {
        final MessageDigest md  = MessageDigest.getInstance("SHA3-256");
        final InputStream   fis = new FileInputStream(file);
        try {
            int n = 0;
            byte[] buffer = new byte[4096];
            while (n != -1) {
                n = fis.read(buffer);
                if (n > 0) {
                    md.update(buffer, 0, n);
                }
            }
        } finally {
            fis.close();
        }
        byte byteData[] = md.digest();
        return getSHA3_256(bytesToHex(byteData));
    }

    public static final String bytesToHex(final byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : bytes) { builder.append(String.format("%02x", b)); }
        return builder.toString();
    }
}
