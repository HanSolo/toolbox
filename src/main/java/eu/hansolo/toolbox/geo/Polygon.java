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

import java.util.Arrays;
import java.util.Objects;


public class Polygon {
    private int            size;
    private PolygonPoint[] points;


    // ******************** Constructors **************************************
    public Polygon() {
        this.size   = 0;
        this.points = new PolygonPoint[8];
    }
    public Polygon(final PolygonPoint... points) {
        this.size   = points.length;
        this.points = new PolygonPoint[size];
        for (int i = 0 ; i < this.size ; i++) {
            this.points[i] = points[i];
        }
    }
    public Polygon(final GeoLocation... locations) {
        this.size   = locations.length;
        this.points = new PolygonPoint[size];
        for (int i = 0 ; i < this.size ; i++) {
            this.points[i] = new PolygonPoint(locations[i].getLongitude(), locations[i].getLatitude());
        }
    }


    // ******************** Methods *******************************************
    private void resize() {
        PolygonPoint[] tmp = new PolygonPoint[2 * size + 1];
        for (int i = 0; i <= size; i++) {
            tmp[i] = points[i];
        }
        points = tmp;
    }

    public int size() { return size; }

    public void add(final GeoLocation location) {
        this.add(new PolygonPoint(location.getLongitude(), location.getLatitude()));
    }
    public void add(final PolygonPoint point) {
        if (size >= points.length - 1) { resize(); } // resize array if needed
        points[size++] = point;                      // add point
        points[size]   = points[0];                  // close polygon
    }

    public boolean contains(final GeoLocation location) {
        return contains(new PolygonPoint(location.getLongitude(), location.getLatitude()));
    }
    public boolean contains(final PolygonPoint point) {
        int winding = 0;
        for (int i = 0; i < size - 1 ; i++) {
            int ccw = PolygonPoint.ccw(points[i], points[i + 1], point);
            if (points[i + 1].y > point.y && point.y >= points[i].y) { // upward crossing
                if (ccw == +1) {
                    winding++;
                }
            }
            if (points[i + 1].y <= point.y && point.y < points[i].y) { // downward crossing
                if (ccw == -1) {
                    winding--;
                }
            }
        }
        return winding != 0;
    }

    public boolean contains2(final PolygonPoint point) {
        int crossings = 0;
        for (int i = 0; i < size; i++) {
            int j = i + 1;
            boolean cond1 = (points[i].y <= point.y) && (point.y < points[j].y);
            boolean cond2 = (points[j].y <= point.y) && (point.y < points[i].y);
            if (cond1 || cond2) {
                if (point.x < (points[j].x - points[i].x) * (point.y - points[i].y) / (points[j].y - points[i].y) + points[i].x) {
                    crossings++;
                }
            }
        }
        return crossings %2 == 1;
    }

    public double getPerimeter() {
        double sum = 0.0;
        for (int i = 0; i < size; i++) {
            sum = sum + points[i].distanceTo(points[i + 1]);
        }
        return sum;
    }

    public double getArea() {
        double sum = 0.0;
        for (int i = 0; i < size; i++) {
            sum = sum + (points[i].x * points[i + 1].y) - (points[i].y * points[i + 1].x);
        }
        return 0.5 * sum;
    }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Polygon polygon = (Polygon) o;
        return size == polygon.size && Arrays.equals(points, polygon.points);
    }

    @Override public int hashCode() {
        int result = Objects.hash(size);
        result = 31 * result + Arrays.hashCode(points);
        return result;
    }

    @Override public String toString() {
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append(Constants.SQUARE_BRACKET_OPEN);
        for (PolygonPoint p : points) {
            msgBuilder.append(p.toString()).append(Constants.COMMA);
        }
        if (msgBuilder.length() > 1) { msgBuilder.setLength(msgBuilder.length() - 1); }
        msgBuilder.append(Constants.SQUARE_BRACKET_CLOSE);
        return msgBuilder.toString();
    }
}
