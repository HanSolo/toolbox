/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 Gerrit Grunwald.
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

package eu.hansolo.toolbox.geo;

import eu.hansolo.toolbox.Constants;

import java.util.Objects;


public class PolygonPoint {
    public double x;
    public double y;


    // ******************** Constructors **************************************
    public PolygonPoint() {
        this(0, 0);
    }
    public PolygonPoint(final GeoLocation location) {
        this(location.getLongitude(), location.getLatitude());
    }
    public PolygonPoint(final double x, final double y) {
        this.x = x;
        this.y = y;
    }


    // ******************** Methods *******************************************
    public double distanceTo(final PolygonPoint point) {
        if (point == null) { return Double.POSITIVE_INFINITY; }
        final double dx = this.x - point.x;
        final double dy = this.y - point.y;
        return Math.hypot(dx, dy);
    }

    public static int ccw(final PolygonPoint point1, final PolygonPoint point2, final PolygonPoint point3) {
        final double area = (point2.x - point1.x) * (point3.y - point1.y) - (point3.x - point1.x) * (point2.y - point1.y);
        return area < 0 ? -1 : area > 0 ? +1 : 0;
    }

    public static boolean collinear(final PolygonPoint point1, final PolygonPoint point2, final PolygonPoint point3) {
        return ccw(point1, point2, point3) == 0;
    }

    public static boolean isBetween(final PolygonPoint point, final PolygonPoint point1, final PolygonPoint point2) {
        if (ccw(point1, point2, point) != 0) { return false; }
        if (Double.compare(point1.x, point2.x) == 0 && Double.compare(point1.y, point2.y) == 0) {
            return Double.compare(point1.x, point.x) == 0 && Double.compare(point1.y, point.y) == 0;
        } else if (point1.x != point2.x) { // p1p2 not vertical
            return (point1.x <= point.x && point.x <= point2.x) || (point1.x >= point.x && point.x >= point2.x);
        } else { // p1p2 not horizontal
            return (point1.y <= point.y && point.y <= point2.y) || (point1.y >= point.y && point.y >= point2.y);
        }
    }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        PolygonPoint point = (PolygonPoint) o;
        return Double.compare(x, point.x) == 0 && Double.compare(y, point.y) == 0;
    }

    @Override public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override public String toString() {
        return new StringBuilder().append(Constants.CURLY_BRACKET_OPEN)
                                  .append(Constants.QUOTES).append("x").append(Constants.QUOTES_COLON).append(this.x).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append("y").append(Constants.QUOTES_COLON).append(this.y)
                                  .append(Constants.CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}

