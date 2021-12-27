package eu.hansolo.toolbox;

import java.time.Duration;
import java.time.Period;
import java.util.regex.Pattern;


public class Constants {
    public static final double             EARTH_RADIUS         = 6_371_000; // [m]
    public static final double             HALF_PI              = Math.PI * 0.5;
    public static final double             TWO_PI               = Math.PI + Math.PI;
    public static final double             THREE_PI             = TWO_PI + Math.PI;
    public static final double             EPSILON              = 1E-6;
    public static final Pattern            INT_PATTERN          = Pattern.compile("[0-9]+");
    public static final Pattern            FLOAT_PATTERN        = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
    public static final Pattern            HEX_PATTERN          = Pattern.compile("#?([A-Fa-f0-9]{8}|[A-Fa-f0-9]{6})");
    public static final long               SECONDS_PER_MINUTE   = 60;
    public static final long               SECONDS_PER_HOUR     = 3_600;
    public static final long               SECONDS_PER_DAY      = 86_400;
    public static final long               SECONDS_PER_MONTH    = 2_592_000;
    public static final java.time.Duration TIME_PERIOD_24_HOURS = Duration.ofHours(24);
    public static final java.time.Duration TIME_PERIOD_3_DAYS   = Duration.ofDays(3);
    public static final java.time.Duration TIME_PERIOD_5_DAYS   = Duration.ofDays(5);
    public static final java.time.Duration TIME_PERIOD_7_DAYS   = Duration.ofDays(7);
    public static final java.time.Duration TIME_PERIOD_1_MONTH  = Duration.ofSeconds(Period.ofMonths(1).getDays() * SECONDS_PER_DAY);
    public static final java.time.Duration TIME_PERIOD_3_MONTH  = Duration.ofSeconds(Period.ofMonths(3).getDays() * SECONDS_PER_DAY);
    public static final java.time.Duration TIME_PERIOD_6_MONTH  = Duration.ofSeconds(Period.ofMonths(6).getDays() * SECONDS_PER_DAY);
    public static final java.time.Duration TIME_PERIOD_12_MONTH = Duration.ofSeconds(Period.ofYears(1).getDays() * SECONDS_PER_DAY);
    public static final String             SQUARE_BRACKET_OPEN  = "[";
    public static final String             SQUARE_BRACKET_CLOSE = "]";
    public static final String             CURLY_BRACKET_OPEN   = "{";
    public static final String             CURLY_BRACKET_CLOSE  = "}";
    public static final String             INDENTED_QUOTES      = "  \"";
    public static final String             QUOTES               = "\"";
    public static final String             COLON                = ":";
    public static final String             COMMA                = ",";
    public static final String             SLASH                = "/";
    public static final String             NEW_LINE             = "\n";
    public static final String             COMMA_NEW_LINE       = ",\n";
    public static final String             INDENT               = "  ";
    public static final String             PERCENTAGE           = "\u0025";
    public static final String             DEGREE               = "\u00B0";
}
