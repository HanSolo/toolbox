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

import java.io.File;
import java.time.Duration;
import java.time.Period;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Constants {
    private Constants() {}

    public enum Architecture {
        SPARC,
        AMD64,
        X86,
        X64,
        S390X,
        PPC64,
        AARCH64,
        ARM,
        ARMEL,
        ARMHF,
        MIPS,
        MIPSEL,
        PPC,
        PPC64LE,
        RISCV64,
        SPARCV9,
        IA64,
        NOT_FOUND;


        public static Architecture fromText(final String text) {
            if (null == text) { return NOT_FOUND; }
            switch (text) {
                case "aarch64", "AARCH64","arm64", "ARM64", "armv8", "ARMV8"                                                                ->  { return AARCH64; }
                case "amd64", "AMD64", "_amd64"                                                                                             -> { return AMD64; }
                case "aarch32", "AARCH32", "arm32", "ARM32", "armv6", "ARMV6", "armv7l", "ARMV7L", "armv7", "ARMV7", "arm", "ARM"           -> { return ARM; }
                case "armel", "ARMEL"                                                                                                       -> { return ARMEL; }
                case "armhf", "ARMHF"                                                                                                       -> { return ARMHF; }
                case "mips", "MIPS"                                                                                                         -> { return MIPS; }
                case "mipsel", "MIPSEL"                                                                                                     -> { return MIPSEL; }
                case "ppc", "PPC"                                                                                                           -> { return PPC; }
                case "ppc64el", "PPC64EL", "ppc64le", "PPC64LE"                                                                             -> { return PPC64LE; }
                case "ppc64", "PPC64"                                                                                                       -> { return PPC64; }
                case "riscv64", "RISCV64"                                                                                                   -> { return RISCV64; }
                case "s390", "s390x", "S390X"                                                                                               -> { return S390X; }
                case "sparc", "SPARC"                                                                                                       -> { return SPARC; }
                case "sparcv9", "SPARCV9"                                                                                                   -> { return SPARCV9; }
                case "x64", "X64", "x86-64", "X86-64", "x86_64", "X86_64", "x86lx64", "X86LX64"                                             -> { return X64; }
                case "x32", "x86", "X86", "286", "386", "486", "586", "686", "i386", "i486", "i586", "i686", "x86-32", "x86lx32", "X86LX32" -> { return X86; }
                case "ia64", "IA64", "ia-64", "IA-64"                                                                                       -> { return IA64; }
                default                                                                                                                     -> { return NOT_FOUND; }
            }
        }
    }

    public enum OperatingSystem {
        ALPINE_LINUX,
        LINUX,
        LINUX_MUSL,
        FREE_BSD,
        MACOS,
        WINDOWS,
        SOLARIS,
        QNX,
        AIX,
        NOT_FOUND;

        public static OperatingSystem fromText(final String text) {
            if (null == text) { return NOT_FOUND; }
            switch (text) {
                case "-linux", "linux", "Linux", "LINUX", "unix", "UNIX", "Unix", "-unix" -> { return LINUX; }
                case "-linux-musl", "-linux_musl", "Linux-Musl", "linux-musl", "Linux_Musl", "LINUX_MUSL", "linux_musl", "alpine", "ALPINE", "Alpine", "alpine-linux", "ALPINE-LINUX", "alpine_linux", "Alpine_Linux", "ALPINE_LINUX", "Alpine Linux", "alpine linux", "ALPINE LINUX" -> { return ALPINE_LINUX; }
                case "freeBSD", "FreeBSD", "-free-bsd", "-free_bsd", "FREE BSD", "FREEBSD", "freebsd", "free bsd" -> { return FREE_BSD; }
                case "-solaris", "solaris", "SOLARIS", "Solaris"                          -> { return SOLARIS; }
                case "-qnx", "qnx", "QNX"                                                 -> { return QNX; }
                case"-aix", "aix", "AIX"                                                  -> { return AIX; }
                case "darwin", "-darwin", "-macosx", "-MACOSX", "MacOS", "Mac OS", "mac_os", "Mac_OS", "mac-os", "Mac-OS", "mac", "MAC", "macos", "MACOS", "osx", "OSX", "macosx", "MACOSX", "Mac OSX", "Mac OS X", "mac osx" -> { return MACOS; }
                case "-win", "windows", "Windows", "WINDOWS", "win", "Win", "WIN"         -> { return WINDOWS; }
                default                                                                   -> { return NOT_FOUND; }
            }
        }
    }

    public enum OperatingMode {
        NATIVE,
        EMULATED,
        NONE,
        NOT_FOUND;

        public static OperatingMode fromText(final String text) {
            if (null == text) { return NOT_FOUND; }
            switch (text) {
                case "native", "NATIVE", "Native"       -> { return NATIVE; }
                case "emulated", "EMULATED", "Emulated" -> { return EMULATED; }
                default                                 -> { return NOT_FOUND; }
            }
        }
    }

    public enum HttpStatus {
        CONTINUE(100, "Continue"),
        SWITCHING_PROTOCOLS(101, "Switching Protocols"),
        PROCESSING(102, "Processing"),
        EARLY_HINTS(103, "Early Hints"),

        OK(200, "OK"),
        CREATED(201, "Created"),
        ACCEPTED(202, "Accepted"),
        NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
        NO_CONTENT(204, "No Content"),
        RESET_CONTENT(205, "Reset Content"),
        PARTIAL_CONTENT(206, "Partial Content"),
        MULTI_STATUS(207, "Multi-Status"),
        ALREADY_REPORTED(208, "Already Reported"),

        MULTIPLE_CHOICES(300, "Multiple Choices"),
        MOVED_PERMANENTLY(301, "Moved Permanently"),
        FOUND(302, "Found"),
        SEE_OTHER(303, "See Other"),
        NOT_MODIFIED(304, "Not Modified"),
        USE_PROXY(305, "Use Proxy"),
        TEMPORARY_REDIRECT(307, "Temporary Redirect"),

        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Not Found"),
        METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
        NOT_ACCEPTABLE(406, "Not Acceptable"),
        PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
        REQUEST_TIMEOUT(408, "Request Timeout"),
        CONFLICT(409, "Conflict"),
        GONE(410, "Gone"),
        LENGTH_REQUIRED(411, "Length Required"),
        PRECONDITION_FAILED(412, "Precondition Failed"),
        REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
        REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),

        INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
        NOT_IMPLEMENTED(501, "Not Implemented"),
        BAD_GATEWAY(502, "Bad Gateway"),
        SERVICE_UNAVAILABLE(503, "Service Unavailable"),
        GATEWAY_TIMEOUT(504, "Gateway Timeout"),
        HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),

        UNKNOWN(-1, "Unknown Status");

        private final int     statusCode;
        private final String  description;


        HttpStatus(final int statusCode, final String description) {
            this.statusCode  = statusCode;
            this.description = description;
        }


        public final int getStatusCode() { return this.statusCode; }

        public final String getDescription() { return this.description; }


        public static final HttpStatus getFromCode(final int statusCode) {
            switch (statusCode) {
                case 100: return CONTINUE;
                case 101: return SWITCHING_PROTOCOLS;
                case 102: return PROCESSING;
                case 103: return EARLY_HINTS;

                case 200: return OK;
                case 201: return CREATED;
                case 202: return ACCEPTED;
                case 203: return NON_AUTHORITATIVE_INFORMATION;
                case 204: return NO_CONTENT;
                case 205: return RESET_CONTENT;
                case 206: return PARTIAL_CONTENT;
                case 207: return MULTI_STATUS;
                case 208: return ALREADY_REPORTED;

                case 300: return MULTIPLE_CHOICES;
                case 301: return MOVED_PERMANENTLY;
                case 302: return FOUND;
                case 303: return SEE_OTHER;
                case 304: return NOT_MODIFIED;
                case 305: return USE_PROXY;
                case 307: return TEMPORARY_REDIRECT;

                case 400: return BAD_REQUEST;
                case 401: return UNAUTHORIZED;
                case 403: return FORBIDDEN;
                case 404: return NOT_FOUND;
                case 405: return METHOD_NOT_ALLOWED;
                case 406: return NOT_ACCEPTABLE;
                case 407: return PROXY_AUTHENTICATION_REQUIRED;
                case 408: return REQUEST_TIMEOUT;
                case 409: return CONFLICT;
                case 410: return GONE;
                case 411: return LENGTH_REQUIRED;
                case 412: return PRECONDITION_FAILED;
                case 413: return REQUEST_ENTITY_TOO_LARGE;
                case 414: return REQUEST_URI_TOO_LONG;
                case 415: return UNSUPPORTED_MEDIA_TYPE;

                case 500: return INTERNAL_SERVER_ERROR;
                case 501: return NOT_IMPLEMENTED;
                case 502: return BAD_GATEWAY;
                case 503: return SERVICE_UNAVAILABLE;
                case 504: return GATEWAY_TIMEOUT;
                case 505: return HTTP_VERSION_NOT_SUPPORTED;
                default : return UNKNOWN;
            }
        }
    }

    public static final double             EPSILON                  = 1E-6;
    public static final Pattern            INT_PATTERN              = Pattern.compile("[0-9]+");
    public static final Pattern            FLOAT_PATTERN            = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
    public static final Pattern            HEX_PATTERN              = Pattern.compile("#?([A-Fa-f0-9]{8}|[A-Fa-f0-9]{6})");
    public static final Pattern            POSITIVE_INTEGER_PATTERN = Pattern.compile("\\d+");
    public static final String             HOME_FOLDER              = new StringBuilder(System.getProperty("user.home")).append(File.separator).toString();
    public static final long               SECONDS_PER_MINUTE       = 60;
    public static final long               SECONDS_PER_HOUR         = 3_600;
    public static final long               SECONDS_PER_DAY          = 86_400;
    public static final long               SECONDS_PER_MONTH        = 2_592_000;
    public static final java.time.Duration TIME_PERIOD_24_HOURS     = Duration.ofHours(24);
    public static final java.time.Duration TIME_PERIOD_3_DAYS       = Duration.ofDays(3);
    public static final java.time.Duration TIME_PERIOD_5_DAYS       = Duration.ofDays(5);
    public static final java.time.Duration TIME_PERIOD_7_DAYS       = Duration.ofDays(7);
    public static final java.time.Duration TIME_PERIOD_1_MONTH      = Duration.ofSeconds(Period.ofMonths(1).getDays() * SECONDS_PER_DAY);
    public static final java.time.Duration TIME_PERIOD_3_MONTH      = Duration.ofSeconds(Period.ofMonths(3).getDays() * SECONDS_PER_DAY);
    public static final java.time.Duration TIME_PERIOD_6_MONTH      = Duration.ofSeconds(Period.ofMonths(6).getDays() * SECONDS_PER_DAY);
    public static final java.time.Duration TIME_PERIOD_12_MONTH     = Duration.ofSeconds(Period.ofYears(1).getDays() * SECONDS_PER_DAY);
    public static final String             SQUARE_BRACKET_OPEN      = "[";
    public static final String             SQUARE_BRACKET_CLOSE     = "]";
    public static final String             CURLY_BRACKET_OPEN       = "{";
    public static final String             CURLY_BRACKET_CLOSE      = "}";
    public static final String             INDENTED_QUOTES          = "  \"";
    public static final String             QUOTES                   = "\"";
    public static final String             QUOTES_COLON             = "\":";
    public static final String             QUOTES_COLON_QUOTES      = "\":\"";
    public static final String             COLON                    = ":";
    public static final String             COMMA                    = ",";
    public static final String             SLASH                    = "/";
    public static final String             NEW_LINE                 = "\n";
    public static final String             COMMA_NEW_LINE           = ",\n";
    public static final String             NULL                     = "null";
    public static final String             INDENT                   = "  ";
    public static final String             PERCENTAGE               = "\u0025";
    public static final String             DEGREE                   = "\u00B0";
    public static final double             EARTH_RADIUS             = 6_367_517; // m on average
}
