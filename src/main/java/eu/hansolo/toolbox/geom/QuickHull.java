package eu.hansolo.toolbox.geom;

import java.util.ArrayList;
import java.util.List;


public class QuickHull {

    public static final List<Point> quickHull(final List<Point> pointList) {
        ArrayList<Point> points     = new ArrayList<>(pointList);
        List<Point>      convexHull = new ArrayList<>();
        if (points.size() < 3) return (ArrayList) points.clone();

        int    minPoint = -1;
        int    maxPoint = -1;
        double minX     = Double.MAX_VALUE;
        double maxX     = -Double.MAX_VALUE;
        for (int i = 0, size = points.size() ; i < size; i++) {
            if (points.get(i).x < minX) {
                minX     = points.get(i).x;
                minPoint = i;
            }
            if (points.get(i).x > maxX) {
                maxX     = points.get(i).x;
                maxPoint = i;
            }
        }
        Point minP = points.get(minPoint);
        Point maxP = points.get(maxPoint);
        convexHull.add(minP);
        convexHull.add(maxP);
        points.remove(minP);
        points.remove(maxP);

        ArrayList<Point> leftSet  = new ArrayList<>();
        ArrayList<Point> rightSet = new ArrayList<>();

        for (Point p : points) {
            if (pointLocation(minP, maxP, p) == -1) {
                leftSet.add(p);
            } else if (pointLocation(minP, maxP, p) == 1) {
                rightSet.add(p);
            }
        }
        hullSet(minP, maxP, rightSet, convexHull);
        hullSet(maxP, minP, leftSet, convexHull);

        return convexHull;
    }

    private static final double distance(final Point p1, final Point p2, final Point p3) {
        double ABx = p2.x - p1.x;
        double ABy = p2.y - p1.y;
        double num = ABx * (p1.y - p3.y) - ABy * (p1.x - p3.x);
        if (num < 0) { num = -num; }
        return num;
    }

    private static final void hullSet(final Point p1, final Point p2, final ArrayList<Point> set, final List<Point> hull) {
        int insertPosition = hull.indexOf(p2);
        int size = set.size();

        if (size == 0) { return; }
        if (size == 1) {
            Point p = set.get(0);
            set.remove(p);
            hull.add(insertPosition, p);
            return;
        }
        double dist          = -Double.MAX_VALUE;
        int    furthestPoint = -1;
        for (int i = 0 ; i < size ; i++) {
            Point p = set.get(i);
            double distance = distance(p1, p2, p);
            if (distance > dist) {
                dist = distance;
                furthestPoint = i;
            }
        }
        Point P = set.get(furthestPoint);
        set.remove(furthestPoint);
        hull.add(insertPosition, P);

        // Determine who's to the left of AP
        ArrayList<Point> leftSetAP = new ArrayList<>();
        for (Point M : set) {
            if (pointLocation(p1, P, M) == 1) { leftSetAP.add(M); }
        }

        // Determine who's to the left of PB
        ArrayList<Point> leftSetPB = new ArrayList<>();
        for (Point M : set) {
            if (pointLocation(P, p2, M) == 1.0) { leftSetPB.add(M); }
        }
        hullSet(p1, P, leftSetAP, hull);
        hullSet(P, p2, leftSetPB, hull);
    }

    private static final double pointLocation(final Point p1, final Point p2, final Point p3) {
        double cp1 = (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x);
        if (cp1 > 0) {
            return 1;
        } else if (cp1 == 0) {
            return 0;
        } else {
            return -1;
        }
    }
}
