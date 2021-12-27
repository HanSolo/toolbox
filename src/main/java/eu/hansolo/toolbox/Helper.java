package eu.hansolo.toolbox;

import eu.hansolo.toolbox.geom.Bounds;
import eu.hansolo.toolbox.geom.CardinalDirection;
import eu.hansolo.toolbox.geom.CatmullRom;
import eu.hansolo.toolbox.geom.Point;
import eu.hansolo.toolbox.geom.Position;
import eu.hansolo.toolbox.geom.QuickHull;
import eu.hansolo.toolbox.geom.Rectangle;
import eu.hansolo.toolbox.tuples.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import java.util.regex.Matcher;

import static eu.hansolo.toolbox.Constants.*;


public class Helper {
    private static final Matcher INT_MATCHER   = INT_PATTERN.matcher("");
    private static final Matcher FLOAT_MATCHER = FLOAT_PATTERN.matcher("");
    private static final Matcher HEX_MATCHER   = HEX_PATTERN.matcher("");


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

    public static final double nearest(final double smaller, final double value, final double larger) {
        return (value - smaller) < (larger - value) ? smaller : larger;
    }

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

    public static final double[] calcAutoScale(final double minValue, final double maxValue) {
        return calcAutoScale(minValue, maxValue, 10, 10);
    }
    public static final double[] calcAutoScale(final double minValue, final double maxValue, final double maxNoOfMajorTicks, final double maxNoOfMinorTicks) {
        final double niceRange      = (calcNiceNumber((maxValue - minValue), false));
        final double majorTickSpace = calcNiceNumber(niceRange / (maxNoOfMajorTicks - 1), true);
        final double niceMinValue   = (Math.floor(minValue / majorTickSpace) * majorTickSpace);
        final double niceMaxValue   = (Math.ceil(maxValue / majorTickSpace) * majorTickSpace);
        final double minorTickSpace = calcNiceNumber(majorTickSpace / (maxNoOfMinorTicks - 1), true);
        return new double[]{ niceMinValue, niceMaxValue, majorTickSpace, minorTickSpace };
    }

    /**
     * Can be used to implement discrete steps e.g. on a slider.
     * @param minValue          The min value of the range
     * @param maxValue          The max value of the range
     * @param value             The value to snap
     * @param newMinorTickCount The number of ticks between 2 major tick marks
     * @param newMajorTickUnit  The distance between 2 major tick marks
     * @return The value snapped to the next tick mark defined by the given parameters
     */
    public static final double snapToTicks(final double minValue, final double maxValue, final double value, final int newMinorTickCount, final double newMajorTickUnit) {
        double v = value;

        final int    minorTickCount = clamp(0, 10, newMinorTickCount);
        final double majorTickUnit  = Double.compare(newMajorTickUnit, 0.0) <= 0 ? 0.25 : newMajorTickUnit;
        final double tickSpacing    = minorTickCount == 0 ? majorTickUnit : majorTickUnit / (Math.max(minorTickCount, 0) + 1);
        final int    prevTick       = (int) ((v - minValue) / tickSpacing);
        final double prevTickValue  = prevTick * tickSpacing + minValue;
        final double nextTickValue  = (prevTick + 1) * tickSpacing + minValue;

        v = nearest(prevTickValue, v, nextTickValue);

        return clamp(minValue, maxValue, v);
    }

    /**
     * Returns a "niceScaling" number approximately equal to the range.
     * Rounds the number if ROUND == true.
     * Takes the ceiling if ROUND = false.
     *
     * @param range the value range (maxValue - minValue)
     * @param round whether to round the result or ceil
     * @return a "niceScaling" number to be used for the value range
     */
    public static final double calcNiceNumber(final double range, final boolean round) {
        double niceFraction;
        final double exponent = Math.floor(Math.log10(range));   // exponent of range
        final double fraction = range / Math.pow(10, exponent);  // fractional part of range

        if (round) {
            if (Double.compare(fraction, 1.5) < 0) {
                niceFraction = 1;
            } else if (Double.compare(fraction, 3)  < 0) {
                niceFraction = 2;
            } else if (Double.compare(fraction, 7) < 0) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        } else {
            if (Double.compare(fraction, 1) <= 0) {
                niceFraction = 1;
            } else if (Double.compare(fraction, 2) <= 0) {
                niceFraction = 2;
            } else if (Double.compare(fraction, 5) <= 0) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        }
        return niceFraction * Math.pow(10, exponent);
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
        String normalized = text.replaceAll("\u00fc", "ue")
                                .replaceAll("\u00f6", "oe")
                                .replaceAll("\u00e4", "ae")
                                .replaceAll("\u00df", "ss");

        normalized = normalized.replaceAll("\u00dc(?=[a-z\u00fc\u00f6\u00e4\u00df ])", "Ue")
                               .replaceAll("\u00d6(?=[a-z\u00fc\u00f6\u00e4\u00df ])", "Oe")
                               .replaceAll("\u00c4(?=[a-z\u00fc\u00f6\u00e4\u00df ])", "Ae");

        normalized = normalized.replaceAll("\u00dc", "UE")
                               .replaceAll("\u00d6", "OE")
                               .replaceAll("\u00c4", "AE");
        return normalized;
    }

    public static final List<Point> subdividePoints(final List<Point> points, final int subDevisions) {
        return Arrays.asList(subdividePoints(points.toArray(new Point[0]), subDevisions));
    }
    public static final Point[] subdividePoints(final Point[] points, final int subDevisions) {
        assert points != null;
        assert points.length >= 3;
        int    noOfPoints = points.length;

        Point[] subdividedPoints = new Point[((noOfPoints - 1) * subDevisions) + 1];

        double increments = 1.0 / (double) subDevisions;

        for (int i = 0 ; i < noOfPoints - 1 ; i++) {
            Point p0 = i == 0 ? points[i] : points[i - 1];
            Point p1 = points[i];
            Point p2 = points[i + 1];
            Point p3 = (i+2 == noOfPoints) ? points[i + 1] : points[i + 2];

            CatmullRom crs = new CatmullRom(p0, p1, p2, p3);

            for (int j = 0; j <= subDevisions; j++) {
                subdividedPoints[(i * subDevisions) + j] = crs.q(j * increments);
            }
        }

        return subdividedPoints;
    }

    public static final List<Point> subdividePointsRadial(final List<Point> points, final int subDevisions) {
        return Arrays.asList(subdividePointsRadial(points.toArray(new Point[0]), subDevisions));
    }
    public static final Point[] subdividePointsRadial(final Point[] points, final int subDivisions){
        assert points != null;
        assert points.length >= 3;
        int    noOfPoints = points.length;

        Point[] subdividedPoints = new Point[((noOfPoints - 1) * subDivisions) + 1];

        double increments = 1.0 / (double) subDivisions;

        for (int i = 0 ; i < noOfPoints - 1 ; i++) {
            Point p0 = i == 0 ? points[noOfPoints - 2] : points[i - 1];
            Point p1 = points[i];
            Point p2 = points[i + 1];
            Point p3 = (i == (noOfPoints - 2)) ? points[1] : points[i + 2];

            CatmullRom<Point> crs = new CatmullRom<>(p0, p1, p2, p3);

            for (int j = 0 ; j <= subDivisions ; j++) {
                subdividedPoints[(i * subDivisions) + j] = crs.q(j * increments);
            }
        }

        return subdividedPoints;
    }

    public static final List<Point> subdividePointsLinear(final List<Point> points, final int subDevisions) {
        return Arrays.asList(subdividePointsLinear(points.toArray(new Point[0]), subDevisions));
    }
    public static final Point[] subdividePointsLinear(final Point[] points, final int subDivisions) {
        assert  points != null;
        assert  points.length >= 3;

        final int     noOfPoints       = points.length;
        final Point[] subdividedPoints = new Point[((noOfPoints - 1) * subDivisions) + 1];
        final double  stepSize         = (points[1].getX() - points[0].getX()) / subDivisions;
        for (int i = 0 ; i < noOfPoints - 1 ; i++) {
            for (int j = 0 ; j <= subDivisions ; j++) {
                subdividedPoints[(i * subDivisions) + j] = calcIntermediatePoint(points[i], points[i+1], stepSize * j);
            }
        }
        return subdividedPoints;
    }

    public static final Point calcIntermediatePoint(final Point leftPoint, final Point rightPoint, final double intervalX) {
        double m = (rightPoint.getY() - leftPoint.getY()) / (rightPoint.getX() - leftPoint.getX());
        double x = intervalX;
        double y = m * x;
        return new Point(leftPoint.getX() + x, leftPoint.getY() + y);
    }

    public static final Point calcIntersectionPoint(final Point leftPoint, final Point rightPoint, final double intersectionY) {
        double[] xy = calculateInterSectionPoint(leftPoint.getX(), leftPoint.getY(), rightPoint.getX(), rightPoint.getY(), intersectionY);
        return new Point(xy[0], xy[1]);
    }
    public static final double[] calculateInterSectionPoint(final Point leftPoint, final Point rightPoint, final double intersectionY) {
        return calculateInterSectionPoint(leftPoint.getX(), leftPoint.getY(), rightPoint.getX(), rightPoint.getY(), intersectionY);
    }
    public static final double[] calculateInterSectionPoint(final double x1, final double y1, final double x2, final double y2, final double intersectionY) {
        double m = (y2 - y1) / (x2 - x1);
        double interSectionX = (intersectionY - y1) / m;
        return new double[] { x1 + interSectionX, intersectionY };
    }

    public static final Point[] smoothSparkLine(final List<Double> dataList, final double minValue, final double maxValue, final Rectangle graphBounds, final int noOfDatapoints) {
        int     size   = dataList.size();
        Point[] points = new Point[size];

        double low  = Statistics.getMin(dataList);
        double high = Statistics.getMax(dataList);
        if (Helper.equals(low, high)) {
            low  = minValue;
            high = maxValue;
        }
        double range = high - low;

        double minX  = graphBounds.getX();
        double maxX  = minX + graphBounds.getWidth();
        double minY  = graphBounds.getY();
        double maxY  = minY + graphBounds.getHeight();
        double stepX = graphBounds.getWidth() / (noOfDatapoints - 1);
        double stepY = graphBounds.getHeight() / range;

        for (int i = 0 ; i < size ; i++) {
            points[i] = new Point(minX + i * stepX, maxY - Math.abs(low - dataList.get(i)) * stepY);
        }

        return Helper.subdividePoints(points, 16);
    }

    public static final boolean isInRectangle(final double x, final double y,
                                              final double minX, final double minY,
                                              final double maxX, final double maxY) {
        return (Double.compare(x, minX) >= 0 &&
                Double.compare(y, minY) >= 0 &&
                Double.compare(x, maxX) <= 0 &&
                Double.compare(y, maxY) <= 0);
    }

    public static final boolean isInEllipse(final double x, final double y,
                                            final double centerX, final double centerY,
                                            final double radiusX, final double radiusY) {
        return Double.compare(((((x - centerX) * (x - centerX)) / (radiusX * radiusX)) +
                               (((y - centerY) * (y - centerY)) / (radiusY * radiusY))), 1) <= 0.0;
    }

    public static final boolean isInCircle(final double x, final double y, final double centerX, final double centerY, final double radius) {
        double deltaX = centerX - x;
        double deltaY = centerY - y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY) < radius;
    }

    public static final boolean isInPolygon(final double x, final double y, final List<Point> pointsOfPolygon) {
        int noOfPointsInPolygon = pointsOfPolygon.size();
        double[] pointsX = new double[noOfPointsInPolygon];
        double[] pointsY = new double[noOfPointsInPolygon];
        for ( int i = 0 ; i < noOfPointsInPolygon ; i++) {
            Point p = pointsOfPolygon.get(i);
            pointsX[i] = p.getX();
            pointsY[i] = p.getY();
        }
        return isInPolygon(x, y, noOfPointsInPolygon, pointsX, pointsY);
    }
    public static final boolean isInPolygon(final double x, final double y, final int noOfPointsInPolygon, final double[] pointsX, final double[] pointsY) {
        if (noOfPointsInPolygon != pointsX.length || noOfPointsInPolygon != pointsY.length) { return false; }
        boolean inside = false;
        for (int i = 0, j = noOfPointsInPolygon - 1; i < noOfPointsInPolygon ; j = i++) {
            if (((pointsY[i] > y) != (pointsY[j] > y)) && (x < (pointsX[j] - pointsX[i]) * (y - pointsY[i]) / (pointsY[j] - pointsY[i]) + pointsX[i])) {
                inside = !inside;
            }
        }
        return inside;
    }

    public static final <T extends Point> boolean isPointInPolygon(final T p, final List<T> points) {
        boolean inside     = false;
        int     noOfPoints = points.size();
        double  x          = p.getX();
        double  y          = p.getY();

        for (int i = 0, j = noOfPoints - 1 ; i < noOfPoints ; j = i++) {
            if ((points.get(i).getY() > y) != (points.get(j).getY() > y) &&
                (x < (points.get(j).getX() - points.get(i).getX()) * (y - points.get(i).getY()) / (points.get(j).getY() - points.get(i).getY()) + points.get(i).getX())) {
                inside = !inside;
            }
        }
        return inside;
    }

    public static final boolean isInRingSegment(final double x, final double y,
                                                final double centerX, final double centerY,
                                                final double outerRadius, final double innerRadius,
                                                final double newStartAngle, final double segmentAngle) {
        double angleOffset = 90.0;
        double pointRadius = Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
        double pointAngle  = getAngleFromXY(x, y, centerX, centerY, angleOffset);
        double startAngle  = angleOffset - newStartAngle;
        double endAngle    = startAngle + segmentAngle;

        return (Double.compare(pointRadius, innerRadius) >= 0 &&
                Double.compare(pointRadius, outerRadius) <= 0 &&
                Double.compare(pointAngle, startAngle) >= 0 &&
                Double.compare(pointAngle, endAngle) <= 0);
    }

    public static final boolean isPointOnLine(final Point p, final Point p1, final Point p2) {
        return (distanceFromPointToLine(p, p1, p2) < EPSILON);
    }

    public static final double distanceFromPointToLine(final Point p, final Point p1, final Point p2) {
        double A = p.getX() - p1.getX();
        double B = p.getY() - p1.getY();
        double C = p2.getX() - p1.getX();
        double D = p2.getY() - p1.getY();

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = dot / len_sq;

        double xx, yy;

        if (param < 0 || (p1.getX() == p2.getX() && p1.getY() == p2.getY())) {
            xx = p1.getX();
            yy = p1.getY();
        } else if (param > 1) {
            xx = p2.getX();
            yy = p2.getY();
        } else {
            xx = p1.getX() + param * C;
            yy = p1.getY() + param * D;
        }

        double dx = p.getX() - xx;
        double dy = p.getY() - yy;

        return Math.sqrt(dx * dx + dy * dy);
    }

    public static final double distance(final Point p1, final Point p2) {
        return distance(p1.x, p1.y, p2.x, p2.y);
    }
    public static final double distance(final double p1X, final double p1Y, final double p2X, final double p2Y) {
        return Math.sqrt((p2X - p1X) * (p2X - p1X) + (p2Y - p1Y) * (p2Y - p1Y));
    }

    public static final double euclideanDistance(final Point p1, final Point p2) { return euclideanDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY()); }
    public static final double euclideanDistance(final double x1, final double y1, final double x2, final double y2) {
        double deltaX = (x2 - x1);
        double deltaY = (y2 - y1);
        return (deltaX * deltaX) + (deltaY * deltaY);
    }

    public static final Point pointOnLine(final double p1X, final double p1Y, final double p2X, final double p2Y, final double distanceToP2) {
        double distanceP1P2 = distance(p1X, p1Y, p2X, p2Y);
        double t = distanceToP2 / distanceP1P2;
        return new Point((1 - t) * p1X + t * p2X, (1 - t) * p1Y + t * p2Y);
    }

    public static final int checkLineCircleCollision(final Point p1, final Point p2, final double centerX, final double centerY, final double radius) {
        return checkLineCircleCollision(p1.x, p1.y, p2.x, p2.y, centerX, centerY, radius);
    }
    public static final int checkLineCircleCollision(final double p1X, final double p1Y, final double p2X, final double p2Y, final double centerX, final double centerY, final double radius) {
        double A = (p1Y - p2Y);
        double B = (p2X - p1X);
        double C = (p1X * p2Y - p2X * p1Y);

        return checkCollision(A, B, C, centerX, centerY, radius);
    }
    public static final int checkCollision(final double a, final double b, final double c, final double centerX, final double centerY, final double radius) {
        // Finding the distance of line from center.
        double dist = (Math.abs(a * centerX + b * centerY + c)) / Math.sqrt(a * a + b * b);
        dist = round(dist, 1);
        if (radius > dist) {
            return 1;  // intersect
        } else if (radius < dist) {
            return -1; // outside
        } else {
            return 0;  // touch
        }
    }

    public static final double getAngleFromXY(final double x, final double y, final double centerX, final double centerY) {
        return getAngleFromXY(x, y, centerX, centerY, 90.0);
    }
    public static final double getAngleFromXY(final double x, final double y, final double centerX, final double centerY, final double angleOffset) {
        // For ANGLE_OFFSET =  0 -> Angle of 0 is at 3 o'clock
        // For ANGLE_OFFSET = 90  ->Angle of 0 is at 12 o'clock
        double deltaX      = x - centerX;
        double deltaY      = y - centerY;
        double radius      = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        double nx          = deltaX / radius;
        double ny          = deltaY / radius;
        double theta       = Math.atan2(ny, nx);
        theta              = Double.compare(theta, 0.0) >= 0 ? Math.toDegrees(theta) : Math.toDegrees((theta)) + 360.0;
        double angle       = (theta + angleOffset) % 360;
        return angle;
    }

    public static final double[] rotatePointAroundRotationCenter(final double x, final double y, final double rX, final double rY, final double angleDeg) {
        final double rad = Math.toRadians(angleDeg);
        final double sin = Math.sin(rad);
        final double cos = Math.cos(rad);
        final double nX  = rX + (x - rX) * cos - (y - rY) * sin;
        final double nY  = rY + (x - rX) * sin + (y - rY) * cos;
        return new double[] { nX, nY };
    }

    public static final Point getPointBetweenP1AndP2(final Point p1, final Point p2) {
        double[] xy = getPointBetweenP1AndP2(p1.x, p1.y, p2.x, p2.y);
        return new Point(xy[0], xy[1]);
    }
    public static final double[] getPointBetweenP1AndP2(final double p1X, final double p1Y, final double p2X, final double p2Y) {
        return new double[] { (p1X + p2X) * 0.5, (p1Y + p2Y) * 0.5 };
    }

    public static final int getDegrees(final double decDeg) { return (int) decDeg; }
    public static final int getMinutes(final double decDeg) { return (int) ((decDeg - getDegrees(decDeg)) * 60); }
    public static final double getSeconds(final double decDeg) { return (((decDeg - getDegrees(decDeg)) * 60) - getMinutes(decDeg)) * 60; }

    public static final double getDecimalDeg(final int degrees, final int minutes, final double seconds) {
        return (((seconds / 60) + minutes) / 60) + degrees;
    }

    public static final <T> Predicate<T> not(Predicate<T> predicate) { return predicate.negate(); }

    public static final <T extends Point> List<T> createConvexHull_OLD(final List<T> points) {
        List<T> convexHull = new ArrayList<>();
        if (points.size() < 3) { return new ArrayList<T>(points); }

        int minDataPoint = -1;
        int maxDataPoint = -1;
        int minX         = Integer.MAX_VALUE;
        int maxX         = Integer.MIN_VALUE;

        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).getX() < minX) {
                minX     = (int) points.get(i).getX();
                minDataPoint = i;
            }
            if (points.get(i).getX() > maxX) {
                maxX     = (int) points.get(i).getX();
                maxDataPoint = i;
            }
        }
        T minPoint = points.get(minDataPoint);
        T maxPoint = points.get(maxDataPoint);
        convexHull.add(minPoint);
        convexHull.add(maxPoint);
        points.remove(minPoint);
        points.remove(maxPoint);

        List<T> leftSet  = new ArrayList<>();
        List<T> rightSet = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            T p = points.get(i);
            if (pointLocation(minPoint, maxPoint, p) == -1) { leftSet.add(p); } else if (pointLocation(minPoint, maxPoint, p) == 1) rightSet.add(p);
        }
        hullSet(minPoint, maxPoint, rightSet, convexHull);
        hullSet(maxPoint, minPoint, leftSet, convexHull);

        // Add last point which is the same as first point
        convexHull.add((T) new Point(convexHull.get(0).getX(), convexHull.get(0).getY()));

        return convexHull;
    }
    public static final List<Point> createConvexHull(final List<Point> points) {
        return QuickHull.quickHull(points);
    }

    public static final List<Point> createSmoothedConvexHull(final List<Point> points, final int subDivisions) {
        List<Point> hullPolygon = createConvexHull(points);
        return subdividePoints(hullPolygon, subDivisions);
    }

    private static final <T extends Point> double distance(final T p1, final T p2, final T p3) {
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        double num = deltaX * (p1.getY() - p3.getY()) - deltaY * (p1.getX() - p3.getX());
        return Math.abs(num);
    }
    private static final <T extends Point> void hullSet(final T p1, final T p2, final List<T> points, final List<T> hull) {
        int insertPosition = hull.indexOf(p2);

        if (points.size() == 0) { return; }

        if (points.size() == 1) {
            T point = points.get(0);
            points.remove(point);
            hull.add(insertPosition, point);
            return;
        }

        int dist              = Integer.MIN_VALUE;
        int furthestDataPoint = -1;
        for (int i = 0; i < points.size(); i++) {
            T point    = points.get(i);
            double distance = distance(p1, p2, point);
            if (distance > dist) {
                dist          = (int) distance;
                furthestDataPoint = i;
            }
        }
        T point = points.get(furthestDataPoint);
        points.remove(furthestDataPoint);
        hull.add(insertPosition, point);

        // Determine who's to the left of AP
        ArrayList<T> leftSetAP = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            T M = points.get(i);
            if (pointLocation(p1, point, M) == 1) { leftSetAP.add(M); }
        }

        // Determine who's to the left of PB
        ArrayList<T> leftSetPB = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            T M = points.get(i);
            if (pointLocation(point, p2, M) == 1) { leftSetPB.add(M); }
        }
        hullSet(p1, point, leftSetAP, hull);
        hullSet(point, p2, leftSetPB, hull);
    }
    private static final <T extends Point> int pointLocation(final T p1, final T p2, final T p3) {
        double cp1 = (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
        return cp1 > 0 ? 1 : Double.compare(cp1, 0) == 0 ? 0 : -1;
    }

    public static final List<Point> reduceHull(final List<Point> points, final List<Point> hullPoints) {
        int noOfAttempts = 0;
        //List<Point> pointsToCheck = removePointsOnConvexHull(points, hullPoints);
        List<Point> pointsToCheck = new ArrayList<>(points);
        while(noOfAttempts < 1_000_000 && noOfDiagonalEdges(hullPoints) != 0) {
            List<Point> pointsToRemove = new ArrayList<>();
            for (int i = 0, size = hullPoints.size() ; i < size - 1 ; i++) {
                Point p1 = hullPoints.get(i);
                Point p2 = hullPoints.get(i + 1);

                if (isHorizontal(p1, p2) || isVertical(p1, p2)) { continue; }

                Optional<Point> newPoint = pointsToCheck.stream()
                                                        .min(Comparator.comparingDouble(p -> distanceFromPointToLine(p, p1, p2)));
                if (newPoint.isPresent()) {
                    Point np = newPoint.get();
                    hullPoints.add(i + 1, np);
                    pointsToRemove.add(np);
                }
            }
            pointsToCheck.removeAll(pointsToRemove);
            noOfAttempts++;
        }
        return hullPoints;
    }
    public static final List<Point> removePointsOnConvexHull(final List<Point> points, final List<Point> convexHull) {
        List<Point> pointsNotOnHullCurve = new ArrayList<>(points);
        List<Point> pointsToRemove       = new ArrayList<>();
        int         pointsInPolygon      = convexHull.size();
        for (int i = 0 ; i < pointsInPolygon - 1 ; i++) {
            Point p1 = convexHull.get(i);
            Point p2 = convexHull.get(i + 1);
            pointsNotOnHullCurve.forEach(p -> {
                if (isPointOnLine(p, p1, p2)) { pointsToRemove.add(p); }
            });
        }
        pointsNotOnHullCurve.removeAll(pointsToRemove);
        return pointsNotOnHullCurve;
    }
    public static final int noOfDiagonalEdges(final List<Point> polygonPoints) {
        int noOfDiagonalEdges = 0;
        int pointsInPolygon = polygonPoints.size();
        for (int i = 0 ; i < pointsInPolygon - 1 ; i++) {
            Point p1 = polygonPoints.get(i);
            Point p2 = polygonPoints.get(i + 1);
            if (isHorizontal(p1, p2) || isVertical(p1, p2)) { continue; }
            noOfDiagonalEdges++;
        }
        if (!isHorizontal(polygonPoints.get(pointsInPolygon - 1), polygonPoints.get(0)) &&
            !isVertical(polygonPoints.get(pointsInPolygon - 1), polygonPoints.get(0))) {
            noOfDiagonalEdges++;
        }
        return noOfDiagonalEdges;
    }
    public static final boolean isHorizontal(final Point p1, final Point p2) { return Math.abs(p1.getY() - p2.getY()) < EPSILON; }
    public static final boolean isVertical(final Point p1, final Point p2)   { return Math.abs(p1.getX() - p2.getX()) < EPSILON; }


    /**
     * Check the given hull curve for diagonals and collect all points that are in the rectangle
     * that will be defined by the two points of each diagonal.
     * This points can then be used to reduce the convex hull curve of a polygon to a rectangular
     * hull curve
     * @param points points to check
     * @param hullCurvePoints points on hull curve
     * @return list of points that can be used to reduce the diagonals in a convex hull curve
     */
    public static final List<Point> getPointsToCheck(final List<Point> points, final List<Point> hullCurvePoints) {
        List<Point[]> diagonals = new ArrayList<>();
        int pointsInPolygon = hullCurvePoints.size();
        for (int i = 0 ; i < pointsInPolygon - 1 ; i++) {
            Point p1 = hullCurvePoints.get(i);
            Point p2 = hullCurvePoints.get(i + 1);
            if (isHorizontal(p1, p2) || isVertical(p1, p2)) { continue; }
            diagonals.add(new Point[]{p1, p2});
        }

        List<Bounds> areas = new ArrayList<>();
        for (Point[] d : diagonals) {
            double x = Math.min(d[0].x, d[1].x);
            double y = Math.min(d[0].y, d[1].y);
            double w = Math.abs(d[1].x - d[0].x);
            double h = Math.abs(d[1].y - d[0].y);
            areas.add(new Bounds(x, y, w, h));
        }

        List<Point> pointsToCheck = new ArrayList<>();
        for (Point p : points) {
            for (Bounds area : areas) {
                if (isInRectangle(p.getX(), p.getY(), area.getX(), area.getY(), area.getX() + area.getWidth(), area.getY() + area.getHeight())) { pointsToCheck.add(p); }
            }
        }

        return pointsToCheck;
    }

    /**
     * Add points from given points to curvePoints if points are on the polygon defined by curvePoints
     * @param curvePoints list of points on curve
     * @param points list of points to add
     * @return list of points incl. the added ones
     */
    public static final List<Point> addPointsOnCurve(final List<Point> curvePoints, final List<Point> points) {
        List<Point> result   = new ArrayList<>();
        List<Point> polygonPoints = new ArrayList<>(curvePoints);
        List<Point> pointsToCheck = new ArrayList<>(points);
        pointsToCheck.removeAll(curvePoints);

        int noOfPointsToCheck   = pointsToCheck.size();
        int noOfPointsOnPolygon = polygonPoints.size();
        for (int i = 0 ; i < noOfPointsOnPolygon - 1 ; i++) {
            Point p1 = polygonPoints.get(i);
            Point p2 = polygonPoints.get(i + 1);

            if (!result.contains(p1)) { result.add(p1); }
            for (int j = 0 ; j < noOfPointsToCheck ; j++) {
                Point p = pointsToCheck.get(j);
                if (isPointOnLine(p, p1, p2)) { result.add(p); }
            }
        }
        result.add(polygonPoints.get(noOfPointsOnPolygon - 1));
        return result;
    }

    /**
     * Curve points should be ordered counterclockwise along the curve
     * @param curvePoints points that define the curve sorted counterclockwise
     * @param width width of the box that will be checked for next point
     * @param height height of the box that will be checked for next point
     * @param points points to check
     * @return list of point pairs that define the start and end points of gaps
     */
    public static final List<Point> findGaps(final List<Point> curvePoints, final double width, final double height, final List<Point> points) {
        List<Point> startEndPoints      = new ArrayList<>();
        List<Point> pointsToCheck       = new ArrayList<>(points);
        int         noOfPointsOnPolygon = curvePoints.size();
        for (int i = 0 ; i < noOfPointsOnPolygon - 1 ; i++) {
            Point    p1  = curvePoints.get(i);
            Point    p2  = curvePoints.get(i + 1);
            Position pos = Position.UNDEFINED;
            if (isHorizontal(p1, p2)) {
                if (p1.getX() < p2.getX()) {
                    pos = Position.BOTTOM;
                } else if (p1.getX() > p2.getX()) {
                    pos = Position.TOP;
                }
            } else if (isVertical(p1, p2)) {
                if (p1.getY() < p2.getY()) {
                    pos = Position.LEFT;
                } else if (p1.getY() > p2.getY()) {
                    pos = Position.RIGHT;
                }
            }

            switch(pos) {
                case TOP:
                    if (!isInRectangle(p2.getX(), p2.getY(), p1.getX() - width, p1.getY() - height, p1.getX(), p1.getY())) {
                        startEndPoints.add(p1);
                        startEndPoints.add(p2);
                        double dX = Math.abs(p1.getX() - p2.getX());
                        // Search for next point in vertical direction
                        for (Point p : pointsToCheck) {
                            if(isInRectangle(p.getX(), p.getY(), p1.getX() - width / 2, p1.getY() - height, p1.getX(), p1.getY())) {
                                if (isVertical(p, p1)) {
                                    //System.out.println("Find next vertical point in gap");
                                }
                            }
                        }
                    }
                    break;
                case LEFT:
                    if (!isInRectangle(p2.getX(), p2.getY(), p1.getX(), p1.getY(), p1.getX() + width, p1.getY() + height)) {
                        startEndPoints.add(p1);
                        startEndPoints.add(p2);
                        double dY = Math.abs(p1.getY() - p2.getY());
                    }
                    break;
                case BOTTOM:
                    if (!isInRectangle(p2.getX(), p2.getY(), p1.getX(), p1.getY() - height, p1.getX() + width, p1.getY())) {
                        startEndPoints.add(p1);
                        startEndPoints.add(p2);
                        double dX = Math.abs(p1.getX() - p2.getX());
                        // Search for next point in vertical direction
                        for (Point p : pointsToCheck) {
                            if(isInRectangle(p.getX(), p.getY(), p1.getX() - width / 2, p1.getY() - height, p1.getX() + width, p1.getY())) {
                                if (isVertical(p, p1)) {
                                    //System.out.println("Find next vertical point in gap");
                                }
                            }
                        }
                    }
                    break;
                case RIGHT:
                    if (!isInRectangle(p2.getX(), p2.getY(), p1.getX() - width, p1.getY() - height, p1.getX(), p1.getY())) {
                        startEndPoints.add(p1);
                        startEndPoints.add(p2);
                        double dY = Math.abs(p1.getY() - p2.getY());
                    }
                    break;
            }
        }
        return startEndPoints;
    }

    public static final double[] getPointsXFromPoints(final List<Point> points) {
        int size = points.size();
        double[] pointsX = new double[size];
        for (int i = 0 ; i < size ; i++) { pointsX[i] = points.get(i).getX(); }
        return pointsX;
    }
    public static final double[] getPointsYFromPoints(final List<Point> points) {
        int size = points.size();
        double[] pointsY = new double[size];
        for (int i = 0 ; i < size ; i++) { pointsY[i] = points.get(i).getY(); }
        return pointsY;
    }

    public static final double[] getDoubleArrayFromPoints(final List<Point> points) {
        int size = points.size();
        double[] pointsArray = new double[size * 2];
        int counter = 0;
        for (int i = 0 ; i < size ; i++) {
            pointsArray[counter]     = points.get(i).getX();
            pointsArray[counter + 1] = points.get(i).getY();
            counter += 2;
        }
        return pointsArray;
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

    public static final void sortXY(final List<Point> points) {
        Collections.sort(points, Comparator.comparingDouble(Point::getX).thenComparingDouble(Point::getY));
    }

    /**
     * Sort a list of points by it's distance from each other. The algorithm starts with the point closest to
     * 0,0 and from there always adds the point closest to the last point
     * @param points list of points to sort
     * @return list of points sorted by it's distance from each other
     */
    public static final List<Point> sortByDistance(final List<Point> points) {
        return sortByDistance(points, true);
    }
    public static final List<Point> sortByDistance(final List<Point> points, final boolean counterClockWise) {
        if (points.isEmpty()) { return points; }
        List<Point> output = new ArrayList<>();
        output.add(points.get(nearestPoint(new Point(0, 0), points)));
        points.remove(output.get(0));
        int x = 0;
        for (int i = 0; i < points.size() + x; i++) {
            output.add(points.get(nearestPoint(output.get(output.size() - 1), points)));
            points.remove(output.get(output.size() - 1));
            x++;
        }
        if (counterClockWise) { Collections.reverse(output); }
        return output;
    }
    public static final int nearestPoint(final Point p, final List<Point> points) {
        Pair<Double, Integer> smallestDistance = new Pair<>(0d, 0);
        for (int i = 0; i < points.size(); i++) {
            double distance = distance(p.getX(), p.getY(), points.get(i).getX(), points.get(i).getY());
            if (i == 0) {
                smallestDistance = new Pair<>(distance, i);
            } else {
                if (distance < smallestDistance.getA()) {
                    smallestDistance = new Pair<>(distance, i);
                }
            }
        }
        return smallestDistance.getB();
    }

    public static final String padLeft(final String text, final String filler, final int n) {
        return String.format("%" + n + "s", text).replace(" ", filler);
    }
    public static final String padRight(final String text, final String filler, final int n) {
        return String.format("%-" + n + "s", text).replace(" ", filler);
    }

    public static final List<Point> removeDuplicatePoints(final List<Point> points, final double tolerance) {
        final double tol  = tolerance < 0 ? 0 : tolerance;
        final int    size = points.size();

        List<Point> reducedPoints  = new ArrayList<>(points);
        Set<Point>  pointsToRemove = new HashSet<>();

        for (int i = 0 ; i < size - 2 ; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);

            double distP1P2  = Helper.distance(p1, p2);

            // Remove duplicates
            if (distP1P2 <= tol) { pointsToRemove.add(p2); }
        }
        reducedPoints.removeAll(pointsToRemove);

        return reducedPoints;
    }

    public static final List<Point> simplify(final List<Point> points, final double angleTolerance, final double minDistance) {
        final double tolerance = angleTolerance < 0 ? 0.5 : angleTolerance / 2.0;
        final double distance  = minDistance < 0 ? 1.0 : minDistance;

        final int    size = points.size();
        if (size <= 4) { return points; }

        List<Point> reducedPoints  = new ArrayList<>(removeDuplicatePoints(points, 1));
        Set<Point>  pointsToRemove = new HashSet<>();

        for (int i = 0 ; i < size - 3 ; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            Point p3 = points.get(i + 2);

            double bearingP1P2  = bearing(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            double bearingP1P3  = bearing(p1.getX(), p1.getY(), p3.getX(), p3.getY());
            double bearingP2P3  = bearing(p2.getX(), p2.getY(), p3.getX(), p3.getY());
            double deltaBearing = Math.abs(bearingP1P2 - bearingP2P3);

            if (deltaBearing < 0.5) {
                pointsToRemove.add(p2); // Points are on same line -> remove p2
            } else if (deltaBearing % 90 == 0) {
                // Keep corner point 90 deg
            } else if (deltaBearing > 80 && deltaBearing < 90) {
                // Keep probable corner point between 80-90 deg
            } else if (bearingP1P3 > bearingP1P2 - tolerance && bearingP1P3 < bearingP1P2 + tolerance) {
                pointsToRemove.add(p2);
            } else if (Helper.distance(p1, p2) < distance) {
                // Remove points within distance
                pointsToRemove.add(p2);
            }
        }

        // Check
        Point lastPoint       = points.get(size - 1);
        Point secondLastPoint = points.get(size - 2);
        Point thirdLastPoint  = points.get(size - 3);
        Point fourthLastPoint = points.get(size - 4);

        if (removeP2(fourthLastPoint, thirdLastPoint, secondLastPoint, lastPoint, tolerance, distance)) {
            pointsToRemove.add(secondLastPoint);
        }

        reducedPoints.removeAll(pointsToRemove);

        return reducedPoints;
    }
    private static final boolean removeP2(final Point p0, final Point p1, final Point p2, final Point p3, final double tolerance, final double distance) {
        double bearingP1P2  = bearing(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        double bearingP1P3  = bearing(p1.getX(), p1.getY(), p3.getX(), p3.getY());
        double bearingP2P3  = bearing(p2.getX(), p2.getY(), p3.getX(), p3.getY());
        double deltaBearing = Math.abs(bearingP1P2 - bearingP2P3);

        if (deltaBearing < 0.5) {
            return true;
        } else if (deltaBearing % 90 == 0) {
            return false;
        } else if (deltaBearing > 80 && deltaBearing < 90) {
            return false;
        } else if (bearingP1P3 > bearingP1P2 - tolerance && bearingP1P3 < bearingP1P2 + tolerance) {
            return true;
        } else if (Helper.distance(p1, p2) < distance) {
            return true;
        }
        return false;
    }

    public static final double bearing(final Point p1, final Point p2) {
        return bearing(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
    public static final double bearing(final double x1, final double y1, final double x2, final double y2) {
        double bearing = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)) + 90;
        if (bearing < 0) { bearing += 360.0; }
        return bearing;
    }

    public static final String getCardinalDirectionFromBearing(final double brng) {
        double bearing = brng % 360.0;
        if (0 == bearing || 360 == bearing || (bearing > CardinalDirection.N.from && bearing < 360)) {
            return CardinalDirection.N.direction;
        } else if (90 == bearing) {
            return CardinalDirection.E.direction;
        } else if (180 == bearing) {
            return CardinalDirection.S.direction;
        } else if (270 == bearing) {
            return CardinalDirection.W.direction;
        } else {
            for (CardinalDirection cardinalDirection : CardinalDirection.values()) {
                if (bearing >= cardinalDirection.from && bearing <= cardinalDirection.to) {
                    return cardinalDirection.direction;
                }
            }
        }
        return "";
    }

    public static String readFromInputStream(final InputStream inputStream) throws IOException {
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

    /**
     * @param startPoint
     * @param controlPoint1
     * @param controlPoint2
     * @param endPoint
     * @param distance in % (0-1)
     * @return
     */
    public static final Point getCubicBezierXYatT(final Point startPoint, final Point controlPoint1, final Point controlPoint2, final Point endPoint, final double distance) {
        final double x = cubicN(distance, startPoint.getX(), controlPoint1.getX(), controlPoint2.getX(), endPoint.getX());
        final double y = cubicN(distance, startPoint.getY(), controlPoint1.getY(), controlPoint2.getY(), endPoint.getY());
        return new Point(x, y);
    }
    public static final double[] getCubicBezierXYatT(final double startPointX, final double startPointY,
                                                     final double controlPoint1X, final double controlPoint1Y,
                                                     final double controlPoint2X, final double controlPoint2Y,
                                                     final double endPointX, final double endPointY, final double distance) {
        final double x = cubicN(distance, startPointX, controlPoint1X, controlPoint2X, endPointX);
        final double y = cubicN(distance, startPointY, controlPoint1Y, controlPoint2Y, endPointY);
        return new double[] { x, y };
    }
    private static final double cubicN(final double distance, final double a, final double b, final double c, final double d) {
        final double t2 = distance * distance;
        final double t3 = t2 * distance;
        return a + (-a * 3 + distance * (3 * a - a * distance)) * distance + (3 * b + distance * (-6 * b + b * 3 * distance)) * distance + (c * 3 - c * 3 * distance) * t2 + d * t3;
    }
}
