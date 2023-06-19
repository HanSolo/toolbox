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

import eu.hansolo.toolbox.Constants.Architecture;
import eu.hansolo.toolbox.Constants.OperatingMode;
import eu.hansolo.toolbox.Constants.OperatingSystem;
import eu.hansolo.toolbox.geo.CardinalDirection;
import eu.hansolo.toolbox.geo.GeoLocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.Charset;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.EPSILON;
import static eu.hansolo.toolbox.Constants.FLOAT_PATTERN;
import static eu.hansolo.toolbox.Constants.HEX_PATTERN;
import static eu.hansolo.toolbox.Constants.INDENT;
import static eu.hansolo.toolbox.Constants.INT_PATTERN;
import static eu.hansolo.toolbox.Constants.NEW_LINE;
import static eu.hansolo.toolbox.Constants.QUOTES;
import static eu.hansolo.toolbox.Constants.QUOTES_COLON;
import static eu.hansolo.toolbox.Constants.SQUARE_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.SQUARE_BRACKET_OPEN;
import static java.nio.charset.StandardCharsets.UTF_8;


public class Helper {
    private Helper() {}

    private static final String[] DETECT_ALPINE_CMDS       = { "/bin/sh", "-c", "cat /etc/os-release | grep 'NAME=' | grep -ic 'Alpine'" };
    private static final String[] UX_DETECT_ARCH_CMDS      = { "/bin/sh", "-c", "uname -m" };
    private static final String[] MAC_DETECT_ROSETTA2_CMDS = { "/bin/sh", "-c", "sysctl -in sysctl.proc_translated" };
    private static final String[] WIN_DETECT_ARCH_CMDS     = { "cmd.exe", "/c", "SET Processor" };
    private static final Pattern  ARCHITECTURE_PATTERN     = Pattern.compile("(PROCESSOR_ARCHITECTURE)=([a-zA-Z0-9_\\-]+)");
    private static final Matcher  ARCHITECTURE_MATCHER     = ARCHITECTURE_PATTERN.matcher("");
    private static final Matcher  INT_MATCHER              = INT_PATTERN.matcher("");
    private static final Matcher  FLOAT_MATCHER            = FLOAT_PATTERN.matcher("");
    private static final Matcher  HEX_MATCHER              = HEX_PATTERN.matcher("");

    public record RootInfo(String absolutePath, long totalSpace, long freeSpace, long usableSpace) {}
    public record JvmInfo(String vmName, String vmVendor, String vmVersion, String specName, String specVendor, String specVersion) {}
    public record OperatingSystemInfo(String arc, int availableProcessors, String operatingSystemName, String operatingSystemVersion, double systemLoadAverage) {}
    public record CompilationInfo(long totalCompilationTime) {}
    public record ClassLoadingInfo(long totalLoadedClassCount, int loadedClassCount, long unloadedClassCount) {}
    public record HeapInfo(MemoryUsage heapMemoryUsage, MemoryUsage noneHeapMemoryUsage) {}
    public record MemInfo(long totalMemory, long freeMemory, long maxMemory) {}
    public record SystemSummary(Architecture architecture, int logicalCores, int physicalCores, MemInfo memInfo, HeapInfo heapInfo, List<RootInfo> rootInfos, OperatingSystem operatingSystem, OperatingSystemInfo operatingSystemInfo, OperatingMode operatingMode, JvmInfo jvmInfo) {
        public String toBeautifiedString() {
            StringBuilder msgBuilder = new StringBuilder().append(CURLY_BRACKET_OPEN).append(NEW_LINE)
                                                          .append(INDENT).append(QUOTES).append("architecture").append(QUOTES_COLON).append(QUOTES).append(architecture.name()).append(QUOTES).append(COMMA).append(NEW_LINE)
                                                          .append(INDENT).append(QUOTES).append("logical_cores").append(QUOTES_COLON).append(logicalCores).append(COMMA).append(NEW_LINE)
                                                          .append(INDENT).append(QUOTES).append("physical_cores").append(QUOTES_COLON).append(physicalCores).append(COMMA).append(NEW_LINE)
                                                          .append(INDENT).append(QUOTES).append("total_memory").append(QUOTES_COLON).append(memInfo.totalMemory()).append(COMMA).append(NEW_LINE)
                                                          .append(INDENT).append(QUOTES).append("free_memory").append(QUOTES_COLON).append(memInfo.freeMemory()).append(COMMA).append(NEW_LINE)
                                                          .append(INDENT).append(QUOTES).append("max_memory").append(QUOTES_COLON).append(memInfo.maxMemory()).append(COMMA).append(NEW_LINE)
                                                          .append(INDENT).append(QUOTES).append("heap_max").append(QUOTES_COLON).append(heapInfo.heapMemoryUsage.getMax()).append(COMMA).append(NEW_LINE)
                                                          .append(INDENT).append(QUOTES).append("heap_committed").append(QUOTES_COLON).append(heapInfo.heapMemoryUsage.getCommitted()).append(COMMA).append(NEW_LINE)
                                                          .append(INDENT).append(QUOTES).append("heap_used").append(QUOTES_COLON).append(heapInfo.heapMemoryUsage.getUsed()).append(COMMA).append(NEW_LINE)
                                                          .append(INDENT).append(QUOTES).append("non_heap_max").append(QUOTES_COLON).append(heapInfo.noneHeapMemoryUsage.getMax()).append(COMMA).append(NEW_LINE)
                                                          .append(INDENT).append(QUOTES).append("non_heap_committed").append(QUOTES_COLON).append(heapInfo.noneHeapMemoryUsage.getCommitted()).append(COMMA).append(NEW_LINE)
                                                          .append(INDENT).append(QUOTES).append("non_heap_used").append(QUOTES_COLON).append(heapInfo.noneHeapMemoryUsage.getUsed()).append(COMMA).append(NEW_LINE)
                                                          .append(INDENT).append(QUOTES).append("root_infos").append(QUOTES_COLON).append(SQUARE_BRACKET_OPEN).append(NEW_LINE);
            rootInfos.forEach(rootInfo ->
                              msgBuilder.append(INDENT).append(INDENT).append(CURLY_BRACKET_OPEN).append(NEW_LINE)
                                        .append(INDENT).append(INDENT).append(INDENT).append(QUOTES).append("absolute_path").append(QUOTES_COLON).append(QUOTES).append(rootInfo.absolutePath()).append(QUOTES).append(COMMA).append(NEW_LINE)
                                        .append(INDENT).append(INDENT).append(INDENT).append(QUOTES).append("total_space").append(QUOTES_COLON).append(rootInfo.totalSpace()).append(COMMA).append(NEW_LINE)
                                        .append(INDENT).append(INDENT).append(INDENT).append(QUOTES).append("free_space").append(QUOTES_COLON).append(rootInfo.freeSpace()).append(COMMA).append(NEW_LINE)
                                        .append(INDENT).append(INDENT).append(INDENT).append(QUOTES).append("usable_space").append(QUOTES_COLON).append(rootInfo.usableSpace()).append(NEW_LINE)
                                        .append(INDENT).append(INDENT).append(CURLY_BRACKET_CLOSE).append(COMMA).append(NEW_LINE));
            msgBuilder.setLength(msgBuilder.length() - 2);
            msgBuilder.append(NEW_LINE).append(INDENT).append(SQUARE_BRACKET_CLOSE).append(COMMA).append(NEW_LINE)
                      .append(INDENT).append(QUOTES).append("operating_system").append(QUOTES_COLON).append(QUOTES).append(operatingSystem.name()).append(QUOTES).append(COMMA).append(NEW_LINE)
                      .append(INDENT).append(QUOTES).append("operating_system_name").append(QUOTES_COLON).append(QUOTES).append(operatingSystemInfo.operatingSystemName).append(QUOTES).append(COMMA).append(NEW_LINE)
                      .append(INDENT).append(QUOTES).append("operating_system_version").append(QUOTES_COLON).append(QUOTES).append(operatingSystemInfo.operatingSystemVersion()).append(QUOTES).append(COMMA).append(NEW_LINE)
                      .append(INDENT).append(QUOTES).append("operating_mode").append(QUOTES_COLON).append(QUOTES).append(operatingMode.name()).append(QUOTES).append(COMMA).append(NEW_LINE)
                      .append(INDENT).append(QUOTES).append("vm_name").append(QUOTES_COLON).append(QUOTES).append(jvmInfo.vmName()).append(QUOTES).append(COMMA).append(NEW_LINE)
                      .append(INDENT).append(QUOTES).append("vm_vendor").append(QUOTES_COLON).append(QUOTES).append(jvmInfo.vmVendor()).append(QUOTES).append(COMMA).append(NEW_LINE)
                      .append(INDENT).append(QUOTES).append("vm_version").append(QUOTES_COLON).append(QUOTES).append(jvmInfo.vmVersion()).append(QUOTES).append(COMMA).append(NEW_LINE)
                      .append(INDENT).append(QUOTES).append("spec_name").append(QUOTES_COLON).append(QUOTES).append(jvmInfo.specName()).append(QUOTES).append(COMMA).append(NEW_LINE)
                      .append(INDENT).append(QUOTES).append("spec_vendor").append(QUOTES_COLON).append(QUOTES).append(jvmInfo.specVendor()).append(QUOTES).append(COMMA).append(NEW_LINE)
                      .append(INDENT).append(QUOTES).append("spec_version").append(QUOTES_COLON).append(QUOTES).append(jvmInfo.specVersion()).append(QUOTES).append(NEW_LINE)
                      .append(CURLY_BRACKET_CLOSE);
            return msgBuilder.toString();
        }
        @Override public String toString() {
            StringBuilder msgBuilder = new StringBuilder().append(CURLY_BRACKET_OPEN)
                                                          .append(QUOTES).append("architecture").append(QUOTES_COLON).append(QUOTES).append(architecture.name()).append(QUOTES).append(COMMA)
                                                          .append(QUOTES).append("logical_cores").append(QUOTES_COLON).append(logicalCores).append(COMMA)
                                                          .append(QUOTES).append("physical_cores").append(QUOTES_COLON).append(physicalCores).append(COMMA)
                                                          .append(QUOTES).append("total_memory").append(QUOTES_COLON).append(memInfo.totalMemory()).append(COMMA)
                                                          .append(QUOTES).append("free_memory").append(QUOTES_COLON).append(memInfo.freeMemory()).append(COMMA)
                                                          .append(QUOTES).append("max_memory").append(QUOTES_COLON).append(memInfo.maxMemory()).append(COMMA)
                                                          .append(QUOTES).append("heap_max").append(QUOTES_COLON).append(heapInfo.heapMemoryUsage.getMax()).append(COMMA)
                                                          .append(QUOTES).append("heap_committed").append(QUOTES_COLON).append(heapInfo.heapMemoryUsage.getCommitted()).append(COMMA)
                                                          .append(QUOTES).append("heap_used").append(QUOTES_COLON).append(heapInfo.heapMemoryUsage.getUsed()).append(COMMA)
                                                          .append(QUOTES).append("non_heap_max").append(QUOTES_COLON).append(heapInfo.noneHeapMemoryUsage.getMax()).append(COMMA)
                                                          .append(QUOTES).append("non_heap_committed").append(QUOTES_COLON).append(heapInfo.noneHeapMemoryUsage.getCommitted()).append(COMMA)
                                                          .append(QUOTES).append("non_heap_used").append(QUOTES_COLON).append(heapInfo.noneHeapMemoryUsage.getUsed()).append(COMMA)
                                                          .append(QUOTES).append("root_infos").append(QUOTES_COLON).append(SQUARE_BRACKET_OPEN);
            rootInfos.forEach(rootInfo ->
                msgBuilder.append(CURLY_BRACKET_OPEN)
                          .append(QUOTES).append("absolute_path").append(QUOTES_COLON).append(QUOTES).append(rootInfo.absolutePath()).append(QUOTES).append(COMMA)
                          .append(QUOTES).append("total_space").append(QUOTES_COLON).append(rootInfo.totalSpace()).append(COMMA)
                          .append(QUOTES).append("free_space").append(QUOTES_COLON).append(rootInfo.freeSpace()).append(COMMA)
                          .append(QUOTES).append("usable_space").append(QUOTES_COLON).append(rootInfo.usableSpace())
                          .append(CURLY_BRACKET_CLOSE).append(COMMA));
            msgBuilder.setLength(msgBuilder.length() - 1);
            msgBuilder.append(SQUARE_BRACKET_CLOSE).append(COMMA)
                      .append(QUOTES).append("operating_system").append(QUOTES_COLON).append(QUOTES).append(operatingSystem.name()).append(QUOTES).append(COMMA)
                      .append(QUOTES).append("operating_system_name").append(QUOTES_COLON).append(QUOTES).append(operatingSystemInfo.operatingSystemName).append(QUOTES).append(COMMA)
                      .append(QUOTES).append("operating_system_version").append(QUOTES_COLON).append(QUOTES).append(operatingSystemInfo.operatingSystemVersion()).append(QUOTES).append(COMMA)
                      .append(QUOTES).append("operating_mode").append(QUOTES_COLON).append(QUOTES).append(operatingMode.name()).append(QUOTES).append(COMMA)
                      .append(QUOTES).append("vm_name").append(QUOTES_COLON).append(QUOTES).append(jvmInfo.vmName()).append(QUOTES).append(COMMA)
                      .append(QUOTES).append("vm_vendor").append(QUOTES_COLON).append(QUOTES).append(jvmInfo.vmVendor()).append(QUOTES).append(COMMA)
                      .append(QUOTES).append("vm_version").append(QUOTES_COLON).append(QUOTES).append(jvmInfo.vmVersion()).append(QUOTES).append(COMMA)
                      .append(QUOTES).append("spec_name").append(QUOTES_COLON).append(QUOTES).append(jvmInfo.specName()).append(QUOTES).append(COMMA)
                      .append(QUOTES).append("spec_vendor").append(QUOTES_COLON).append(QUOTES).append(jvmInfo.specVendor()).append(QUOTES).append(COMMA)
                      .append(QUOTES).append("spec_version").append(QUOTES_COLON).append(QUOTES).append(jvmInfo.specVersion()).append(QUOTES)
                      .append(CURLY_BRACKET_CLOSE);
            return msgBuilder.toString();
        }
    }

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

    public static final String readTextFileToString(final String filename) {
        if (null == filename || !new File(filename).exists()) { throw new IllegalArgumentException("File: " + filename + " not found or null"); }
        try {
            Path fileObj = Path.of(filename);
            return Files.readString(fileObj);
        } catch (IOException e) {
            return "";
        }
    }
    public static final String readTextFileToString(final File file) {
        if (null == file || !file.isFile()) { throw new IllegalArgumentException("Given file is either null or no file"); }
        try {
            Path fileObj = file.toPath();
            return Files.readString(fileObj);
        } catch (IOException e) {
            return "";
        }
    }

    public static final void saveStringToTextFile(final String filename, final String text) {
        if (null == filename || filename.isEmpty()) { throw new IllegalArgumentException("filename cannot be null or empty"); }
        if (null == text || text.isEmpty()) { throw new IllegalArgumentException("text cannot be null or empty"); }
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

    public static final Optional<String> nonEmpty(final String text) {
        return (null == text || text.length() == 0) ? Optional.empty() : Optional.ofNullable(text);
    }

    public static final String padLeft(final String input, final char ch, final int length) {
        return String.format("%" + length + "s", input).replace(' ', ch);
    }
    public static final String padRight(final String input, final char ch, final int length) {
        return String.format("%" + (-length) + "s", input).replace(' ', ch);
    }

    public static final int getPhysicalCores() {
        //final OperatingSystemInfo osInfo = getOperatingSystemInfo();
        //return osInfo.availableProcessors();
        final OperatingSystem operatingSystem = getOperatingSystem();
        Integer noOfPhysicalCores;
        switch(operatingSystem) {
            case LINUX, ALPINE_LINUX, LINUX_MUSL -> noOfPhysicalCores = readFromProc();
            case WINDOWS                         -> noOfPhysicalCores =  readFromWMIC();
            case MACOS                           -> noOfPhysicalCores = readFromSysctlOsX();
            case FREE_BSD                        -> noOfPhysicalCores =  readFromSysctlFreeBSD();
            default                              -> noOfPhysicalCores =  -1;
        }
        return null == noOfPhysicalCores ? -1 : noOfPhysicalCores;
    }

    public static final int getLogicalCores() {
        final Runtime runtime = Runtime.getRuntime();
        return runtime.availableProcessors();
    }

    public static final long getTotalMemory() {
        final Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory();
    }

    public static final long getMaxMemory() {
        final Runtime runtime = Runtime.getRuntime();
        return runtime.maxMemory();
    }

    public static final long getFreeMemory() {
        final Runtime runtime = Runtime.getRuntime();
        return runtime.freeMemory();
    }

    public static final Architecture getArchitecture() {
        OperatingSystemInfo osInfo = getOperatingSystemInfo();
        Architecture        arch   = Architecture.fromText(osInfo.arc());
        return Architecture.NOT_FOUND == arch ? getArchitecture(getOperatingSystem()) : arch;
    }
    public static final Architecture getArchitecture(final OperatingSystem operatingSystem) {
        // Try to get architecture via process
        try {
            final ProcessBuilder processBuilder = OperatingSystem.WINDOWS == operatingSystem ? new ProcessBuilder(WIN_DETECT_ARCH_CMDS) : new ProcessBuilder(UX_DETECT_ARCH_CMDS);
            final Process        process        = processBuilder.start();
            final String         result         = new BufferedReader(new InputStreamReader(process.getInputStream())).lines().collect(Collectors.joining("\n"));
            switch(operatingSystem) {
                case WINDOWS:
                    ARCHITECTURE_MATCHER.reset(result);
                    final List<MatchResult> results = ARCHITECTURE_MATCHER.results().collect(Collectors.toList());
                    if(results.size() > 0) { Architecture.fromText(results.get(0).group(2)); }
                    break;
                case MACOS: return Architecture.fromText(result);
                case LINUX: return Architecture.fromText(result);
            }
        } catch (IOException e) {
            return Architecture.NOT_FOUND;
        }

        // Try to get architecture via system property
        final String arch = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH);
        if (arch.contains("sparc"))                           { return Architecture.SPARC; }
        if (arch.contains("amd64") || arch.contains("86_64")) { return Architecture.X64; }
        if (arch.contains("86"))                              { return Architecture.X86; }
        if (arch.contains("s390x"))                           { return Architecture.S390X; }
        if (arch.contains("ppc64"))                           { return Architecture.PPC64; }
        if (arch.contains("arm") && arch.contains("64"))      { return Architecture.AARCH64; }
        if (arch.contains("arm"))                             { return Architecture.ARM; }
        if (arch.contains("aarch64"))                         { return Architecture.AARCH64; }

        return Architecture.NOT_FOUND;
    }

    public static final OperatingSystem getOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return OperatingSystem.WINDOWS;
        } else if (os.contains("apple") || os.contains("mac")) {
            return OperatingSystem.MACOS;
        } else if (os.contains("nix") || os.contains("nux")) {
            try {
                final ProcessBuilder processBuilder = new ProcessBuilder(DETECT_ALPINE_CMDS);
                final Process        process        = processBuilder.start();
                final String         result         = new BufferedReader(new InputStreamReader(process.getInputStream())).lines().collect(Collectors.joining("\n"));
                return null == result ? OperatingSystem.LINUX : result.equals("1") ? OperatingSystem.ALPINE_LINUX : OperatingSystem.LINUX;
            } catch (IOException e) {
                e.printStackTrace();
                return OperatingSystem.LINUX;
            }
        } else if (os.contains("sunos")) {
            return OperatingSystem.SOLARIS;
        } else if (os.contains("freebsd")) {
            return OperatingSystem.FREE_BSD;
        } else {
            OperatingSystemInfo osInfo = getOperatingSystemInfo();
            return OperatingSystem.fromText(osInfo.operatingSystemName);
        }
    }

    public static final OperatingMode getOperatingMode() { return getOperatingMode(getOperatingSystem()); }
    public static final OperatingMode getOperatingMode(final OperatingSystem operatingSystem) {
        try {
            final ProcessBuilder processBuilder = OperatingSystem.WINDOWS == operatingSystem ? new ProcessBuilder(WIN_DETECT_ARCH_CMDS) : new ProcessBuilder(UX_DETECT_ARCH_CMDS);
            final Process        process        = processBuilder.start();
            final String         result         = new BufferedReader(new InputStreamReader(process.getInputStream())).lines().collect(Collectors.joining("\n"));
            switch(operatingSystem) {
                case WINDOWS:
                    ARCHITECTURE_MATCHER.reset(result);
                    final List<MatchResult> results     = ARCHITECTURE_MATCHER.results().collect(Collectors.toList());
                    final int               noOfResults = results.size();
                    return noOfResults > 0 ? OperatingMode.NATIVE : OperatingMode.NOT_FOUND;
                case MACOS:
                    final ProcessBuilder processBuilder1 = new ProcessBuilder(MAC_DETECT_ROSETTA2_CMDS);
                    final Process        process1        = processBuilder1.start();
                    final String         result1         = new BufferedReader(new InputStreamReader(process1.getInputStream())).lines().collect(Collectors.joining("\n"));
                    return result1.equals("1") ? OperatingMode.EMULATED : OperatingMode.NATIVE;
                case LINUX:
                    return OperatingMode.NATIVE;
                default: return OperatingMode.NOT_FOUND;
            }
        } catch (IOException e) {
            return OperatingMode.NOT_FOUND;
        }
    }

    public static final OperatingSystemInfo getOperatingSystemInfo() {
        final OperatingSystemMXBean operatingSystemMXBean  = ManagementFactory.getOperatingSystemMXBean();
        final String                arc                    = operatingSystemMXBean.getArch();
        final int                   availableProcessors    = operatingSystemMXBean.getAvailableProcessors();
        final String                operatingSystemName    = operatingSystemMXBean.getName();
        final String                operatingSystemVersion = operatingSystemMXBean.getVersion();
        final double                systemLoadAverage      = operatingSystemMXBean.getSystemLoadAverage();
        return new OperatingSystemInfo(arc, availableProcessors, operatingSystemName, operatingSystemVersion, systemLoadAverage);
    }

    public static final JvmInfo getJvmInfo() {
        final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        final String        vmName        = runtimeMXBean.getVmName();
        final String        vmVendor      = runtimeMXBean.getVmVendor();
        final String        vmVersion     = runtimeMXBean.getVmVersion();
        final String        specName      = runtimeMXBean.getSpecName();
        final String        specVendor    = runtimeMXBean.getSpecVendor();
        final String        specVersion   = runtimeMXBean.getSpecVersion();
        return new JvmInfo(vmName, vmVendor, vmVersion, specName, specVendor, specVersion);
    }
    
    public static final CompilationInfo getCompilationInfo() {
        final CompilationMXBean compilationMXBean    = ManagementFactory.getCompilationMXBean();
        final long              totalCompilationtime = compilationMXBean.getTotalCompilationTime();
        return new CompilationInfo(totalCompilationtime);
    }

    public static final ClassLoadingInfo getClassLoadingInfo() {
        final ClassLoadingMXBean classLoadingMXBean    = ManagementFactory.getClassLoadingMXBean();
        final long               totalLoadedClassCount = classLoadingMXBean.getTotalLoadedClassCount();
        final int                loadedClassCount      = classLoadingMXBean.getLoadedClassCount();
        final long               unloadedClassCount    = classLoadingMXBean.getUnloadedClassCount();
        return new ClassLoadingInfo(totalLoadedClassCount, loadedClassCount, unloadedClassCount);
    }

    public static final HeapInfo getHeapInfo() {
        final MemoryMXBean memoryMXBean       = ManagementFactory.getMemoryMXBean();
        final MemoryUsage  heapMemoryUsage    = memoryMXBean.getHeapMemoryUsage();
        final MemoryUsage  nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        return new HeapInfo(heapMemoryUsage, nonHeapMemoryUsage);
    }

    public static final MemInfo getMemInfo() {
        return new MemInfo(getTotalMemory(), getFreeMemory(), getMaxMemory());
    }

    public static final List<RootInfo> getRootInfos() {
        final List<RootInfo> rootInfos = new ArrayList<>();
        final File[]         roots     = File.listRoots();
        for (File root : roots) {
            final String absolutePath = root.getAbsolutePath();
            final long   totalSpace   = root.getTotalSpace();
            final long   freeSpace    = root.getFreeSpace();
            final long   usableSpace  = root.getUsableSpace();
            rootInfos.add(new RootInfo(absolutePath, totalSpace, freeSpace, usableSpace));
        }
        return rootInfos;
    }

    public static final SystemSummary getSystemSummary() {
        final List<RootInfo>      rootInfos       = getRootInfos();
        final OperatingSystemInfo osInfo          = getOperatingSystemInfo();
        final JvmInfo             jvmInfo         = getJvmInfo();
        final HeapInfo            heapInfo        = getHeapInfo();
        final MemInfo             memInfo         = getMemInfo();
        final OperatingSystem     operatingSystem = getOperatingSystem();
        final Architecture        arc             = getArchitecture(operatingSystem);
        final OperatingMode       operatingMode   = getOperatingMode(operatingSystem);
        final int                 logicalCores    = getLogicalCores();
        final int                 physicalCores   = getPhysicalCores();
        return new SystemSummary(arc, logicalCores, physicalCores, memInfo, heapInfo, rootInfos, operatingSystem, osInfo, operatingMode, jvmInfo);
    }

    public static final double calcDistanceInMeter(final GeoLocation location1, final GeoLocation location2) {
        return calcDistanceInMeter(location1.getLatitude(), location1.getLongitude(), location2.getLatitude(), location2.getLongitude());
    }
    public static final double calcDistanceInMeter(final double latitude1, final double longitude1, final double latitude2, final double longitude2) {
        final double lat1Radians     = Math.toRadians(latitude1);
        final double lat2Radians     = Math.toRadians(latitude2);
        final double deltaLatRadians = Math.toRadians(latitude2 - latitude1);
        final double deltaLonRadians = Math.toRadians(longitude2 - longitude1);

        final double a = Math.sin(deltaLatRadians * 0.5) * Math.sin(deltaLatRadians * 0.5) + Math.cos(lat1Radians) * Math.cos(lat2Radians) * Math.sin(deltaLonRadians * 0.5) * Math.sin(deltaLonRadians * 0.5);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        final double distance = Constants.EARTH_RADIUS * c;
        return distance;
    }

    public static final double calcBearingInDegree(final GeoLocation location1, final GeoLocation location2) {
        return calcBearingInDegree(location1.getLatitude(), location1.getLongitude(), location2.getLatitude(), location2.getLongitude());
    }
    public static final double calcBearingInDegree(final double latitude1, final double longitude1, final double latitude2, final double longitude2) {
        final double lat1     = Math.toRadians(latitude1);
        final double lon1     = Math.toRadians(longitude1);
        final double lat2     = Math.toRadians(latitude2);
        final double lon2     = Math.toRadians(longitude2);
        final double deltaPhi = Math.log(Math.tan(lat2 * 0.5 + Math.PI * 0.25) / Math.tan(lat1 * 0.5 + Math.PI * 0.25));
        double deltaLon = lon2 - lon1;
        if (Math.abs(deltaLon) > Math.PI) {
            deltaLon = deltaLon > 0 ? -(2.0 * Math.PI - deltaLon) : (2.0 * Math.PI + deltaLon);
        }
        final double bearing = (Math.toDegrees(Math.atan2(deltaLon, deltaPhi)) + 360.0) % 360.0;
        return bearing;
    }

    public static final CardinalDirection getCardinalDirectionFromBearing(final double brng) {
        double bearing = brng % 360.0;
        for (CardinalDirection cardinalDirection : CardinalDirection.getValues()) {
            if (Double.compare(bearing, cardinalDirection.from) >= 0 && Double.compare(bearing, cardinalDirection.to) < 0) {
                return cardinalDirection;
            }
        }
        return CardinalDirection.NOT_FOUND;
    }

    // private methods needed to figure out physical number of cores
    private static Integer readFromProc() {
        final String path = "/proc/cpuinfo";
        File cpuinfo = new File(path);
        if (!cpuinfo.exists()) { return null; }
        try (InputStream in = new FileInputStream(cpuinfo)) {
            String s = readToString(in, Charset.forName("UTF-8"));
            // Count number of different tuples (physical id, core id) to discard hyper threading and multiple sockets
            Map<String, Set<String>> physicalIdToCoreId = new HashMap<>();

            int coreIdCount = 0;
            String[] split = s.split("\n");
            String latestPhysicalId = null;
            for (String row : split)
                if (row.startsWith("physical id")) {
                    latestPhysicalId = row;
                    if (physicalIdToCoreId.get(row) == null)
                        physicalIdToCoreId.put(latestPhysicalId, new HashSet<String>());

                } else if (row.startsWith("core id"))
                    // "physical id" row should always come before "core id" row, so that physicalIdToCoreId should
                    // not be null here.
                    physicalIdToCoreId.get(latestPhysicalId).add(row);

            for (Set<String> coreIds : physicalIdToCoreId.values())
                coreIdCount += coreIds.size();

            return coreIdCount;
        } catch (SecurityException | IOException e) {
            String msg = String.format("Error while reading %s", path);
        }
        return null;
    }
    private static Integer readFromWMIC() {
        ProcessBuilder pb = new ProcessBuilder("WMIC", "/OUTPUT:STDOUT", "CPU", "Get", "/Format:List");
        pb.redirectErrorStream(true);
        Process wmicProc;
        try {
            wmicProc = pb.start();
            wmicProc.getOutputStream().close();
        } catch (IOException | SecurityException e) {
            return null;
        }
        waitFor(wmicProc);
        try (InputStream in = wmicProc.getInputStream()) {
            String wmicOutput = readToString(in, Charset.forName("US-ASCII"));
            return parseWmicOutput(wmicOutput);
        } catch (UnsupportedEncodingException e) {
            // Java implementations are required to support US-ASCII, so this can't happen
            throw new RuntimeException(e);
        } catch (SecurityException | IOException e) {
            return null;
        }
    }
    private static Integer parseWmicOutput(String wmicOutput) {
        String[] rows = wmicOutput.split("\n");
        int coreCount = 0;
        for (String row : rows) {
            if (row.startsWith("NumberOfCores")) {
                String num = row.split("=")[1].trim();
                try {
                    coreCount += Integer.parseInt(num);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return coreCount > 0 ? coreCount : null;
    }
    private static Integer readFromSysctlOsX() {
        String result = readSysctl("hw.physicalcpu", "-n");
        if (result == null) {
            return null;
        }
        try {
            return Integer.parseInt(result);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private static Integer readFromSysctlFreeBSD() {
        String result = readSysctl("dev.cpu");
        if (result == null) {
            return null;
        }
        Set<String> cpuLocations = new HashSet<>();
        for (String row : result.split("\n")) {
            if (row.contains("location")) {
                cpuLocations.add(row.split("\\\\")[1]);
            }
        }
        return cpuLocations.isEmpty() ? null : cpuLocations.size();
    }
    private static String readSysctl(String variable, String... options) {
        List<String> command = new ArrayList<>();
        command.add("sysctl");
        command.addAll(Arrays.asList(options));
        command.add(variable);
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process sysctlProc;
        try {
            sysctlProc = pb.start();
        } catch (IOException | SecurityException e) {
            return null;
        }
        String result;
        try {
            result = readToString(sysctlProc.getInputStream(), Charset.forName("UTF-8")).trim();
        } catch (UnsupportedEncodingException e) {
            // Java implementations are required to support UTF-8, so this can't happen
            throw new RuntimeException(e);
        } catch (IOException e) {
            return null;
        }
        int exitStatus = waitFor(sysctlProc);
        if (exitStatus != 0) { return null; }
        return result;
    }
    private static String readToString(InputStream in, Charset charset) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(in , charset)) {
            StringWriter sw  = new StringWriter();
            char[]       buf = new char[10000];
            while (reader.read(buf) != -1) {
                sw.write(buf);
            }
            return sw.toString();
        }
    }
    private static int waitFor(Process proc) {
        try {
            return proc.waitFor();
        } catch (InterruptedException e) {
            return 1;
        }
    }
}
